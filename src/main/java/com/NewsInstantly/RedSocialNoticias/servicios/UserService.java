package com.NewsInstantly.RedSocialNoticias.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import com.NewsInstantly.RedSocialNoticias.entidades.Token;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.enums.Role;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProfilePhotoService profilePhotoService;
	@Autowired
	private NotificationService mailService;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private CoverPhotoService coverPhotoService;

	public UserService() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * metodo que permite guardar un usuario en la DB
	 * 
	 * @param email
	 * @param nick
	 * @param password
	 * @param name
	 * @param lastName
	 * @param birthDate
	 * @return
	 * @throws ErrorServicio
	 */
	@Transactional
	public Usuario registerUser(String email, String nick, String password, String name, String lastName,
			Date birthDate) throws ErrorServicio {
		if (userExists(email)) {
			Usuario user = new Usuario();
			user.setNombre(name);
			user.setEmail(email);
			user.setNick(nick);
			// encrypting user password
			String encript = new BCryptPasswordEncoder().encode(password);
			user.setContrasenia(encript);
			user.setApellido(lastName);
			user.setFechaNacimiento(birthDate);
			user.setRol(Role.USER);
			user.setAlta(false);

			userRepository.save(user);

			Token token = tokenService.buildToken(email, 0);

			mailService.sendMail(
					"¡Felicidades! Se registro con éxito en nuestra pagina, ingrese al siguiente link para confirmar su registro: "
							+ "http://localhost:8080/confirmar-cuenta/" + token.getId(),
					"News Instantly: confirmar su cuenta", user.getEmail());

			return user;

		} else {
			throw new ErrorServicio("Ya existe ese usuario");
		}
	}

	/**
	 * comprobar que exista el usuario
	 * 
	 * @param email
	 * @return true si no existe y false si existe
	 */
	public Boolean userExists(String email) {
		Usuario user = userRepository.findByEmail(email);
		return user == null;
	}

	/**
	 * metodo que permite comprobar que los datos recibidos cumplen con las
	 * condiciones minimas
	 * 
	 * @param password
	 * @param confirmPassword
	 * @param nick
	 * @param name
	 * @param lastName
	 * @param mail
	 * @param year
	 * @param month
	 * @param day
	 * @return true si todos los datos son correctos
	 * @throws ErrorServicio
	 */
	public boolean checkRegistrationData(String password, String confirmPassword, String nick, String name,
			String lastName, String mail, Integer year, Integer month, Integer day) throws ErrorServicio {
		if (nick == null || nick.isEmpty()) {
			throw new ErrorServicio("Error en el nick: esta vacio");
		}

		if (userRepository.findStringNick(nick) != null) {
			throw new ErrorServicio("Error en el nick: ese nick ya esta en uso");
		}

		if (name == null || name.isEmpty()) {
			throw new ErrorServicio("Error en el nombre: esta vacio");
		}

		if (lastName == null || lastName.isEmpty()) {
			throw new ErrorServicio("Error en el apellido: esta vacio");
		}

		if (mail == null || mail.isEmpty()) {
			throw new ErrorServicio("Error en el mail: esta vacio");
		}

		if (userRepository.findByEmail(mail) != null) {
			throw new ErrorServicio("Error en el mail: ya hay un usuario registrado con ese nombre");
		}

		if (password.isEmpty() || password.length() < 6) {
			throw new ErrorServicio("Error la clave: esta vacia");
		}

		if (password.compareTo(confirmPassword) != 0) {
			throw new ErrorServicio("Error la clave: no coincide con la confirmación");
		}

		if (year == 0) {
			throw new ErrorServicio("Error en la fecha de nacimiento: no se selecciono un año");
		}

		if (month == 0) {
			throw new ErrorServicio("Error en la fecha de nacimiento: no se selecciono un mes");
		}

		if (day == 0) {
			throw new ErrorServicio("Error en la fecha de nacimiento: no se selecciono un dia");
		}

		return true;
	}

	/**
	 * le setea una nueva contraseña a un usuario ya creado.
	 * 
	 * @param user
	 * @param password
	 */
	@Transactional
	public void passwordChange(Usuario user, String password) {
		String encript = new BCryptPasswordEncoder().encode(password);
		user.setContrasenia(encript);
		userRepository.save(user);
	}

	/**
	 * comprueba que la contraseña nueva sea igual a la contraseña de comprobacion.
	 * En caso que se reciba una contraseña ya seteada, comprueba que sea igual a la
	 * de la DB
	 * 
	 * @param user
	 * @param lastPassword
	 * @param password
	 * @param confirmPassword
	 * @throws ErrorServicio
	 */
	public void passwordCheck(Usuario user, String lastPassword, String password, String confirmPassword)
			throws ErrorServicio {
		if (lastPassword != null && !new BCryptPasswordEncoder().matches(lastPassword, user.getContrasenia())) {
			throw new ErrorServicio("La contraseña que va a ser cambiada no coincide con la base de datos");
		}

		if (password == null || password.isEmpty() || password.length() < 6) {
			throw new ErrorServicio("Error la clave: esta vacia o tiene menos de 6 caracteres");
		}

		if (password.compareTo(confirmPassword) != 0) {
			throw new ErrorServicio("Error la clave: no coincide con la confirmación");
		}
	}

	/**
	 * cuando el atributo alta del usuario esta en false, lo cambia a true y si el
	 * atributo esta en true lo cambia a false
	 * 
	 * @param email
	 * @throws ErrorServicio
	 */
	@Transactional()
	public void grantWithdrawMembership(String email) throws ErrorServicio {
		Usuario usuario = userByEmail(email);

		if (usuario.getAlta() == false) {
			usuario.setAlta(true);
			userRepository.save(usuario);
		} else {
			usuario.setAlta(false);
			userRepository.save(usuario);
		}
	}

	/**
	 * reemplaza los parametros de un usuario ya existente por los parametros
	 * recibidos
	 * 
	 * @param email
	 * @param nick
	 * @param name
	 * @param lastName
	 * @param year
	 * @param month
	 * @param day
	 * @param profilePhoto
	 * @param coverPhoto
	 * @throws ErrorServicio
	 */
	@Transactional
	public void changeUserData(String email, String nick, String name, String lastName, Integer year, Integer month,
			Integer day, MultipartFile profilePhoto, MultipartFile coverPhoto) throws ErrorServicio {
		Usuario user = userByEmail(email);
		checkChangeData(user, nick, name, lastName, year, month, day);

		user.setNick(nick);
		user.setNombre(name);
		user.setApellido(lastName);
		user.setFechaNacimiento(dateBuilder(year, month, day));

		if (profilePhoto.getSize() != 0) {
			if (user.getImagenPerfil() != null) {
				user.setImagenPerfil(profilePhotoService.uploadPhoto(user.getImagenPerfil().getId(), profilePhoto));
			} else {
				user.setImagenPerfil(profilePhotoService.uploadPhoto(null, profilePhoto));
			}
		}

		if(coverPhoto.getSize()!=0) {
			if (user.getImagenPortada() != null) {
				user.setImagenPortada(coverPhotoService.uploadPhoto(user.getImagenPortada().getId(), coverPhoto));
			} else {
				user.setImagenPortada(coverPhotoService.uploadPhoto(null, coverPhoto));
			}
		}

		userRepository.save(user);

	}

	/**
	 * controla que los datos utilizados para modificar el usuario sean validos
	 * 
	 * @param user
	 * @param nick
	 * @param name
	 * @param lastName
	 * @param year
	 * @param month
	 * @param day
	 * @throws ErrorServicio
	 */
	public void checkChangeData(Usuario user, String nick, String name, String lastName, Integer year, Integer month,
			Integer day) throws ErrorServicio {
		if (nick == null || nick.isEmpty()) {
			throw new ErrorServicio("Error en el nick: esta vacio");
		}

		if (!user.getNick().equalsIgnoreCase(nick) && userRepository.findStringNick(nick) != null) {
			throw new ErrorServicio("Error en el nick: ya esta en uso");
		}

		if (name == null || name.isEmpty()) {
			throw new ErrorServicio("Error en el nombre: esta vacio");
		}

		if (lastName == null || lastName.isEmpty()) {
			throw new ErrorServicio("Error en el apellido: esta vacio");
		}

		if (year == 0) {
			throw new ErrorServicio("Error en la fecha de nacimiento: no se selecciono un año");
		}

		if (month == 0) {
			throw new ErrorServicio("Error en la fecha de nacimiento: no se selecciono un mes");
		}

		if (day == 0) {
			throw new ErrorServicio("Error en la fecha de nacimiento: no se selecciono un dia");
		}
	}

	/**
	 * permite guardar de forma correcta una fecha
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @return
	 */
	public Date dateBuilder(Integer year, Integer month, Integer day) {
		return new Date(year - 1900, month - 1, day);
	}

	// metodo que se utiliza cuando un usario quiere loguearse
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		Usuario user = userRepository.findByEmail(email);

		if (user != null && user.getAlta() == true) {
			List<GrantedAuthority> permissions = new ArrayList<>();
			GrantedAuthority p = new SimpleGrantedAuthority("ROLE_" + user.getRol().toString());
			permissions.add(p);
			ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
			HttpSession session = attr.getRequest().getSession(true);
			session.setAttribute("user", user);
			return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getContrasenia(),
					permissions);
		}
		return null;
	}

	/**
	 * metodo que utiliza UsuarioRepositorio para buscar un usuario atraves de su
	 * email (ID) comprueba que exista y lo devuelve
	 * 
	 * @param email
	 * @return Usuario
	 * @throws ErrorServicio
	 */
	@Transactional(readOnly = true)
	public Usuario userByEmail(String email) throws ErrorServicio {
		Optional<Usuario> result = userRepository.findById(email);

		if (result.isPresent()) {
			Usuario usuario = result.get();
			return usuario;
		} else {
			throw new ErrorServicio("no se encontro usuario");
		}
	}

	/**
	 * metodo que busca un usuario por su nick
	 * 
	 * @param nick
	 * @return Usuario
	 * @throws ErrorServicio
	 */
	@Transactional(readOnly = true)
	public Usuario userByNick(String nick) throws ErrorServicio {
		Usuario usuario = userRepository.findByNick(nick);
		if (usuario == null) {
			throw new ErrorServicio("no se encontro ese usuario");
		} else {
			return usuario;
		}
	}

}

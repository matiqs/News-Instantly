package com.NewsInstantly.RedSocialNoticias.servicios;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.NewsInstantly.RedSocialNoticias.entidades.Token;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.TokenRepository;

@Service
public class TokenService {

	@Autowired
	TokenRepository tokenRepository;
	@Autowired
	UserService userService;

	/**
	 * crea un token con un usuario y en caso de necesitarlo, una fecha de vencimiento
	 * @param email
	 * @param option
	 * @return Token
	 */
	@Transactional
	public Token buildToken(String email, int option) throws ErrorServicio{
		Token token = new Token();
		Date creation = new Date();
		Usuario user;
		try {
			user = userService.userByEmail(email);
			token.setUsuario(user);
		} catch (ErrorServicio e) {
			throw new ErrorServicio("no se encontro un usuario con ese email");
		}
		//la opc 1 es para cuando se olvido la contraseña, se le da un tiempo de expiracion de 48 hs
		if (option == 1) {
			token.setExpirar(new Date(creation.getTime() + (1000 * 60 * 60 * 48)));
		}
		token.setCreacion(creation);
		token.setCompletado(false);

		return tokenRepository.save(token);
	}

	/**
	 * utiliza un token para poner en true el atributo alta del usuario asociado al mismo
	 * @param id
	 * @throws ErrorServicio
	 */
	@Transactional
	public void authorizeUser(String id) throws ErrorServicio {
		Token token = findToken(id);

		checkToken(token);

		userService.grantWithdrawMembership(token.getUsuario().getEmail());
		token.setCompletado(true);
		tokenRepository.save(token);

	}

	/**
	 * busca un token en la DB
	 * @param id
	 * @return Token
	 * @throws ErrorServicio
	 */
	@Transactional(readOnly = true)
	public Token findToken(String id) throws ErrorServicio {
		Optional<Token> result = tokenRepository.findById(id);

		if (result.isEmpty()) {
			throw new ErrorServicio("no se encotro el token");
		} else {
			return result.get();
		}
	}

	/**
	 * comprueba que el token no este usado o , si tiene vencimiento, que no este vencido.
	 * @param token
	 * @throws ErrorServicio
	 */
	public void checkToken(Token token) throws ErrorServicio {
		if (token.getCompletado() == true) {
			throw new ErrorServicio("Este link ya no es valido.");
		}
		if (token.getExpirar() != null && token.getExpirar().before(new Date())) {
			token.setCompletado(true);
			tokenRepository.save(token);
			throw new ErrorServicio("El tiempo de espera ha caducado, vuelva a solicitar la peticion");
		}
	}
	
	/**
	 * utiliza un token para acceder al usuario asociado y cambiarle la contraseña
	 * sin tener que saber la contraseña anterior.
	 * @param tokenID
	 * @param password
	 * @param confirmPassword
	 * @throws ErrorServicio
	 */
	@Transactional
	public void recoveryPassword(String tokenID,String password,String confirmPassword) throws ErrorServicio{
		Token token=findToken(tokenID);
		checkToken(token);
		
		userService.passwordCheck(token.getUsuario(), null, password, confirmPassword);
		userService.passwordChange(token.getUsuario(), password);
		
		token.setCompletado(true);
		tokenRepository.save(token);
	}
}

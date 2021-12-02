package com.NewsInstantly.RedSocialNoticias.controlador;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.NewsInstantly.RedSocialNoticias.entidades.Publicacion;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.servicios.NotificationService;
import com.NewsInstantly.RedSocialNoticias.servicios.PublicationService;
import com.NewsInstantly.RedSocialNoticias.servicios.UserService;

@Controller
@RequestMapping("/perfil")
public class PerfilController {

	@Autowired
	PublicationService publicationService;
	@Autowired
	UserService userService;
	@Autowired
	NotificationService notificationService;

	@GetMapping("/{nick}")
	public String showProfile(ModelMap model, @PathVariable String nick,RedirectAttributes redirect) {
		try {
			model.put("publications", publicationService.publicationsByNick(nick));
			model.put("user", userService.userByNick(nick));
			return "profile";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
	}

	@PreAuthorize("hasRole('ROLE_USER')"
			+ " || hasRole('ROLE_ADMIN')")
	@GetMapping("/modificar/{email}")
	public String modProfile(ModelMap model, @PathVariable String email, HttpSession session,
			RedirectAttributes redirect) {
		Usuario login = (Usuario) session.getAttribute("user");

		if (login == null || !(login.getEmail().equals(email))) {
			redirect.addFlashAttribute("error", "Por favor, no intente cambiar los datos de otra persona");
			return "redirect:/inicio";
		}
		try {
			Usuario user = userService.userByEmail(email);
			model.put("email", email);
			model.put("user", user);
			model.put("day", user.getFechaNacimiento().getDate());
			model.put("month", user.getFechaNacimiento().getMonth() + 1);
			model.put("year", user.getFechaNacimiento().getYear() + 1900);

			String monthName = "";
			switch (user.getFechaNacimiento().getMonth() + 1) {
			case 1:
				monthName = "Enero";
				break;
			case 2:
				monthName = "Febrero";
				break;
			case 3:
				monthName = "Marzo";
				break;
			case 4:
				monthName = "Abril";
				break;
			case 5:
				monthName = "Mayo";
				break;
			case 6:
				monthName = "Junio";
				break;
			case 7:
				monthName = "Julio";
				break;
			case 8:
				monthName = "Agosto";
				break;
			case 9:
				monthName = "Septiembre";
				break;
			case 10:
				monthName = "Octubre";
				break;
			case 11:
				monthName = "Noviembre";
				break;
			case 12:
				monthName = "Diciembre";
				break;
			}
			model.put("monthName", monthName);
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
		return "editProfile";
	}

	@PreAuthorize("hasRole('ROLE_USER')"
			+ " || hasRole('ROLE_ADMIN')")
	@PostMapping("/modificar-perfil/{email}")
	public String changeProfileUser(ModelMap modelo, @PathVariable(required = false) String email,
			@RequestParam(required = false) String nick, @RequestParam(required = false) String name,
			@RequestParam(required = false) String lastName, @RequestParam(required = false) Integer day,
			@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year,
			@RequestParam(required = false) MultipartFile profilePhoto,
			@RequestParam(required = false) MultipartFile coverPhoto, HttpSession session,
			RedirectAttributes redirect) {
		Usuario user;
		try {
			user = userService.userByEmail(email);
		} catch (ErrorServicio e1) {
			redirect.addFlashAttribute("error", e1.getMessage());
			return "redirect:/inicio";
		}
		try {
			Usuario login = (Usuario) session.getAttribute("user");

			if (login == null || !(login.getEmail().equals(email))) {
				redirect.addFlashAttribute("error", "Por favor, no intente cambiar los datos de otra persona");
				return "redirect:/inicio";
			}

			userService.changeUserData(email, nick, name, lastName, year, month, day, profilePhoto, coverPhoto);
			session.setAttribute("user", userService.userByEmail(email));
			
			return "redirect:" + "/perfil/" + nick;

		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			redirect.addFlashAttribute("email", email);
			redirect.addFlashAttribute("user", user);
			redirect.addFlashAttribute("day", day);
			redirect.addFlashAttribute("month", month);
			redirect.addFlashAttribute("year", year);

			return "redirect:/perfil/modificar/" + email;
		}
	}

	@PreAuthorize("hasRole('ROLE_USER')"
			+ " || hasRole('ROLE_ADMIN')")
	@PostMapping("/modificar-password/{email}")
	public String changePasswordUser(ModelMap modelo, @PathVariable String email, @RequestParam String lastPassword,
			@RequestParam String password, @RequestParam String confirmPassword, RedirectAttributes redirect,
			HttpSession session) {
		Usuario login = (Usuario) session.getAttribute("user");
		try {

			if (login == null || !(login.getEmail().equals(email))) {
				redirect.addFlashAttribute("error", "Por favor, no intente cambiar los datos de otra persona");
				return "redirect:/inicio";
			}

			Usuario user = userService.userByEmail(email);
			userService.passwordCheck(user, lastPassword, password, confirmPassword);
			userService.passwordChange(user, password);
			session.setAttribute("user", userService.userByEmail(email));
			return "redirect:" + "/perfil/" + user.getNick();

		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			redirect.addFlashAttribute("email", email);
			e.printStackTrace();
			return "redirect:" + "/perfil/" + login.getNick();
		}
	}

	@PostMapping("/eliminar/{email}")
	public String userDown(@PathVariable String email, @RequestParam String password, RedirectAttributes redirect,
			HttpSession session) {
		Usuario login = (Usuario) session.getAttribute("user");

		if (login == null || !(login.getEmail().equals(email))) {
			redirect.addFlashAttribute("error", "Por favor, no intente cambiar los datos de otra persona");
			return "redirect:/inicio";
		}

		if (new BCryptPasswordEncoder().matches(password, login.getContrasenia())) {
			try {
				userService.grantWithdrawMembership(email);
				return "redirect:/logout";
			} catch (ErrorServicio e) {
				redirect.addFlashAttribute("error", e.getMessage());
				return "redirect:/perfil/" + login.getNick();
			}

		} else {
			redirect.addFlashAttribute("error", "contraseña incorrecta");
			return "redirect:/perfil/" + login.getNick();
		}
	}

	@GetMapping("/denunciar/{email}")
	public String reportProfile(@PathVariable String email, HttpSession session, RedirectAttributes redirect) {
		try {
			Usuario login = (Usuario) session.getAttribute("user");
			Usuario user;
			user = userService.userByEmail(email);
			
			if (login == null || login.getEmail().equals(user.getEmail())) {
				redirect.addFlashAttribute("error",
						"no puede denunciar su propio perfil o no puede realizar una denuncia"
								+ " sin estar logeado");
				return "redirect:/inicio";
			}
			//COLOCAR UN EMAIL PARA RECIBIR LOS MENSAJES DE DENUNCIA
			notificationService.sendMail("El usuario: "+login.getEmail()+
					" denuncio el perfil: http://localhost:8080/perfil/" +user.getNick(), "Denuncia perfil", "emailADMIN@gmail.com");
			redirect.addFlashAttribute("success", "Perfil reportardo correctamente");
			return "redirect:/inicio";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/ban/{email}")
	public String userBan(@PathVariable String email, RedirectAttributes redirect) {
		try {
			userService.grantWithdrawMembership(email);
			redirect.addFlashAttribute("success", "Se realizo la operacion con exito");

			if (userService.userByEmail(email).getAlta() == true) {
				notificationService.sendMail("Analizamos su sitacion y su usuario fue dado de alta nuevamente",
						"Ban de cuenta", email);
			} else {
				notificationService.sendMail("Su usuario fue dado de baja, escribanos si quiere recuperar su cuenta"
						+ " Atentamente News Instantly", "Ban de cuenta", email);
			}
			
			return "redirect:/inicio";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
	}
}

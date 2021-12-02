package com.NewsInstantly.RedSocialNoticias.controlador;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.NewsInstantly.RedSocialNoticias.entidades.Token;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.servicios.NotificationService;
import com.NewsInstantly.RedSocialNoticias.servicios.TokenService;
import com.NewsInstantly.RedSocialNoticias.servicios.UserService;

@Controller
@RequestMapping("/")
public class LoginController {
	
	@Autowired
	TokenService tokenService;
	@Autowired
	UserService userService;
	@Autowired
    NotificationService mailService;
	
	@GetMapping("")
	public String allAccess(ModelMap model,@RequestParam(required = false) String error) {
		if(error != null) {
			model.put("error", "El usuario o contraseña son incorrectos");
		}
		return "login.html";
	}
	
	
	@PostMapping("/email-recuperacion")
	public String formPassword(RedirectAttributes redirect,@RequestParam String email,HttpSession session) {
		try {
			Usuario login = (Usuario) session.getAttribute("user");

			if (login != null) {
				Token token=tokenService.buildToken(login.getEmail(), 1);
				mailService.sendMail("Por favor ingrese al siguiente link antes de 48hs y escriba una nueva contraseña: "
						+ "http://localhost:8080/recuperar-contraseña/"+token.getId(), "News Instantly: cambio de contraseña", token.getUsuario().getEmail());
				redirect.addFlashAttribute("success", "se envio un email para cambiar su contraseña, recuerde que el link expira en 48hs");
				return "redirect:" + "/perfil/" + login.getNick();
			}
			
			Token token=tokenService.buildToken(email, 1);
			mailService.sendMail("Por favor ingrese al siguiente link antes de 48hs y escriba una nueva contraseña: "
					+ "http://localhost:8080/recuperar-contraseña/"+token.getId(), "News Instantly: cambio de contraseña", token.getUsuario().getEmail());
			redirect.addFlashAttribute("success", "se envio un email para cambiar su contraseña, recuerde que el link expira en 48hs");
			return"redirect:/";
			
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return"redirect:/";
		}
	}
	
}

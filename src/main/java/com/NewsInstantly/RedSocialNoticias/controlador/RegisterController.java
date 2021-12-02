package com.NewsInstantly.RedSocialNoticias.controlador;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.UserRepository;
import com.NewsInstantly.RedSocialNoticias.servicios.UserService;

@Controller
@RequestMapping("/")
public class RegisterController {
	

	@Autowired 
	private UserService userService;
	
	//register direct //
	@GetMapping("register")
	public String registrationPage(){
		return "register.html";
	}
	
	// register user post //
	@PostMapping("/registerUser")
	public String userRegister(ModelMap model, @RequestParam String email, @RequestParam String name, @RequestParam String lastName, @RequestParam String password, @RequestParam String confirmPassword, @RequestParam String nick, @RequestParam Integer day, @RequestParam Integer month, @RequestParam Integer year, RedirectAttributes redirect) {
		try {
		userService.checkRegistrationData(password, confirmPassword, nick, name, lastName, email, year, month, day);
		
		Date birthDate = userService.dateBuilder(year, month, day);
		
		userService.registerUser(email, nick, password, name, lastName, birthDate);		
		
		redirect.addFlashAttribute("success", "Se registro con exito, recuerder ingresar al link que enviamos a su email para poder habilitar la cuenta");
		return "redirect:/";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			redirect.addFlashAttribute("email", email);
			redirect.addFlashAttribute("name", name);
			redirect.addFlashAttribute("lastName", lastName);
			redirect.addFlashAttribute("nick", nick);
			return "redirect:/register";
		}
	}
	
	@GetMapping("/register/condiciones")
	public String terms() {
		return "Condiciones";
	}
	
	@GetMapping("/register/politicas-datos")
	public String dataPolicy() {
		return "Politica-de-datos";
	}
	
	@GetMapping("/register/politicas-cookies")
	public String cookiesPolicy() {
		return "Politica-de-cookies";
	}
}

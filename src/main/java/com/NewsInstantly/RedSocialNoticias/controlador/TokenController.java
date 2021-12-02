package com.NewsInstantly.RedSocialNoticias.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.servicios.TokenService;

@Controller
@RequestMapping("/")
public class TokenController {

	@Autowired
	TokenService tokenService;
	
	@GetMapping("/confirmar-cuenta/{id}")
	public String confirmAccount(@PathVariable String id,RedirectAttributes redirect) {
		try {
			tokenService.authorizeUser(id);
			redirect.addFlashAttribute("success", "Su cuenta ya se encuentra habilitada");
			return "redirect:/";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/";
		}
	}
	
	@GetMapping("/recuperar-contraseña/{id}")
	public String viewRecoveryPassword(ModelMap model,@PathVariable String id,RedirectAttributes redirect) {
		try {
			tokenService.checkToken(tokenService.findToken(id));
			model.put("token", id);
			return "olvidarContra";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/";
		}
	}
	
	@PostMapping("/recuperar-contraseña/{id}")
	public String changePassword(@PathVariable String id,@RequestParam String password,@RequestParam String confirmPassword,RedirectAttributes redirect) {
		try {
			tokenService.recoveryPassword(id, password, confirmPassword);
			redirect.addFlashAttribute("success", "su contraseña fue modificada");
			return "redirect:/";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/";
		}
	}
}

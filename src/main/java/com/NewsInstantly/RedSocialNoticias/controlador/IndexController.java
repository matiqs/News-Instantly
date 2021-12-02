package com.NewsInstantly.RedSocialNoticias.controlador;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.NewsInstantly.RedSocialNoticias.entidades.Publicacion;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.servicios.PublicationService;

@Controller
@RequestMapping("/")
public class IndexController {

	@Autowired
	PublicationService publicationService;
	
	@GetMapping("/inicio")
	public String userAccess(ModelMap model,HttpSession session) {
		Usuario login=(Usuario) session.getAttribute("user");
		List<Publicacion> publications=publicationService.allPublications();
		List<Publicacion> userPublications;
		try {
			if(login != null) {
			userPublications = publicationService.publicationsByNick(login.getNick());
			}else {
				userPublications=null;
			}
		} catch (ErrorServicio e) {
			System.err.println(e.getMessage());
			userPublications=null;
		}
		model.put("publications", publications);
		model.put("userPublications", userPublications);
		return "index";
	}
}

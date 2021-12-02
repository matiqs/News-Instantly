package com.NewsInstantly.RedSocialNoticias.controlador;


import javax.servlet.http.HttpSession;
import javax.swing.JTextArea;

import org.springframework.beans.factory.annotation.Autowired;
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

@Controller
@RequestMapping("/publicacion")
public class publicationController {

	@Autowired
	PublicationService publicationService;
	@Autowired
	NotificationService notificationService;

	@PostMapping("/publicar")
	public String toPost(@RequestParam String title, @RequestParam(required = false) String epigraphy,
			@RequestParam(required = false) JTextArea publicationText, @RequestParam(required = false) MultipartFile file,
			@RequestParam String email, RedirectAttributes redirect) {
		try {
			JTextArea body = publicationText;
			if (file.getSize() == 0) {
				publicationService.post(title, epigraphy, body, null, email);
			} else {
				publicationService.post(title, epigraphy, body, file, email);
			}
			redirect.addFlashAttribute("success", "Publicacion realizada correctamente");
			return "redirect:/inicio";
		} catch (ErrorServicio e) {
			String publicationBody=publicationText.getText();
			redirect.addFlashAttribute("title", title);
			redirect.addFlashAttribute("epigraphy", epigraphy);
			redirect.addFlashAttribute("publicationText", publicationBody);
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
	}

	@GetMapping("/{id}")
	public String readNote(@PathVariable String id, ModelMap model,RedirectAttributes redirect) {
		try {
			Publicacion publication = publicationService.publicationByID(id);
			model.put("publication", publication);
			model.put("bodyPublication", publicationService.lineBreaks(publication.getTexto()));
			return "LeerPublicacion";
		} catch (ErrorServicio e) {
			System.err.println(e.getMessage());
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
	}

	@GetMapping("/eliminar/{id}")
	public String deletePublication(@PathVariable String id, HttpSession session, RedirectAttributes redirect) {
		try {
			Usuario login = (Usuario) session.getAttribute("user");
			Publicacion publication = publicationService.publicationByID(id);

			if (login == null || (!(login.getEmail().equals(publication.getUsuario().getEmail()))
					&& !login.getRol().toString().equals("ADMIN"))) {
				redirect.addFlashAttribute("error", "no tiene los permisos para eliminar esta publicacion");
				return "redirect:/inicio";
			}

			publicationService.delete(id);
			if (login.getRol().toString().equals("ADMIN") && !(login.getEmail().equals(publication.getUsuario().getEmail()))) {
				notificationService.sendMail("Se elimino su publicacion titulada: " + publication.getTitulo(),
						"Publicacion eliminada", publication.getUsuario().getEmail());
			}
			redirect.addFlashAttribute("success", "Publicacion eliminada");
			return "redirect:/inicio";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}

	}

	@GetMapping("/denunciar/{id}")
	public String reportPublication(@PathVariable String id, HttpSession session, RedirectAttributes redirect) {
		try {
			Usuario login = (Usuario) session.getAttribute("user");
			Publicacion publication;
			publication = publicationService.publicationByID(id);
			
			if (login == null || login.getEmail().equals(publication.getUsuario().getEmail())) {
				redirect.addFlashAttribute("error",
						"no puede denunciar su propia publicacion o no puede realizar una denuncia"
								+ " sin estar logeado");
				return "redirect:/inicio";
			}
			//COLOCAR UN EMAIL PARA RECIBIR LOS MENSAJES DE DENUNCIA
			notificationService.sendMail("El usuario: "+login.getEmail()+
					" denuncio la publicacion: http://localhost:8080/publicacion/" +id, "Denuncia publicacion", "emailADMIN@gmail.com");
			redirect.addFlashAttribute("success", "Publicacion denunciada con exito");
			return "redirect:/inicio";
		} catch (ErrorServicio e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/inicio";
		}
	}
}

package com.NewsInstantly.RedSocialNoticias.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.NewsInstantly.RedSocialNoticias.entidades.Publicacion;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.servicios.PublicationService;
import com.NewsInstantly.RedSocialNoticias.servicios.UserService;

@Controller
@RequestMapping("/imagen")
public class imageController {

	@Autowired
	private UserService userService;
	@Autowired
	private PublicationService publicacionServicio;
	
	@GetMapping("/portada/{id}")
	public ResponseEntity<byte[]> coverPhoto(@PathVariable String id){
		try {
			Usuario user = userService.userByEmail(id);
			
			if(user.getImagenPortada()==null) {
				throw new ErrorServicio("El usuario no tiene una foto de portada asignada.");
			}
			
			byte[] photo=user.getImagenPortada().getContenido();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<>(photo, headers, HttpStatus.OK);
		} catch (ErrorServicio e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/perfil/{id}")
	public ResponseEntity<byte[]> profilePhoto(@PathVariable String id){
		try {
			Usuario user = userService.userByEmail(id);
			
			if(user.getImagenPerfil()==null) {
				throw new ErrorServicio("El usuario no tiene una foto de perfil asignada.");
			}
			
			byte[] photo=user.getImagenPerfil().getContenido();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<>(photo, headers, HttpStatus.OK);
		} catch (ErrorServicio e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/publicacion/{id}")
	public ResponseEntity<byte[]> publicationPhoto(@PathVariable String id){
		try {
			Publicacion publication = publicacionServicio.publicationByID(id);
			
			if(publication.getImagen()==null) {
				throw new ErrorServicio("La publicacion no tiene una foto asignada.");
			}
			
			byte[] photo=publication.getImagen().getContenido();
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<>(photo, headers, HttpStatus.OK);
		} catch (ErrorServicio e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}

package com.NewsInstantly.RedSocialNoticias.servicios;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsInstantly.RedSocialNoticias.entidades.ImagenNota;
import com.NewsInstantly.RedSocialNoticias.entidades.Publicacion;
import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.PublicationRepository;

@Service
public class PublicationService {

	@Autowired
	PublicationRepository publicationRepository;
	@Autowired
	PublicationPhotoService publicationPhotoService;
	@Autowired
	UserService userService;

	/**
	 * metodo que permite guardar una publicacion el la DB
	 * 
	 * @param title
	 * @param epigraph
	 * @param body
	 * @param file
	 * @param usuario
	 * @throws ErrorServicio
	 */
	@Transactional
	public void post(String title, String epigraph, JTextArea body, MultipartFile file, String email)
			throws ErrorServicio {
		checkData(title, epigraph, body);

		Usuario user = userService.userByEmail(email);
		ImagenNota image = publicationPhotoService.uploadPhoto(null, file);
		Date publicationDate = new Date();

		Publicacion publication = new Publicacion();

		publication.setTitulo(title);
		publication.setResumen(epigraph);
		publication.setTexto(body);
		publication.setImagen(image);
		publication.setFechaPublicacion(publicationDate);
		publication.setUsuario(user);

		publicationRepository.save(publication);
	}

	/**
	 * metodo que devuelve todas las publicacion en la DB
	 * 
	 * @return List de publicaciones
	 */
	@Transactional(readOnly = true)
	public List<Publicacion> allPublications() {
		return publicationRepository.publicationsAllOrdered();
	}

	/**
	 * metodo que permite eliminar una publicacion en la DB
	 * 
	 * @param publicationID
	 * @throws ErrorServicio
	 */
	@Transactional
	public void delete(String publicationID) throws ErrorServicio {
		Publicacion publication = publicationByID(publicationID);
		publicationRepository.delete(publication);
	}

	/**
	 * utiliza un JTextArea para crear una lista de String a partir de los saltos de linea.
	 * @param body
	 * @return List<String> en caso que no haya errores.
	 */
	public List<String> lineBreaks(JTextArea body) {
		List<String> text = new ArrayList<>();
		//itera por cada linea que tiene el Jtextarea
		for (int i = 0; i < body.getLineCount(); i++) {
			try {
				//si la resta entre la posicion final de la linea i y la posicion inicial de la linea i (largo de la linea)
				//es distinto de 0, entrea en el if
				if(body.getLineEndOffset(i) - body.getLineStartOffset(i)!=0) {
				//agrega un string a la lista que comienza en el primer caracter de la linea i y tiene el largo de la linea
				text.add(body.getText(body.getLineStartOffset(i), body.getLineEndOffset(i) - body.getLineStartOffset(i)));
				}else {
					//agrega un string vacio a la lista
					text.add("");
				}
			} catch (BadLocationException e) {
				System.out.println(e.getMessage());
				return null;
			}
		}
		return text;
	}

	/**
	 * metodo que busca y retorna una publicacion por su Id
	 * 
	 * @param id
	 * @return Publicacion
	 * @throws ErrorServicio
	 */
	@Transactional(readOnly = true)
	public Publicacion publicationByID(String id) throws ErrorServicio {
		Optional<Publicacion> result = publicationRepository.findById(id);

		if (result.isPresent()) {
			return result.get();
		} else {
			throw new ErrorServicio("no se pudo encontrar la nota");
		}
	}
	
	/**
	 * metodo que se encarga de buscar la publicaciones de un usuario por su nick y devolverlas 
	 * ordenas de mas nueva a mas vieja
	 * @param nick
	 * @return List<Publicacion>
	 * @throws ErrorServicio
	 */
	@Transactional(readOnly = true)
	public List<Publicacion> publicationsByNick(String nick) throws ErrorServicio{
		return publicationRepository.publicationsByNickOrdered(nick);
	}

	/**
	 * Metodo el cual comprueba que los parametros no esten vacios o nulos. En caso
	 * que lo esten, lanza una excepcion
	 * 
	 * @param title
	 * @param epigraph
	 * @param body
	 * @throws ErrorServicio
	 */
	public void checkData(String title, String epigraph, JTextArea body) throws ErrorServicio {
		if (title == null || title.isEmpty()) {
			throw new ErrorServicio("No puede guardar una nota sin ponerle un titulo");
		}
		if (epigraph == null || epigraph.isEmpty()) {
			throw new ErrorServicio("No puede guardar una nota sin un breve resumen de la misma");
		}
		if (body == null || body.getText().isEmpty()) {
			throw new ErrorServicio("No puede guardar una nota sin escribir nada en ella");
		}
	}
}

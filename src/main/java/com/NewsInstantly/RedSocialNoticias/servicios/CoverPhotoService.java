package com.NewsInstantly.RedSocialNoticias.servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsInstantly.RedSocialNoticias.entidades.ImagenPortada;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.CoverPhotoRepository;

@Service
public class CoverPhotoService {

	@Autowired
	CoverPhotoRepository coverPhotoRepository;
	
	/**
	 * metodo que crea o actualiza una imagen a partir de un MultiPartFile, la
	 * guarda en la tabla de ImagenPortada y retorna la imagen para ser guarda en un
	 * usuario.
	 * 
	 * @param photoId
	 * @param file
	 * @return ImagenPortada || null
	 */
	@Transactional
	public ImagenPortada uploadPhoto(String photoId, MultipartFile file) throws ErrorServicio{
		if (file != null) {
			
			//lanza error al querer subir imagenes con un peso mayor a 1MB
			if(file.getSize()>(1024*1024*4)) {
				throw new ErrorServicio("Tamaño de imagen excedido");
			}
			try {
				ImagenPortada image = new ImagenPortada();

				if (photoId != null) {
					image = photoByID(photoId);
				}
				image.setMime(file.getContentType());
				image.setNombre(file.getName());
				image.setContenido(file.getBytes());

				return coverPhotoRepository.save(image);
			} catch (ErrorServicio es) {
				System.err.println(es.getMessage());
			} catch (Exception e) {
				System.err.println(e.getMessage());
				throw new ErrorServicio(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * busca una imagen de portada en su DB y retornarla
	 * 
	 * @param id
	 * @return ImagenPortada
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public ImagenPortada photoByID(String id) throws ErrorServicio {
		Optional<ImagenPortada> result = coverPhotoRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		} else {
			throw new ErrorServicio("no se encontro foto de portada con ese id");
		}
	}
	
	/**
	 * metodo que permite eliminar una imagen de portada de la DB
	 * @param id
	 * @throws ErrorServicio
	 */
	@Transactional
	public void deletePhoto(String id) throws ErrorServicio {
		ImagenPortada imagen=photoByID(id);
		coverPhotoRepository.delete(imagen);
	}
	
}

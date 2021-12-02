package com.NewsInstantly.RedSocialNoticias.servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsInstantly.RedSocialNoticias.entidades.ImagenPerfil;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.ProfilePhotoRepository;

@Service
public class ProfilePhotoService {

	@Autowired
	ProfilePhotoRepository profilePhotoRepository;

	/**
	 * metodo que crea o actualiza una imagen a partir de un MultiPartFile, la
	 * guarda en la tabla de ImagenPerfil y retorna la imagen para ser guarda en un
	 * usuario.
	 * 
	 * @param photoId
	 * @param file
	 * @return ImagenPerfil || null
	 */
	@Transactional
	public ImagenPerfil uploadPhoto(String photoId, MultipartFile file) throws ErrorServicio{
		if (file != null) {
			
			//lanza error al querer subir imagenes con un peso mayor a 1MB
			if(file.getSize()>(1024*1024*4)) {
				throw new ErrorServicio("Tamaño de imagen excedido");
			}
			
			try {
				ImagenPerfil image = new ImagenPerfil();

				if (photoId != null) {
					image = photoByID(photoId);
				}
				image.setMime(file.getContentType());
				image.setNombre(file.getName());
				image.setContenido(file.getBytes());

				return profilePhotoRepository.save(image);
			} catch (ErrorServicio es) {
				throw new ErrorServicio(es.getMessage());
			} catch (Exception e) {
				System.err.println(e.getMessage());
				throw new ErrorServicio(e.getMessage());
			}
		}
		return null;
	}

	/**
	 * metodo que se encarga de buscar una imagen de perfil en su DB y retornarla
	 * 
	 * @param id
	 * @return ImagenPerfil
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public ImagenPerfil photoByID(String id) throws ErrorServicio {
		Optional<ImagenPerfil> result = profilePhotoRepository.findById(id);
		if (result.isPresent()) {
			return result.get();
		} else {
			throw new ErrorServicio("no se encontro foto de perfil con ese id");
		}
	}

	/**
	 * metodo que permite eliminar una imagen de perfil de la DB
	 * @param id
	 * @throws ErrorServicio
	 */
	@Transactional
	public void deletePhoto(String id) throws ErrorServicio {
		Optional<ImagenPerfil> result = profilePhotoRepository.findById(id);

		if (result.isPresent()) {
			ImagenPerfil image = result.get();

			profilePhotoRepository.delete(image);
		} else {
			throw new ErrorServicio("no se pudo eliminar la imagen ya que no se pudo encontrar");
		}
	}
}

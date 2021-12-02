package com.NewsInstantly.RedSocialNoticias.servicios;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.NewsInstantly.RedSocialNoticias.entidades.ImagenNota;
import com.NewsInstantly.RedSocialNoticias.excepciones.ErrorServicio;
import com.NewsInstantly.RedSocialNoticias.repositorio.PublicationPhotoRepository;

@Service
public class PublicationPhotoService {

	@Autowired
	PublicationPhotoRepository publicationPhotoRepository;

	/**
	 * metodo que crea o actualiza una imagen a partir de un MultiPartFile, la guarda en la
	 * tabla de imagenNota y retorna la imagen para ser guarda en una nota.
	 * 
	 * @param file
	 * @return ImagenNota || null
	 * 
	 */
	@Transactional
	public ImagenNota uploadPhoto(String photoId,MultipartFile file) throws ErrorServicio{
		if (file != null) {
			
			//lanza error al querer subir imagenes con un peso mayor a 2MB
			if(file.getSize()>(1024*1024*2)) {
				throw new ErrorServicio("Tamaño de imagen excedido");
			}
			try {
				ImagenNota image;
				if(photoId!=null) {
					image=photoByID(photoId);
				}else {
					image = new ImagenNota();
				}
				
				image.setMime(file.getContentType());
				image.setNombre(file.getName());
				image.setContenido(file.getBytes());

				return publicationPhotoRepository.save(image);
			} catch(ErrorServicio es) {
				System.err.println(es.getMessage());
			}
			catch (Exception e) {
				System.err.println(e.getMessage());
				throw new ErrorServicio(e.getMessage());
			}
		}
		return null;
	}
	
	/**
	 * metodo que se encarga de buscar una imagen de nota en su DB y retornarla
	 * @param id
	 * @return ImagenNota
	 * @throws Exception
	 */
	@Transactional(readOnly = true)
	public ImagenNota photoByID(String id) throws ErrorServicio{
		Optional<ImagenNota> result=publicationPhotoRepository.findById(id);
		
		if(result.isPresent()) {
			return result.get();
		}else {
			throw new ErrorServicio("no se encontro foto de nota con ese id");
		}
	}
	
	/**
	 * metodo que permite eliminar una imagen de nota de la DB
	 * @param Id
	 * @throws ErrorServicio
	 */
	@Transactional
	public void deletePhoto(String id) throws ErrorServicio{
		Optional<ImagenNota> result=publicationPhotoRepository.findById(id);
		
		if(result.isPresent()) {
			ImagenNota image=result.get();
			
			publicationPhotoRepository.delete(image);
		}else {
			throw new ErrorServicio("no se pudo eliminar la imagen ya que no se pudo encontrar");
		}
	}
}

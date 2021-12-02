package com.NewsInstantly.RedSocialNoticias.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.NewsInstantly.RedSocialNoticias.entidades.Publicacion;

@Repository
public interface PublicationRepository extends JpaRepository<Publicacion, String>{

	/**
	 * busca y devuelve todas las publicaciones ordenadas desde la mas reciente en la DB
	 * @return Lista de publicaciones ordenada desde mas reciente
	 */
	@Query("SELECT p FROM Publicacion p ORDER BY p.fechaPublicacion Desc")
	public List<Publicacion> publicationsAllOrdered();
	
	/**
	 * busca en la DB todas las publicaciones de un usuario por su nick y las acomoda desde la mas reciente
	 * @param nick
	 * @return Lista de publicaciones, si se encuentra un usuario con ese nick
	 */
	@Query("SELECT p FROM Publicacion p WHERE p.usuario.nick LIKE :nick ORDER BY p.fechaPublicacion Desc")
	public List<Publicacion> publicationsByNickOrdered(@Param("nick") String nick);
}

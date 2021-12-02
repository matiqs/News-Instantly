package com.NewsInstantly.RedSocialNoticias.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.NewsInstantly.RedSocialNoticias.entidades.Usuario;

@Repository
public interface UserRepository extends JpaRepository<Usuario, String>{

	/**
	 * busca un usuario por su email en la DB
	 * @param email
	 * @return Usuario 
	 */
	@Query("SELECT a from Usuario a WHERE a.email LIKE :email")
	public Usuario findByEmail(@Param("email") String email);
	
	/**
	 * busca un usuario por su nick en la DB
	 * @param nick
	 * @return Usuario
	 */
	@Query("SELECT a FROM Usuario a WHERE a.nick LIKE :nick")
	public Usuario findByNick(@Param("nick") String nick);
	
	/**
	 * busca el nick de un usuario en la DB
	 * @param nick
	 * @return String con el nick del usuario, si es que lo encontro
	 */
	@Query("SELECT a.nick FROM Usuario a WHERE a.nick LIKE :nick")
	public String findStringNick(@Param("nick") String nick);
}

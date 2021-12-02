package com.NewsInstantly.RedSocialNoticias.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsInstantly.RedSocialNoticias.entidades.ImagenPerfil;

@Repository
public interface ProfilePhotoRepository extends JpaRepository<ImagenPerfil, String>{

}

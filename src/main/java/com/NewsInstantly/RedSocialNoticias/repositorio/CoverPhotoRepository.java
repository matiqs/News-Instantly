package com.NewsInstantly.RedSocialNoticias.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsInstantly.RedSocialNoticias.entidades.ImagenPortada;

@Repository
public interface CoverPhotoRepository extends JpaRepository<ImagenPortada, String>{

}

package com.NewsInstantly.RedSocialNoticias.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.NewsInstantly.RedSocialNoticias.entidades.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, String>{

}

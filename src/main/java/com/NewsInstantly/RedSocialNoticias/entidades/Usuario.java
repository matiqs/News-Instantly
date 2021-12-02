package com.NewsInstantly.RedSocialNoticias.entidades;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.NewsInstantly.RedSocialNoticias.enums.Role;

@Entity
public class Usuario {

	@Id
	private String email;
	private String nick;
	private String contrasenia;
	private String nombre;
	private String apellido;
	@Temporal(TemporalType.DATE)
	private Date fechaNacimiento;

	@OneToMany(mappedBy = "usuario",
			   cascade = CascadeType.ALL,
			   orphanRemoval = true)
	private List<Publicacion> publicaciones;
	@OneToOne
	private ImagenPerfil imagenPerfil;
	@OneToOne
	private ImagenPortada imagenPortada;
	@Enumerated(EnumType.STRING)
	private Role rol;
	private Boolean alta;

	public Usuario() {
		super();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getContrasenia() {
		return contrasenia;
	}

	public void setContrasenia(String contrasenia) {
		this.contrasenia = contrasenia;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public List<Publicacion> getPublicaciones() {
		return publicaciones;
	}

	public void setPublicaciones(List<Publicacion> publicaciones) {
		this.publicaciones = publicaciones;
	}

	public ImagenPerfil getImagenPerfil() {
		return imagenPerfil;
	}

	public void setImagenPerfil(ImagenPerfil imagenPerfil) {
		this.imagenPerfil = imagenPerfil;
	}

	public Role getRol() {
		return rol;
	}

	public void setRol(Role rol) {
		this.rol = rol;
	}

	public Boolean getAlta() {
		return alta;
	}

	public void setAlta(Boolean alta) {
		this.alta = alta;
	}

	public ImagenPortada getImagenPortada() {
		return imagenPortada;
	}

	public void setImagenPortada(ImagenPortada imagenPortada) {
		this.imagenPortada = imagenPortada;
	}

}

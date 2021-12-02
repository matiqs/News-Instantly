package com.NewsInstantly.RedSocialNoticias.entidades;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.swing.JTextArea;

import org.hibernate.annotations.GenericGenerator;

@Entity
public class Publicacion {

	@Id
	@GeneratedValue(generator ="uuid")
	@GenericGenerator(name = "uuid",strategy = "uuid2")
	private String id;
	private String titulo;
	private String resumen;
	@Lob
	private JTextArea texto;
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaPublicacion;
	@OneToOne
	private ImagenNota imagen;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USUARIO_ID")
	private Usuario usuario;
	
	public Publicacion() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getResumen() {
		return resumen;
	}

	public void setResumen(String resumen) {
		this.resumen = resumen;
	}

	public JTextArea getTexto() {
		return texto;
	}

	public void setTexto(JTextArea texto) {
		this.texto = texto;
	}

	public Date getFechaPublicacion() {
		return fechaPublicacion;
	}

	public void setFechaPublicacion(Date fechaPublicacion) {
		this.fechaPublicacion = fechaPublicacion;
	}

	public ImagenNota getImagen() {
		return imagen;
	}

	public void setImagen(ImagenNota imagen) {
		this.imagen = imagen;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}

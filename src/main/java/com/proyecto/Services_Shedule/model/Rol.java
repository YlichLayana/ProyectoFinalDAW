package com.proyecto.Services_Shedule.model;

import java.util.ArrayList;
//import java.util.HashSet;
import java.util.List;
//import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "roles")
public class Rol {
	@Id
	@Column(name = "id_rol")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String nombre_rol;

	@ManyToMany(mappedBy = "roles")
	
	// private Set<Usuario> usuario = new HashSet<>();
	private List<Usuario> usuario = new ArrayList<>();// probar

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre_rol() {
		return nombre_rol;
	}

	public void setNombre_rol(String nombre_rol) {
		this.nombre_rol = nombre_rol;
	}

	/*
	 * public Set<Usuario> getUsuario() { return usuario; }
	 * 
	 * public void setUsuario(Set<Usuario> usuario) { this.usuario = usuario; }
	 */


	public List<Usuario> getUsuario() {
		return usuario;
	}

	public void setUsuario(List<Usuario> usuario) {
		this.usuario = usuario;
	}

	@Override
	public String toString() {
		return "Rol [id=" + id + ", nombre_rol=" + nombre_rol + "]";
	}

}

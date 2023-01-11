package com.proyecto.Services_Shedule.model;



import java.util.ArrayList;
//import java.util.HashSet;
import java.util.List;
//import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;



@Entity
@Table(name = "usuarios")
public class Usuario {
	

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "dni", nullable = false, unique = true, length = 12)
	private String dni;
	
	@Column(name = "nombre", nullable = false, length = 45)
	private String nombre;
	
	@Column(name = "apellido", nullable = false, length = 45)
	private String apellido;  
	
	@Column(name = "movil", nullable = false, length = 9)
	private String movil;  
	
	@Column(name = "direccion", nullable = false, length = 45)
	private String direccion;  
	
	@Column(name = "cod_postal", nullable = false, length = 5)
	private String cod_postal; 
	
	@Column(nullable = false, unique = true, length = 45)
	private String email;
	
	@Column(nullable = false, length = 64)
	private String password;
	
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
			name = "usuarios_roles",
			joinColumns = @JoinColumn(name = "id_usuario"),
			inverseJoinColumns = @JoinColumn(name = "id_rol")
			)
	//private Set<Rol> roles = new HashSet<>();
	private List<Rol> roles = new ArrayList<>();
	
	
	@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Proveedor proveedor;
	
	
   /**
    * Getters and Setters propios de Usuario
    */
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
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
	
	public String getMovil() {
		return movil;
	}

	public void setMovil(String movil) {
		this.movil = movil;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getCod_postal() {
		return cod_postal;
	}

	public void setCod_postal(String cod_postal) {
		this.cod_postal = cod_postal;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	
	//Metodo para agregar rol a usuario
	public void addRols(Rol rol) {
		if(!roles.contains(rol)) {
			roles.add(rol);
			rol.getUsuario().add(this);
		}
	}
	
	/*public Set<Rol> getRoles() {
		return roles;
	}

	public void setRoles(Set<Rol> roles) {
		this.roles = roles;
	}*/
	
	/*Getter and Setter de ROLES*/
	public List<Rol> getRoles(){
		return roles;
	}
	
	public void setRoles(List<Rol> roles) {
		this.roles = roles;
	}

	/*Getter and Setter de PROVEEDOR*/
	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public String usuarioFullNombre() {
		return nombre + " " + apellido ;
	}

	
	
}

package com.proyecto.Services_Shedule.model;




import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "proveedor")
public class Proveedor {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
		
	@Column(name = "especialidad", nullable = false, length = 45)
	private String especialidad;
	
	@Column(name = "anio_experiencia", nullable = false)
	private Long anio_experiencia;

	// Que columna en la tabla proveedor tiene la FK
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_usuario", referencedColumnName = "id")
	private Usuario usuario;
	
	//relacion Proveedor ServicioOfrecido
	@OneToOne(mappedBy = "proveedor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ServicioOfrecido servicioOfrecido;
		
	//Getters and Setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public Long getAnio_experiencia() {
		return anio_experiencia;
	}

	public void setAnio_experiencia(Long anio_experiencia) {
		this.anio_experiencia = anio_experiencia; 
	}
	
	/*Getter and Setter de USUARIO*/
	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/*Getter and Setter de ServicioOfrecido*/
	public ServicioOfrecido getServicioOfrecido() {
		return servicioOfrecido;
	}

	public void setServicioOfrecido(ServicioOfrecido servicioOfrecido) {
		this.servicioOfrecido = servicioOfrecido;
	}

	

	
}

package com.proyecto.Services_Shedule.model;




import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;




@Entity
@Table(name = "ctg_servicio")
public class CategServicios {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private String nombre;
	
	
	@OneToMany(mappedBy = "categoriaServicio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	//private List<ServicioOfrecido> serviciosOfrecidos = new ArrayList<>();
	private List<ServicioOfrecido> serviciosOfrecidos;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<ServicioOfrecido> getServiciosOfrecidos() {
		return serviciosOfrecidos;
	}

	public void setServiciosOfrecidos(List<ServicioOfrecido> serviciosOfrecidos) {
		this.serviciosOfrecidos = serviciosOfrecidos;
	}
	
	

}

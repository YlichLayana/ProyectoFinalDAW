package com.proyecto.Services_Shedule.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;


@Entity
@Table(name="servicio_ofrecido")
public class ServicioOfrecido {
	
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "precio")
	private float precio;
	
	private String descripcion;
	
	@Column(name = "turno")
	private String turno;
	
	@Column(name = "tipo_servicio")
	private String tipoServicio;
	
	// Que columna en la tabla ServicioOfrecido tiene la FK
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_proveedor", referencedColumnName = "id")
	private Proveedor proveedor;
	
	
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_ctg_servicio", referencedColumnName = "id")
	private CategServicios categoriaServicio;
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public float getPrecio() {
		return precio;
	}
	public void setPrecio(float precio) {
		this.precio = precio;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public String getTipoServicio() {
		return tipoServicio;
	}
	public void setTipoServicio(String tipoServicio) {
		this.tipoServicio = tipoServicio;
	}
	
	
	/*Getter y Setter de PROVEEDOR*/
	public Proveedor getProveedor() {
		return proveedor;
	}
	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}
	
	
	public CategServicios getCategoriaServicio() {
		return categoriaServicio;
	}
	public void setCategoriaServicio(CategServicios categoriaServicio) {
		this.categoriaServicio = categoriaServicio;
	}
	
	
}

package com.proyecto.Services_Shedule.utils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.repository.ServicioOfrecidoRepository;

public class ServicioAgendarUtil {
	
	private Integer id;
	private String servicio;
	private String proveedor;
	private String movil;
	private String turno;
	private String tipoAtencion;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getServicio() {
		return servicio;
	}
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}
	public String getProveedor() {
		return proveedor;
	}
	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}
	public String getMovil() {
		return movil;
	}
	public void setMovil(String movil) {
		this.movil = movil;
	}
	
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	
	public String getTipoAtencion() {
		return tipoAtencion;
	}
	public void setTipoAtencion(String tipoAtencion) {
		this.tipoAtencion = tipoAtencion;
	}
	/**
	 * Probar esto para la paginación 
	 */
	public static Page<ServicioOfrecido> obtenerPaginaServiciosBuscado(int pagina, int tamanioPagina, 
			String nombreCategoria, ServicioOfrecidoRepository servicioOfrecidoRepository) {
		Pageable paginaActual = PageRequest.of(pagina - 1, tamanioPagina);
		return servicioOfrecidoRepository.findByCategoriaServicioNombreContainingIgnoreCase(nombreCategoria,
				paginaActual);
	}
	
}

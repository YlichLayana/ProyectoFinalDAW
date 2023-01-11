package com.proyecto.Services_Shedule.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.Services_Shedule.model.CategServicios;





public interface CatgServiciosRepository extends JpaRepository <CategServicios, Integer>{
	public CategServicios findById (int id);

	public CategServicios findByNombreContainingIgnoreCase(String nombre);

}

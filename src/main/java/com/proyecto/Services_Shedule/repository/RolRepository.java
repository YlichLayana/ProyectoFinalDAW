package com.proyecto.Services_Shedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.Services_Shedule.model.Rol;




public interface RolRepository extends JpaRepository<Rol, Integer> {
	public Rol findById (int id_rol);
    
}

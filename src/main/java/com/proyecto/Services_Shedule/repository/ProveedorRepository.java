package com.proyecto.Services_Shedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.Services_Shedule.model.Proveedor;
import com.proyecto.Services_Shedule.model.Usuario;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long>{

	Proveedor findByUsuario(Usuario usuario);
	
}

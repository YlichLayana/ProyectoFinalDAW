package com.proyecto.Services_Shedule.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;

import com.proyecto.Services_Shedule.model.Proveedor;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;


public interface ServicioOfrecidoRepository extends JpaRepository<ServicioOfrecido, Integer>{

	ServicioOfrecido findByProveedor(Proveedor proveedor);
	
	@Query("SELECT s FROM ServicioOfrecido s WHERE LOWER(s.categoriaServicio.nombre) LIKE LOWER(CONCAT('%',:nombre,'%'))")
    
	//Page<ServicioOfrecido> findByCategoriaServicioNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);
	Page<ServicioOfrecido> findByCategoriaServicioNombreContainingIgnoreCase(String nombre, Pageable paginaActual);	
	
}

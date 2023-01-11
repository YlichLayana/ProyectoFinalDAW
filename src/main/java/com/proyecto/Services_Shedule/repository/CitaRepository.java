package com.proyecto.Services_Shedule.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.Services_Shedule.model.Cita;

import com.proyecto.Services_Shedule.model.Usuario;

public interface CitaRepository extends JpaRepository<Cita, Integer> {
    List<Cita> findByFechaAndHora(LocalDate fecha, LocalTime hora);
    
	List<Cita> findByUsuario(Usuario usuario);
	List<Cita> findByEstado(String estado);
	Page<Cita> findByUsuario(Usuario usuario, Pageable paginaActual);
}

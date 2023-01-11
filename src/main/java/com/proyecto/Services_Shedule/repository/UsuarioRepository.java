package com.proyecto.Services_Shedule.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.proyecto.Services_Shedule.model.Usuario;

/*aqui se realizan las consultas necesaria en el repository */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	@Query("SELECT u FROM Usuario u WHERE u.email = ?1")
	public Usuario findByEmail(String email);
	public Usuario findByDni (String dni);
	public Usuario findById (int id);
	
	
}

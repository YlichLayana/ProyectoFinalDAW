package com.proyecto.Services_Shedule.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import com.proyecto.Services_Shedule.model.Rol;
import com.proyecto.Services_Shedule.model.Usuario;


@SuppressWarnings("serial")
public class UsuarioDetails implements UserDetails {

	private Usuario user;
	
	public UsuarioDetails(Usuario user) {
		this.user = user;
	}

	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		//Set<Rol> roles = user.getRoles();
		List<Rol> roles = user.getRoles();
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		
		for (Rol rol : roles) {
			authorities.add(new SimpleGrantedAuthority(rol.getNombre_rol()));
			//System.out.println(authorities);
		}
		
		return authorities;
	}
	

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	
	@Override
	public String getUsername() {
		return user.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
	
	public Long getUserId() {
		return user.getId();
	}
	
	public String getFullName() {
		return user.getNombre() + " " + user.getApellido();
	}
	
	public void setNombre(String nombre) {
		this.user.setNombre(nombre);
	}
	
	public void setApellido(String apellido) {
		this.user.setApellido(apellido);
	}
	
}

package com.proyecto.Services_Shedule.service;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.UsuarioRepository;

public class UsuarioDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private UsuarioRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario user = userRepo.findByEmail(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("Usuario no encontrado");
		}
		return new UsuarioDetails(user);
	}

	/*@Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario user = userRepo.getUserByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user with that email");
        }
         
        return new UsuarioDetails(user);
    }*/
	

}

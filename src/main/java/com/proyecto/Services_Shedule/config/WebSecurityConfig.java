package com.proyecto.Services_Shedule.config;

import com.proyecto.Services_Shedule.service.UsuarioDetailsServiceImpl;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@SuppressWarnings("unused")
	@Autowired
	private DataSource dataSource;

	@Bean
	public UserDetailsService userDetailsService() {
		return new UsuarioDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.authenticationProvider(authenticationProvider());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().antMatchers("/").authenticated()
				.antMatchers("/citas/gestionar", "/citas/completadas", "/cita/gestionar")
				.hasAnyAuthority("PROVEEDOR")
				.antMatchers("/citas/agendadas", "/cita/editar", "/cita/agendada", "/cita/eliminar")
				.hasAnyAuthority("CLIENTE")
				.anyRequest().permitAll()
				.and()
				.formLogin()
					.loginPage("/login")
					.usernameParameter("email")
					.defaultSuccessUrl("/dashboardPage")
					.permitAll()
				.and()
				.logout()
				.logoutSuccessUrl("/").permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/403");
	}

}

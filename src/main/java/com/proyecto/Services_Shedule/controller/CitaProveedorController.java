package com.proyecto.Services_Shedule.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto.Services_Shedule.model.Cita;
import com.proyecto.Services_Shedule.model.Proveedor;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.CitaRepository;
import com.proyecto.Services_Shedule.repository.ProveedorRepository;
import com.proyecto.Services_Shedule.repository.ServicioOfrecidoRepository;
import com.proyecto.Services_Shedule.repository.UsuarioRepository;
import com.proyecto.Services_Shedule.service.UsuarioDetails;
import com.proyecto.Services_Shedule.utils.CitaDatosUtil;

@Controller
public class CitaProveedorController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private ProveedorRepository proveedorRepository;

	@Autowired
	private ServicioOfrecidoRepository servicioOfrecidoRepository;
	
	@Autowired
	private CitaRepository citaRepository;
	
	
	/***
	 * Metodo GET para que el usuario PROVEEDOR pueda visualizar las citas que ya
	 * han sida atendidas
	 * 
	 * @param usuarioDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/citas/completadas")
	public String mostrarCitasCompletadas(@AuthenticationPrincipal UsuarioDetails usuarioDetails, 
			Model model) {
		// Recuperar el usuario a partir de su ID de usuario
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);

		// Recuperar el proveedor a partir del usuario
		Proveedor proveedor = proveedorRepository.findByUsuario(usuario);

		// Recuperar los servicios ofrecidos por el proveedor
		ServicioOfrecido servicio = servicioOfrecidoRepository.findByProveedor(proveedor);

		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasCompletadas = CitaDatosUtil.obtenerCitasCompletadas(proveedor, servicio, citaRepository);
		model.addAttribute("citasCompletadas", citasCompletadas);
		
		if (citasCompletadas.isEmpty()) {
			model.addAttribute("messageCitas", "Aún no tiene citas completadas");
		}

		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "citasCompletadasProveedor");

		return "dashboard1";
	}
	
	/***
	 * 
	 * @param usuarioDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/citas/canceladas")
	public String mostrarCitasCanceladas(@AuthenticationPrincipal UsuarioDetails usuarioDetails, Model model) {
		// Recuperar el usuario a partir de su ID de usuario
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);

		// Recuperar el proveedor a partir del usuario
		Proveedor proveedor = proveedorRepository.findByUsuario(usuario);

		// Recuperar los servicios ofrecidos por el proveedor
		ServicioOfrecido servicio = servicioOfrecidoRepository.findByProveedor(proveedor);

		// Lista para almacenar las citas canceladas del proveedor
		List<Cita> citasCanceladas = CitaDatosUtil.obtenerCitasCanceladas(proveedor, servicio, citaRepository);
		model.addAttribute("citasCanceladas", citasCanceladas);

		if (citasCanceladas.isEmpty()) { 
			model.addAttribute("messageCitas", "No tiene citas Canceladas");
		}

		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "citasCanceladasProveedor");

		return "dashboard1";
	}
	
	/***
	 * Metodo GET para que el Usuario PROVEEDOR visualizar las citas que tiene por
	 * atender donde esta le permite gestionar y cambiar el estado de las mismas y tambien
	 * se hace paginacion
	 * 
	 * @param usuarioDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/citas/gestionar")
	public String mostrarCitasGestionar(@AuthenticationPrincipal UsuarioDetails usuarioDetails, 
			@RequestParam(name = "pagina", defaultValue = "1") int pagina, Model model) {
		// Recuperar el usuario a partir de su ID de usuario
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);

		// Recuperar el proveedor a partir del usuario
		Proveedor proveedor = proveedorRepository.findByUsuario(usuario);

		// Recuperar los servicios ofrecidos por el proveedor
		ServicioOfrecido servicio = servicioOfrecidoRepository.findByProveedor(proveedor);

		// Obtener la lista de CITAS que su estado no sea COMPLETADA o CANCELADA
		List<Cita> citasGetionar = CitaDatosUtil.obtenerCitasGetionar(proveedor, servicio, citaRepository);
		//model.addAttribute("citasGetionar", citasGetionar);
		
		if (citasGetionar.isEmpty()) {
			System.out.println("No tiene citas pendientes de gestión");
			model.addAttribute("messageCitasGetion", "Aún no tiene citas por Gestionar");
		}
		/***
		 * A partir de aqui crearemos la paginacion de las citas 
		 */
		// Establecer el tamaño de la página y la página actual
		int tamanioPagina = 4;
		// Obtener la sublista de citas que corresponde a la página actual
		List<Cita> citasPagina = CitaDatosUtil.obtenerCitasPagina(citasGetionar, pagina, tamanioPagina);
		
		if (!citasPagina.isEmpty()) {
			System.out.println("No esta vacia la paginación");
			// Añadir el número total de páginas y la página actual al objeto Model
			model.addAttribute("totalPaginas", (int) Math.ceil((double) citasGetionar.size() / tamanioPagina));
			model.addAttribute("paginaActual", pagina); 
			model.addAttribute("citasGetionar", citasPagina);
		}
		
		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "citasGetionarProveedor");

		return "dashboard1";
	}
	
	/***
	 * Metodo POST que usara el usuario PROVEEDOR, este le permitira cambiar el
	 * estado de la cita que esta siendo atendida por el.
	 * 
	 * @param usuarioDetails
	 * @param idCita
	 * @param estado
	 * @param model
	 * @return
	 */
	@PostMapping("/cita/gestionar")
	public String modificarEstado(@AuthenticationPrincipal UsuarioDetails usuarioDetails,
			@RequestParam("idCita") Integer idCita, @RequestParam("estado") String estado, 
			@RequestParam(name = "pagina", defaultValue = "1") int pagina, Model model) {

		// Recuperar la cita a partir del ID
		Cita cita = citaRepository.findById(idCita).orElse(null);

		String nuevoEstado = "";
		if (cita.getEstado().equals("PENDIENTE")) {
			nuevoEstado = "PROCESADA";
		} else if (cita.getEstado().equals("PROCESADA")) {
			nuevoEstado = "COMPLETADA";
		}
		// Actualizar el estado de y Guardar la cita modificada en la tabla cita de la
		// BBDD
		cita.setEstado(nuevoEstado);
		citaRepository.save(cita);

		/***
		 * A partir de aqui es para volver a cargar la tabla de citas del usuario
		 * proveedor logeado Recuperar los datos que deseamos mostran en las tablas
		 * y mostrar la paginacion
		 */
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);

		// Recuperar el proveedor a partir del usuario
		Proveedor proveedor = proveedorRepository.findByUsuario(usuario);

		// Recuperar los servicios ofrecidos por el proveedor
		ServicioOfrecido servicio = servicioOfrecidoRepository.findByProveedor(proveedor);

		// Obtener la lista de servicios que estan pendientes de realizar gestion
		List<Cita> citasGetionActual = CitaDatosUtil.obtenerCitasGetionar(proveedor, servicio, citaRepository);
		
		// Establecer el tamaño de la página y la página actual
		int tamanioPagina = 4;
		
		// Obtener la sublista de citas que corresponde a la página actual
		List<Cita> citasPagina = CitaDatosUtil.obtenerCitasPagina(citasGetionActual, pagina, tamanioPagina);
			
		if (!citasPagina.isEmpty()) {
			// Añadir el número total de páginas y la página actual al objeto Model
			model.addAttribute("totalPaginas", (int) Math.ceil((double) citasGetionActual.size() / tamanioPagina));
			model.addAttribute("paginaActual", pagina);
			model.addAttribute("citasGetionar", citasPagina);
		}
		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "citasGetionarProveedor");

		return "dashboard1";
	}
}

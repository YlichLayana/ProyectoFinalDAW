package com.proyecto.Services_Shedule.controller;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto.Services_Shedule.model.Cita;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.CitaRepository;
import com.proyecto.Services_Shedule.repository.ServicioOfrecidoRepository;
import com.proyecto.Services_Shedule.repository.UsuarioRepository;
import com.proyecto.Services_Shedule.service.UsuarioDetails;
import com.proyecto.Services_Shedule.utils.ServicioAgendarUtil;

@Controller
public class ServiciosController {
	@Autowired
	private ServicioOfrecidoRepository servicioOfrecidoRepository;

	@Autowired
	private CitaRepository citaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	/**
	 * Metodo Para hacer paginación de los resultados de tu búsqueda de servicios y
	 * mostrar sólo 6 tarjetas por página, puedes seguir los siguientes pasos
	 * 
	 * @param termino
	 * @param pagina
	 * @param model
	 * @return
	 */
	@GetMapping("/servicios/buscar")
	public String buscarServicios(@RequestParam("termino") String termino,
			@RequestParam(name = "pagina", defaultValue = "1") int pagina, Model model) {

		// Guardo el nombre de la categoria recibida lo usare durante todo el proceso de
		// busqueda
		String nombreCategoria = termino;

		// Establecer el tamaño de la página y la página actual
		int tamanioPagina = 3;

		// Obtener la página de citas agendadas del usuario
		Page<ServicioOfrecido> servicios = ServicioAgendarUtil.obtenerPaginaServiciosBuscado(pagina, tamanioPagina,
				nombreCategoria, servicioOfrecidoRepository);

		if (servicios.isEmpty()) {
			model.addAttribute("errorMessage", "No se han encontrado servicios para la búsqueda: " + nombreCategoria);
		}

		System.out.println("Cantidad de Servicios Filtrados: " + servicios.getNumberOfElements());
		if (!servicios.isEmpty()) {
			// Añadir la lista de citas agendadas al modelo
			model.addAttribute("servicios", servicios);
		}

		// Devuelve el valor capturado de categoria
		model.addAttribute("nombreCategoria", nombreCategoria);

		// Añadir la paginación al modelo
		model.addAttribute("paginacion", true);
		model.addAttribute("totalPaginas", servicios.getTotalPages());
		model.addAttribute("paginaActual", servicios.getNumber() + 1);

		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "servicios");

		return "dashboard1";
	}

	/**
	 * Metodo para crea un JSON con los datos de la cita que me interezan para luego
	 * mostrarla en el modal donde editare la fecha y hora de la cita
	 * 
	 * @param idCita
	 * @return
	 */
	@GetMapping("/servicio/agendar")
	public ResponseEntity<ServicioAgendarUtil> servicioAgendar(@RequestParam("idServicio") Integer idServicio) {
		Optional<ServicioOfrecido> optionalServicioOfrecido = servicioOfrecidoRepository.findById(idServicio);
		ServicioOfrecido servicio = optionalServicioOfrecido.get();

		ServicioAgendarUtil servicioData = new ServicioAgendarUtil();
		servicioData.setId(servicio.getId());
		servicioData.setServicio(servicio.getCategoriaServicio().getNombre());
		servicioData.setProveedor(servicio.getProveedor().getUsuario().usuarioFullNombre());
		servicioData.setMovil(servicio.getProveedor().getUsuario().getMovil());
		servicioData.setTurno(servicio.getTurno());
		servicioData.setTipoAtencion(servicio.getTipoServicio());

		return ResponseEntity.ok(servicioData);
	}

	/**
	 * Metodo para que el Cliente pueda agendar y guardar un servicio en la tabla
	 * cita
	 * 
	 * @param fecha
	 * @param hora
	 * @param servicioId
	 * @param usuarioDetails
	 * @param model
	 * @return
	 */
	@PostMapping("/servicio/agendar/cita")
	public String agendarCita(@RequestParam("fecha") String fecha, @RequestParam("hora") String hora,
			@RequestParam("servicioId") Integer servicioId, @AuthenticationPrincipal UsuarioDetails usuarioDetails,
			Model model) {
		// Obtener el usuario que está haciendo la cita a partir de su nombre de usuario
		Usuario usuario = usuarioRepository.findByEmail(usuarioDetails.getUsername());

		// Obtener el servicio que está siendo elegido
		ServicioOfrecido servicio = servicioOfrecidoRepository.findById(servicioId).get();

		// Crear una nueva cita y asignarle los valores recibidos
		Cita cita = new Cita();
		cita.setFecha(LocalDate.parse(fecha));
		cita.setHora(LocalTime.parse(hora));
		cita.setUsuario(usuario);
		cita.setServicioOfrecido(servicio);
		cita.setEstado("PENDIENTE"); // Establecer el estado en "pendiente"

		// Comprobar que la fecha de la cita no sea anterior a la fecha actual
		LocalDate hoy = LocalDate.now();
		
		// Comprobar que el usuario tenga al menos dos horas de anticipación para la cita
		LocalDateTime ahora = LocalDateTime.now();
		Duration tiempoAnticipacion = Duration.between(ahora, cita.getFecha().atTime(cita.getHora()));
		System.out.println(tiempoAnticipacion.toHours()); 
		
		if (cita.getFecha().isBefore(hoy)) {
			model.addAttribute("mesajeError", "Error: No puedes agendar una cita en una fecha anterior a hoy");
			// Agregar la tarjeta a la vista
			model.addAttribute("servicio", servicio);
			// Esta es la vista que se mostrara el el dashboard
			model.addAttribute("view", "servicios");
			return "dashboard1";
		}
		
		if (cita.getFecha().equals(hoy) && tiempoAnticipacion.toHours() < 2) {
			
			model.addAttribute("mesajeError",
					"Error: Debes tener al menos dos horas de anticipación para agendar una cita");
			// Agregar la tarjeta a la vista
			model.addAttribute("servicio", servicio);
			// Esta es la vista que se mostrara el el dashboard
			model.addAttribute("view", "servicios");
			return "dashboard1";
		}

		// Buscar citas en la misma fecha y hora
		List<Cita> citasMismaFechaHora = citaRepository.findByFechaAndHora(cita.getFecha(), cita.getHora());
		
		// Si no hay citas en la misma fecha y hora, guardar la cita y mostrar mensaje de éxito
		if (citasMismaFechaHora.isEmpty()) {
			// Guardar la cita en la base de datos
			citaRepository.save(cita);
			// Mostrar un mensaje de éxito al usuario y redirigirlo a la página
			model.addAttribute("mesajeExito", "Cita agendada con éxito");
		} else {
			// Si hay al menos una cita a la misma hora y está en estado "CANCELADO",
			// guardar la cita y mostrar mensaje de éxito
			boolean existeCitaCancelada = false;
			for (Cita c : citasMismaFechaHora) {
				if (c.getEstado().equals("CANCELADO")) {
					existeCitaCancelada = true;
					break;
				}
			}
			if (existeCitaCancelada) {
				// Guardar la cita en la base de datos
				citaRepository.save(cita);
				// Mostrar un mensaje de éxito al usuario y redirigirlo a la página
				model.addAttribute("mesajeExito", "Cita agendada con éxito");
			} else {
				// Si hay citas a la misma hora, mostrar mensaje de error
				model.addAttribute("mesajeError",
						"Error: El dia y la hora indicada, no están disponibles. Por favor vuelva a intentarlo");
			}
		}
		// Agregar la tarjeta a la vista
		model.addAttribute("servicio", servicio);

		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "servicios");

		return "dashboard1";
	}
}

package com.proyecto.Services_Shedule.controller;

import java.time.Duration;
import java.time.LocalDate;

import java.time.LocalTime;
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
import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.CitaRepository;
import com.proyecto.Services_Shedule.repository.UsuarioRepository;
import com.proyecto.Services_Shedule.service.UsuarioDetails;
import com.proyecto.Services_Shedule.utils.CitaDatosUtil;

@Controller
public class CitaClienteController {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private CitaRepository citaRepository;

	/**
	 * Metodo GET para que el usuario CLIENTE pueda ve las citas que tiene agendada*
	 * 
	 * @param usuarioDetails
	 * @param model
	 * @return
	 */
	@GetMapping("/citas/agendadas")
	public String mostrarCitasAgendadas(@AuthenticationPrincipal UsuarioDetails usuarioDetails,
			@RequestParam(name = "pagina", defaultValue = "1") int pagina, Model model) {

		// Recuperar el usuario a partir de su ID de usuario
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);

		// Establecer el numero de filas de la tabla la cual dara el tamaño de la página
		int tamanioPagina = 4;

		// Obtener la página de citas agendadas del usuario
		Page<Cita> citasAgendadas = CitaDatosUtil.obtenerPaginaCitasAgendadas(pagina, tamanioPagina, usuario,
				citaRepository);

		if (citasAgendadas.isEmpty()) {
			model.addAttribute("messageCitasAgendadas", "Aún no tiene Servicios solicitados en la Agenda");
		}

		if (!citasAgendadas.isEmpty()) {
			// Añadir la lista de citas agendadas al modelo
			model.addAttribute("citasAgendadas", citasAgendadas);
		}

		// Añadir la paginación al modelo
		model.addAttribute("paginacion", true);
		model.addAttribute("totalPaginas", citasAgendadas.getTotalPages());
		model.addAttribute("paginaActual", citasAgendadas.getNumber() + 1);

		// Mostrar la vista con las citas agendadas
		model.addAttribute("view", "citasAgendadasCliente");

		return "dashboard1";
	}

	/**
	 * Metodo GET que permite al usuario CLIENTE, crear un JSON con los datos de la
	 * cita que me interesan los cuales luego se mostrarán en el modal, donde el
	 * CLIENTE luego podra editar la fecha y hora de la cita, usando el siguiente
	 * metodo post
	 * 
	 * @param idCita
	 * @return
	 */
	@GetMapping("/cita/agendada")
	public ResponseEntity<CitaDatosUtil> citaAgendada(@RequestParam("idCita") Integer idCita) {
		Optional<Cita> optionalCita = citaRepository.findById(idCita);
		Cita cita = optionalCita.get();

		CitaDatosUtil citaData = new CitaDatosUtil();
		citaData.setId(cita.getId());
		citaData.setServicio(cita.getServicioOfrecido().getCategoriaServicio().getNombre());
		citaData.setProveedor(cita.getServicioOfrecido().getProveedor().getUsuario().usuarioFullNombre());
		citaData.setMovil(cita.getServicioOfrecido().getProveedor().getUsuario().getMovil());
		citaData.setCliente(cita.getUsuario().getNombre());
		citaData.setFecha(cita.getFecha());
		citaData.setHora(cita.getHora());

		return ResponseEntity.ok(citaData);
	}

	/**
	 * Metodo POST que permite al usuario CLIENTE cambiar la fecha y la hora de la
	 * cita
	 * 
	 * @param citaId
	 * @param usuarioDetails
	 * @param model
	 * @return
	 */
	@PostMapping("/cita/editar")
	public String editarCita(@AuthenticationPrincipal UsuarioDetails usuarioDetails, @RequestParam("id") Integer citaId,
			@RequestParam("fechaEditar") String fecha, @RequestParam("horaEditar") String hora,
			@RequestParam(name = "pagina", defaultValue = "1") int pagina, Model model) {

		// Obtengo el usuario logeado
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);
		int tamanioPagina = 4;// Establecer el tamaño de la página y crear Paginacion de citas agendadas
		Page<Cita> citasAgendadas = CitaDatosUtil.obtenerPaginaCitasAgendadas(pagina, tamanioPagina, usuario,
				citaRepository);

		model.addAttribute("paginacion", true);
		model.addAttribute("totalPaginas", citasAgendadas.getTotalPages());
		model.addAttribute("paginaActual", citasAgendadas.getNumber() + 1);
		model.addAttribute("citasAgendadas", citasAgendadas);

		// Recuperar la cita a a editar partir del ID
		Cita cita = citaRepository.findById(citaId).orElse(null);

		// Fecha actual, hora actual y diferencia de horas entre la hora de cita y la
		// hora actual
		LocalDate hoy = LocalDate.now();
		LocalTime ahora = LocalTime.now();
		Duration tiempoAnticipacion = Duration.between(hoy.atTime(ahora), cita.getFecha().atTime(cita.getHora()));

		// fecha y hora de modificacion capturadas
		LocalDate fechaModificar = LocalDate.parse(fecha);
		LocalTime horaModificar = LocalTime.parse(hora);

		if (!cita.getEstado().equals("PENDIENTE")) {
			model.addAttribute("errorMessageCancelaCita",
					"No puede modificar la cita que ya ha sido (COMPLETADA, PROCESADA o CANCELADA)");
			model.addAttribute("view", "citasAgendadasCliente");
			return "dashboard1";
		}
		if (fechaModificar.isBefore(hoy) || cita.getFecha().isEqual(hoy) && horaModificar.isBefore(ahora)) {
			model.addAttribute("errorMessageCancelaCita",
					"No puede modificar la cita, la Fecha y Hora no son pueden ser antes de la actual");
			model.addAttribute("view", "citasAgendadasCliente");
			return "dashboard1";
		}
		if (cita.getFecha().isEqual(hoy) && tiempoAnticipacion.toHours() < 2) {
			model.addAttribute("errorMessageCancelaCita",
					"No puede modificar la cita debido a que no tiene al menos 2 horas de antelación.");
			model.addAttribute("view", "citasAgendadasCliente");
			return "dashboard1";
		}
		cita.setFecha(fechaModificar);
		cita.setHora(horaModificar);
		citaRepository.save(cita);
		model.addAttribute("successMessageCancelaCita", "Se ha reagendado su cita con exito.");
		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "citasAgendadasCliente");
		return "dashboard1";
	}

	/***
	 * Metodo POST para que el usuario CLIENTE pueda CANCELAR una cita, donde si
	 * esta cumple con ciertas validaciones su estado cambiara a cancelada
	 * 
	 * @param usuarioDetails
	 * @param idCita
	 * @param estado
	 * @param pagina
	 * @param model
	 * @return
	 */
	@PostMapping("/cita/cancelar")
	public String cancelarCita(@AuthenticationPrincipal UsuarioDetails usuarioDetails,
			@RequestParam("idCita") Integer idCita, @RequestParam("estado") String estado,
			@RequestParam(name = "pagina", defaultValue = "1") int pagina, Model model) {
		// Recuperar la cita a partir del ID
		Cita cita = citaRepository.findById(idCita).orElse(null);

		// Obtener fecha y hora actual
		LocalDate hoy = LocalDate.now();
		LocalTime ahora = LocalTime.now();
		Duration tiempoAnticipacion = Duration.between(hoy.atTime(ahora), cita.getFecha().atTime(cita.getHora()));
		System.out.println(tiempoAnticipacion.toHours());
		// Verificar que la cita está en estado pendiente
		if (cita.getEstado().equals("PENDIENTE")) {
			if ((cita.getFecha().isEqual(hoy) && tiempoAnticipacion.toHours() >= 2) || cita.getFecha().isAfter(hoy)) {

				cita.setEstado("CANCELADA");
				// citaRepository.save(cita);
				model.addAttribute("successMessageCancelaCita", "Se ha Cancelado la cita con éxito.");
			} else {
				model.addAttribute("errorMessageCancelaCita",
						"No puede cancelar la cita debido a que no tiene al menos 2 horas de antelación.");
			}

		} else {
			model.addAttribute("errorMessageCancelaCita",
					"No puede modificar la cita que ya ha sido (COMPLETADA, PROCESADA o CANCELADA)");
		}

		// Obtengo el usuario logeado
		Usuario usuario = usuarioRepository.findById(usuarioDetails.getUserId()).orElse(null);

		// Establecer el tamaño de la página
		int tamanioPagina = 4;

		// Obtener la página de citas agendadas del usuario
		Page<Cita> citasAgendadas = CitaDatosUtil.obtenerPaginaCitasAgendadas(pagina, tamanioPagina, usuario,
				citaRepository);

		// Añadir la paginación al modelo
		model.addAttribute("paginacion", true);
		model.addAttribute("totalPaginas", citasAgendadas.getTotalPages());
		model.addAttribute("paginaActual", citasAgendadas.getNumber() + 1);
		model.addAttribute("citasAgendadas", citasAgendadas);

		// Esta es la vista que se mostrara el el dashboard
		model.addAttribute("view", "citasAgendadasCliente");

		return "dashboard1";
	}

}

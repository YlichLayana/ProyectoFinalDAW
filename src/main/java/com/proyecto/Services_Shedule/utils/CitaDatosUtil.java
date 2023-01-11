package com.proyecto.Services_Shedule.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.proyecto.Services_Shedule.model.Cita;
import com.proyecto.Services_Shedule.model.Proveedor;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.CitaRepository;

public class CitaDatosUtil {
	private Integer id;
	private String servicio;
	private String proveedor;
	private String movil;
	private String cliente;
	private LocalDate fecha;
	private LocalTime hora;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	public String getProveedor() {
		return proveedor;
	}

	public void setProveedor(String proveedor) {
		this.proveedor = proveedor;
	}

	public String getMovil() {
		return movil;
	}

	public void setMovil(String movil) {
		this.movil = movil;
	}

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public LocalTime getHora() {
		return hora;
	}

	public void setHora(LocalTime hora) {
		this.hora = hora;
	}

	/***
	 * Método el cual retorna una lista de citas con estado CANCELADA
	 * 
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static List<Cita> obtenerCitasCanceladas(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {
		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasGetionar = new ArrayList<>();

		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findByEstado("CANCELADA");

		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				System.out.println("CANCELADAS, SOLO SI ES EL MISMO SERVICIO Y PROVEEDOR: " + cita.getId() + " - "
						+ cita.getFecha() + " - " + cita.getEstado());
				citasGetionar.add(cita);
			}
		}
		return citasGetionar;
	}

	/***
	 * Metodo que devuelve una lista de citas con solo con estado COMPLETADA
	 * 
	 * @param proveedor
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static List<Cita> obtenerCitasCompletadas(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {
		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasGetionar = new ArrayList<>();

		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findByEstado("COMPLETADA");

		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				System.out.println("COMPLETADAS, SOLO SI ES EL MISMO SERVICIO Y PROVEEDOR: " + cita.getId() + " - "
						+ cita.getFecha() + " - " + cita.getEstado());
				citasGetionar.add(cita);
			}
		}
		return citasGetionar;
	}

	/***
	 * Método que me devolvera un List de citas con Estado distintos a CANCELADA y
	 * COMPLETADA para poder gestionar su estado cuando esta cita sea atendida
	 * 
	 * @param proveedor
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static List<Cita> obtenerCitasGetionar(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {
		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasGetionar = new ArrayList<>();

		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findAll();

		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				if (!cita.getEstado().equals("CANCELADA") && !cita.getEstado().equals("COMPLETADA")) {
					System.out.println("COMPLETADAS, SOLO SI ES EL MISMO SERVICIO Y PROVEEDOR: " + cita.getId() + " - "
							+ cita.getFecha() + " - " + cita.getEstado());
					citasGetionar.add(cita);
				}
			}
		}
		return citasGetionar;
	}

	/***
	 * Método que retornara un valor entero del total de las citas que tenga Solo el
	 * estado PENDIENTE
	 * 
	 * @param proveedor
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static int totalCitasAgendadasPendientes(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {
		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasTotal = new ArrayList<>();

		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findByEstado("PENDIENTE");

		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				citasTotal.add(cita);
			}
		}
		return citasTotal.size();
	}

	/***
	 * Método que retornara un valor entero del total de las citas que tenga Solo el
	 * estado PROCESADAS
	 * 
	 * @param proveedor
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static int totalCitasAgendadasProcesadas(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {
		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasTotal = new ArrayList<>();

		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findByEstado("PROCESADA");

		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				citasTotal.add(cita);
			}
		}
		return citasTotal.size();
	}

	/***
	 * Método que retornara un valor entero del total de las citas que tenga Solo el
	 * estado COMPLETADAS
	 * 
	 * @param proveedor
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static int totalCitasCompletadas(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {
		// Lista para almacenar las citas completadas del proveedor
		List<Cita> citasGetionar = new ArrayList<>();

		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findByEstado("COMPLETADA");

		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				citasGetionar.add(cita);
			}
		}
		return citasGetionar.size();
	}

	/***
	 * Método que retornara un String con el total del precio recaudado esto Solo de
	 * citas con estado COMPLETADAS
	 * 
	 * @param proveedor
	 * @param servicio
	 * @param citaRepository
	 * @return
	 */
	public static String totalFacturadoCitas(Proveedor proveedor, ServicioOfrecido servicio,
			CitaRepository citaRepository) {

		DecimalFormatSymbols separadoresPersonalizados = new DecimalFormatSymbols();
		separadoresPersonalizados.setDecimalSeparator('.');
		DecimalFormat formato = new DecimalFormat("#.00", separadoresPersonalizados);
		// Recuperar las citas completadas del servicio
		List<Cita> citas = citaRepository.findByEstado("COMPLETADA");
		float total = 0;
		for (Cita cita : citas) {
			if (cita.getServicioOfrecido().equals(servicio) && servicio.getProveedor().equals(proveedor)) {
				total += cita.getServicioOfrecido().getPrecio();
			}
		}
		return formato.format(total);
	}

	/***
	 * Método que nos sirve para crear la paginación en de las citas que debe
	 * gestional el PROVEEDOR
	 * 
	 * @param citas
	 * @param pagina
	 * @param tamanioPagina
	 * @return
	 */
	public static List<Cita> obtenerCitasPagina(List<Cita> citas, int pagina, int tamanioPagina) {
		int inicio = (pagina - 1) * tamanioPagina;
		int fin = Math.min(inicio + tamanioPagina, citas.size());
		return citas.subList(inicio, fin);
	}

	/***
	 * Método que nos sive para crear la paginación de las citas que tenga agendadas
	 * el USUARIO
	 * 
	 * @param pagina
	 * @param tamanioPagina
	 * @param usuario
	 * @param citaRepository
	 * @return
	 */
	public static Page<Cita> obtenerPaginaCitasAgendadas(int pagina, int tamanioPagina, Usuario usuario,
			CitaRepository citaRepository) {
		Pageable paginaActual = PageRequest.of(pagina - 1, tamanioPagina);
		return citaRepository.findByUsuario(usuario, paginaActual);
	}

}

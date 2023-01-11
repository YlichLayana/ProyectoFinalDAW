package com.proyecto.Services_Shedule.controller;


import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.proyecto.Services_Shedule.model.CategServicios;
import com.proyecto.Services_Shedule.model.Proveedor;
import com.proyecto.Services_Shedule.model.Rol;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.CatgServiciosRepository;
import com.proyecto.Services_Shedule.repository.CitaRepository;
import com.proyecto.Services_Shedule.repository.ProveedorRepository;
import com.proyecto.Services_Shedule.repository.RolRepository;
import com.proyecto.Services_Shedule.repository.ServicioOfrecidoRepository;
import com.proyecto.Services_Shedule.repository.UsuarioRepository;
import com.proyecto.Services_Shedule.service.UsuarioDetails;
import com.proyecto.Services_Shedule.utils.CitaDatosUtil;
import com.proyecto.Services_Shedule.utils.TarjetaUtil;

@Controller
public class AppController {

	@Autowired
	private RolRepository rolRepo;
	
	@Autowired
	private UsuarioRepository userRepo;
	
	@Autowired
	private ProveedorRepository proveedorRepository;

	@Autowired
	private CatgServiciosRepository categoOfrecidaRepo;

	@Autowired
	private ServicioOfrecidoRepository servicioRepository;
	
	@Autowired
	private CitaRepository citaRepository;

	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}

	/***
	 * Metodo GET que sera usado por todos los usuarios el cual retorna la pagina
	 * del login
	 * 
	 * @return
	 */
	@GetMapping("/login")
	public String iniciarSesion() {
		return "login";
	}

	/***
	 * Metodo GET que puede ser usado por todos los usuarios, el que les sirve para
	 * dirigirse al formularioRegistro el cual es para darse de alta como usuario
	 * CLIENTE.
	 * 
	 * @param model 
	 * @return
	 */
	@GetMapping("/registrarse")
	public String showRegistrationForm(Model model) {
		model.addAttribute("usuario", new Usuario());

		return "formularioRegistro";
	}

	/***
	 * Este método POST es el que usaremos para que el usuario que desea darse de
	 * alta como CLIENTE pueda enviar los datos a la tabla USUARIO la cual esta
	 * esta relacionada de M:N con la de ROLES Este método recibe un objeto de tipo
	 * USUARIO y gestionara los datos del formulario
	 * 
	 * @param user
	 * @return
	 */
	@PostMapping("/proceso_registro")
	public String processRegister(Usuario user) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		if (userRepo.findByDni(user.getDni()) != null) {// aqui valido que el usuario con dni ya existe

			return "redirect:/registrarse?error";
		}
		/*
		 * AQUI HAGO DARLE EL TIPO DE ROL QUE TENDRA EL CLIENTE AL CUAL SE LE SETEA EL
		 * ID_ROL 1
		 */
		int rolCliente = 1;
		Rol rolUser = rolRepo.findById(rolCliente);
		user.addRols(rolUser);

		userRepo.save(user);

		return "redirect:/registrarse?exito";
	}

	/***
	 * Método GET que puede ser usado por cualquier usuario, que desee darse de alta
	 * como PROVEEDOR en este metodo usamos tres tipos de model uno por cada tabla
	 * de la BBDD los cuales son: usuario, proveedor, servicioOfrecido. Y este
	 * metodo nos retorna el formulario en el que se podra dar de alta
	 * 
	 * @param model
	 * @return
	 */
	@GetMapping("/registrarseProveedor")
	public String showRegistrationFormProveedor(Model model) {
		model.addAttribute("usuario", new Usuario());
		model.addAttribute("proveedor", new Proveedor());
		model.addAttribute("servicioOfrecido", new ServicioOfrecido());

		// Aqui se hace la select de las categorias de servicios y se muestra en el form
		// de alta USUARIO PROVEEDOR
		List<CategServicios> categorias = categoOfrecidaRepo.findAll();
		model.addAttribute("categorias", categorias);
		
		// Aqui se agregan los turnos posibles al modelo
		List<String> turnos = Arrays.asList("MAÑANA (09h00 - 14h00)", "TARDE (16h00 - 20h00)", "COMPLETO (09h00 - 20h00)");
		model.addAttribute("turnos", turnos);
		
		// Aqui se agregan los tipos de servicio posibles al modelo
		List<String> tiposServicio = Arrays.asList("DOMICILIO", "LOCAL");
		model.addAttribute("tiposServicio", tiposServicio);

		return "formularioRegistroProveedor";
	}

	/***
	 * Este metodo POST es el que se encarga de enviar los datos de usuario que
	 * quiera darse de alta como PROVEEDOR y alimentara las diferentes tablas
	 * relacionadas, entre ellas estan la tabla usuarios la que esta relacionada de
	 * M:N con la tabla roles y esta tabla usarios tambien esta relacionada de 1:1
	 * con la tabla proveedor y esta esta relacionada de 1:1 con la tabla
	 * servicio_ofrecido. Es por ello que este método recibira Objetos de casa una
	 * de la entidades.
	 * 
	 * @param user
	 * @param prov
	 * @param servOfrec
	 * @param categoriaServicio
	 * @return
	 */
	@PostMapping("/proceso_registroProveedor")
	public String processRegisterProveedor(Usuario user, Proveedor prov, ServicioOfrecido servOfrec,
			@RequestParam("categoriaServicioId") CategServicios categoriaServicio, 
			@RequestParam("turno") String turno,
	        @RequestParam("tipoServicio") String[] tipoServicio) {
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		String encodedPassword = passwordEncoder.encode(user.getPassword());
		user.setPassword(encodedPassword);

		System.out.println(
				"Datos Categorias Servicio: " + categoriaServicio.getId() + " " + categoriaServicio.getNombre());
		
		System.out.println("Turno Escojido: "+ turno);
		for (String tipo : tipoServicio) {
			System.out.println("Tipo de servicio elegido: "+  tipo );
		}
		
		if (userRepo.findByDni(user.getDni()) != null) {// aqui valido que el usuario con dni ya existe

			return "redirect:/registrarseProveedor?error";
		}
		/*
		 * AQUI HAGO DARLE EL TIPO DE ROL QUE TENDRA EL PROVEEDOR AL CUAL SE LE SETEA EL
		 * ID_ROL 2
		 */
		int rolProveedor = 2;
		Rol rolUser = rolRepo.findById(rolProveedor);
		user.addRols(rolUser);

		prov.setUsuario(user);

		servOfrec.setProveedor(prov);// esto es para agregarle el id de proveedor
		servOfrec.setCategoriaServicio(categoriaServicio);// esto es para agregarle el id de ctg_servicio
		
		// Asignación de turno y tipo de servicio
	    servOfrec.setTurno(turno);
	    servOfrec.setTipoServicio(String.join(", ", tipoServicio));

		prov.setServicioOfrecido(servOfrec);

		user.setProveedor(prov);

		userRepo.save(user);

		return "redirect:/registrarseProveedor?exito";
	}
	

	/***
	 * Método que se usara para que todos los usuarios que esten dados de alta y
	 * Logeados puedan acceder al Tablero Dashboard, el cual esta configuracion la
	 * tenemos en la class WebSecurityConfig. Este metodo tambie nos dara o mostrara
	 * en el tablero una cantidad de 6 cards de servicios aleatoriamente siempre y
	 * cuando este usuario logeado tenga como Rol CLIENTE
	 * 
	 * @param model
	 * @param usuarioDetails
	 * @return
	 */
	@GetMapping("/dashboardPage")
	public String dasboard(Model model, @AuthenticationPrincipal UsuarioDetails usuarioDetails) {

		// Obetener el rol del usuario logeado
		Collection<? extends GrantedAuthority> userRol = usuarioDetails.getAuthorities();
		List<ServicioOfrecido> tarjetas = TarjetaUtil.obtenerTarjetasServiciosAleatorios(servicioRepository);

		// Recuperar el usuario a partir de su ID de usuario
		Usuario usuario = userRepo.findById(usuarioDetails.getUserId()).orElse(null);
		// Recuperar el proveedor a partir del usuario
		Proveedor proveedor = proveedorRepository.findByUsuario(usuario);
		// Recuperar los servicios ofrecidos por el proveedor
		ServicioOfrecido servicio = servicioRepository.findByProveedor(proveedor);
		int citasCompletadas = CitaDatosUtil.totalCitasCompletadas(proveedor, servicio, citaRepository);
		int citasTotalPendientes = CitaDatosUtil.totalCitasAgendadasPendientes(proveedor, servicio, citaRepository);
		int citasTotalProcesadas = CitaDatosUtil.totalCitasAgendadasProcesadas(proveedor, servicio, citaRepository);
		String totalRecaudado = CitaDatosUtil.totalFacturadoCitas(proveedor, servicio, citaRepository);

		// Si el usuario cliente visualizara 6 servicios aleatorios
		if (userRol.contains(new SimpleGrantedAuthority("CLIENTE"))) {
			// Agregar la lista de tarjetas a la vista
			model.addAttribute("tarjetas", tarjetas);
			// Esta es la vista que se mostrara el el dashboard
			model.addAttribute("view", "serviciosAleatorios");
		}
		
		if (userRol.contains(new SimpleGrantedAuthority("PROVEEDOR"))
				|| userRol.contains(new SimpleGrantedAuthority("CLIENTE"))
						&& userRol.contains(new SimpleGrantedAuthority("PROVEEDOR"))) {
			
			model.addAttribute("citasAtendidas", citasCompletadas);
			model.addAttribute("citasTotalPendientes", citasTotalPendientes);
			model.addAttribute("citasTotalProcesadas", citasTotalProcesadas);
			model.addAttribute("totalRecaudado", totalRecaudado);

			// Esta es la vista que se mostrara el el dashboard
			model.addAttribute("view", "informativaProveedor");
		}

		return "dashboard1";
	}

}

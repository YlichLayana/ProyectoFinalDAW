package com.proyecto.Services_Shedule.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.proyecto.Services_Shedule.model.CategServicios;
import com.proyecto.Services_Shedule.model.Proveedor;
import com.proyecto.Services_Shedule.model.Rol;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.model.Usuario;
import com.proyecto.Services_Shedule.repository.CatgServiciosRepository;

import com.proyecto.Services_Shedule.repository.ProveedorRepository;
import com.proyecto.Services_Shedule.repository.RolRepository;
import com.proyecto.Services_Shedule.repository.ServicioOfrecidoRepository;
import com.proyecto.Services_Shedule.repository.UsuarioRepository;
import com.proyecto.Services_Shedule.service.UsuarioDetails;



@Controller
public class UsuarioController {
	
	@Autowired
	private RolRepository rolRepo;
	
	@Autowired
    private UsuarioRepository usuarioRepository;
	
	@Autowired
    private ProveedorRepository proveedorRepository;
	
	@Autowired
    private CatgServiciosRepository categoriaServicioRepository;
	
	@Autowired
    private ServicioOfrecidoRepository servicioOfrecidoRepository;

    
    
	/***
	 * Este Módelo permite que el usuario pueda ver sus datos y los modifique
	 * 
	 * @param model
	 * @param usuarioDetails
	 * @return
	 */
	@GetMapping("/dashboard/datosCuentaUsuario")
	public String dashboardviewUsuario(Model model, @AuthenticationPrincipal UsuarioDetails usuarioDetails) {

		String userEmail = usuarioDetails.getUsername();
		Collection<? extends GrantedAuthority> userRol = usuarioDetails.getAuthorities();

		Usuario usuario = usuarioRepository.findByEmail(userEmail);

		/*
		 * Aqui se crea una instancia de la clase Proveedor y una instancia de la clase
		 * ServicioOfrecido para cuando un usuario es solo cliente y este pueda editar
		 * sus datos y agregar los de proveedor y servicio
		 */
		Proveedor usuarioClienteProveedor = new Proveedor();
		ServicioOfrecido servicioOfrecidoClienteProveedor = new ServicioOfrecido();

		model.addAttribute("usuario", usuario);
		model.addAttribute("usuarioClienteProveedor", usuarioClienteProveedor);
		model.addAttribute("servicioOfrecidoClienteProveedor", servicioOfrecidoClienteProveedor);

		// Aqui se agregan los turnos posibles al modelo
		List<String> turnos = Arrays.asList("MAÑANA (09h00 - 14h00)", "TARDE (16h00 - 20h00)",
				"COMPLETO (09h00 - 20h00)");
		model.addAttribute("turnos", turnos);

		// Aqui se agregan los tipos de servicio posibles al modelo
		List<String> tiposServicio = Arrays.asList("DOMICILIO", "LOCAL");
		model.addAttribute("tiposServicio", tiposServicio);

		List<CategServicios> categorias = categoriaServicioRepository.findAll();
		model.addAttribute("categorias", categorias);

		model.addAttribute("view", "editar_usuario");// esta es la vista del formulario Cliente

		if (userRol.contains(new SimpleGrantedAuthority("PROVEEDOR"))
				|| userRol.contains(new SimpleGrantedAuthority("CLIENTE"))
						&& userRol.contains(new SimpleGrantedAuthority("PROVEEDOR"))) {
			Proveedor proveedor = proveedorRepository.findByUsuario(usuario);
			ServicioOfrecido servicioOfrecido = servicioOfrecidoRepository.findByProveedor(proveedor);
			model.addAttribute("proveedor", proveedor);
			model.addAttribute("servicioOfrecido", servicioOfrecido);
			model.addAttribute("turnoActual", servicioOfrecido.getTurno());
			model.addAttribute("tipoServicioActual", servicioOfrecido.getTipoServicio());
			model.addAttribute("view", "editar_usuarioProveedor"); // esta es la vista del formulario Proveedor

		}
		return "dashboard1";
	}
	
	
	/***
	 * Este Método POST permite la actualizacion de los datos del usuario ya sea
	 * CLIENTE o PROVEEDOR Con HttpServletRequest request puedo capturar el value de
	 * un check-box
	 * 
	 * @param user
	 * @param prove
	 * @param servOfrec
	 * @param redirectAttributes
	 * @param usuarioDetails
	 * @param request
	 * @param categoriaServicio
	 * @return
	 */
	@PostMapping("/usuarioUpdate")
	public String saveDetails(@AuthenticationPrincipal UsuarioDetails usuarioDetails, Usuario user, Proveedor prove,
			ServicioOfrecido servOfrec, RedirectAttributes redirectAttributes, HttpServletRequest request,
			@RequestParam("categoriaServicioId") CategServicios categoriaServicio,
			@RequestParam(value = "turno", required = false) String turno,
			@RequestParam(value = "tipoServicio", required = false) String[] tipoServicio) {

		// Obtener los roles del usuario logeado
		Collection<? extends GrantedAuthority> userRolActual = usuarioDetails.getAuthorities();
		// Busco los datos del usuario logeado y existente en la bbdd
		Usuario userExistente = usuarioRepository.findByEmail(usuarioDetails.getUsername());
		// Busqueda del Provedor
		Proveedor proveedorActualizar = proveedorRepository.findByUsuario(userExistente);
		// Busqueda del ServicioOfrecido del usuario - proveedor logeado
		ServicioOfrecido servicioActualizar = servicioOfrecidoRepository.findByProveedor(proveedorActualizar);
		// Variables para captura de los check box del formulario de cliente y de
		// proveedor
		String checkboxValueClie = request.getParameter("mi-checkbox-cliente");
		String checkboxValueProv = request.getParameter("mi-checkbox-proveedor");

		// Variable para luego usarla en el agregado de un rol
		int valRol = 0;
		Rol rolUser = null;

		userExistente.setNombre(user.getNombre());
		userExistente.setApellido(user.getApellido());
		userExistente.setMovil(user.getMovil());
		userExistente.setDireccion(user.getDireccion());
		userExistente.setCod_postal(user.getCod_postal());
		userExistente.setEmail(user.getEmail());

		/*
		 * Condición donde pregunto que el usuario sea solo CLIENTE y el check box no
		 * vaya en null, entonces se le agrega el rol (proveedor) y se hace el insert de
		 * los datos en la tabla proveedor
		 */
		if (userRolActual.contains(new SimpleGrantedAuthority("CLIENTE")) && checkboxValueClie != null) {
			valRol = 2;
			rolUser = rolRepo.findById(valRol);
			userExistente.addRols(rolUser);// se le agrega el rol correspondiente
			prove.setUsuario(userExistente);
			servOfrec.setProveedor(prove);// se setea el proveedor al servicio ofrecido
			servOfrec.setCategoriaServicio(categoriaServicio);// se setea la categoria al servicio ofrecido
			servOfrec.setTurno(turno);// Asignación de turno
			servOfrec.setTipoServicio(String.join(", ", tipoServicio));// Asignacion de tipo de servicio
			prove.setServicioOfrecido(servOfrec);
			userExistente.setProveedor(prove);
		}

		// Solo el usuario tiene rol PROVEEDOR o Tiene el rol PROVEEDOR y CLIENTE
		if (userRolActual.contains(new SimpleGrantedAuthority("PROVEEDOR"))
				|| userRolActual.contains(new SimpleGrantedAuthority("CLIENTE"))
						&& userRolActual.contains(new SimpleGrantedAuthority("PROVEEDOR"))) {
			valRol = 1;
			rolUser = rolRepo.findById(valRol);

			if (checkboxValueProv != null) { // Solo si el usuario le da el Check este
				userExistente.addRols(rolUser); // agrego el rol
			}
			// Aqui seteo los datos para la tabla PROVEEDOR
			proveedorActualizar.setEspecialidad(prove.getEspecialidad());
			proveedorActualizar.setAnio_experiencia(prove.getAnio_experiencia());
			// Aqui seteo los datos para la tabla SERVICIO_OFRECIDO
			servicioActualizar.setPrecio(servOfrec.getPrecio());
			servicioActualizar.setDescripcion(servOfrec.getDescripcion());
			servicioActualizar.setTurno(turno);
			servicioActualizar.setTipoServicio(String.join(", ", tipoServicio));
			servicioActualizar.setCategoriaServicio(categoriaServicio);
		}

		usuarioRepository.save(userExistente);

		return "redirect:/dashboard/datosCuentaUsuario?exito";
	}
	
}

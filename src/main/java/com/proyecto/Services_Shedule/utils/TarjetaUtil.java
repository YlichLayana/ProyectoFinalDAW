package com.proyecto.Services_Shedule.utils;


import java.util.Collections;
import java.util.List;
import com.proyecto.Services_Shedule.model.ServicioOfrecido;
import com.proyecto.Services_Shedule.repository.ServicioOfrecidoRepository;

public class TarjetaUtil {
	 public static List<ServicioOfrecido> obtenerTarjetasServiciosAleatorios(ServicioOfrecidoRepository servicioRepository) {
	// Obtener todos los servicios y desordenarlos aleatoriamente
        List<ServicioOfrecido> servicios = servicioRepository.findAll();
        Collections.shuffle(servicios);

        // Tomar los primeros 6 servicios de la lista desordenada
        List<ServicioOfrecido> serviciosAleatorios = servicios.subList(0, Math.min(6, servicios.size()));

        return serviciosAleatorios;
    }
}

package jsbm.registroEvento;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@Service
public class RegistroEventoApplication {

	public static void main(String[] args) {
		SpringApplication.run(RegistroEventoApplication.class, args);
	}

	public Respuesta guardarEvento(Evento mensaje) {
		ManejaArchivo ma = new ManejaArchivo("bd.txt");
		ma.setMensaje(mensaje.getNombre() + "|" + mensaje.getEmpleado() + "|" + mensaje.getFecha_hora());
		ma.Save();

		return new Respuesta(ma.getRespuesta(),ma.isError());
	}

}

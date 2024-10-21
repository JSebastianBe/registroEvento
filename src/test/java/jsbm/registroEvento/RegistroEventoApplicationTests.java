package jsbm.registroEvento;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegistroEventoApplicationTests {

	private static final Logger logger = LoggerFactory.getLogger(RegistroEventoApplicationTests.class);
	@Test
	void contextLoads() throws InterruptedException {
		final int NUM_SOLICITUDES = 6000;
		final int DURACION_SEGUNDOS = 6; // Ajuste para más tiempo
		final int REQUESTS_PER_SECOND = 1000; // Límite de solicitudes por segundo
		final long DELAY_BETWEEN_REQUESTS = 1000 / REQUESTS_PER_SECOND;

		ExecutorService executor = new ThreadPoolExecutor(
				100, // Tamaño mínimo del pool
				1000, // Tamaño máximo del pool
				10L, // Tiempo para liberar hilos inactivos
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>()
		);

		List<Long> tiemposRespuesta = Collections.synchronizedList(new ArrayList<>());
		List<Exception> errores = Collections.synchronizedList(new ArrayList<>());
		int[] solicitudesExitosas = {0};

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < NUM_SOLICITUDES; i++) {
			executor.submit(() -> {
				try {
					long startRequestTime = System.currentTimeMillis();
					boolean resultado = simularSolicitudConReintento(3);
					long endRequestTime = System.currentTimeMillis();

					long tiempoRespuesta = endRequestTime - startRequestTime;
					synchronized (tiemposRespuesta) {
						tiemposRespuesta.add(tiempoRespuesta);
					}

					if (resultado) {
						synchronized (solicitudesExitosas) {
							solicitudesExitosas[0]++;
						}
					}
				} catch (Exception e) {
					synchronized (errores) {
						errores.add(e);
					}
					logger.error("Error en la solicitud: {}", e.getMessage());
				}
			});
			Thread.sleep(DELAY_BETWEEN_REQUESTS);
		}

		executor.shutdown();
		executor.awaitTermination(DURACION_SEGUNDOS, TimeUnit.SECONDS);

		long elapsedTime = System.currentTimeMillis() - startTime;
		logger.info("Tiempo transcurrido: {} ms", elapsedTime);
		logger.info("Solicitudes esperadas: {}", NUM_SOLICITUDES);
		logger.info("Solicitudes exitosas: {}", solicitudesExitosas[0]);

		// Cálculo de métricas de tiempo de respuesta
		double promedio = tiemposRespuesta.stream().mapToLong(Long::longValue).average().orElse(0.0);
		long max = tiemposRespuesta.stream().mapToLong(Long::longValue).max().orElse(0);
		long min = tiemposRespuesta.stream().mapToLong(Long::longValue).min().orElse(0);

		logger.info("Tiempo de respuesta promedio: {} ms", promedio);
		logger.info("Tiempo de respuesta máximo: {} ms", max);
		logger.info("Tiempo de respuesta mínimo: {} ms", min);

		assertTrue(solicitudesExitosas[0] >= NUM_SOLICITUDES, "No se alcanzó el 95% de las solicitudes exitosas");
	}

	private boolean simularSolicitudConReintento(int reintentosMaximos) {
		int intentos = 0;
		boolean exito = false;

		while (intentos < reintentosMaximos && !exito) {
			exito = simularSolicitud();
			intentos++;
		}

		if (!exito) {
			logger.warn("Solicitud fallida tras {} intentos", reintentosMaximos);
		}

		return exito;
	}

	private	boolean simularSolicitud(){
		try{
			URL url = new URL("http://localhost:8070/enviarEvento");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setDoOutput(true);

			String jsonInputString = "{\n" +
					"    \"empleado\":\"Usuario de prueba\",\n" +
					"    \"nombre\":\"Evento Prueba\",\n" +
					"    \"fecha_hora\": \"2024-10-01 14:23:00\"\n" +
					"}";

			try(OutputStream os = connection.getOutputStream()){
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input,0,input.length);
			}
			int responseCode = connection.getResponseCode();

			if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
				String inputline;
				StringBuilder response = new StringBuilder();
				while ((inputline = in.readLine()) != null){
					response.append(inputline);
				}
				in.close();

				return true;
			}else{
				System.out.println("Código de respuesta: " + responseCode);
				return false;
			}
		}catch (Exception e){
			System.out.println("Error al realizar la solicitud: " + e.getMessage());
			return false;
		}
	}

}

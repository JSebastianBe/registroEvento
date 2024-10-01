package jsbm.registroEvento;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class RegistroEventoApplicationTests {

	@Test
	void contextLoads() throws InterruptedException{
		final int NUM_SOLICITUDES = 6000;
		final int DURACION_SEGUNDOS = 6;

		ExecutorService executor = Executors.newFixedThreadPool(1000);
		int[] solicitudesExistosas = {0};

		long starTime = System.currentTimeMillis();

		for (int i = 0; i<NUM_SOLICITUDES; i++){
			executor.submit(() ->{
				try {
					boolean resultado = simularSolicitud();
					if(resultado){
						synchronized (solicitudesExistosas){
							solicitudesExistosas[0]++;
						}
					}
				}catch (Exception e){
					e.printStackTrace();
				}
			});
		}

		executor.shutdown();
		executor.awaitTermination(DURACION_SEGUNDOS, TimeUnit.SECONDS);

		long elapsedTime = System.currentTimeMillis() - starTime;
		System.out.println("Tiempo transcurrido: " + elapsedTime + " ms");


		System.out.println("Solicitudes esperadas: " + NUM_SOLICITUDES);
		System.out.println("Solicitudes exitosas: " + solicitudesExistosas[0]);

		assertTrue(solicitudesExistosas[0] >= NUM_SOLICITUDES, "No se alcanzaron las " + NUM_SOLICITUDES + " por " + DURACION_SEGUNDOS + " segundos");
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

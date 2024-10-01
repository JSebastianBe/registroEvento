package jsbm.registroEvento;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ManejaArchivo {

    private String nombre;
    private String mensaje;
    private String respuesta;
    private boolean error;
    private File archivo;

    public ManejaArchivo(String nombre) {
        this.nombre = nombre;
        this.error = false;
    }

    public ManejaArchivo(String nombre, String mensaje) {
        this.nombre = nombre;
        this.mensaje = mensaje;
        this.error = false;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isError() {
        return error;
    }

    public void Save(){
        try{
            this.archivo = new File(this.nombre);
            FileWriter escritor = new FileWriter(this.archivo,true);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ahora = LocalDateTime.now();
            String fechaHoraFormateada = ahora.format(formatter);
            Thread.sleep(100);
            escritor.write(fechaHoraFormateada + " -> " + this.mensaje + System.lineSeparator());
            this.respuesta = "Mensaje guardado";
            escritor.close();

        }catch (Exception e){
            this.error = true;
            this.respuesta = "Ocurrió un error en la escritura de la base de datos: " + e.getMessage();
        }


    }
}

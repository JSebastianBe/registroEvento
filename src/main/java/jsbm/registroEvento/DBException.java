package jsbm.registroEvento;

public class DBException extends Exception {
    // Constructor sin parámetros
    public DBException() {
        super("Ocurrió un error en la base de datos.");
    }

    // Constructor que permite pasar un mensaje
    public DBException(String mensaje) {
        super(mensaje);
    }

    // Constructor que permite pasar un mensaje y la causa de la excepción
    public DBException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    // Constructor que permite pasar solo la causa
    public DBException(Throwable causa) {
        super(causa);
    }
}
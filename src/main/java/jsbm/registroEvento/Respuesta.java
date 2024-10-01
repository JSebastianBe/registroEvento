package jsbm.registroEvento;

public class Respuesta {
    private String msg;
    private boolean error;

    public Respuesta(String msg, boolean error) {
        this.msg = msg;
        this.error = error;
    }

    public String getMsg() {
        return msg;
    }

    public boolean isError() {
        return error;
    }

}

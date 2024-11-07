package net.ddsmedia.baceh.asistencia.qr.entidad;

public class Faltas {

    private boolean response;
    private String mensaje;

    public Faltas() {
        this.response = response;
        this.mensaje = mensaje;
    }

    public boolean isResponse() {
        return response;
    }

    public void setResponse(boolean response) {
        this.response = response;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}

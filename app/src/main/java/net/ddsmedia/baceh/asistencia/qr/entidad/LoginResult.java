package net.ddsmedia.baceh.asistencia.qr.entidad;

public class LoginResult {

    private boolean error;
    private String mensaje;
    private String id_usuario;

    public LoginResult() {
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getId_usuario() { return id_usuario; }

    public void setId_usuario(String id_usuario) { this.id_usuario = id_usuario; }
}

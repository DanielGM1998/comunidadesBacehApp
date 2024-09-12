package net.ddsmedia.baceh.asistencia.qr.entidad;

public class Asistencia {

    private String fk_titular, fecha, credencial, nombrebene, usuario;

    public Asistencia() {
        this.fk_titular = fk_titular;
        this.fecha = fecha;
        this.credencial = credencial;
        this.nombrebene = nombrebene;
        this.usuario = usuario;
    }

    public String getFk_titular() {
        return fk_titular;
    }

    public void setFk_titular(String fk_titular) {
        this.fk_titular = fk_titular;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }

    public String getNombrebene() {
        return nombrebene;
    }

    public void setNombrebene(String nombrebene) {
        this.nombrebene = nombrebene;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
}

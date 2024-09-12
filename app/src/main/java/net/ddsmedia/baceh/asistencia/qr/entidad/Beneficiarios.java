package net.ddsmedia.baceh.asistencia.qr.entidad;

import java.io.Serializable;

public class Beneficiarios implements Serializable {

    private String id_titular;
    private String credencial;
    private String fecha_visita;
    private String fk_municipio;
    private String estatus;
    private String fecha_baja;
    private String faltas;
    private String ultima_visita;
    private String ultima_falta;
    private String bajas;
    private String tipo;
    private String observaciones_asist;
    private String id_integrante;
    private String parentesco;
    private String nombre;
    private String apaterno;
    private String amaterno;
    private String lista;
    private String sincronizar;

    public Beneficiarios() {
        this.id_titular = id_titular;
        this.credencial = credencial;
        this.fecha_visita = fecha_visita;
        this.fk_municipio = fk_municipio;
        this.estatus = estatus;
        this.fecha_baja = fecha_baja;
        this.faltas = faltas;
        this.ultima_visita = ultima_visita;
        this.ultima_falta = ultima_falta;
        this.bajas = bajas;
        this.tipo = tipo;
        this.observaciones_asist = observaciones_asist;
        this.id_integrante = id_integrante;
        this.parentesco = parentesco;
        this.nombre = nombre;
        this.apaterno = apaterno;
        this.amaterno = amaterno;
        this.lista = lista;
        this.sincronizar = sincronizar;
    }

    public String getId_titular() {
        return id_titular;
    }

    public void setId_titular(String id_titular) {
        this.id_titular = id_titular;
    }

    public String getCredencial() {
        return credencial;
    }

    public void setCredencial(String credencial) {
        this.credencial = credencial;
    }

    public String getFecha_visita() {
        return fecha_visita;
    }

    public void setFecha_visita(String fecha_visita) {
        this.fecha_visita = fecha_visita;
    }

    public String getFk_municipio() {
        return fk_municipio;
    }

    public void setFk_municipio(String fk_municipio) {
        this.fk_municipio = fk_municipio;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public String getFecha_baja() {
        return fecha_baja;
    }

    public void setFecha_baja(String fecha_baja) {
        this.fecha_baja = fecha_baja;
    }

    public String getFaltas() {
        return faltas;
    }

    public void setFaltas(String faltas) {
        this.faltas = faltas;
    }

    public String getUltima_visita() {
        return ultima_visita;
    }

    public void setUltima_visita(String ultima_visita) {
        this.ultima_visita = ultima_visita;
    }

    public String getUltima_falta() {
        return ultima_falta;
    }

    public void setUltima_falta(String ultima_falta) {
        this.ultima_falta = ultima_falta;
    }

    public String getBajas() {
        return bajas;
    }

    public void setBajas(String bajas) {
        this.bajas = bajas;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getObservaciones_asist() {
        return observaciones_asist;
    }

    public void setObservaciones_asist(String observaciones_asist) {
        this.observaciones_asist = observaciones_asist;
    }

    public String getId_integrante() {
        return id_integrante;
    }

    public void setId_integrante(String id_integrante) {
        this.id_integrante = id_integrante;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApaterno() {
        return apaterno;
    }

    public void setApaterno(String apaterno) {
        this.apaterno = apaterno;
    }

    public String getAmaterno() {
        return amaterno;
    }

    public void setAmaterno(String amaterno) {
        this.amaterno = amaterno;
    }

    public String getLista() {
        return lista;
    }

    public void setLista(String lista) {
        this.lista = lista;
    }

    public String getSincronizar() {
        return sincronizar;
    }

    public void setSincronizar(String sincronizar) {
        this.sincronizar = sincronizar;
    }
}

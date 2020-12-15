package com.agricolalaventa.seguridad;

public class Name {
    private String dni, idreferencia, idsucursal, idpda, fecha, idtraslado, idtipo;
    private int status;

    public Name( int status, String dni, String idreferencia, String idsucursal, String idpda, String fecha, String pedateador, String idtraslado, String idtipo ) {
        this.status = status;
        this.dni = dni;
        this.idreferencia = idreferencia;
        this.idsucursal = idsucursal;
        this.idpda = idpda;
        this.fecha = fecha;
        this.idtraslado = idtraslado;
        this.idtipo = idtipo;
    }

    public int getStatus() {
        return status;
    }

    public String getDni() {
        return dni;
    }

    public String getIdreferencia() {
        return idreferencia;
    }

    public String getIdsucursal() {
        return idsucursal;
    }

    public void setIdsucursal(String idsucursal) {
        this.idsucursal = idsucursal;
    }

    public String getIdpda() {
        return idpda;
    }

    public void setIdpda(String idpda) {
        this.idpda = idpda;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getIdtraslado() {
        return idtraslado;
    }

    public void setIdtraslado(String idtraslado) {
        this.idtraslado = idtraslado;
    }

    public String getIdtipo() {
        return idtipo;
    }

    public void setIdtipo(String idtipo) {
        this.idtipo = idtipo;
    }
}

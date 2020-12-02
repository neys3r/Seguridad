package com.agricolalaventa.seguridad;

public class Name {
    private String name, dni, placa, idsucursal, hostname, fecharegistro, idtraslado, idtipo;
    private int status;

    public Name(String name, int status, String dni, String placa, String idsucursal, String hostname, String fecharegistro, String pedateador, String idtraslado, String idtipo ) {
        this.name = name;
        this.status = status;
        this.dni = dni;
        this.placa = placa;
        this.idsucursal = idsucursal;
        this.hostname = hostname;
        this.fecharegistro = fecharegistro;
        this.idtraslado = idtraslado;
        this.idtipo = idtipo;
    }

    public String getName() {
        return name;
    }

    public int getStatus() {
        return status;
    }

    public String getDni() {
        return dni;
    }

    public String getPlaca() {
        return placa;
    }

    public String getIdsucursal() {
        return idsucursal;
    }

    public void setIdsucursal(String idsucursal) {
        this.idsucursal = idsucursal;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getFecharegistro() {
        return fecharegistro;
    }

    public void setFecharegistro(String fecharegistro) {
        this.fecharegistro = fecharegistro;
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

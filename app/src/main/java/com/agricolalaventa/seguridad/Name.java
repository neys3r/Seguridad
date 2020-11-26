package com.agricolalaventa.seguridad;

public class Name {
    private String name, dni, placa;
    private int status;

    public Name(String name, int status, String dni, String placa) {
        this.name = name;
        this.status = status;
        this.dni = dni;
        this.placa = placa;
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
}

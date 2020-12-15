package com.agricolalaventa.seguridad;

import android.provider.BaseColumns;

public class DBContract {

    public static  final String DATABASE_NAME = "seguridad.db";
    public static final String TABLE_CONTACTOS = "contactos";
    public static final String TABLE_PDAS = "pdas";
    public static final String TABLE_VIGILANTES = "vigilantes";

    public static class Contactos implements BaseColumns {
        public static String ID = "id";
        public static String NOMBRE = "nombre";
        public static String APELLIDOS = "apellidos";
        public static String TELEFONO = "telefono";
    }

    public static class Pdas implements BaseColumns{
        public static String ID = "id";
        public static String NOMBRE = "nombre";
        public static String IDSUCURSAL = "idsucursal";
        public static String DESCSUCURSAL = "descsucursal";

    }

    public static class Vigilantes implements BaseColumns{
        public static String IDVIGILANTE = "idvigilante";
        public static String NOMBRES = "nombres";
        public static String ESTADO = "estado";
        public static String IDAREA = "idarea";
    }

}

package com.agricolalaventa.seguridad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.format.DateFormat;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "security.db";
    public static final String TABLE_NAME = "names";
    public static final String VIEW_REPO01 = "repo01";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DNI = "dni";
    public static final String COLUMN_PLACA = "placa";
    public static final String COLUMN_IDSUCURSAL = "idsucursal";
    public static final String COLUMN_HOSTNAME = "hostname";
    public static final String COLUMN_PEDATEADOR = "pedateador";
    public static final String COLUMN_FECHAREGISTRO = "fecharegistro";
    public static final String COLUMN_IDTRASLADO = "idtraslado";
    public static final String COLUMN_IDTIPO = "idtipo";

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
   /* public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }*/

    public DatabaseHelper(Context context) {
        super(context,DBContract.DATABASE_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME
                + "(" + COLUMN_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME +
                " VARCHAR, " + COLUMN_STATUS +
                " VARCHAR, " + COLUMN_DNI +
                " VARCHAR, " + COLUMN_PLACA +
                " VARCHAR, " + COLUMN_IDSUCURSAL +
                " VARCHAR, " + COLUMN_HOSTNAME +
                " VARCHAR, " + COLUMN_FECHAREGISTRO +
                " VARCHAR, " + COLUMN_PEDATEADOR +
                " VARCHAR, " + COLUMN_IDTRASLADO +
                " VARCHAR, " + COLUMN_IDTIPO +
                " VARCHAR);";
        db.execSQL(sql);
        String sql2 = "CREATE VIEW " + VIEW_REPO01 + " as SELECT placa, count(1) as cantidad FROM names group by placa ";
        db.execSQL(sql2);

        createContactos(db);
        createPdas(db);
        createVigilantes(db);
    }


    // Métodos SQLITE
    public void createContactos(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_CONTACTOS+"("+DBContract.Contactos.ID+" INTEGER PRIMARY KEY,"+DBContract.Contactos.NOMBRE+" TEXT," +
                ""+DBContract.Contactos.APELLIDOS+" TEXT,"+DBContract.Contactos.TELEFONO+" TEXT);");
    }

    public void createPdas(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_PDAS+"("+DBContract.Pdas.ID+" TEXT PRIMARY KEY,"+DBContract.Pdas.NOMBRE+" TEXT," +
                ""+DBContract.Pdas.IDSUCURSAL+" TEXT,"+DBContract.Pdas.DESCSUCURSAL+" TEXT);");
    }

    public void createVigilantes(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_VIGILANTES+"("+DBContract.Vigilantes.IDVIGILANTE+" TEXT PRIMARY KEY,"+DBContract.Vigilantes.NOMBRES+" TEXT," +
                ""+DBContract.Vigilantes.ESTADO+" TEXT);");
    }

    public void savePdas(String id,String nombre,String idsucursal,String descsucursal, SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Pdas.ID,id);
        contentValues.put(DBContract.Pdas.NOMBRE,nombre);
        contentValues.put(DBContract.Pdas.IDSUCURSAL,idsucursal);
        contentValues.put(DBContract.Pdas.DESCSUCURSAL,descsucursal);

        db.insert(DBContract.TABLE_PDAS,null, contentValues);
    }

    public void saveVigilantes(String idvigilante,String nombres,String estado, SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Vigilantes.IDVIGILANTE,idvigilante);
        contentValues.put(DBContract.Vigilantes.NOMBRES,nombres);
        contentValues.put(DBContract.Vigilantes.ESTADO,estado);

        db.insert(DBContract.TABLE_VIGILANTES,null, contentValues);
    }

    // Fin Metodos SQLITE

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Persons";
        db.execSQL(sql);
        onCreate(db);
    }


    public boolean addName(String name, int status, String dni, String placa, String idsucursal, String hostname, String fecharegistro, String pedateador, String idtraslado, String idtipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_DNI, dni);
        contentValues.put(COLUMN_PLACA, placa);
        contentValues.put(COLUMN_IDSUCURSAL, idsucursal);
        contentValues.put(COLUMN_HOSTNAME, hostname);
        contentValues.put(COLUMN_FECHAREGISTRO, fecharegistro);
        contentValues.put(COLUMN_PEDATEADOR, pedateador);
        contentValues.put(COLUMN_IDTRASLADO, idtraslado);
        contentValues.put(COLUMN_IDTIPO, idtipo);

        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }


    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }


    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        //String sql = "SELECT id, Substr(name,32,8) as name  FROM names WHERE LENGTH(NAME) = 39 order by id";
        String sql = "SELECT id, name, status, dni, placa, idsucursal, hostname, fecharegistro, pedateador, idtraslado, idtipo FROM " + TABLE_NAME + " WHERE LENGTH(dni) = 8 and strftime('%Y-%m-%d',fecharegistro) = strftime('%Y-%m-%d','now') order by id";
        //String sql = "SELECT * FROM " + TABLE_NAME + " WHERE LENGTH(NAME) = 39 ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE LENGTH(dni) = 8 and strftime('%Y-%m-%d',fecharegistro) = strftime('%Y-%m-%d','now') and  " + COLUMN_STATUS + " = 0 ;";
        //String sql = "SELECT * FROM " + TABLE_NAME + " WHERE  " + COLUMN_STATUS + " = 0 ;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


        public String totalRegistro(){
            String lsViaje="0";
            String sql="select count(1) from names WHERE LENGTH(dni) = 8 and strftime('%Y-%m-%d',fecharegistro) = strftime('%Y-%m-%d','now')";
            SQLiteDatabase db= getReadableDatabase();
            Cursor cursor= db.rawQuery(sql, null);
            if(cursor.moveToFirst()){
                do {
                    lsViaje = cursor.getString(0);
                }while (cursor.moveToNext());
            }
            return lsViaje;
        }

    public String totalNoSync(){
        String lsViaje="0";
        String sql="select count(1) from names WHERE length(dni) = 8 and status = 0 and strftime('%Y-%m-%d',fecharegistro) = strftime('%Y-%m-%d','now')";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public String miIdSucursal(){
        String lsViaje="0";
        String sql="SELECT idsucursal FROM pdas where id = '"+ seriePda() +"';";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    private String seriePda(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        }else{
            return Build.SERIAL;
        }
    }

    public String miDescSucursal(){
        String lsViaje="0";
        String sql="SELECT descsucursal FROM pdas where id = '"+ Build.SERIAL +"';";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public String existeVigilante(String dni){
        String lsViaje="0";
        String sql="SELECT idvigilante from vigilantes where idvigilante = '"+dni+"'";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }



    public String qrepo01(){
        String lsViaje="0";
        String sql="select placa, cantidad from repo01 ";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public ArrayList<HashMap<String, String>> qrepo02(){

        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<HashMap<String, String>> userList = new ArrayList<>();

        String query = "select placa, cantidad from repo01 ";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("placa",cursor.getString(cursor.getColumnIndex("placa")));
            user.put("cantidad",cursor.getString(cursor.getColumnIndex("cantidad")));
            userList.add(user);
        }
        return  userList;
    }

    public String totalSync(){
        String lsViaje="0";
        String sql="select count(1) from names WHERE length(dni) = 8 and status = 1 and strftime('%Y-%m-%d',fecharegistro) = strftime('%Y-%m-%d','now')";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

}

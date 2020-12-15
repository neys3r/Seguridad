package com.agricolalaventa.seguridad.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;

import java.util.ArrayList;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "security.db";
    public static final String VIEW_REPO01 = "repo01";

    //database version
    private static final int DB_VERSION = 1;



    public DatabaseHelper(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DB_VERSION);
    }

    //creating the database
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql2 = "CREATE VIEW " + VIEW_REPO01 + " as SELECT idreferencia, count(1) as cantidad FROM checkinout group by idreferencia ";
        db.execSQL(sql2);

        createContactos(db);
        createPdas(db);
        createVigilantes(db);
        createAsistencia(db);
    }


    // Métodos SQLITE
    public void createAsistencia(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_ASISTENCIA+"("+DBContract.Checkinout.ID+" INTEGER PRIMARY KEY,"+
                ""+DBContract.Checkinout.STATUS+" VARCHAR,"+DBContract.Checkinout.DNI+" VARCHAR,"+DBContract.Checkinout.IDREFERENCIA+" VARCHAR,"+DBContract.Checkinout.IDSUCURSAL+" VARCHAR,"+
                ""+DBContract.Checkinout.IDPDA+" VARCHAR,"+DBContract.Checkinout.FECHA+" VARCHAR,"+DBContract.Checkinout.PEDATEADOR+" VARCHAR,"+
                ""+DBContract.Checkinout.IDTRASLADO+" VARCHAR,"+DBContract.Checkinout.IDTIPO+" VARCHAR);");
    }

    public void createContactos(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_CONTACTOS+"("+DBContract.Contactos.ID+" INTEGER PRIMARY KEY,"+DBContract.Contactos.NOMBRE+" TEXT," +
                ""+DBContract.Contactos.APELLIDOS+" TEXT,"+DBContract.Contactos.TELEFONO+" TEXT);");
    }

    public void createPdas(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_PDAS+"("+DBContract.Pdas.IDPDA+" TEXT PRIMARY KEY,"+DBContract.Pdas.NOMBRE+" TEXT," +
                ""+DBContract.Pdas.IDSUCURSAL+" TEXT,"+DBContract.Pdas.DESCSUCURSAL+" TEXT);");
    }

    public void createVigilantes(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE "+DBContract.TABLE_VIGILANTES+"("+DBContract.Vigilantes.IDVIGILANTE+" TEXT PRIMARY KEY,"+DBContract.Vigilantes.NOMBRES+" TEXT," +
                ""+DBContract.Vigilantes.ESTADO+" TEXT,"+DBContract.Vigilantes.IDAREA+" TEXT);");
    }

    public void savePdas(String idpda,String nombre,String idsucursal,String descsucursal, SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Pdas.IDPDA,idpda);
        contentValues.put(DBContract.Pdas.NOMBRE,nombre);
        contentValues.put(DBContract.Pdas.IDSUCURSAL,idsucursal);
        contentValues.put(DBContract.Pdas.DESCSUCURSAL,descsucursal);

        db.insert(DBContract.TABLE_PDAS,null, contentValues);
    }

    public void saveVigilantes(String idvigilante,String nombres,String estado,String idarea, SQLiteDatabase db)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.Vigilantes.IDVIGILANTE,idvigilante);
        contentValues.put(DBContract.Vigilantes.NOMBRES,nombres);
        contentValues.put(DBContract.Vigilantes.ESTADO,estado);
        contentValues.put(DBContract.Vigilantes.IDAREA,idarea);

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


    public boolean addName( int status, String dni, String idreferencia, String idsucursal, String idpda, String fecha, String pedateador, String idtraslado, String idtipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put("status", status);
        contentValues.put("dni", dni);
        contentValues.put("idreferencia", idreferencia);
        contentValues.put("idsucursal", idsucursal);
        contentValues.put("idpda", idpda);
        contentValues.put("fecha", fecha);
        contentValues.put("pedateador", pedateador);
        contentValues.put("idtraslado", idtraslado);
        contentValues.put("idtipo", idtipo);

        db.insert("checkinout", null, contentValues);
        db.close();
        return true;
    }


    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("status", status);
        db.update("checkinout", contentValues, "id" + "=" + id, null);
        db.close();
        return true;
    }


    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, status, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo FROM " + "checkinout" + " WHERE LENGTH(dni) = 8 and strftime('%Y-%m-%d',fecha) = date('now','localtime') order by id desc";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT id, status, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo FROM " + "checkinout" + " WHERE LENGTH(dni) = 8 and strftime('%Y-%m-%d',fecha) = date('now','localtime') and  " + "status" + " = 0 ;";
        //String sql = "SELECT * FROM " + TABLE_NAME + " WHERE  " + COLUMN_STATUS + " = 0 ;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


    public String totalNoSync(){
        String lsViaje="0";
        String sql="select count(1) from checkinout WHERE length(dni) = 8 and status = 0 and strftime('%Y-%m-%d',fecha) = date('now','localtime')";
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
        String sql="SELECT idsucursal FROM pdas where idpda = '"+ seriePda() +"';";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public String seriePda(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        }else{
            return Build.SERIAL;
        }
    }

    public String miDescSucursal(){
        String lsViaje="0";
        String sql="SELECT descsucursal FROM pdas where idpda = '"+ Build.SERIAL +"';";
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

    public String areaVigilante(String dni){
        String lsViaje="0";
        String sql="SELECT idarea from vigilantes where idvigilante = '"+dni+"'";
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

        String query = "select idreferencia, cantidad from repo01 ";
        Cursor cursor = db.rawQuery(query,null);
        while (cursor.moveToNext()){
            HashMap<String,String> user = new HashMap<>();
            user.put("idreferencia",cursor.getString(cursor.getColumnIndex("idreferencia")));
            user.put("cantidad",cursor.getString(cursor.getColumnIndex("cantidad")));
            userList.add(user);
        }
        return  userList;
    }

    public String totalSync(){
        String lsViaje="0";
        String sql="select count(1) from checkinout WHERE length(dni) = 8 and status = 1 and strftime('%Y-%m-%d',fecha) = date('now','localtime')";
        //String sql="select count(1) from checkinout WHERE length(dni) = 8 and status = 1 and strftime('%Y-%m-%d',fecha) = strftime('%Y-%m-%d','now')";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public String totalIngresos(){
        String lsViaje="0";
        String sql="select count(1) from checkinout WHERE length(dni) = 8 and status = 1 and strftime('%Y-%m-%d',fecha) = date('now','localtime') and idtipo = '0' ";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public String totalSalidas(){
        String lsViaje="0";
        String sql="select count(1) from checkinout WHERE length(dni) = 8 and status = 1 and strftime('%Y-%m-%d',fecha) = date('now','localtime') and idtipo = '1' ";
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

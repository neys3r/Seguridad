package com.agricolalaventa.seguridad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Constants for Database name, table name, and column names
    public static final String DB_NAME = "security.db";
    public static final String TABLE_NAME = "names";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STATUS = "status";
    public static final String COLUMN_DNI = "dni";
    public static final String COLUMN_PLACA = "placa";

    //database version
    private static final int DB_VERSION = 1;

    //Constructor
    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
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
                " TINYINT);";
        db.execSQL(sql);
    }

    //upgrading the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS Persons";
        db.execSQL(sql);
        onCreate(db);
    }

    /*
     * This method is taking two arguments
     * first one is the name that is to be saved
     * second one is the status
     * 0 means the name is synced with the server
     * 1 means the name is not synced with the server
     * */
    public boolean addName(String name, int status, String dni, String placa) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_NAME, name);
        contentValues.put(COLUMN_STATUS, status);
        contentValues.put(COLUMN_DNI, dni);
        contentValues.put(COLUMN_PLACA, placa);


        db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    /*
     * This method taking two arguments
     * first one is the id of the name for which
     * we have to update the sync status
     * and the second one is the status that will be changed
     * */
    public boolean updateNameStatus(int id, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_STATUS, status);
        db.update(TABLE_NAME, contentValues, COLUMN_ID + "=" + id, null);
        db.close();
        return true;
    }

    /*
     * this method will give us all the name stored in sqlite
     * */
    public Cursor getNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        //String sql = "SELECT id, Substr(name,32,8) as name  FROM names WHERE LENGTH(NAME) = 39 order by id";
        String sql = "SELECT id, Substr(name,45,8) as name, status, dni, placa FROM " + TABLE_NAME + " WHERE LENGTH(NAME) > 30 and substr( name, 1, 10 ) = strftime('%Y-%m-%d','now') order by id";
        //String sql = "SELECT * FROM " + TABLE_NAME + " WHERE LENGTH(NAME) = 39 ORDER BY " + COLUMN_ID + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }

    /*
     * this method is for getting all the unsynced name
     * so that we can sync it with database
     * */
    public Cursor getUnsyncedNames() {
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM " + TABLE_NAME + " WHERE LENGTH(NAME) > 30 and substr( name, 1, 10 ) = strftime('%Y-%m-%d','now') and  " + COLUMN_STATUS + " = 0 ;";
        Cursor c = db.rawQuery(sql, null);
        return c;
    }


        public String totalRegistro(){
            String lsViaje="0";
            String sql="select count(1) from names WHERE LENGTH(NAME) > 30 and substr( name, 1, 10 ) = strftime('%Y-%m-%d','now')";
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
        String sql="select count(1) from names WHERE LENGTH(NAME) > 30 and status = 0 and substr( name, 1, 10 ) = strftime('%Y-%m-%d','now')";
        SQLiteDatabase db= getReadableDatabase();
        Cursor cursor= db.rawQuery(sql, null);
        if(cursor.moveToFirst()){
            do {
                lsViaje = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        return lsViaje;
    }

    public String totalSync(){
        String lsViaje="0";
        String sql="select count(1) from names WHERE LENGTH(NAME) > 30 and status = 1 and substr( name, 1, 10 ) = strftime('%Y-%m-%d','now')";
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

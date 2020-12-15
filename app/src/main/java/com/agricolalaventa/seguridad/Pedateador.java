package com.agricolalaventa.seguridad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pedateador extends AppCompatActivity {

    private TextView tvSincronizacion, tvTitPedateador, tvCodVigilante;
    private RadioButton radio_ingreso, radio_salida;
    private RadioGroup radio_tipo;
    private EditText edtVigilante;
    private Button btnGuardarVigilante, btnVerificarVigilante;
    private String codPDA, mensaje, tipoIS, descIS, idvigilante, idArea;
    private DatabaseHelper db;
    int longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedateador);

        //Relacion con elementos del Layout

        tvTitPedateador = (TextView)findViewById(R.id.tvTitPedateador);
        tvCodVigilante = (TextView)findViewById(R.id.tvCodVigilante);
        edtVigilante = (EditText)findViewById(R.id.edtVigilante);
        btnGuardarVigilante = (Button)findViewById(R.id.btnGuardarVigilante);
        btnVerificarVigilante = (Button)findViewById(R.id.btnVerificarVigilante);
        tvSincronizacion = (TextView)findViewById(R.id.tvSincronizacion);

        //Radio Button
        //radio_tipo = (RadioGroup) findViewById(R.id.radio_tipo);
       // radio_ingreso = (RadioButton)findViewById(R.id.radio_ingreso);
       // radio_salida = (RadioButton)findViewById(R.id.radio_salida);

/*
        // Jalar Info Sharedpreference
        /*SharedPreferences preferences = getSharedPreferences("datosPedateador", Context.MODE_PRIVATE);
        edtVigilante.setText(preferences.getString("dniPedateador", ""));

        // Declarar Info Sharedpreference
        SharedPreferences preferencias = getSharedPreferences("datosPedateador", Context.MODE_PRIVATE);
        SharedPreferences.Editor Obj_editor = preferencias.edit();
        Obj_editor.putString("dniPedateador", edtVigilante.getText().toString());
        Obj_editor.commit();

*/
        // Impedir Ingreso manual de DNI
        edtVigilante.setInputType(InputType.TYPE_NULL);
        cargarPreferenciasSync();

        // Sincronización

        //initializing views and objects
        db = new DatabaseHelper(this);

        // Ejecutar SharedPreferences



        //Obtener información de RadioButton

        //Acciones Boton Verificar
        btnVerificarVigilante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valor01 = edtVigilante.getText().toString();
                String valor02 = db.existeVigilante(valor01);

                //Log.i("prueba","prueba1:|"+prueba3+"| prueba2:|"+prueba4+"|");

                if (valor01.equalsIgnoreCase(valor02)){
                    Toast.makeText(getApplicationContext(), "Verificado", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),"No verificado|"+valor01+"|"+valor02,Toast.LENGTH_LONG).show();
                }
            }
        });

        // Acciones del Boton Guardar Vigilante
        btnGuardarVigilante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                codPDA = edtVigilante.getText().toString();
                longitud = edtVigilante.getText().toString().length();
                idvigilante = db.existeVigilante(codPDA);

                // SharedPreferences
                /*SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
                SharedPreferences.Editor Obj_editor = preferencias.edit();
                Obj_editor.putString("dni", edtVigilante.getText().toString());
                Obj_editor.commit();*/

                // Verificar check
/*
                if (radio_ingreso.isChecked() == true){
                    tipoIS = "0";
                    descIS = "Ingreso";
                } else if (radio_salida.isChecked() == true){
                    tipoIS = "1";
                    descIS = "Salida";
                }else{
                    tipoIS = "9";
                    descIS = "Otros";
                }
*/


                if(codPDA.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Ingrese un DNI",Toast.LENGTH_LONG).show();

                }
                else {
                    if ( longitud != 8) {
                        Toast.makeText(getApplicationContext(),"El DNI debe tener 8 dígitos, ",Toast.LENGTH_LONG).show();
                    }else if(codPDA.equalsIgnoreCase(idvigilante)){

                        mensaje = "DNI " + idvigilante + " grabado";

                        //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                        Intent i =new Intent(getApplicationContext(), MainSeguridad.class);
                        i.putExtra("codPDA", codPDA);
                        startActivity(i);
                    }
                    else {

                        Toast.makeText(getApplicationContext(),"Vigilante no reconicido",Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        // Acción del Enter EdtVigilante

        edtVigilante.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if((keyEvent.getAction()==KeyEvent.ACTION_DOWN ) && (i==KeyEvent.KEYCODE_ENTER)  ){

                    codPDA = edtVigilante.getText().toString();
                    longitud = edtVigilante.getText().toString().length();
                    idvigilante = db.existeVigilante(codPDA);

                    guardarPreferencias();
                    // Verificar check
/*
                    if (radio_ingreso.isChecked() == true){
                        tipoIS = "0";
                        descIS = "Ingreso";
                    } else if (radio_salida.isChecked() == true){
                        tipoIS = "1";
                        descIS = "Salida";
                    }else{
                        tipoIS = "9";
                        descIS = "Otros";
                    }*/

                    if(codPDA.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Ingrese un DNI",Toast.LENGTH_LONG).show();

                    }
                    else {
                        if ( longitud != 8) {
                            Toast.makeText(getApplicationContext(),"El DNI debe tener 8 dígitos, ",Toast.LENGTH_LONG).show();
                        }else if(db.miIdSucursal().length()!=3){

                            Toast.makeText(getApplicationContext(),"PDA no Activo, Sincronizar!!!!" +
                                    "",Toast.LENGTH_LONG).show();

                        }else if(db.areaVigilante(idvigilante).equalsIgnoreCase("rrhh")){

                            Toast.makeText(getApplicationContext(),"Ingresando a Modo RRHH |",Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                            Intent ii =new Intent(getApplicationContext(), Main_RRHH.class);
                            //ii.putExtra("codPDA", codPDA);
                            startActivity(ii);

                        }else if(db.areaVigilante(idvigilante).equalsIgnoreCase("sst")){

                            Toast.makeText(getApplicationContext(),"Ingresando a Modo SST",Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                            Intent ii =new Intent(getApplicationContext(), Main_SST.class);
                            //ii.putExtra("codPDA", codPDA);
                            startActivity(ii);

                        }else if(db.areaVigilante(idvigilante).equalsIgnoreCase("seguridad")){

                            Toast.makeText(getApplicationContext(),"Ingresando a Modo Seguridad",Toast.LENGTH_LONG).show();
                            //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();
                            Intent ii =new Intent(getApplicationContext(), MainSeguridad.class);
                            //ii.putExtra("codPDA", codPDA);
                            startActivity(ii);
                        }
                        else {

                            Toast.makeText(getApplicationContext(),"Usuario no reconicido",Toast.LENGTH_LONG).show();
                        }
                    }
                    return true;
                }
                else {
                    return false;
                }
            }
        });



    }

    public static boolean checkNetworkConnection(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pedateador, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_salir_login) {
            finish();
        }
        if(id == R.id.sincronizar)
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sincronizando");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            new Sincronizar(this,progressDialog).execute();
            guardarPreferenciaSincronizacion();
            guardarPreferencias();
        }

        return super.onOptionsItemSelected(item);
    }

    public static void sincronizarPdas(Context context)
    {
        if(checkNetworkConnection(context))
        {
            final DatabaseHelper database = new DatabaseHelper(context);
            final SQLiteDatabase db = database.getWritableDatabase();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://wslaventa.agricolalaventa.com/asistencia/getPdas.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONArray array = new JSONArray(response);
                        for(int i = 0; i<array.length(); i++)
                        {
                            JSONObject object = array.getJSONObject(i);
                            database.savePdas(object.getString("id"),object.getString("nombre"),object.getString("idsucursal"),object.getString("descsucursal"),db);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error",""+error);
                }
            });
            MySingleton.getInstance(context).addToRequestQue(stringRequest);
        }
    }

    public static void sincronizarPdasPost(Context context)
    {
        if(checkNetworkConnection(context))
        {
            final DatabaseHelper database = new DatabaseHelper(context);
            final SQLiteDatabase db = database.getWritableDatabase();
            String urlApiLogin = "http://wslaventa.agricolalaventa.com/asistencia/pda.php";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlApiLogin, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONArray array = new JSONArray(response);
                        for(int i = 0; i<array.length(); i++)
                        {
                            JSONObject object = array.getJSONObject(i);
                            database.savePdas(object.getString("id"),object.getString("nombre"),object.getString("idsucursal"),object.getString("descsucursal"),db);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error",""+error);
                }
            });
            MySingleton.getInstance(context).addToRequestQue(stringRequest);
        }
    }

    public static void sincronizarVigilantes(Context context)
    {
        if(checkNetworkConnection(context))
        {
            final DatabaseHelper database = new DatabaseHelper(context);
            final SQLiteDatabase db = database.getWritableDatabase();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "http://wslaventa.agricolalaventa.com/asistencia/getVigilantes.php", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONArray array = new JSONArray(response);
                        for(int i = 0; i<array.length(); i++)
                        {
                            JSONObject object = array.getJSONObject(i);
                            database.saveVigilantes(object.getString("idvigilante"),object.getString("nombres"),object.getString("estado"), object.getString("idarea"),db);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error",""+error);
                }
            });
            MySingleton.getInstance(context).addToRequestQue(stringRequest);
        }
    }

    public static class Sincronizar extends AsyncTask<Void,Void,Void>
    {
        Context context;
        ProgressDialog progressDialog;
        public Sincronizar(Context context,ProgressDialog progressDialog)
        {
            this.progressDialog = progressDialog;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //sincronizarContactos(context);
            sincronizarPdas(context);
            sincronizarVigilantes(context);
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.hide();
            Toast.makeText(context, "Sincronizado Correctamente", Toast.LENGTH_LONG).show();

            //readContactos(context);
        }
    }

    // Obtener N° Serie PDA
    /*private String seriePda(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        }else{
            return Build.SERIAL;
        }
    }*/

    private void guardarPreferencias(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosPedateador", Context.MODE_PRIVATE);
        String idPedateador = edtVigilante.getText().toString();
        String idAreaPedateador = db.areaVigilante(idPedateador);
        String idPDA = db.seriePda();

        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("idpetateador", idPedateador);
        editor.putString("idpda", idPDA);
        editor.putString("idAreaPedateador", idAreaPedateador);
        editor.commit();
    }

    private void cargarPreferenciasSync(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosSincronizacion", Context.MODE_PRIVATE);
        String fechasync = preferencias.getString("fechasync", "PDA no Sincronizado !!!");
        tvSincronizacion.setText(fechasync);
    }


    public void guardarPreferenciaSincronizacion(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosSincronizacion", Context.MODE_PRIVATE);
        String fechasync = fechaASync();
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("fechasync", fechasync);
        tvSincronizacion.setText(fechasync);
        editor.commit();
    }

    private String fechaASync(){
        String fecha = "";
        fecha = "Última Sincronización: "+(DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString());
        return fecha;
    }


}
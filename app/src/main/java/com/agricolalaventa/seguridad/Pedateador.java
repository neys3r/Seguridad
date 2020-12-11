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

    private TextView tvTitPedateador, tvCodVigilante;
    private RadioButton radio_ingreso, radio_salida;
    private RadioGroup radio_tipo;
    private EditText edtVigilante;
    private Button btnGuardarVigilante;
    public String codPDA, mensaje, tipoIS, descIS;
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

        //Radio Button
        radio_tipo = (RadioGroup) findViewById(R.id.radio_tipo);
        radio_ingreso = (RadioButton)findViewById(R.id.radio_ingreso);
        radio_salida = (RadioButton)findViewById(R.id.radio_salida);


        // Sharepreference
        SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        edtVigilante.setText(preferences.getString("dni", ""));

        // Impedir Ingreso manual de DNI
        edtVigilante.setInputType(InputType.TYPE_NULL);

        // Sincronización




        //Obtener información de RadioButton


        // Acciones del Boton Guardar Vigilante
        btnGuardarVigilante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                codPDA = edtVigilante.getText().toString();
                longitud = edtVigilante.getText().toString().length();

                // SharedPreferences
                SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
                SharedPreferences.Editor Obj_editor = preferencias.edit();
                Obj_editor.putString("dni", edtVigilante.getText().toString());
                Obj_editor.commit();

                // Verificar check

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

                if(codPDA.isEmpty()){
                    Toast.makeText(getApplicationContext(),descIS+tipoIS+"Ingrese un DNI",Toast.LENGTH_LONG).show();

                }else {
                    if ( longitud != 8) {
                        Toast.makeText(getApplicationContext(),"El DNI debe tener 8 dígitos, ",Toast.LENGTH_LONG).show();
                    }else if(tipoIS == "9"){
                        Toast.makeText(getApplicationContext(),"Ingresar Ingreso / Salida, ",Toast.LENGTH_LONG).show();
                    }
                    else {

                        mensaje = "DNI " + codPDA + " grabado";

                        Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();


                        Intent i =new Intent(getApplicationContext(),MainInicio.class);
                        i.putExtra("codPDA", codPDA);
                        i.putExtra("tipoIS", tipoIS);
                        i.putExtra("descIS", descIS);
                        startActivity(i);
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

                    // Verificar check

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

                    if(codPDA.isEmpty()){
                        Toast.makeText(getApplicationContext(),"Ingrese un DNI",Toast.LENGTH_LONG).show();

                    }else {
                        if ( longitud != 8) {
                            Toast.makeText(getApplicationContext(),"El DNI debe tener 8 dígitos, ",Toast.LENGTH_LONG).show();
                        }else if(tipoIS == "9"){
                            Toast.makeText(getApplicationContext(),"Ingresar Ingreso / Salida, ",Toast.LENGTH_LONG).show();
                        }
                        else {

                            mensaje = "DNI " + codPDA + " grabado";

                            Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                            Intent ii =new Intent(getApplicationContext(),MainInicio.class);
                            ii.putExtra("codPDA", codPDA);
                            ii.putExtra("tipoIS", tipoIS);
                            ii.putExtra("descIS", descIS);
                            startActivity(ii);
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
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo!=null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.sincronizar)
        {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sincronizando");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            new Sincronizar(this,progressDialog).execute();

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
                            database.savePdas(object.getString("id"),object.getString("nombre"),object.getString("idsucursal"),db);
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
                            database.saveVigilantes(object.getString("idvigilante"),object.getString("nombres"),object.getString("estado"),db);
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
                Thread.sleep(5000);
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


}
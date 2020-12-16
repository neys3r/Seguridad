package com.agricolalaventa.seguridad.Modelos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.agricolalaventa.seguridad.Modelos.Main_Asistencia;
import com.agricolalaventa.seguridad.NetworkStateChecker;
import com.agricolalaventa.seguridad.R;
import com.agricolalaventa.seguridad.VolleySingleton;
import com.agricolalaventa.seguridad.an_reporteplaca;
import com.agricolalaventa.seguridad.db.DBContract;
import com.agricolalaventa.seguridad.db.DatabaseHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Main_Seguridad extends AppCompatActivity {

    private TextView tvTitInicio, tvFechaInicio, tvTotalIngresos, tvTotalSalidas, tvTotalSync;
    private EditText edtSucursal, edtPlaca;
    private Button btnInicio;
    private String placa, dscSucursal, idSucursal, formaTraslado, mensaje;
    private int longitud;
    //private Spinner spinner;
    private RadioButton  radio_bus, radio_moto, radio_vehiculo, radio_peatonal;
    private RadioGroup radio_opciones;
    private TextView tvTipoIS, codPdaAcceso;
    private Switch swOpcionTipoRegistro;
    private DatabaseHelper db;
    private Context context;
    //private String codPDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguridad);

        //RED
        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        tvTitInicio = (TextView)findViewById(R.id.tvTitInicio);
        //tvFechaInicio = (TextView)findViewById(R.id.tvFechaInicio);

        //edtSucursal = (EditText) findViewById(R.id.edtSucursal);
        edtPlaca = (EditText)findViewById(R.id.edtPlaca);
        btnInicio = (Button)findViewById(R.id.btnInicio);
        //spinner = (Spinner)findViewById(R.id.spinner);

        radio_opciones = (RadioGroup) findViewById(R.id.radio_opciones);
        radio_bus = (RadioButton)findViewById(R.id.radio_bus);
        radio_moto = (RadioButton)findViewById(R.id.radio_moto);
        radio_vehiculo = (RadioButton)findViewById(R.id.radio_vehiculo);
        radio_peatonal = (RadioButton)findViewById(R.id.radio_peatonal);
        swOpcionTipoRegistro = (Switch)findViewById(R.id.swOpcionTipoRegistro);
        tvTipoIS = (TextView) findViewById(R.id.tvTipoIS);
        tvTotalIngresos = (TextView) findViewById(R.id.tvTotalIngresos);
        tvTotalSalidas = (TextView) findViewById(R.id.tvTotalSalidas);
        tvTotalSync = (TextView) findViewById(R.id.tvTotalSync);

        //initializing views and objects
        db = new DatabaseHelper(this);

        boolean estado = radio_moto.isChecked();

        String [] opciones = {"Seleccionar Sucursal", "Pinilla", "Mayorazgo", "Don Jorge", "San Judas"};
        ArrayAdapter <String> adapterS = new ArrayAdapter<String>(this,R.layout.spinner_item_sucursal, opciones);
        //spinner.setAdapter(adapterS);

        //tvTitInicio.setText("Registro de "+descIS);

        // Cambiar Información TipoIngreso/Salida
         infoSwithc();

        // Mostrar Totales:
        tvTotalIngresos.setText(db.totalIngresos());
        tvTotalSalidas.setText(db.totalSalidas());
        tvTotalSync.setText(db.totalSync()+"/"+db.totalDiario());


        edtPlaca.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        // Validaciones Radio

        radio_opciones.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.radio_bus){
                    Toast.makeText(getApplicationContext(),"Bus",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.VISIBLE);
                    edtPlaca.setHint("Placa Bus");
                    //placa = edtPlaca.getText().toString();
                }else if (checkedId == R.id.radio_moto){
                    Toast.makeText(getApplicationContext(),"Moto",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.INVISIBLE);
                    placa = "MMMMMM";
                }else if (checkedId == R.id.radio_vehiculo){
                    Toast.makeText(getApplicationContext(),"Vehiculo",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.VISIBLE);
                    //edtPlaca.setHint("Placa Vehiculo");
                    placa = edtPlaca.getText().toString();
                }else if (checkedId == R.id.radio_peatonal){
                    Toast.makeText(getApplicationContext(),"Peatonal",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.INVISIBLE);
                    placa = "PPPPPP";
                }
            }

        });

        // Fin Validación Radio

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //placaBus = edtPlaca.getText().toString();
                //idSucursal = edtSucursal.getText().toString();
                longitud = edtPlaca.getText().toString().length();


                // Seleccionar tipo de ingreso

                if (radio_bus.isChecked() == true){
                    formaTraslado = "1";
                    placa = edtPlaca.getText().toString();
                } else if (radio_moto.isChecked() == true){
                    formaTraslado = "2";
                } else if (radio_vehiculo.isChecked() == true){
                    formaTraslado = "3";
                    placa = edtPlaca.getText().toString();
                } else if (radio_peatonal.isChecked() == true){
                    formaTraslado = "4";
                }else{
                    formaTraslado = "9";
                }

                    //Toast.makeText(getApplicationContext(),"Seleccionar válida:T-"+tipoIngreso,Toast.LENGTH_LONG).show();
                    switch (formaTraslado){
                        case  "1":

                            if (longitud != 6 ){
                                Toast.makeText(getApplicationContext(),"La placa del bus de tener 6 dígitos",Toast.LENGTH_LONG).show();
                            } else {
                                //Muestra la lista de BUS
                                //mensaje = "Placa " + placaBus + "en Sede "+ idSucursal +" grabado ";
                                //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                                Intent a1 =new Intent(getApplicationContext(), Main_Asistencia.class);
                                //a1.putExtra("placaBus", placaBus);
                                //a1.putExtra("idSucursal", idSucursal);
                                //a1.putExtra("dscSucursal", dscSucursal);
                                //a1.putExtra("tipoIngreso", tipoIngreso);
                                //a1.putExtra("codPDA", codPDA);
                                startActivity(a1);
                            }

                            break;
                        case  "2":
                            //Muestra la lista de MOTO

                                //Muestra la lista de BUS
                                mensaje = "Moto en Sede "+ idSucursal +" grabado ";
                                //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                                Intent a2 =new Intent(getApplicationContext(), Main_Asistencia.class);
                                //a2.putExtra("placaBus", "MMMMMM");
                                //a2.putExtra("idSucursal", idSucursal);
                                //a2.putExtra("dscSucursal", dscSucursal);
                                //a2.putExtra("tipoIngreso", tipoIngreso);
                                //a2.putExtra("codPDA", codPDA);
                                startActivity(a2);
                                //finish();

                            break;
                        case  "3":
                            //Muestra la lista de Frutas

                            if (longitud != 6 ){
                                Toast.makeText(getApplicationContext(),"La placa del vehículo de tener 6 dígitos",Toast.LENGTH_LONG).show();
                            } else {
                                //Muestra la lista de VEHICULO
                                //mensaje = "Placa " + placaBus + "en Sede "+ idSucursal +" grabado ";
                                //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                                Intent a3 =new Intent(getApplicationContext(), Main_Asistencia.class);
                                //a3.putExtra("placaBus", placaBus);
                                //a3.putExtra("idSucursal", idSucursal);
                                //a3.putExtra("dscSucursal", dscSucursal);
                                //a3.putExtra("tipoIngreso", tipoIngreso);
                                //a3.putExtra("codPDA", codPDA);
                                startActivity(a3);
                            }
                            break;
                        case  "4":
                            //Muestra la lista de Frutas

                            //mensaje = "Lista de Personal  " + "en Sede "+ idSucursal +" grabado ";
                            //Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                            Intent a4 =new Intent(getApplicationContext(), Main_Asistencia.class);
                            //a4.putExtra("placaBus", "PPPPPP");
                            //a4.putExtra("idSucursal", idSucursal);
                            //a4.putExtra("dscSucursal", dscSucursal);
                            //a4.putExtra("tipoIngreso", tipoIngreso);
                            //a4.putExtra("codPDA", codPDA);
                            startActivity(a4);

                            break;
                        default:
                            //Muestra mensaje: "Ingresa un valor valido"
                            Toast.makeText(getApplicationContext(),"Debes elegir un tipo de Ingreso válido",Toast.LENGTH_LONG).show();
                            break;
                    }

                guardarPreferenciaTraslado();


                //} -- eliminar

            }
        });




    }

    public void onClick(View view){
        if (view.getId()==R.id.swOpcionTipoRegistro){
            infoSwithc();

        }

    }


    // Guardar Nombres
    private void saveNameMA(final int id, final String dni, final String idreferencia, final String idsucursal, final String hostname, final String fecha, final String pedateador, final String idtraslado, final String idtipo ) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Main_Asistencia.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNameStatus(id, Main_Asistencia.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(Main_Asistencia.DATA_SAVED_BROADCAST));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni", dni);
                params.put("idreferencia", idreferencia);
                params.put("idsucursal", idsucursal);
                params.put("hostname", hostname);
                params.put("fecha", fecha);
                params.put("pedateador", pedateador);
                params.put("idtraslado", idtraslado);
                params.put("idtipo", idtipo);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

    // Menús

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_seguridad, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sync_seguridad) {

            syncOffline();
            //tvTotalSync.setText(db.totalSync()+"/"+db.totalDiario());
            //Toast.makeText(getApplicationContext(),"Prueba2", Toast.LENGTH_LONG).show();
            //Intent i =new Intent(getApplicationContext(),Main_Seguridad.class);
            //startActivity(i);

            //tvTotalSync.setText("Holas");
            tvTotalSync.setText(db.totalSync()+"/"+db.totalDiario());
            return true;

        }
        if(id == R.id.salir_seguridad)
        {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void infoSwithc(){
        if (swOpcionTipoRegistro.isChecked()){
            //Toast.makeText(getApplicationContext(),"Switch Salida", Toast.LENGTH_LONG).show();

            SharedPreferences preferencias = getSharedPreferences
                    ("datosTipoIS", Context.MODE_PRIVATE);
            String idTipoIS = "1";
            String descTipoIS = "Salida";
            tvTipoIS.setText(descTipoIS);

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("idTipoIS", idTipoIS);
            editor.putString("descTipoIS", descTipoIS);
            editor.commit();

        }else{
            //Toast.makeText(getApplicationContext(),"Switch Ingreso", Toast.LENGTH_LONG).show();
            SharedPreferences preferencias = getSharedPreferences
                    ("datosTipoIS", Context.MODE_PRIVATE);
            String idTipoIS = "0";
            String descTipoIS = "Ingreso";
            tvTipoIS.setText(descTipoIS);

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("idTipoIS", idTipoIS);
            editor.putString("descTipoIS", descTipoIS);
            editor.commit();
        }
    }

    public void guardarPreferenciaTraslado(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosTraslado", Context.MODE_PRIVATE);
        String idreferencia = placa;
        String idtraslado = formaTraslado;
        SharedPreferences.Editor editor = preferencias.edit();
        editor.putString("idreferencia", idreferencia);
        editor.putString("idtraslado", idtraslado);
        editor.commit();
    }

    private void syncOffline(){
        Cursor cursor = db.getUnsyncedNames();
        if (cursor.moveToFirst()) {
            do {
                //calling the method to save the unsynced name to MySQL
                saveNameMA(
                        cursor.getInt(cursor.getColumnIndex(DBContract.Checkinout.ID)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.DNI)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDREFERENCIA)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDSUCURSAL)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDPDA)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.FECHA)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.PEDATEADOR)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDTRASLADO)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDTIPO))
                );
            } while (cursor.moveToNext());
        }
    }





}
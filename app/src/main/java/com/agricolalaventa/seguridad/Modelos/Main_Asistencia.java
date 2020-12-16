package com.agricolalaventa.seguridad.Modelos;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.agricolalaventa.seguridad.db.DBContract;
import com.agricolalaventa.seguridad.db.DatabaseHelper;
import com.agricolalaventa.seguridad.Name;
import com.agricolalaventa.seguridad.NameAdapter;
import com.agricolalaventa.seguridad.NetworkStateChecker;
import com.agricolalaventa.seguridad.R;
import com.agricolalaventa.seguridad.VolleySingleton;
import com.agricolalaventa.seguridad.an_reporteplaca;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main_Asistencia extends AppCompatActivity implements View.OnClickListener {

    private String placaBus, mensaje, idSucursal, dscSucursal, conteo, tipoIngreso;


    //public static final String URL_SAVE_NAME = "http://192.168.1.15/SqliteSync/saveName.php";
    public static final String URL_SAVE_NAME = "http://wslaventa.agricolalaventa.com/wstest.php";
    //public static final String URL_SAVE_NAME = "http://wslaventa.agricolalaventa.com/wscampo.php";

    //database helper object
    private Context context;
    private DatabaseHelper db;
    private NetworkStateChecker nt;

    //View objects
    private Button buttonSave, btnSync, btnRepo01;
    private EditText editTextName;
    private ListView listViewNames;
    private TextView tvFecha, tvBus, tvSucursal, tvConteoAsistencia, tvConteoPrueba, tvHostname, tvTitulo;
    private String codPDA, tipoIS, descIS, idVigilante;
    private LinearLayout linearRegistro;
    private ImageView ivLogoTipo;



    private String fechaLectura = (DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString());
    private String fechaLectura2 = (DateFormat.format("dd-MM-yyyy", System.currentTimeMillis()).toString());

    //List to store all the names
    private List<Name> names;

    //1 means data is synced and 0 means data is not synced
    public static final int NAME_SYNCED_WITH_SERVER = 1;
    public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "com.agricolalaventa.datasaved";

    //Broadcast receiver to know the sync status
    private BroadcastReceiver broadcastReceiver;

    //adapterobject for list view
    private NameAdapter nameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);

        registerReceiver(new NetworkStateChecker(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        //initializing views and objects
        db = new DatabaseHelper(this);
        names = new ArrayList<>();

        buttonSave = (Button) findViewById(R.id.buttonSave);
        btnSync = (Button) findViewById(R.id.btnSync);
        btnRepo01 = (Button) findViewById(R.id.btnRepo01);
        editTextName = (EditText) findViewById(R.id.editTextName);
        listViewNames = (ListView) findViewById(R.id.listViewNames);
        tvTitulo = (TextView) findViewById(R.id.tvTitulo);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        tvBus = (TextView) findViewById(R.id.tvBus);
        tvSucursal = (TextView) findViewById(R.id.tvSucursal);
        tvConteoAsistencia = (TextView) findViewById(R.id.tvConteoAsistencia);
        //tvConteoPrueba = (TextView) findViewById(R.id.tvConteoPrueba);
        tvHostname = (TextView) findViewById(R.id.tvHostname);
        linearRegistro = (LinearLayout)findViewById(R.id.activity_main);


        //Toast.makeText(this, db.miSucursal(), Toast.LENGTH_LONG).show();

        cargarTipoIS();

        tvFecha.setText(fechaLectura2);
        //tvHostname.setText(hostname()+ " | " +fechaActual());
        tvHostname.setText(hostname());

        //adding click listener to button
        buttonSave.setOnClickListener(this);



        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = db.getUnsyncedNames();
                //NetworkStateChecker netw = nt.saveName();
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
        });



        //idVigilante = db.existeVigilante(codPDA);

        //Cargar las Preferencias
        cargarPreferencias();

        btnRepo01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(), an_reporteplaca.class);
                startActivity(i);
            }
        });

        idSucursal = db.miIdSucursal();
        dscSucursal = db.miDescSucursal();

        // Recogemos placaBus y idSucursal de MainInicio
        //Bundle bundle = getIntent().getExtras();
        //idSucursal = bundle.getString("idSucursal");
        //dscSucursal = bundle.getString("dscSucursal");

        //placaBus = bundle.getString("placaBus");
        //tipoIngreso = bundle.getString("tipoIngreso");
        //codPDA = bundle.getString("codPDA");
        cargarPreferenciasTraslado();


        tvBus.setText(placaBus);
        tvSucursal.setText(dscSucursal);

       // tvConteo.setText(conteo);
        cargarTipoIS();
        cargarPreferenciasTraslado();
        tvConteoAsistencia.setText(db.totalISTraslado(tipoIS, placaBus));
        //tvConteoNS.setText(db.totalNoSync());

        // INICIO PRUEBAS ENTER

        editTextName.setOnClickListener(this);
        //editTextName.setInputType(InputType.TYPE_NULL);


        //calling the method to load all the stored names
        loadNames();

        //the broadcast receiver to update sync status
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //loading the names again
                loadNames();
            }
        };

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
    }

    //Prueba de Menus

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sinc_asistencia) {
            Cursor cursor = db.getUnsyncedNames();
            //NetworkStateChecker netw = nt.saveName();
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
            return true;

        }
        if(id == R.id.repo_asistencia)
        {
            Intent i =new Intent(getApplicationContext(),an_reporteplaca.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadNames() {
        names.clear();
        // Contador
        //tvConteoSN.setText(db.totalSync());
        //tvConteoNS.setText(db.totalDiario());
        // tvHostname.setText(hostname()+ " | " +fechaActual());
        //cargarTipoIS();
        //cargarPreferenciasTraslado();
        //tvConteoAsistencia.setText(db.totalISTraslado(tipoIS, placaBus));

        tvHostname.setText(hostname());
        cargarPreferenciasTraslado();
        cargarTipoIS();

        Cursor cursor = db.getNames(placaBus, tipoIS);
        Toast.makeText(getApplicationContext(), "placa: "+placaBus+tipoIS, Toast.LENGTH_LONG).show();
        if (cursor.moveToFirst()) {
            do {
                Name registro = new Name(

                        cursor.getInt(cursor.getColumnIndex(DBContract.Checkinout.STATUS)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.DNI)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDREFERENCIA)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDSUCURSAL)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDPDA)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.FECHA)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.PEDATEADOR)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDTRASLADO)),
                        cursor.getString(cursor.getColumnIndex(DBContract.Checkinout.IDTIPO))
                );
                names.add(registro);
            } while (cursor.moveToNext());
        }

        nameAdapter = new NameAdapter(this, R.layout.names, names);
        listViewNames.setAdapter(nameAdapter);
    }

    /*
     * this method will simply refresh the list
     * */
    private void refreshList() {
        nameAdapter.notifyDataSetChanged();
    }

    /*
     * this method is saving the name to ther server
     * */
    private void saveNameToServer() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Guardando Registro...");
        progressDialog.show();

        //FECHA
        //String fechaLectura = (DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString());

        //tvFecha.setText(fechaLectura);
        cargarTipoIS();

        //final String name = fechaActual()+"|"+idSucursal+"|"+placaBus+"|"+tipoIngreso+"|"+tipoIS+"|"+codPDA+"|"+editTextName.getText().toString().trim()+"|"+hostname();
        //final String name = editTextName.getText().toString().trim();
        final String dni = editTextName.getText().toString().trim();
        final String idreferencia = placaBus;
        final String idsucursal = idSucursal;
        final String idpda = hostname();
        final String fecha = fechaActual();
        final String pedateador = codPDA;
        final String idtraslado = tipoIngreso;
        final String idtipo = tipoIS;
        // final String name = tipoIngreso+"|"+fechaActual()+"|"+idSucursal+"|"+placaBus+"|"+editTextName.getText().toString().trim()+"|"+hostname();

        //cargarTipoIS();
        //cargarPreferenciasTraslado();
        //tvConteoAsistencia.setText(db.totalISTraslado(tipoIS, placaBus));
        // Contador
        //tvConteoSN.setText(db.totalSync());
        //tvConteoNS.setText(db.totalNoSync());
        //tvHostname.setText(hostname()+ " | " +fechaActual());
        tvHostname.setText(hostname());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //if there is a success
                                //storing the name to sqlite with status synced
                                saveNameToLocalStorage( NAME_SYNCED_WITH_SERVER, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                saveNameToLocalStorage( NAME_NOT_SYNCED_WITH_SERVER, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        //on error storing the name to sqlite with status unsynced
                        saveNameToLocalStorage( NAME_NOT_SYNCED_WITH_SERVER, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni", dni);
                params.put("idreferencia", idreferencia);
                params.put("idsucursal", idsucursal);
                params.put("idpda", idpda);
                params.put("fecha", fecha);
                params.put("pedateador", pedateador);
                params.put("idtraslado", idtraslado);
                params.put("idtipo", idtipo);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //saving the name to local storage
    private void saveNameToLocalStorage(int status, String dni, String idreferencia, String idsucursal, String idpda, String fecha, String pedateador, String idtraslado, String idtipo) {
        editTextName.setText("");

        db.addName(status, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo);
        Name n = new Name(status, dni, idreferencia, idsucursal, idpda, fecha, pedateador, idtraslado, idtipo);
        names.add(n);
        refreshList();
        // Contador
        //tvConteoSN.setText(db.totalSync());
        //tvConteoNS.setText(db.totalNoSync());
        //tvHostname.setText(hostname()+ " | " +fechaActual());
        cargarTipoIS();
        cargarPreferenciasTraslado();
        tvConteoAsistencia.setText(db.totalISTraslado(tipoIS, placaBus));
        tvHostname.setText(hostname());
    }

    // ATRIBUTOS DE DISPOSITIVO
    public String fabricante(){
        return Build.MANUFACTURER;
    }
    public String modelo(){
        return Build.MODEL;
    }

    private String hostname(){
        return db.seriePda();
    }

    private String macbluetooth(){
        //return UUID.randomUUID().toString();
        return db.seriePda();
    }

    private String seriePda(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Build.getSerial();
        }else{
            return Build.SERIAL;
        }
    }
    // ---------------------------

    @Override
    public void onClick(View view) {
        saveNameToServer();
        cargarTipoIS();
        cargarPreferenciasTraslado();
        tvConteoAsistencia.setText(db.totalISTraslado(tipoIS, placaBus));
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(),"PResione",Toast.LENGTH_LONG).show();
        Intent i =new Intent(getApplicationContext(), Main_Seguridad.class);
        startActivity(i);

    }

    private int mYear, mMonth, mDay, mHour, mMinute, mSec;

    public String fechaActual_test()
    {

        String fecha="", fecha2="";

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH) + 1;
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSec = c.get(Calendar.SECOND);
        //fecha=String.format("%02d",mDay) + "/" + String.format("%02d",mMonth) + "/" + String.format("%02d",mYear);
        fecha=String.format("%02d",mYear) + "-" + String.format("%02d",mMonth) +"-" +String.format("%02d",mDay) +" " +String.format("%02d",mHour) +":" +String.format("%02d",mMinute) +":" +String.format("%02d",mSec);
        //fecha2= String.format()
        return fecha;
    }
    public String fechaActual(){
        String fecha = "";
        fecha = (DateFormat.format("yyyy-MM-dd HH:mm:ss", System.currentTimeMillis()).toString());
        return fecha;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishAffinity();
    }

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

    // Otros métodos

    private void cargarPreferencias(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosPedateador", Context.MODE_PRIVATE);
        codPDA = preferencias.getString("idpetateador", "No existe Info");
        idVigilante = preferencias.getString("idpda", "No existe Info");
    }

    private void cargarTipoIS(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosTipoIS", Context.MODE_PRIVATE);
        tipoIS = preferencias.getString("idTipoIS", "No existe Info");
        descIS = preferencias.getString("descTipoIS", "No existe Info");
        tvTitulo.setText("Registro de "+descIS);
    }

    private void cargarPreferenciasTraslado(){
        SharedPreferences preferencias = getSharedPreferences
                ("datosTraslado", Context.MODE_PRIVATE);
        placaBus = preferencias.getString("idreferencia", "No existe Info");
        tipoIngreso = preferencias.getString("idtraslado", "No existe Info");
    }




}
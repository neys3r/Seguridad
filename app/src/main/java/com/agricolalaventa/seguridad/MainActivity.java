package com.agricolalaventa.seguridad;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

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

import static android.graphics.Color.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

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
    private TextView tvFecha, tvBus, tvSucursal, tvConteoSN, tvConteoNS, tvHostname, tvTitulo;
    private String codPDA, tipoIS, descIS;
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
        setContentView(R.layout.activity_main);

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
        tvConteoSN = (TextView) findViewById(R.id.tvConteoSN);
        tvConteoNS = (TextView) findViewById(R.id.tvConteoNS);
        tvHostname = (TextView) findViewById(R.id.tvHostname);
        linearRegistro = (LinearLayout)findViewById(R.id.activity_main);

        tvFecha.setText(fechaLectura2);
        //tvHostname.setText(hostname()+ " | " +fechaActual());
        tvHostname.setText(codPDA);

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
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DNI)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PLACA)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDSUCURSAL)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HOSTNAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHAREGISTRO)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PEDATEADOR)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDTRASLADO)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDTIPO))
                        );
                    } while (cursor.moveToNext());
                }
            }
        });


        btnRepo01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),an_reporteplaca.class);
                startActivity(i);
            }
        });

        // Prueba de Enter en PDA
        /*
        editTextName.setOnKeyListener(new View.OnKeyListener(){
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ( keyCode==KeyEvent.KEYCODE_ENTER))  {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            saveNameToServer();
                            editTextName.forceLayout();
                            editTextName.setText("");
                            editTextName.forceLayout();
                            Log.i("edtDNI", "enter pressed");
                            return true;
                        default:
                            break;
                    }
                    return true;
                }
                return false;
            }
        });

*/


        // Recogemos placaBus y idSucursal de MainInicio
        Bundle bundle = getIntent().getExtras();
        placaBus = bundle.getString("placaBus");
        idSucursal = bundle.getString("idSucursal");
        dscSucursal = bundle.getString("dscSucursal");
        tipoIngreso = bundle.getString("tipoIngreso");
        codPDA = bundle.getString("codPDA");
        tipoIS = bundle.getString("tipoIS");
        descIS = bundle.getString("descIS");


        tvBus.setText(placaBus);
        tvSucursal.setText(dscSucursal);
        tvTitulo.setText("Registro de "+descIS);
       // tvConteo.setText(conteo);

        tvConteoSN.setText(db.totalSync());
        tvConteoNS.setText(db.totalNoSync());

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

    private void loadNames() {
        names.clear();
        // Contador
        tvConteoSN.setText(db.totalSync());
        tvConteoNS.setText(db.totalNoSync());
        // tvHostname.setText(hostname()+ " | " +fechaActual());
        tvHostname.setText(codPDA);

        Cursor cursor = db.getNames();
        if (cursor.moveToFirst()) {
            do {
                Name name = new Name(
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_STATUS)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DNI)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PLACA)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDSUCURSAL)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HOSTNAME)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_FECHAREGISTRO)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PEDATEADOR)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDTRASLADO)),
                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_IDTIPO))
                );
                names.add(name);
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

        //final String name = fechaActual()+"|"+idSucursal+"|"+placaBus+"|"+tipoIngreso+"|"+tipoIS+"|"+codPDA+"|"+editTextName.getText().toString().trim()+"|"+hostname();
        final String name = editTextName.getText().toString().trim();
        final String dni = editTextName.getText().toString().trim();
        final String placa = placaBus;
        final String idsucursal = idSucursal;
        final String hostname = hostname();
        final String fecharegistro = fechaActual();
        final String pedateador = codPDA;
        final String idtraslado = tipoIngreso;
        final String idtipo = tipoIS;
        // final String name = tipoIngreso+"|"+fechaActual()+"|"+idSucursal+"|"+placaBus+"|"+editTextName.getText().toString().trim()+"|"+hostname();


        // Contador
        tvConteoSN.setText(db.totalSync());
        tvConteoNS.setText(db.totalNoSync());
        //tvHostname.setText(hostname()+ " | " +fechaActual());
        tvHostname.setText(codPDA);

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
                                saveNameToLocalStorage(name, NAME_SYNCED_WITH_SERVER, dni, placa, idsucursal, hostname, fecharegistro, pedateador, idtraslado, idtipo);
                            } else {
                                //if there is some error
                                //saving the name to sqlite with status unsynced
                                saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER, dni, placa, idsucursal, hostname, fecharegistro, pedateador, idtraslado, idtipo);
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
                        saveNameToLocalStorage(name, NAME_NOT_SYNCED_WITH_SERVER, dni, placa, idsucursal, hostname, fecharegistro, pedateador, idtraslado, idtipo);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", name);
                params.put("dni", dni);
                params.put("placa", placa);
                params.put("idsucursal", idsucursal);
                params.put("hostname", hostname);
                params.put("fecharegistro", fecharegistro);
                params.put("pedateador", pedateador);
                params.put("idtraslado", idtraslado);
                params.put("idtipo", idtipo);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    //saving the name to local storage
    private void saveNameToLocalStorage(String name, int status, String dni, String placa, String idsucursal, String hostname, String fecharegistro, String pedateador, String idtraslado, String idtipo) {
        editTextName.setText("");

        db.addName(name, status, dni, placa, idsucursal, hostname, fecharegistro, pedateador, idtraslado, idtipo);
        Name n = new Name(name, status, dni, placa, idsucursal, hostname, fecharegistro, pedateador, idtraslado, idtipo);
        names.add(n);
        refreshList();
        // Contador
        tvConteoSN.setText(db.totalSync());
        tvConteoNS.setText(db.totalNoSync());
        //tvHostname.setText(hostname()+ " | " +fechaActual());
        tvHostname.setText(codPDA);
    }

    // ATRIBUTOS DE DISPOSITIVO
    public String fabricante(){
        return Build.MANUFACTURER;
    }
    public String modelo(){
        return Build.MODEL;
    }

    private String hostname(){
        return Settings.Secure.getString(getContentResolver(), "bluetooth_name");
    }
    // ---------------------------

    @Override
    public void onClick(View view) {
        saveNameToServer();
    }

    @Override
    public void onBackPressed() {
        //Toast.makeText(getApplicationContext(),"PResione",Toast.LENGTH_LONG).show();
        Intent i =new Intent(getApplicationContext(),MainInicio.class);
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

    @Override
    protected void onDestroy() {

        // SharedPreferences
        SharedPreferences preferencias = getSharedPreferences("datos", Context.MODE_PRIVATE);
        SharedPreferences.Editor Obj_editor = preferencias.edit();
        Obj_editor.putString("dni", "");
        Obj_editor.commit();

        super.onDestroy();
        super.onPause();
        finishAffinity();


    }









    private void saveNameMA(final int id, final String name, final String dni, final String placa, final String idsucursal, final String hostname, final String fecharegistro, final String pedateador, final String idtraslado, final String idtipo ) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, MainActivity.URL_SAVE_NAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                //updating the status in sqlite
                                db.updateNameStatus(id, MainActivity.NAME_SYNCED_WITH_SERVER);

                                //sending the broadcast to refresh the list
                                context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
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
                params.put("name", name);
                params.put("dni", dni);
                params.put("placa", placa);
                params.put("idsucursal", idsucursal);
                params.put("hostname", hostname);
                params.put("fecharegistro", fecharegistro);
                params.put("pedateador", pedateador);
                params.put("idtraslado", idtraslado);
                params.put("idtipo", idtipo);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}
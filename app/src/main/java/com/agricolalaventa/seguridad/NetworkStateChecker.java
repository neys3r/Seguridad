package com.agricolalaventa.seguridad;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.agricolalaventa.seguridad.Modelos.Main_Asistencia;
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

public class NetworkStateChecker extends BroadcastReceiver {

    //context and database helper object
    private Context context;
    private DatabaseHelper db;


    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        db = new DatabaseHelper(context);

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        saveName(
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
    }

    private void saveName(final int id, final String dni, final String idreferencia, final String idsucursal, final String idpda, final String fecha, final String pedateador, final String idtraslado, final String idtipo ) {
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
                params.put("idpda", idpda);
                params.put("fecha", fecha);
                params.put("pedateador", pedateador);
                params.put("idtraslado", idtraslado);
                params.put("idtipo", idtipo);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(stringRequest);
    }

}

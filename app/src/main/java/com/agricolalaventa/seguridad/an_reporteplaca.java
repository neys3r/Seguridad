package com.agricolalaventa.seguridad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.agricolalaventa.seguridad.Modelos.Main_Asistencia;
import com.agricolalaventa.seguridad.db.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class an_reporteplaca extends AppCompatActivity {

    private DatabaseHelper db;
    Context context;
    private TextView tvCantTPlaca;
    private ListView repoplaca_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ln_reporteplaca);
        context     = this;
        db          = new DatabaseHelper(context);

        tvCantTPlaca = (TextView)findViewById(R.id.tvCantTPlaca);

        tvCantTPlaca.setText(db.totalDiario());
        //String userList = db.qrepo02();
        ArrayList<HashMap<String, String>> userList = db.qrepo02();
        ListView lv = (ListView) findViewById(R.id.repoplaca_list);
        ListAdapter adapter = new SimpleAdapter(an_reporteplaca.this, userList, R.layout.ln_reporteplaca_det,new String[]{"idreferencia","cantidad"}, new int[]{R.id.tvRPlacaD, R.id.tvRPlacaC});
        lv.setAdapter(adapter);




    }
}
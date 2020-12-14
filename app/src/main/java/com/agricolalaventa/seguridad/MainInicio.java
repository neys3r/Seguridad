package com.agricolalaventa.seguridad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainInicio extends AppCompatActivity {

    private TextView tvTitInicio, tvFechaInicio;
    private EditText edtSucursal, edtPlaca;
    private Button btnInicio;
    private String placaBus, dscSucursal, idSucursal, tipoIngreso, mensaje;
    private int longitud;
    //private Spinner spinner;
    private RadioButton  radio_bus, radio_moto, radio_vehiculo, radio_peatonal;
    private RadioGroup radio_opciones;
    private TextView tvTipoIS, codPdaAcceso;
    private Switch swOpcionTipoRegistro;
    //private String codPDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

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

        // Recogemos codPDA del activity Pedateador
        //Bundle bundle = getIntent().getExtras();
        //codPDA = bundle.getString("codPDA");
        //codPdaAcceso.setText(codPDA);

        boolean estado = radio_moto.isChecked();

        String [] opciones = {"Seleccionar Sucursal", "Pinilla", "Mayorazgo", "Don Jorge", "San Judas"};
        ArrayAdapter <String> adapterS = new ArrayAdapter<String>(this,R.layout.spinner_item_sucursal, opciones);
        //spinner.setAdapter(adapterS);

        //tvTitInicio.setText("Registro de "+descIS);

        // Cambiar Información TipoIngreso/Salida
         infoSwithc();

        /*if (swOpcionTipoRegistro.isChecked()){
            //Toast.makeText(getApplicationContext(),"Prueba 0", Toast.LENGTH_LONG).show();

            SharedPreferences preferencias = getSharedPreferences
                    ("datosTipoIS", Context.MODE_PRIVATE);
            String idTipoIS = "0";
            String descTipoIS = "Salida";
            tvTipoIS.setText(descTipoIS);
            tvTipoIS.setBackgroundColor(Integer.parseInt("#FF0000"));

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("idTipoIS", idTipoIS);
            editor.putString("descTipoIS", descTipoIS);
            editor.commit();

        }else{
            SharedPreferences preferencias = getSharedPreferences
                    ("datosTipoIS", Context.MODE_PRIVATE);
            String idTipoIS = "0";
            String descTipoIS = "Ingreso";
            tvTipoIS.setText(descTipoIS);

            SharedPreferences.Editor editor = preferencias.edit();
            editor.putString("idTipoIS", idTipoIS);
            editor.putString("descTipoIS", descTipoIS);
            editor.commit();
        }*/

        // FIN


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
                }else if (checkedId == R.id.radio_moto){
                    Toast.makeText(getApplicationContext(),"Moto",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.INVISIBLE);
                }else if (checkedId == R.id.radio_vehiculo){
                    Toast.makeText(getApplicationContext(),"Vehiculo",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.VISIBLE);
                    edtPlaca.setHint("Placa Vehiculo");
                }else if (checkedId == R.id.radio_peatonal){
                    Toast.makeText(getApplicationContext(),"Peatonal",Toast.LENGTH_LONG).show();
                    edtPlaca.setVisibility(View.INVISIBLE);
                }
            }

        });

        // Fin Validación Radio

        btnInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                placaBus = edtPlaca.getText().toString();
                //idSucursal = edtSucursal.getText().toString();
                longitud = edtPlaca.getText().toString().length();

                //String seleccion = spinner.getSelectedItem().toString();
/*
                if (seleccion.equals("Pinilla")){
                    idSucursal = "006";
                    dscSucursal = "Pinilla";
                } else if (seleccion.equals("Mayorazgo")){
                    idSucursal = "001";
                    dscSucursal = "Mayorazgo";
                } else if (seleccion.equals("Don Jorge")){
                    idSucursal = "023";
                    dscSucursal = "Don Jorge";
                }else if(seleccion.equals("San Judas")){
                    idSucursal = "034";
                    dscSucursal = "San Judas";
                }else{
                    idSucursal = "999";
                    dscSucursal = "Sin Sucursal";
                }*/


                // Seleccionar tipo de ingreso

                if (radio_bus.isChecked() == true){
                    tipoIngreso = "1";
                } else if (radio_moto.isChecked() == true){
                    tipoIngreso = "2";
                } else if (radio_vehiculo.isChecked() == true){
                    tipoIngreso = "3";
                } else if (radio_peatonal.isChecked() == true){
                    tipoIngreso = "4";
                }else{
                    tipoIngreso = "9";
                }

                    //Toast.makeText(getApplicationContext(),"Seleccionar válida:T-"+tipoIngreso,Toast.LENGTH_LONG).show();
                    switch (tipoIngreso){
                        case  "1":

                            if (longitud != 6 ){
                                Toast.makeText(getApplicationContext(),"La placa del bus de tener 6 dígitos",Toast.LENGTH_LONG).show();
                            } else {
                                //Muestra la lista de BUS
                                mensaje = "Placa " + placaBus + "en Sede "+ idSucursal +" grabado ";
                                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                                Intent a1 =new Intent(getApplicationContext(),MainActivity.class);
                                a1.putExtra("placaBus", placaBus);
                                //a1.putExtra("idSucursal", idSucursal);
                                //a1.putExtra("dscSucursal", dscSucursal);
                                a1.putExtra("tipoIngreso", tipoIngreso);
                                //a1.putExtra("codPDA", codPDA);
                                startActivity(a1);
                            }

                            break;
                        case  "2":
                            //Muestra la lista de MOTO

                                //Muestra la lista de BUS
                                mensaje = "Moto en Sede "+ idSucursal +" grabado ";
                                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                                Intent a2 =new Intent(getApplicationContext(),MainActivity.class);
                                a2.putExtra("placaBus", "MMMMMM");
                                //a2.putExtra("idSucursal", idSucursal);
                                //a2.putExtra("dscSucursal", dscSucursal);
                                a2.putExtra("tipoIngreso", tipoIngreso);
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
                                mensaje = "Placa " + placaBus + "en Sede "+ idSucursal +" grabado ";
                                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                                Intent a3 =new Intent(getApplicationContext(),MainActivity.class);
                                a3.putExtra("placaBus", placaBus);
                                //a3.putExtra("idSucursal", idSucursal);
                                //a3.putExtra("dscSucursal", dscSucursal);
                                a3.putExtra("tipoIngreso", tipoIngreso);
                                //a3.putExtra("codPDA", codPDA);
                                startActivity(a3);
                            }
                            break;
                        case  "4":
                            //Muestra la lista de Frutas

                            mensaje = "Lista de Personal  " + "en Sede "+ idSucursal +" grabado ";
                            Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

                            Intent a4 =new Intent(getApplicationContext(),MainActivity.class);
                            a4.putExtra("placaBus", "PPPPPP");
                            //a4.putExtra("idSucursal", idSucursal);
                            //a4.putExtra("dscSucursal", dscSucursal);
                            a4.putExtra("tipoIngreso", tipoIngreso);
                            //a4.putExtra("codPDA", codPDA);
                            startActivity(a4);

                            break;
                        default:
                            //Muestra mensaje: "Ingresa un valor valido"
                            Toast.makeText(getApplicationContext(),"Debes elegir un tipo de Ingreso válido",Toast.LENGTH_LONG).show();
                            break;
                    }
                //} -- eliminar

            }
        });




    }

    public void onClick(View view){
        if (view.getId()==R.id.swOpcionTipoRegistro){
            infoSwithc();
        // Cambiar Información TipoIngreso/Salida
            /*if (swOpcionTipoRegistro.isChecked()){
                Toast.makeText(getApplicationContext(),"Switch Salida", Toast.LENGTH_LONG).show();

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
                Toast.makeText(getApplicationContext(),"Switch Ingreso", Toast.LENGTH_LONG).show();
                SharedPreferences preferencias = getSharedPreferences
                        ("datosTipoIS", Context.MODE_PRIVATE);
                String idTipoIS = "0";
                String descTipoIS = "Ingreso";
                tvTipoIS.setText(descTipoIS);

                SharedPreferences.Editor editor = preferencias.edit();
                editor.putString("idTipoIS", idTipoIS);
                editor.putString("descTipoIS", descTipoIS);
                editor.commit();
            }*/
        }

    }

    public void infoSwithc(){
        if (swOpcionTipoRegistro.isChecked()){
            Toast.makeText(getApplicationContext(),"Switch Salida", Toast.LENGTH_LONG).show();

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
            Toast.makeText(getApplicationContext(),"Switch Ingreso", Toast.LENGTH_LONG).show();
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





}
package com.agricolalaventa.seguridad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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

        // Impedir Ingreso manual de DNI
        edtVigilante.setInputType(InputType.TYPE_NULL);

        // Validaciones Radio




        //Obtener información de RadioButton
/*
        radio_tipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                if (checkedId == R.id.radio_ingreso){
                    tipoIS = "0";
                    descIS = "Ingreso";
                }else if (checkedId == R.id.radio_salida){
                    tipoIS = "1";
                    descIS = "Salida";
                }else{
                    tipoIS = "9";
                    descIS = "Otros";
                }

            }

        });

        */


        // Fin Validación Radio

        // Acciones del Boton Guardar Vigilante
        btnGuardarVigilante.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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


}
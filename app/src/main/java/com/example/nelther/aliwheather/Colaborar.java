package com.example.nelther.aliwheather;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nelther on 14/12/2015.
 */

public class Colaborar extends Fragment implements View.OnClickListener{
    Context context;
    ArrayAdapter AdapterTmp,AdapterViento;
    List<String> listaTmp = new ArrayList<String>();
    List<String> listaViento = new ArrayList<String>();
    Spinner spnTemperatura,spnViento;
    String ciudad;

    public Colaborar(){

    }

    @SuppressLint("ValidFragment")
    public Colaborar(Context c,String ciudad) {
        context = c;
        this.ciudad=ciudad;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_colaborar,container,false);
        ImageButton btn = (ImageButton) view.findViewById(R.id.fabAddColaboracion);
        btn.setOnClickListener(this);
        listaTmp.add("Frio");
        listaTmp.add("Normal");
        listaTmp.add("Calor");
        listaViento.add("Nada");
        listaViento.add("Normal");
        listaViento.add("Mucho");

        spnTemperatura=(Spinner)view.findViewById(R.id.spn_temperatura);
        AdapterTmp= new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,listaTmp);
        spnTemperatura.setAdapter(AdapterTmp);

        spnViento=(Spinner)view.findViewById(R.id.spn_viento);
        AdapterViento=new ArrayAdapter<String>(context,android.R.layout.simple_spinner_dropdown_item,listaViento);
        spnViento.setAdapter(AdapterViento);
        return view;
    }

    public void insertarOp(){

        String url = "http://162.243.64.92:8080/AliWheather/rest/aliwheather/crearOp";
        Log.i("Moviles", url);
        Map<String,String> params = new HashMap<String,String>();
        params.put("ciudad",ciudad);
        params.put("temperatura",spnTemperatura.getSelectedItem().toString());
        params.put("viento",spnViento.getSelectedItem().toString());

        Log.i("Moviles",new JSONObject(params).toString());
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url,new JSONObject(params),new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(context, "Tu Colaboracion Fue Enviada", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {


                        VolleyLog.d("Moviles", "Error: " + error.getMessage());
                        // TODO Auto-generated method stub

                    }


                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.i("Moviles","GET HEADERS");
                Map<String,String> headers = new HashMap<String,String>();
                headers.put("Content-Type","application/json");
                return headers;
            }
        };

        Volley.newRequestQueue(context).add(jsObjRequest);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.fabAddColaboracion: insertarOp(); break;
        }
    }
}

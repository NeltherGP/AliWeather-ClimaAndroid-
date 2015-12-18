package com.example.nelther.aliwheather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by nelther on 06/12/2015.
 */
public class Clima extends Fragment {

    Context context;
    String city,tmp;
    TextView txtvCiudad,txtvTemp;
    SeekBar skbTemp,skbViento;

    public Clima(){

    }

    @SuppressLint("ValidFragment")
    public Clima(Context c,String city, String tmp) {
        context = c;
        this.city=city;
        this.tmp=tmp;
    }


    @SuppressLint("ValidFragment")
    public Clima(int color, Context context){
        this.context=context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_layout,container,false);
        setHasOptionsMenu(true);
        txtvCiudad=(TextView)view.findViewById(R.id.tv_ciudad);
        txtvCiudad.setText(city);

        txtvTemp = (TextView)view.findViewById(R.id.txtv_temp);
        txtvTemp.setText(tmp);

        skbTemp=(SeekBar)view.findViewById(R.id.seekBar);
        skbViento= (SeekBar)view.findViewById(R.id.seekBar2);

        cargarOpTemp();

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intAceerca;
        if(item.getItemId()==R.id.AcercaDe){
            intAceerca = new Intent(context,AcercaDe.class);
            startActivity(intAceerca);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void cargarOpTemp(){
        String url="http://162.243.64.92:8080/AliWheather/rest/aliwheather/getTemp";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url,new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                           // Log.i("Moviles", "Respuesta: " + response.getJSONObject("main").getString("temp"));
                            //txtvTemp.setText(response.getJSONObject("main").getString("temp")+"°C");
                            skbTemp.setMax(response.getInt("total"));
                            skbViento.setMax(response.getInt("total"));
                            skbTemp.setProgress(Integer.parseInt(response.getString("temperatura")));
                            skbViento.setProgress(Integer.parseInt(response.getString("viento")));



                        } catch (JSONException e) {
                            Log.e("Moviles", "Error : " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Moviles","Error: "+error.getMessage());
                        VolleyLog.d("Moviles", "Error: " + error.getStackTrace());
                        // TODO Auto-generated method stub

                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsObjRequest);
    }



}

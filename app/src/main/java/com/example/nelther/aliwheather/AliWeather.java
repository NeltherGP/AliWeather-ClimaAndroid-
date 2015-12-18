package com.example.nelther.aliwheather;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AliWeather extends AppCompatActivity implements LocationListener {
    Toolbar toolbar;
    ViewPager viewPager;
    TabLayout tabLayout;
    ImageButton imgbtn;
    LocationManager adminLoc;
    Location localizacion;
    private float disActualiza=10;
    private long tiempoAct = 10000;
    Context context;
    String city;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ali_weather);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        getMyLocation();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(localizacion.getLatitude(), localizacion.getLongitude(), 1);
            city=addresses.get(0).getSubAdminArea();
           // Log.i("Moviles", addresses.get(0).getSubAdminArea());
            getClima(city);

        } catch (IOException e) {
            e.printStackTrace();
        }


        tabLayout = (TabLayout) findViewById(R.id.tablayout);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_ali_weather, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void setupViewPager (ViewPager viewPager,String city,String tmp,Location loc){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Log.e("Moviles","ViewPager: "+city);
        Log.e("Moviles","ViewPager: "+tmp);
        Log.e("Moviles","ViewPagerLoc"+loc.toString());

        adapter.addFrag(new Clima(this, city, tmp), "Clima");
        adapter.addFrag(new Colaborar(this,city),"Colaborar");
        adapter.addFrag(new MapsActivity(this,loc,tmp),"Mapa");


        viewPager.setAdapter(adapter);
    }

    public void getMyLocation() {
        try {
            adminLoc = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
            adminLoc.isProviderEnabled(LocationManager.GPS_PROVIDER);


            adminLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,tiempoAct,disActualiza,this);

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            localizacion = adminLoc.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Toast.makeText(this, "GPS: Latitud " + localizacion.getLatitude() + ", " + "Longitud " + localizacion.getLongitude(), Toast.LENGTH_SHORT).show();

        }catch (Exception e){

        }
    }

    public void getClima(final String city){
        String url="http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid=2195b44befdb015cef0616194a38984e";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url,new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.i("Moviles", "Respuesta: " + response.getJSONObject("main").getString("temp"));
                            //txtvTemp.setText(response.getJSONObject("main").getString("temp")+"°C");
                            setupViewPager(viewPager,city,response.getJSONObject("main").getString("temp")+"°C",
                                    localizacion);
                            tabLayout.setupWithViewPager(viewPager);
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
        Volley.newRequestQueue(this).add(jsObjRequest);


    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

package com.example.nelther.aliwheather;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A fragment that launches other parts of the demo application.
 */
public class MapsActivity extends Fragment implements LocationListener,GoogleMap.OnMapClickListener{

    MapView mMapView;
    Location loc;
    MarkerOptions marker;
    String tmp;
    LocationManager adminLoc;
    Location localizacion;
    private float disActualiza=10;
    private long tiempoAct = 10000;
    Context context;
    String city;
    private GoogleMap googleMap;
    @SuppressLint("ValidFragment")
    public MapsActivity(Context context, Location loc,String tmp){
        this.loc=loc;
        this.tmp=tmp;
        this.context=context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflat and return the layout
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.activity_maps, container,
                false);
        mMapView = (MapView) v.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        // latitude and longitude
        double latitude = loc.getLatitude();
        double longitude = loc.getLongitude();

        // create marker
       marker = new MarkerOptions().position(
                new LatLng(latitude, longitude)).title("Hello Maps");

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(getIdicon(Double.parseDouble(tmp.split("°C")[0]))));

        // adding marker
        googleMap.addMarker(marker);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude)).zoom(15).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));

        googleMap.setOnMapClickListener(this);

        // Perform any camera updates here
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_ali_weather,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.map_normal: googleMap.setMapType(GoogleMap.MAP_TYPE_NONE); break;
            case R.id.map_hybrid: googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID); break;
            case R.id.map_satellite: googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE); break;
            case R.id.map_terrain: googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapClick(LatLng latLng) {

        getMyLocation();
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(localizacion.getLatitude(), localizacion.getLongitude(), 1);
            city = addresses.get(0).getSubAdminArea();
//            Log.i("Moviles", addresses.get(0).getSubAdminArea());
            getClima(city, latLng);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getIdicon(double tmp){
        if(tmp<=20){
            return R.mipmap.d13d;
        }
        return R.mipmap.d01d;
    }
    public void getMyLocation() {
        try {
            adminLoc = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
            adminLoc.isProviderEnabled(LocationManager.GPS_PROVIDER);


            adminLoc.requestLocationUpdates(LocationManager.GPS_PROVIDER,tiempoAct,disActualiza,this);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


        }catch (Exception e){

        }
    }

    public void getClima(final String city, final LatLng latLng){
        String url="http://api.openweathermap.org/data/2.5/weather?lat="+latLng.latitude+"&lon="+latLng.longitude+"&units=metric&appid=2195b44befdb015cef0616194a38984e";
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url,new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            Log.i("Moviles", "Respuesta: " + response.getJSONObject("main").getString("temp"));
                            marker = new MarkerOptions().position(
                                    new LatLng(latLng.latitude, latLng.longitude)).title(response.getJSONObject("main").getString("temp")+"°C");
                            marker.icon(BitmapDescriptorFactory.fromResource(getIdicon(Double.parseDouble(response.getJSONObject("main").getString("temp")))));

                            googleMap.clear();
                            googleMap.addMarker(marker);
                            //txtvTemp.setText(response.getJSONObject("main").getString("temp")+"°C");

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
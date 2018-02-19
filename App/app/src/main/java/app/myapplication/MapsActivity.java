package app.myapplication;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//Made by Valtteri Hutri, Joel Rapp and Rolle Alatensiö

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        BasicFrag.FragmentStatusListener,
        GoogleMap.OnMapClickListener {

    private GoogleMap mMap;
    SettingsInfo settingsInfo;

    HashMap<String, Boolean> fragStates = new HashMap<>();
    public static List<Marker> markerList = new ArrayList<>();
    public static List<String> nimilista = new ArrayList<>();

    SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Toast.makeText(getApplicationContext(),"Valitse hakuvaihtoehdot alakulman napista", Toast.LENGTH_LONG).show();

        if (savedInstanceState != null){
            if(savedInstanceState.containsKey("settingsBtnState")){
                //noinspection ResourceType
                (findViewById(R.id.button2)).setVisibility(savedInstanceState.getInt("settingsBtnState"));
            }
        }

        sPrefs = getPreferences(MODE_PRIVATE);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        mMap.setOnMapClickListener(this);
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter(getLayoutInflater()));

        mMap.setPadding(13, 0, 0, 165);


        mMap.setMinZoomPreference(10.0f);
        LatLngBounds tampere = new LatLngBounds(new LatLng(61.261316, 23.484083), new LatLng(61.649442, 24.126538));

        mMap.setLatLngBoundsForCameraTarget(tampere);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tampere.getCenter(),12));


        jsonParser("http://lipas.cc.jyu.fi/geoserver/lipas/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=lipas:lipas_kaikki_pisteet&filter=%3CPropertyIsEqualTo%3E%3CPropertyName%3Ekuntanumero%3C/PropertyName%3E%3CLiteral%3E837%3C/Literal%3E%3C/PropertyIsEqualTo%3E&outputformat=json&srsName=EPSG:4326");


    }

    private void jsonParser(String jsonUrl) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest json_req = new JsonObjectRequest(Request.Method.GET, jsonUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Parsing JSON from Api
                    JSONArray features = response.getJSONArray("features");
                    for (int i = 0; i < features.length(); i++) {
                        JSONObject feature = (JSONObject) features.get(i);
                        if (!feature.getString("geometry").equals("null")) {
                            JSONObject geometry = feature.getJSONObject("geometry");
                            JSONArray coordinates = geometry.getJSONArray("coordinates");
                            JSONObject properties = feature.getJSONObject("properties");
                            String name = getName(properties);

                            LatLng coords = null;

                            if (geometry.has("type")) {
                                if (geometry.getString("type").equals("Point")) {
                                    coords = new LatLng(coordinates.getDouble(1), coordinates.getDouble(0));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error: Unknown geotype", Toast.LENGTH_LONG).show();
                                    Log.d("geotype", geometry.getString("type"));
                                }

                            }
                            if (coords != null) {
                                Marker marker = mMap.addMarker(new MarkerOptions().position(coords).title(name).visible(false));
                                HashMap<String, String> infoProperties;
                                infoProperties = getProperties(properties);
                                marker.setTag(infoProperties);
                                String nimi = properties.getString("tyyppi_nimi_fi");
                                marker.setVisible(markerVisibility(sPrefs.getBoolean(nimi, false), marker, "tyyppi_nimi_fi", nimi));
                                markerList.add(marker);
                            }
                        }
                    }

                    for (Marker marker : markerList) {
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> infoProperties = (HashMap<String,String>) marker.getTag();
                        marker.setIcon(setIconColor(infoProperties.get("tyyppi_nimi_fi")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("VolleyError", error.toString());
            }
        });
        queue.add(json_req);
    }

    private HashMap<String, String> getProperties(JSONObject properties) {
        HashMap<String, String> infoProperties = new HashMap<>();
        try {
            if (properties.has("tyyppi_nimi_fi")) {
                infoProperties.put("tyyppi_nimi_fi", properties.getString("tyyppi_nimi_fi"));
            }
            if (properties.has("nimi_fi")) {
                infoProperties.put("nimi_fi", properties.getString("nimi_fi"));
            }

            if (properties.has("sahkoposti")) {
                infoProperties.put("sahkoposti", properties.getString("sahkoposti"));
            }
            if (properties.has("puhelinnumero")) {
                infoProperties.put("puhelinnumero", properties.getString("puhelinnumero"));
            }
            if (properties.has("yllapitaja")) {
                infoProperties.put("yllapitaja", properties.getString("yllapitaja"));
            }
            if (properties.has("katuosoite")) {
                infoProperties.put("katuosoite", properties.getString("katuosoite"));
            }
            if (properties.has("lisatieto_fi")) {
                infoProperties.put("lisatieto_fi", properties.getString("lisatieto_fi"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return infoProperties;
    }

    public void markerOpen(View v)
    {
        settingsInfo = new SettingsInfo();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.activity_maps, settingsInfo, "settingsWindow");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void infoWindow(View V){
        InfoWindow info = new InfoWindow();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
        transaction.replace(R.id.activity_maps, info, "infoWindow");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void backpress(View v){
        Fragment infoWindow = getSupportFragmentManager().findFragmentByTag("infoWindow");
        Fragment settingsWindow = getSupportFragmentManager().findFragmentByTag("settingsWindow");
        if (infoWindow != null && infoWindow.isVisible()) {
            ((InfoWindow) getSupportFragmentManager().findFragmentByTag("infoWindow")).closeFrag();
        } else if (settingsWindow != null && settingsWindow.isVisible()) {
            ((SettingsInfo) getSupportFragmentManager().findFragmentByTag("settingsWindow")).closeFrag();
        }
    }

    public static void hideMarkers(CheckBox check) {
        for (Marker marker : markerList) {
            marker.setVisible(markerVisibility(check.isChecked(), marker, "tyyppi_nimi_fi", check.getText().toString()));
        }
    }

    private static boolean markerVisibility(Boolean isChecked, Marker marker, String key, String value) {
        Boolean visibility = marker.isVisible();
        @SuppressWarnings("unchecked")
        HashMap<String, String> infoProperties = (HashMap<String,String>) marker.getTag();

        if (infoProperties != null && infoProperties.containsKey(key) && infoProperties.get(key).equals(value)) {
            visibility = isChecked;
        }

        return visibility;
    }

    private String getName(JSONObject properties) {
        String name = null;
        try {
            if (properties.has("tyyppi_nimi_fi")) {
                if (!nimilista.contains(properties.getString("tyyppi_nimi_fi")))
                    nimilista.add(properties.getString("tyyppi_nimi_fi"));
                name = properties.getString("tyyppi_nimi_fi");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return name;
    }

    @Override
    public void onStatusPass(HashMap<String, Boolean> status) {
        fragStates.putAll(status);
        if(findViewById(R.id.button2) != null)
        {
            if (fragStates.containsValue(true))
                findViewById(R.id.button2).setVisibility(View.INVISIBLE);
            else
                findViewById(R.id.button2).setVisibility(View.VISIBLE);
        }
    }

    private BitmapDescriptor setIconColor(String tyyppinimi) {
        BitmapDescriptor color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        for (int i = 0; i < nimilista.size(); i++) {
            if(tyyppinimi.equals(nimilista.get(i)))
                color = BitmapDescriptorFactory.defaultMarker(360/nimilista.size()*i);
        }

        return color;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        Fragment settingsWindow = getSupportFragmentManager().findFragmentByTag("settingsWindow");
        Fragment infoWindow = getSupportFragmentManager().findFragmentByTag("infoWindow");
        if (settingsWindow != null && settingsWindow.isVisible()) {
            ((SettingsInfo) getSupportFragmentManager().findFragmentByTag("settingsWindow")).closeFrag();
        }
        //Close infowindow back into map
        if (infoWindow != null && infoWindow.isVisible()) {
            ((InfoWindow) getSupportFragmentManager().findFragmentByTag("infoWindow")).closeFrag();
            ((SettingsInfo) getSupportFragmentManager().findFragmentByTag("settingsWindow")).closeFrag();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("settingsBtnState", (findViewById(R.id.button2)).getVisibility());
    }
}
package com.teleport;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textViewCoordinates;
    private GoogleMap mMap;
    private RealTimeLocationUpdater locationUpdater;
    private MapOrientationListener mapOrientation;
    private Marker userMarker;
    private RouteManager routeManager;
    private double latitude;
    private double longitude;
    private LatLng userLocation;
    private BusStopManager busStopManager;
    private PlaceSearchManager placeSearchManager;
    private SearchView searchView;
    private String apiKey = "key";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCoordinates = findViewById(R.id.textViewCoordinates);
        textViewCoordinates.setText("Permissões de localização não concedidas");

        // Verifique se as permissões de localização foram concedidas
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            // Se as permissões não foram concedidas, solicite-as

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_CODE_LOCATION_PERMISSION);

        }
        else {
            // Permissões já concedidas, pode acessar a localização


            // Configurar a Toolbar
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            placeSearchManager = new PlaceSearchManager(this, mMap, apiKey);

            //inicializar o SearchView
            searchView = findViewById(R.id.search_view);
            setupSearchView();
        }
    }



    // Método para lidar com a resposta às solicitações de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

            }
            else {
                // Permissão negada
            }
        }

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Personalize o mapa aqui, por exemplo, defina o tipo de mapa
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        // Habilite o botão de localização do usuário no mapa
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

            try {
            // Obtenha a última localização do usuário e mova a câmera para essa posição
            FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    String coordinatesText = "Latitude: " + latitude + ", Longitude: " + longitude;
                    textViewCoordinates.setText(coordinatesText);

                    userLocation = new LatLng(latitude, longitude);


                    busStopManager = new BusStopManager(this, mMap);
                    busStopManager.findBusStops(userLocation, 1000, apiKey);

                    //crie um marcador para o usuário
                    MarkerOptions userMarkerOptions = new MarkerOptions().position(userLocation).title("Você está aqui");
                    userMarker = mMap.addMarker(userMarkerOptions);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                    locationUpdater = new RealTimeLocationUpdater(this, mMap);
                    locationUpdater.startLocationUpdates();

                    mapOrientation = new MapOrientationListener(this, mMap, userMarker);
                    mapOrientation.start();


                    routeManager = new RouteManager(mMap, apiKey);

                    //LatLng origin = locationUpdater.getCurrentLocation();
                    LatLng destination = new LatLng(-30.0330600, -51.2300000);
                    routeManager.drawRoute(userLocation, destination);

                }
            });
            } catch (SecurityException e) {
                // Trate a exceção de segurança
                textViewCoordinates.setText("Erro de permissão: " + e.getMessage());
            }
        }
    }


    public void openNavDrawer(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }



    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                placeSearchManager.searchPlace(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }


}
package com.teleport;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

public class RealTimeLocationUpdater {
    private final FusedLocationProviderClient fusedLocationClient;
    private final GoogleMap googleMap;
    private final LocationRequest locationRequest;
    private final LocationCallback locationCallback;
    private final Context context;
    private LatLng userLocation;

    public RealTimeLocationUpdater(Context context, GoogleMap googleMap) {
        this.context = context;
        this.googleMap = googleMap;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Crie uma solicitação de localização com a precisão desejada e intervalo de tempo
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // Atualizações a cada 5 segundos

        // Crie um LocationCallback para receber atualizações de localização
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    for (Location location : locationResult.getLocations()) {
                        updateMapLocation(location);
                    }
                }
            }
        };
    }

    // Método para iniciar atualizações de localização
    public void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    // Método para parar atualizações de localização
    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    // Atualiza a localização do usuário no mapa
    private void updateMapLocation(Location location) {
        if (location != null) {
            // Armazene o estado de zoom atual
            float currentZoom = googleMap.getCameraPosition().zoom;

            userLocation = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(userLocation)
                    .zoom(currentZoom) // Mantenha o zoom atual
                    .build();

            // Atualize a câmera para a nova posição
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    public LatLng getCurrentLocation(){
        return userLocation;
    }


}
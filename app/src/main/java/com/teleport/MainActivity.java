package com.teleport;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textViewCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewCoordinates = findViewById(R.id.textViewCoordinates);

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
            accessLocation();
        }
    }



    // Método para lidar com a resposta às solicitações de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
                accessLocation();
            } else {
                // Permissão negada
                // Lidar com a falta de permissão aqui
            }
        }
    }



    // Método para acessar a localização
    private void accessLocation() {
        // Verifique se as permissões de localização foram concedidas
        boolean hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (hasFineLocationPermission || hasCoarseLocationPermission) {
            try {
                // Tente obter a última localização
                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, location -> {
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                                String coordinatesText = "Latitude: " + latitude + ", Longitude: " + longitude;
                                textViewCoordinates.setText(coordinatesText);
                            } else {
                                textViewCoordinates.setText("Localização não disponível");
                            }
                        })
                        .addOnFailureListener(this, e -> {
                            // Trate o erro aqui, por exemplo, mostre uma mensagem ao usuário
                            textViewCoordinates.setText("Erro ao obter a localização: " + e.getMessage());
                        });
            } catch (SecurityException e) {
                // Trate a exceção de segurança
                textViewCoordinates.setText("Erro de permissão: " + e.getMessage());
            }
        } else {
            // Caso as permissões não tenham sido concedidas, trate a falta de permissão
            textViewCoordinates.setText("Permissões de localização não concedidas");
        }
    }



}
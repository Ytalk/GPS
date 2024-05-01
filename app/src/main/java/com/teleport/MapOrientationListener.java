package com.teleport;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class MapOrientationListener implements SensorEventListener {
    private final SensorManager sensorManager;
    private final Sensor rotationSensor;
    private final GoogleMap googleMap;
    private Marker userMarker;

    public MapOrientationListener(Context context, GoogleMap googleMap, Marker userMarker) {
        this.googleMap = googleMap;
        this.userMarker = userMarker;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
    }

    // Inicie o sensor de orientação
    public void start() {
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    // Pare o sensor de orientação
    public void stop() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            // Converta os dados de rotação para uma matriz de orientação
            float[] orientationMatrix = new float[9];
            float[] orientationValues = new float[3];
            SensorManager.getRotationMatrixFromVector(orientationMatrix, event.values);
            SensorManager.getOrientation(orientationMatrix, orientationValues);

            // Obtenha o azimute em radianos
            float azimute = orientationValues[0];

            // Converta o azimute de radianos para graus
            float azimuteDegrees = (float) Math.toDegrees(azimute);

            // Atualize a rotação do marcador do usuário com base no azimute
            if (userMarker != null) {
                userMarker.setRotation(azimuteDegrees);
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Trate mudanças de precisão, se necessário
    }
}

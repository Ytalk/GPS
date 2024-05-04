package com.teleport;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.DirectionsApi;

import java.util.ArrayList;
import java.util.List;


public class RouteManager {
    private final GoogleMap mMap;
    private final GeoApiContext geoApiContext;


    public RouteManager(GoogleMap map, String apiKey) {
        mMap = map;
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(apiKey)
                .build();
    }


    /*public void drawRoute(LatLng origin, LatLng destination) {
        // Execute a solicitação de rota em uma tarefa assíncrona para não bloquear a thread principal
        new RouteTask().execute(origin, destination);
    }


    public class RouteTask extends AsyncTask<LatLng, Void, DirectionsResult> {
        @Override
        protected DirectionsResult doInBackground(LatLng... params) {
            LatLng origin = params[0];
            LatLng destination = params[1];

            try {
                // Obtenha os resultados da rota da API Directions
                return DirectionsApi.newRequest(geoApiContext)
                        .origin(new com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                        .destination(new com.google.maps.model.LatLng(destination.latitude, destination.longitude))
                        .await();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(DirectionsResult result) {
            if (result != null && result.routes != null) {
                for (DirectionsRoute route : result.routes) {
                    mMap.addPolyline(new PolylineOptions()
                            .addAll(getLatLngListFromRoute(route))
                            .width(10)
                            .color(Color.BLUE));
                }
            } else {
                // Mensagem de erro para o usuário ou log
            }
        }

    }


    public List<LatLng> getLatLngListFromRoute(DirectionsRoute route) {
        List<LatLng> path = new ArrayList<>();
        for (com.google.maps.model.LatLng point : route.overviewPolyline.decodePath()) {
            path.add(new LatLng(point.lat, point.lng));
        }
        return path;
    }*/


}
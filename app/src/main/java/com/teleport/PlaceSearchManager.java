package com.teleport;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;

public class PlaceSearchManager {

    private Context context;
    private GoogleMap googleMap;
    private PlacesClient placesClient;

    public PlaceSearchManager(Context context, GoogleMap googleMap, String apiKey) {
        this.context = context;
        this.googleMap = googleMap;
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey);
        }
        this.placesClient = Places.createClient(context);
    }

    public void searchPlace(String query) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.LAT_LNG);
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

        placesClient.findCurrentPlace(request).addOnSuccessListener((response) -> {
            for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                Place place = placeLikelihood.getPlace();
                if (place.getName().toLowerCase().contains(query.toLowerCase())) {
                    LatLng latLng = place.getLatLng();
                    if (latLng != null) {
                        googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    }
                }
            }
        }).addOnFailureListener((exception) -> {
            Toast.makeText(context, "Error finding place: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}


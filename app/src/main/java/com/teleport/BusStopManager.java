package com.teleport;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.teleport.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BusStopManager {

    private final GoogleMap mMap;
    private final Context mContext;
    private final OkHttpClient client = new OkHttpClient();

    public BusStopManager(Context context, GoogleMap map) {
        this.mContext = context;
        this.mMap = map;
    }

    public void findBusStops(@NonNull LatLng location, int radius, String apiKey) {
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                location.latitude + "," + location.longitude + "&radius=" + radius +
                "&type=bus_stop&key=" + apiKey;

        Log.d("BusStopManager", "Request URL: " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        new Thread(() -> {
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("BusStopManager", "Response: " + responseBody);

                    JSONObject json = new JSONObject(responseBody);
                    JSONArray results = json.getJSONArray("results");

                    if (results.length() == 0) {
                        Log.d("BusStopManager", "No bus stops found.");
                    } else {
                        for (int i = 0; i < results.length(); i++) {
                            JSONObject place = results.getJSONObject(i);
                            JSONObject locationObj = place.getJSONObject("geometry").getJSONObject("location");
                            double lat = locationObj.getDouble("lat");
                            double lng = locationObj.getDouble("lng");
                            String name = place.getString("name");

                            LatLng busStopLatLng = new LatLng(lat, lng);

                            Log.d("BusStopManager", "Adding bus stop: " + name + " at " + busStopLatLng);

                            // Adicionar marcador no mapa na thread principal
                            ((MainActivity) mContext).runOnUiThread(() -> {
                                mMap.addMarker(new MarkerOptions()
                                        .position(busStopLatLng)
                                        .title(name)
                                        .icon(BitmapDescriptorFactory.fromBitmap(getBusStopIcon())));
                            });
                        }
                    }
                } else {
                    Log.e("BusStopManager", "Request failed: " + response.message());
                }
            } catch (IOException | JSONException e) {
                Log.e("BusStopManager", "Error: ", e);
            }
        }).start();
    }

    private Bitmap getBusStopIcon() {
        return BitmapFactory.decodeResource(mContext.getResources(), R.drawable.bus_icon24);
    }

}

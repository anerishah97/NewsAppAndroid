package com.example.csci571hw9;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class HomeFragment extends Fragment implements LocationListener {

    private final int REQUEST_LOCATION_PERMISSION = 1;
    private static String BACKEND = "https://nodejs-backend-new.wl.r.appspot.com";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    public ArrayList<NewsCardData> newsData = new ArrayList<>();
    HomePageAdapter adapter;
    RecyclerView recyclerView;
    HashMap<String,Integer> background = new HashMap<>();
    Handler handler;

    private TextView weather_city, weather_state, weather_temperature, weather_condition;
    private LinearLayout weather_background_ll;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setVisibility(View.GONE);
        View view = getView();
        makeHttpRequest(view, 0);
    }


    // type = 0 if the function is called onCreate/ onresume
    // type = 1 if the function is called from swipeToRefresh
    public void makeHttpRequest(View rootView, int type) {
        final LinearLayout linearLayout = rootView.findViewById(R.id.progress_custom_home);

        if (type == 0) {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url = BACKEND + "/top-guardian-android";
        Log.d("url ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            newsData.clear();
                            linearLayout.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("articles");
                            int j = jsonArray.length();
                            for (int i = 0; i < j; i++) {
                                JSONObject currentObject = jsonArray.getJSONObject(i);

                                String thumbnail = currentObject.getString("thumbnail");
                                String id = currentObject.getString("id");
                                String section = currentObject.getString("section");
                                String date = currentObject.getString("date");
                                String title = currentObject.getString("title");
                                String url = currentObject.getString("urlToShare");
                                NewsCardData newsCardData = new NewsCardData(id, thumbnail, title, section, date, url);
                                newsData.add(newsCardData);
                            }
                            mSwipeRefreshLayout.setRefreshing(false);
                            recyclerView.setVisibility(View.VISIBLE);

                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
                            setAdapter(newsData);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("error", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    public void setAdapter(List<NewsCardData> list) {
        Log.d("home sze of array ", list.size() + "");
        adapter = new HomePageAdapter(newsData);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_LOCATION_PERMISSION)
    public void requestLocationPermission() {
        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION};
        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            // Toast.makeText(getContext(), "Permission already granted", Toast.LENGTH_SHORT).show();
            setUpWeatherDetails();

        } else {
            EasyPermissions.requestPermissions(this, "Please grant the location permission", REQUEST_LOCATION_PERMISSION, perms);
        }
    }

    public void setUpWeatherDetails() {
        LocationManager lm= (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,1000, (android.location.LocationListener) this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview =  inflater.inflate(R.layout.fragment_home, container, false);
        background.put("Clouds", R.drawable.cloudy_weather);
        background.put("Clear", R.drawable.clear_weather);
        background.put("Snow", R.drawable.snowy_weather);
        background.put("Rain/Drizzle", R.drawable.rainy_weather);
        background.put("Rain",R.drawable.rainy_weather);
        background.put("Drizzle", R.drawable.rainy_weather);
        background.put("Thunderstorm", R.drawable.thunder_weather);

        recyclerView = rootview.findViewById(R.id.list_rv);
        weather_city = rootview.findViewById(R.id.weather_card_city);
        weather_temperature = rootview.findViewById(R.id.weather_card_temperature);
        weather_state = rootview.findViewById(R.id.weather_card_state);
        weather_condition = rootview.findViewById(R.id.weather_card_current_weather);
        weather_background_ll = rootview.findViewById(R.id.weather_card_background_ll);

        requestLocationPermission();

        mSwipeRefreshLayout = rootview.findViewById(R.id.home_swiperefresh_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(false);
        makeHttpRequest(rootview,0);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeHttpRequest(rootview,1);
            }
        });
        return rootview;
    }

    public void makeOpenWeatherAPIcall(String city){
        Log.d("weather", city);
        //Enter Openweather api key
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&units=metric&appid=";
        RequestQueue queue = Volley.newRequestQueue(getContext());
        Log.d("url ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                           Log.d("responeweather", ""+response);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray weatherJSON = jsonObject.getJSONArray("weather");
                            JSONObject mainObj = weatherJSON.getJSONObject(0);
                            String currentWeatherCondition = mainObj.getString("main");
                            weather_condition.setText(currentWeatherCondition);
                            Integer backgroundResource;
                            if(background.containsKey(currentWeatherCondition))
                            {
                                backgroundResource = background.get(currentWeatherCondition);
                            }
                            else{
                                backgroundResource = R.drawable.sunny_weather;
                            }
                            weather_background_ll.setBackgroundResource(backgroundResource);
                            JSONObject mainTemperatureObj = jsonObject.getJSONObject("main");
                            String temperature = mainTemperatureObj.getString("temp");
                            Log.d("tempstring", temperature);
                            int formattedWeather = Math.round(Float.parseFloat(temperature));
                            Log.d("tempformat", String.valueOf(formattedWeather));
                            String finalString = String.valueOf(formattedWeather) + " °C";
                            weather_temperature.setText(finalString);

                        } catch (Exception e) {
                            Log.d("exceptioncatch", e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("exceptionerror", error.toString());
            }
        });

        queue.add(stringRequest);
    }

    @Override
    public void onLocationChanged(Location location) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(3);
        double longitude = Double.parseDouble(df.format(location.getLongitude()));
        double latitude = Double.parseDouble(df.format(location.getLatitude()));

        Log.d("longitude", String.valueOf(longitude));
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude,1);
            String cityName = addresses.get(0).getLocality();
            String stateName = addresses.get(0).getAdminArea();
            Log.d("weather city", cityName);
            weather_state.setText(stateName);
            weather_city.setText(cityName);
            makeOpenWeatherAPIcall(cityName);

        } catch (IOException e) {
            Log.d("weather", e.getMessage());
            e.printStackTrace();
        }


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
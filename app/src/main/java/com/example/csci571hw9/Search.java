package com.example.csci571hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Search extends AppCompatActivity {

    ImageView backButton;
    TextView toolbarTitle;
    public ArrayList<NewsCardData> newsData = new ArrayList<>();
    HomePageAdapter adapter;
    RecyclerView recyclerView;
    String query = "";
    LinearLayout progressBar;

    @Override
    protected void onResume() {
        super.onResume();
        adapter = new HomePageAdapter(newsData);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);

        recyclerView.setVisibility(View.GONE);
        toolbarTitle.setText("Search results for " + query);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        makeHttpRequest(query);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoToolBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.search_view_toolbar);
        setSupportActionBar(toolbar);
        backButton = findViewById(R.id.search_toolbar_back);
        toolbarTitle = findViewById(R.id.search_toolbar_title);

        recyclerView = findViewById(R.id.list_rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HomePageAdapter(newsData);
        recyclerView.setAdapter(adapter);
        progressBar = findViewById(R.id.progress_custom_search);
        progressBar.setVisibility(View.VISIBLE);

        query = "";
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            Log.d("debugQuery", query);
        }

        toolbarTitle.setText("Search results for " + query);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        makeHttpRequest(query);

    }

    public void makeHttpRequest(String keyword){
        String url = "https://nodejs-backend-new.wl.r.appspot.com/guardian-search-keyword?keyword=" + keyword;
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        try{
                            newsData.clear();
//                            linearLayout.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("articles");
                            int j = jsonArray.length();
                            for(int i=0; i<j; i++){
                                JSONObject currentObject = jsonArray.getJSONObject(i);

                                String thumbnail = currentObject.getString("image");
                                String id = currentObject.getString("id");
                                String section = currentObject.getString("section");
                                String date = currentObject.getString("completeDate");
                                String title = currentObject.getString("title");
                                String url = currentObject.getString("urlToShare");
                                NewsCardData newsCardData = new NewsCardData(id,thumbnail, title, section, date,url);
                                newsData.add(newsCardData);
                            }
                            recyclerView.setVisibility(View.VISIBLE);

//                            mSwipeRefreshLayout.setRefreshing(false);
                            setAdapter(newsData);

                        }
                        catch (Exception e){
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

    public void setAdapter(List<NewsCardData> list){
        Log.d("home sze of array ", list.size() + "");
        adapter = new HomePageAdapter(newsData);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

    }
}

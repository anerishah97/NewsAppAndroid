package com.example.csci571hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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

public class BusinessFragment extends Fragment {

    private String url = "https://nodejs-backend-new.wl.r.appspot.com/guardian-section?section=business";
    public ArrayList<NewsCardData> newsData = new ArrayList<>();
    HomePageAdapter adapter;
    RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public BusinessFragment() {
        // Required empty public constructor
    }
    public static BusinessFragment newInstance(String param1, String param2) {
        BusinessFragment fragment = new BusinessFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        View view = getView();
        recyclerView.setVisibility(View.GONE);
        makeHttpRequest(view,0);
    }


    public void makeHttpRequest(View rootView, int type){
        final LinearLayout linearLayout = rootView.findViewById(R.id.progress_custom_business);

        if(type == 0)
        {
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        }        newsData.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            linearLayout.setVisibility(View.GONE);

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
                                //Log.i("eeeeeeeeeeeeeeeeeee",section);
                                //test.add(section);
                                NewsCardData newsCardData = new NewsCardData(id,thumbnail, title, section, date,url);
                                newsData.add(newsCardData);
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
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
        Log.d("business sze of array ", list.size() + "");
        adapter = new HomePageAdapter(newsData);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview =  inflater.inflate(R.layout.fragment_business, container, false);
        recyclerView = rootview.findViewById(R.id.list_rv_business);
        mSwipeRefreshLayout = rootview.findViewById(R.id.business_swiperefresh_items);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        makeHttpRequest(rootview,0);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                makeHttpRequest(rootview,1);
            }
        });
        return rootview;
    }
}

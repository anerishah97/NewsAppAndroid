package com.example.csci571hw9;

import android.app.ProgressDialog;
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

public class TechnologyFragment extends Fragment {

    private String url = "https://nodejs-backend-new.wl.r.appspot.com/guardian-section?section=technology";
    public ArrayList<NewsCardData> newsData = new ArrayList<>();
    HomePageAdapter adapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    RecyclerView recyclerView;
    public TechnologyFragment() {
        // Required empty public constructor
    }
    public static TechnologyFragment newInstance(String param1, String param2) {
        TechnologyFragment fragment = new TechnologyFragment();
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
        makeHttpRequest(view,0);
    }


    public void makeHttpRequest(final View rootView, int type){

        final LinearLayout linearLayout = rootView.findViewById(R.id.progress_custom_tech);

        if(type == 0)
        {
            recyclerView.setVisibility(View.GONE);
            linearLayout.setVisibility(View.VISIBLE);
        }
        newsData.clear();

        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                     //   progress.dismiss();
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
                                String url = currentObject.getString("urlToShare");
                                String title = currentObject.getString("title");
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
        Log.d("tech sze of array ", list.size() + "");
        adapter = new HomePageAdapter(newsData);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootview =  inflater.inflate(R.layout.fragment_technology, container, false);
        recyclerView = rootview.findViewById(R.id.list_rv_technology);
        mSwipeRefreshLayout = rootview.findViewById(R.id.technology_swiperefresh_items);

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

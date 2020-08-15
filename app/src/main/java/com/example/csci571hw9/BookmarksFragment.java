package com.example.csci571hw9;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.csci571hw9.BookmarkPageAdapter;
import com.example.csci571hw9.NewsCardData;
import com.example.csci571hw9.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BookmarksFragment extends Fragment {
    int totalRequests;
    RecyclerView recyclerView;
    TextView noBookmarksFound;

    BookmarkPageAdapter adapter;
    ArrayList<NewsCardData> data = new ArrayList<>();
    public BookmarksFragment() {
    }


    public void toggleBookmarksVisibility(){
        Log.d("toggle", "toggling");
        noBookmarksFound.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        Log.d("sze in", "inside on resume");
        super.onResume();
        reloadAllBookmarks();
//        data.clear();
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("bookmarks",0);
//        Map<String, ?> map = sharedPreferences.getAll();
//        totalRequests = map.size();
//
//        Log.d("sze resume", "" + map.size());
//
//        final LinearLayout linearLayout = getView().findViewById(R.id.progress_custom_bookmarks);
//        linearLayout.setVisibility(View.VISIBLE);
//
//        for(String key: map.keySet()){
//            makeHttpRequest(key);
//            Log.d("on resume key", key );
//        }
//        Log.d("sze", "bookmarks onresume");
    }

    public static BookmarksFragment newInstance(String param1, String param2) {
        BookmarksFragment fragment = new BookmarksFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void reloadAllBookmarks(){
        data.clear();
        adapter.notifyDataSetChanged();
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("bookmarks",0);
        Map<String, ?> map = sharedPreferences.getAll();
        totalRequests = map.size();

        Log.d("sze resume", "" + map.size());
        if(map.size() != 0){
            final LinearLayout linearLayout = getView().findViewById(R.id.progress_custom_bookmarks);
           linearLayout.setVisibility(View.VISIBLE);
           noBookmarksFound.setVisibility(View.GONE);
            for(String key: map.keySet()){
                makeHttpRequest(key);
                Log.d("on resume key", key );
            }
        }
        if (map.size()==0){
            noBookmarksFound.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        Log.d("sze", "bookmarks onresume");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview =  inflater.inflate(R.layout.fragment_bookmarks, container, false);
        noBookmarksFound = rootview.findViewById(R.id.bookmarks_textview_nobookmarks);
        recyclerView = rootview.findViewById(R.id.list_rv_bookmarks);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        adapter = new BookmarkPageAdapter(data, BookmarksFragment.this);
        //adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);


        //  SharedPreferences sharedPreferences = getContext().getSharedPreferences("bookmarks",0);
        // Map<String, ?> map = sharedPreferences.getAll();
        //totalRequests = map.size()-1;
        //Log.d("sze create", "" + totalRequests);

        //for(String key: map.keySet()){
        //  makeHttpRequest(key);
        //}
        Log.d("sze", "bookmarks oncreate");

        return rootview;
    }

    public void makeHttpRequest(String key){

        String url = "https://nodejs-backend-new.wl.r.appspot.com/guardian-detailed-article-android?id=" + key;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            totalRequests--;
                            if(totalRequests == 0){
                                final LinearLayout linearLayout = getView().findViewById(R.id.progress_custom_bookmarks);
                                linearLayout.setVisibility(View.GONE);

                                settingUpRecyclerView(data);
                            }
                            final JSONObject jsonObject = new JSONObject(response);
                            String section = jsonObject.getString("section");
                            String date = jsonObject.getString("date");
                            section = section.substring(0,1).toUpperCase() + section.substring(1,section.length());
                            String title = jsonObject.getString("title");
                            String image = jsonObject.getString("image");
                            String id = jsonObject.getString("id");
                            String url = jsonObject.getString("urlToShare");
                            NewsCardData currentCardData = new NewsCardData(id,image,title, section,date, url);
                            data.add(currentCardData);

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
        //  Log.d("bookmarks sze of array ", list.size() + "");
        adapter = new BookmarkPageAdapter(data, BookmarksFragment.this);
        adapter.notifyDataSetChanged();
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

    }

    public void settingUpRecyclerView(List<NewsCardData> data){
        setAdapter(data);
    }
}

package com.example.csci571hw9;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import androidx.appcompat.widget.SearchView;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;
    private AutoSuggestAdapter autoSuggestAdapter;
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(HomeFragment.newInstance("", ""));
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            openFragment(HomeFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_bookmarks:
                            openFragment(BookmarksFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_headlines:
                            openFragment(HeadlinesFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_trending:
                            openFragment(TrendingFragment.newInstance("",""));
                            return true;
                    }
                    return false;
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.search_view_toolbar);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, Search.class)));
        }

        assert searchView != null;
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("");
        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        final String dataArr[] = {"Apple" , "Amazon" , "Amd", "Microsoft", "Microwave", "MicroNews", "Intel", "Intelligence"};
        autoSuggestAdapter = new AutoSuggestAdapter(this,
                android.R.layout.simple_dropdown_item_1line);
        searchAutoComplete.setAdapter(autoSuggestAdapter);
        autoSuggestAdapter.clear();
        autoSuggestAdapter.notifyDataSetChanged();

        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                searchAutoComplete.clearListSelection();
                String searchString=(String)parent.getItemAtPosition(position);
                searchAutoComplete.setText(""+searchString);
                autoSuggestAdapter.clear();
                autoSuggestAdapter.notifyDataSetChanged();

            }
        });

        searchAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().isEmpty() || s.toString().length() ==0){
                    autoSuggestAdapter.clear();
                    autoSuggestAdapter.notifyDataSetChanged();
                }
                Log.d("hola before", s.toString());

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("hola on", s.toString());
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
//               makeAutosuggestRequest(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("hola after", s.toString());
            }
        });

        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(searchAutoComplete.getText())) {
                        String textToSearch = searchAutoComplete.getText().toString();
                        if(textToSearch.length()>=3)
                        {
                            makeAutosuggestRequest(searchAutoComplete.getText().toString());
                        }
                        else
                        {
                            autoSuggestAdapter.setData(new ArrayList<String>());
                            autoSuggestAdapter.clear();
                            autoSuggestAdapter.notifyDataSetChanged();

                        }
                    }
                    else if(TextUtils.isEmpty(searchAutoComplete.getText())){
                        Log.d("hola", "textutils empty");
                        autoSuggestAdapter.setData(new ArrayList<String>());
                        autoSuggestAdapter.clear();
                        autoSuggestAdapter.notifyDataSetChanged();
                    }
                }
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void makeAutosuggestRequest(final String query){

        String url = "https://api.cognitive.microsoft.com/bing/v7.0/suggestions?mkt=en-US&q="+query;
        Log.d("test", url);

        RequestQueue queue = Volley.newRequestQueue(this);
        final List<String> stringList = new ArrayList<>();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try{

                    JSONArray  jsonArray= response.getJSONArray("suggestionGroups");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    JSONArray searchSuggestions = obj.getJSONArray("searchSuggestions");

                    Log.d("testt", searchSuggestions.length()  + "");
                    for(int i=0;i< Math.min(searchSuggestions.length(), 5);i++){
                        //data[i]
                        JSONObject currentSuggestion = searchSuggestions.getJSONObject(i);
                        String suggestionText = currentSuggestion.getString("displayText");
                        Log.d("test", suggestionText);
                        stringList.add(suggestionText);

                    }
                    if(query.isEmpty()){
                        Log.d("hola","query empty");
                    }
                    autoSuggestAdapter.setData(stringList);
                    autoSuggestAdapter.notifyDataSetChanged();
                }

                catch (Exception e){
                    Log.d("Error", e.toString());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tset", error.getMessage());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Ocp-Apim-Subscription-Key", "13014038bc0f4b488c82ede340a11e27");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
}


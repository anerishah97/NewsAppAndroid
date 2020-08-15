package com.example.csci571hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class TrendingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    LineChart chart;
    EditText searchTerm;
    public TrendingFragment() {
    }

    public static TrendingFragment newInstance(String param1, String param2) {
        TrendingFragment fragment = new TrendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        parseChartData(chart, searchTerm.getText().toString());

    }

    public void parseChartData(final Chart chart, final String keyword){

        String url = "https://nodejs-backend-new.wl.r.appspot.com/trends?keyword=" + keyword;
        RequestQueue queue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response)
                    {
                        //   progress.dismiss();
                        try{
                            List<Entry> entries = new ArrayList<>();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonResponse = (JSONObject)jsonObject.get("response");
                            JSONObject jsonObjDefault = (JSONObject)jsonResponse.get("default");
                            JSONArray jsonTimeline = jsonObjDefault.getJSONArray("timelineData");

                            for(int i=0;i<jsonTimeline.length();i++){
                                JSONObject timelineJSONObj= jsonTimeline.getJSONObject(i);
                                JSONArray valueArray = timelineJSONObj.getJSONArray("value");
                                String value = valueArray.getString(0);
                                Log.d("valuee", value);
                                entries.add(new Entry(i, Integer.parseInt(value)));
                            }
                            chart.invalidate();
                            //chart.getXA
                                LineDataSet dataSet = new LineDataSet(entries, "Trending Chart for "+keyword);
                            dataSet.setColor(R.color.colorPrimaryDark);
                            dataSet.setValueTextColor(R.color.colorPrimaryDark); // styling, ...
                            LineData lineData = new LineData(dataSet);
                            dataSet.setCircleColor(R.color.colorPrimaryDark);
                            dataSet.setCircleHoleColor(R.color.colorPrimaryDark);
                            dataSet.setColor(R.color.colorPrimaryDark);
                            dataSet.setLineWidth(1f);
                            dataSet.setCircleRadius(3f);
                            dataSet.setDrawCircleHole(false);
                            dataSet.setValueTextSize(9f);

                            //dataSet.setDrawFilled(true);
                            chart.setData(lineData);

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



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview =  inflater.inflate(R.layout.fragment_trending, container, false);
        chart = rootview.findViewById(R.id.chart);
        chart.getXAxis().setDrawGridLines(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.setGridBackgroundColor(R.color.white);
        Legend legend = chart.getLegend();
        legend.setTextSize(16);
        searchTerm = rootview.findViewById(R.id.trending_edittext);
        searchTerm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((actionId == EditorInfo.IME_ACTION_SEND)) {
                    if(!searchTerm.getText().toString().isEmpty()){
                        parseChartData(chart,searchTerm.getText().toString());
                    }
                    Log.i("Trending",searchTerm.getText().toString());
                }
                return false;
            }
        });
        parseChartData(chart, "CoronaVirus");
        return rootview;
    }
}
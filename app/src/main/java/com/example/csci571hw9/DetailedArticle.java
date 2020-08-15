package com.example.csci571hw9;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

public class DetailedArticle extends AppCompatActivity {

    ImageView backButton, bookamark, twitter, articleImage;
    TextView articleTitle, articleDescription, articleSection, articleDate, articleShare, toolbarTitle;
    String id;
    SharedPreferences sharedPreferences;
    CardView cardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoToolBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_article);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedPreferences = getSharedPreferences("bookmarks",0);

        backButton = findViewById(R.id.detailed_article_toolbar_back);
        articleImage = findViewById(R.id.detailed_article_image);
        bookamark = findViewById(R.id.detailed_article_toolbar_bookmark);
        twitter = findViewById(R.id.detailed_article_toolbar_twitter);

        articleTitle = findViewById(R.id.detailed_article_title);
        toolbarTitle = findViewById(R.id.detailed_article_toolbar_title);
        articleDescription = findViewById(R.id.detailed_article_description);
        articleSection = findViewById(R.id.detailed_article_section);
        articleDate = findViewById(R.id.detailed_article_date);
        articleShare = findViewById(R.id.detailed_article_view_full_article);
        articleShare.setText(Html.fromHtml("<u>View Full Article</u>"));
        cardView = findViewById(R.id.detailed_article_entire_layout);

        id = getIntent().getExtras().getString("articleId");
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if(sharedPreferences.contains(id)){
            bookamark.setImageResource(R.drawable.ic_bookmark_black_24dp);
        }

        bookamark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.contains(id)){
                    //remove from shared pref
                    //change color of bookmark
                    editor.remove(id);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "\""+ articleTitle.getText().toString()+ "\" removed from bookmarks", Toast.LENGTH_SHORT).show();
                    bookamark.setImageResource(R.drawable.ic_turned_in_not_black_24dp);
                }

                else
                {
                    //write in sharedpref
                    editor.putString(id,"true");
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "\""+articleTitle.getText().toString()+ "\" added to bookmarks", Toast.LENGTH_SHORT).show();
                    bookamark.setImageResource(R.drawable.ic_bookmark_black_24dp);

                }
            }
        });

        makeHTTPRequest();
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void makeHTTPRequest(){
        String url = "https://nodejs-backend-new.wl.r.appspot.com/guardian-detailed-article-android" + "?id=" + id;
        final LinearLayout linearLayout = findViewById(R.id.progress_custom_detailedview);
        linearLayout.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            linearLayout.setVisibility(View.GONE);
                            cardView.setVisibility(View.VISIBLE);
                            final JSONObject jsonObject = new JSONObject(response);
                            toolbarTitle.setText(jsonObject.getString("title"));
                            String section = jsonObject.getString("section");
                            section = section.substring(0,1).toUpperCase() + section.substring(1,section.length());
                            articleSection.setText(section);
                            articleTitle.setText(jsonObject.getString("title"));
                            articleDescription.setText(Html.fromHtml(jsonObject.getString("bodyhtml")));
                            String imageUrl = jsonObject.getString("image");
                            if(!imageUrl.isEmpty()){
                                Picasso.get().load(imageUrl).fit().centerCrop().into(articleImage);
                            }

                            final String urlToShare = jsonObject.getString("urlToShare");
                            articleShare.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToShare));
                                    startActivity(browserIntent);
                                }
                            });

                            twitter.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String url = "http://www.twitter.com/intent/tweet?text=Check Out This Link&url=" + urlToShare +"&hashtags=CSCI571NewsSearch";
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(url));
                                    startActivity(i);
                                }
                            });

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
}

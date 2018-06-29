package com.example.shannonyan.flicks;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.shannonyan.flicks.models.Config;
import com.example.shannonyan.flicks.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.shannonyan.flicks.MovieListActivity.API_BASE_URL;
import static com.example.shannonyan.flicks.MovieListActivity.API_KEY_PARAM;

public class MovieDetailsActivity extends AppCompatActivity {

    // the movie to display
    Movie movie;

    // the view objects
    TextView tvTitle;
    TextView tvOverview;
    RatingBar rbVoteAverage;
    AsyncHttpClient client;
    Config config;
    ImageView im;


    public static final String VIDEO_ID = "id";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        // resolve the view objects
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvOverview = (TextView) findViewById(R.id.tvOverview);
        rbVoteAverage = (RatingBar) findViewById(R.id.rbVoteAverage);
        //movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        config = (Config) Parcels.unwrap(getIntent().getParcelableExtra(Config.class.getSimpleName()));


        // unwrap the movie passed in via intent, using its simple name as a key
        movie = (Movie) Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        // set the title and overview
        tvTitle.setText(movie.getTitle());
        tvOverview.setText(movie.getOverview());

        client = new AsyncHttpClient();

        final String backdropUrl = config.getImageUrl(config.getBackdropSize(),movie.getBackdropPath());
        im = findViewById(R.id.imageView);
        //GlideApp.with(this).load(backdropUrl).into(im);

        GlideApp.with(this)
                .load(backdropUrl)
                .transform(new RoundedCornersTransformation(15, 0))
                .into(im);


        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage = voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }


    public void onClick(View view){
        String url = API_BASE_URL + String.format("/movie/%s/videos", movie.getId());
        RequestParams params = new RequestParams();
        params.put(API_KEY_PARAM, getString(R.string.api_key));

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    JSONArray results = response.getJSONArray("results");
                    String videoID = null;
                    videoID = results.getJSONObject(0).getString("key");

                    if (videoID != null) {
                        Intent intent = new Intent(MovieDetailsActivity.this, MovieTrailerActivity.class);
                        intent.putExtra(VIDEO_ID, videoID);
                        //imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
                        startActivity(intent);
                    }

                } catch (JSONException e) {
                    Log.i("MovieTrailerActivity", "failed");
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("MovieTrailerActivity", "failed");
            }
        });

    }
}


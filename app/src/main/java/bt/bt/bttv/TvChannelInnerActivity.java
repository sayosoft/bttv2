package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.helper.WebRequest;

public class TvChannelInnerActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_VIDEO_INFO = "channels";
    private static final String TAG_VIDEO_RELATED_INFO = "related";
    private static final String TAG_VIDEO_ID = "channel_id";
    private static final String TAG_VIDEO_TITLE = "channel_name";
    private static final String TAG_VIDEO_CATEGORY = "channel_category";
    private static final String TAG_VIDEO_POSTER = "channel_poster";
    private static final String TAG_VIDEO_GENRES = "channel_genres";
    private static final String TAG_VIDEO_DESCRIPTION = "channel_description";
    private static final String TAG_VIDEO_SOURCE = "channel_stream_url";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    JSONArray related = null;
    String video_source = null;
    String MovieTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_channel_inner);

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        new GetMovies().execute();
    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.relatedGallery);
        if (imgs != null) {
            for (int i = 0; i < imgs.length(); i++) {

                JSONObject m = imgs.getJSONObject(i);
                String c_genres = m.getString(TAG_VIDEO_GENRES);
                List<String> genre = Arrays.asList(c_genres.split("\\s*,\\s*"));
                Integer c_id = Integer.parseInt(m.getString(TAG_VIDEO_ID));
                String c_title = m.getString(TAG_VIDEO_TITLE);
                //String c_category = m.getString(TAG_VIDEO_CATEGORY);
                String c_poster = m.getString(TAG_VIDEO_POSTER);
                String FinalImage = getString(R.string.url_base_image) + c_poster;
                if (imageGallery != null) {
                    imageGallery.addView(getImageView(FinalImage, c_id));
                    System.gc();
                }
            }
        } else {
            Toast.makeText(getApplicationContext(), "Error Getting Episodes Data",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }


    private void SetMovieValues(JSONArray movie) throws JSONException {
        TextView movietitle = (TextView) findViewById(R.id.movietitle);
        TextView movieduration = (TextView) findViewById(R.id.movieduration);
        TextView moviegenre = (TextView) findViewById(R.id.moviegenre);
        TextView moviedesc = (TextView) findViewById(R.id.moviedesc);
        TextView moviecast = (TextView) findViewById(R.id.moviecast);
        TextView moviedirector = (TextView) findViewById(R.id.moviedirector);
        ImageView movieposter = (ImageView) findViewById(R.id.imageView2);

        String Ret = addrelated(related);

        if (Ret == "NOTOK") {
            Toast.makeText(getApplicationContext(), "Could Not Find Related Videos",
                    Toast.LENGTH_LONG).show();
        }

        for (int i = 0; i < movie.length(); i++) {

            JSONObject m = movie.getJSONObject(i);
            video_source = "";
            video_source = m.getString(TAG_VIDEO_SOURCE);
            String mpost = getString(R.string.url_base_image) + m.getString(TAG_VIDEO_POSTER);
            Ion.with(movieposter).load(mpost);
            assert movietitle != null;
            movietitle.setText(m.getString(TAG_VIDEO_TITLE));

            assert moviedesc != null;
            moviedesc.setText(m.getString(TAG_VIDEO_DESCRIPTION));

            MovieTitle = m.getString(TAG_VIDEO_TITLE);
        }
    }

    private View getImageView(String newimage, Integer uid) {
        ImageView imageView = new ImageView(getApplicationContext());

        final float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (130 * scale);
        int dpHeightInPx = (int) (180 * scale);
        //LinearLayout.LayoutParams.WRAP_CONTENT
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
        lp.setMargins(0, 0, 20, 30);
        imageView.setLayoutParams(lp);
        imageView.setId(uid);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Integer gid = v.getId();
                PlayMovie(gid);
            }
        });
        Ion.with(imageView).load(newimage);
        return imageView;
    }


    public void PlayMoviePlayer(View view) {
        startActivity(new Intent(this, PlayVideoNew.class).putExtra("vurl", video_source).putExtra("title", MovieTitle));
    }

    public void PlayMovie(Integer gid) {
        startActivity(new Intent(this, TvChannelInnerActivity.class).putExtra("vid", gid));
    }

    private String addrelated(JSONArray related) {
        if (related != null) {
            try {
                addImagesToThegallery(related);
                return "OK";
            } catch (JSONException e) {
                e.printStackTrace();
                return "NOTOK";
            }
        } else {
            Toast.makeText(getApplicationContext(), "Error Getting Related Videos",
                    Toast.LENGTH_LONG).show();
        }
        return "NOTOK";
    }

    private ArrayList<HashMap<String, String>> ParseJSONMovie(String json) {
        if (json != null) {
            try {
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);

                JSONArray movies;
                JSONArray relatedVideos;
                try {
                    movies = jsonObj.getJSONArray(TAG_VIDEO_INFO);
                    relatedVideos = jsonObj.getJSONArray(TAG_VIDEO_RELATED_INFO);
                } catch (JSONException e) {
                    movies = null;
                    relatedVideos = null;
                    e.printStackTrace();
                }
                mvs = movies;
                if (relatedVideos != null) {
                    related = relatedVideos;
                } else {
                    related = null;
                }
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject c = movies.getJSONObject(i);

                    String m_id = c.getString(TAG_VIDEO_ID);
                    String m_title = c.getString(TAG_VIDEO_TITLE);
                    String m_category = c.getString(TAG_VIDEO_CATEGORY);
                    String m_poster = c.getString(TAG_VIDEO_POSTER);
                    String m_genres = c.getString(TAG_VIDEO_GENRES);

                    String m_description = c.getString(TAG_VIDEO_DESCRIPTION);
                    String m_source = c.getString(TAG_VIDEO_SOURCE);

                    HashMap<String, String> movie = new HashMap<String, String>();

                    movie.put(TAG_VIDEO_ID, m_id);
                    movie.put(TAG_VIDEO_TITLE, m_title);
                    movie.put(TAG_VIDEO_CATEGORY, m_category);
                    movie.put(TAG_VIDEO_POSTER, m_poster);
                    movie.put(TAG_VIDEO_GENRES, m_genres);

                    movie.put(TAG_VIDEO_DESCRIPTION, m_description);
                    movie.put(TAG_VIDEO_SOURCE, m_source);

                    onlymovie.put(TAG_VIDEO_ID, m_id);
                    onlymovie.put(TAG_VIDEO_TITLE, m_title);
                    onlymovie.put(TAG_VIDEO_CATEGORY, m_category);
                    onlymovie.put(TAG_VIDEO_POSTER, m_poster);
                    onlymovie.put(TAG_VIDEO_GENRES, m_genres);

                    onlymovie.put(TAG_VIDEO_DESCRIPTION, m_description);
                    onlymovie.put(TAG_VIDEO_SOURCE, m_source);

                    moviesList.add(movie);
                    moviesList2.add(onlymovie);
                }

                return moviesList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private class GetMovies extends AsyncTask<Void, Void, Void> {

        ArrayList<HashMap<String, String>> moviesList;
        ProgressDialog proDialog;
        String TestMovies = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proDialog = new ProgressDialog(TvChannelInnerActivity.this);
            proDialog.setMessage(getString(R.string.msg_progress_dialog));
            proDialog.setCancelable(false);
            if (!proDialog.isShowing()) {
                proDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String MoviesStr = webreq.makeWebServiceCall(getString(R.string.url_get_tv_channels) + getIntent().getStringExtra("vid"), WebRequest.GETRequest);

            moviesList = ParseJSONMovie(MoviesStr);
            TestMovies = MoviesStr;
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }
            try {
                SetMovieValues(mvs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

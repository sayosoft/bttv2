package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class TvChannelInnerActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_VIDEO_INFO = "channels";
    private static final String TAG_VIDEO_RELATED_INFO = "related";
    private static final String TAG_VIDEO_ID = "channel_id";
    private static final String TAG_VIDEO_TITLE = "channel_name";
    private static final String TAG_VIDEO_CATEGORY = "channel_category";
    private static final String TAG_VIDEO_POSTER = "channel_poster";
    private static final String TAG_VIDEO_GENRES = "channel_genres";
    //private static final String TAG_VIDEO_DIRECTOR = "video_director";
    //private static final String TAG_VIDEO_ACTING = "video_acting";
    //private static final String TAG_VIDEO_LANGUAGE= "video_language";
    //private static final String TAG_VIDEO_DURATION= "video_duration";
    private static final String TAG_VIDEO_DESCRIPTION = "channel_description";
    private static final String TAG_VIDEO_SOURCE = "channel_stream_url";
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/getchannel/";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    JSONArray related = null;
    String video_source = null;
    String MovieTitle = null;
    private Integer images[] = {R.drawable.mm1, R.drawable.mm3, R.drawable.mm4, R.drawable.mm3, R.drawable.mm1, R.drawable.mm4};
    //private static final String TAG_VIDEO_GENRES_TEXT= "video_genre_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_channel_inner);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer value = extras.getInt("vid");
            moviesurl = "";
            String furl = "http://bflix.ignitecloud.in/jsonApi/getchannel/";
            moviesurl = furl + value;
            Log.i("INTENTVALUE:", moviesurl);
            //The key argument here must match that used in the other activity
        } else {
            Log.i("INTENTVALUE:", moviesurl);
        }

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        new GetMovies().execute();

        //addImagesToThegallery();


    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.relatedGallery);
        if (imgs != null) {
            for (int i = 0; i < imgs.length(); i++) {

                JSONObject m = imgs.getJSONObject(i);
                String c_genres = m.getString(TAG_VIDEO_GENRES);
                //String c_genres_text = m.getString(TAG_VIDEO_GENRES_TEXT);
                List<String> genre = Arrays.asList(c_genres.split("\\s*,\\s*"));


                Integer c_id = Integer.parseInt(m.getString(TAG_VIDEO_ID));

                String c_title = m.getString(TAG_VIDEO_TITLE);
                //String c_category = m.getString(TAG_VIDEO_CATEGORY);
                String c_poster = m.getString(TAG_VIDEO_POSTER);
                Log.i("Movies 158 ", "> " + c_title);
                Log.i("Movie Genre 158 ", "> " + c_genres);

                String FinalImage = "http://bflix.ignitecloud.in/uploads/images/" + c_poster;
                if (imageGallery != null) {
                    //Action
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
            String mpost = "http://bflix.ignitecloud.in/uploads/images/" + m.getString(TAG_VIDEO_POSTER);
            Ion.with(movieposter)

                    .load(mpost);
            assert movietitle != null;
            movietitle.setText(m.getString(TAG_VIDEO_TITLE));

            assert moviedesc != null;
            moviedesc.setText(m.getString(TAG_VIDEO_DESCRIPTION));

            MovieTitle = m.getString(TAG_VIDEO_TITLE);


        }

    }

    private View getImageView(String newimage, Integer uid) {
        /* "http://bflix.ignitecloud.in/uploads/images/1.jpg" */
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
                //v.getId() will give you the image id

            }
        });
        //imageView.setAdjustViewBounds(true);

        //imageView.setMaxWidth(400);
        //imageView.setMaxWidth(500);
        //imageView.setImageResource(newimage);
        Ion.with(imageView)

                .load(newimage);

        return imageView;
    }


    public void PlayMoviePlayer(View view) {
        Intent intent = new Intent(this, PlayVideoNew.class);
        intent.putExtra("vurl", video_source);
        intent.putExtra("title", MovieTitle);
        startActivity(intent);
    }

    public void PlayMovie(Integer gid) {

        Intent intent = new Intent(this, TvChannelInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
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
// Hashmap for ListView
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();


                JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray movies;
                JSONArray relatedVideos;
                try {
                    movies = jsonObj.getJSONArray(TAG_VIDEO_INFO);
                    relatedVideos = jsonObj.getJSONArray(TAG_VIDEO_RELATED_INFO);
                    Log.i("CategoriesObj 410: ", "> " + movies);
                } catch (JSONException e) {
                    movies = null;
                    relatedVideos = null;
                    Log.i("CategoriesObj 413: ", "> " + "Movies Object Null");
                    e.printStackTrace();
                }
                mvs = movies;
                if (relatedVideos != null) {
                    related = relatedVideos;
                } else {
                    related = null;
                }
// looping through All Students
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject c = movies.getJSONObject(i);

                    String m_id = c.getString(TAG_VIDEO_ID);
                    String m_title = c.getString(TAG_VIDEO_TITLE);
                    String m_category = c.getString(TAG_VIDEO_CATEGORY);
                    String m_poster = c.getString(TAG_VIDEO_POSTER);
                    String m_genres = c.getString(TAG_VIDEO_GENRES);

                    String m_description = c.getString(TAG_VIDEO_DESCRIPTION);
                    String m_source = c.getString(TAG_VIDEO_SOURCE);


                    Log.i("Movies 425 ", "> " + m_title);


// Phone node is JSON Object
                    //JSONObject phone = c.getJSONObject(TAG_STUDENT_PHONE);
                    //String mobile = phone.getString(TAG_STUDENT_PHONE_MOBILE);
                    //String home = phone.getString(TAG_STUDENT_PHONE_HOME);

// tmp hashmap for single student
                    HashMap<String, String> movie = new HashMap<String, String>();


// adding every child node to HashMap key => value
                    movie.put(TAG_VIDEO_ID, m_id);
                    movie.put(TAG_VIDEO_TITLE, m_title);
                    movie.put(TAG_VIDEO_CATEGORY, m_category);
                    movie.put(TAG_VIDEO_POSTER, m_poster);
                    movie.put(TAG_VIDEO_GENRES, m_genres);

                    movie.put(TAG_VIDEO_DESCRIPTION, m_description);
                    movie.put(TAG_VIDEO_SOURCE, m_source);

                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);

                    onlymovie.put(TAG_VIDEO_ID, m_id);
                    onlymovie.put(TAG_VIDEO_TITLE, m_title);
                    onlymovie.put(TAG_VIDEO_CATEGORY, m_category);
                    onlymovie.put(TAG_VIDEO_POSTER, m_poster);
                    onlymovie.put(TAG_VIDEO_GENRES, m_genres);

                    onlymovie.put(TAG_VIDEO_DESCRIPTION, m_description);
                    onlymovie.put(TAG_VIDEO_SOURCE, m_source);


// adding student to students list

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

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMovies extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> moviesList;
        ProgressDialog proDialog;
        String TestMovies = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(TvChannelInnerActivity.this);
            proDialog.setMessage("Loading Video...");
            proDialog.setCancelable(false);
            if (!proDialog.isShowing()) {
                proDialog.show();
            }

        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();

// Making a request to url and getting response
            String MoviesStr = webreq.makeWebServiceCall(moviesurl, WebRequest.GETRequest);

            Log.i("Response: ", "> " + MoviesStr);

            moviesList = ParseJSONMovie(MoviesStr);
            TestMovies = MoviesStr;


            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            proDialog.dismiss();
            Log.i("Movies: ", "> " + TestMovies);
            //studentList = ParseJSON(TestString);
            ArrayList<HashMap<String, String>> movList;

            movList = ParseJSONMovie(TestMovies);

            Log.i("Movies 2: ", "> " + movList);
// Dismiss the progress dialog
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }
/**
 * Updating received data from JSON into ListView
 * */
          /*   Log.i("Images : ", "> " +"PreExIG");
           // addImagesToThegallery(movList);
            Log.i("Images : ", "> " +"PostExIG");
            */
            try {
                SetMovieValues(mvs);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}

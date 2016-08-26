package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

public class TvShowInnerActivity extends AppCompatActivity {

    // JSON Node names
    private static final String TAG_VIDEO_INFO = "videos";
    private static final String TAG_VIDEO_EPISODES_INFO = "episodes";
    private static final String TAG_VIDEO_ID = "video_id";
    private static final String TAG_VIDEO_TITLE = "video_title";
    private static final String TAG_VIDEO_CATEGORY = "video_category";
    private static final String TAG_VIDEO_POSTER = "video_poster";
    private static final String TAG_VIDEO_GENRES = "video_genre";
    private static final String TAG_VIDEO_DIRECTOR = "video_director";
    private static final String TAG_VIDEO_ACTING = "video_acting";
    private static final String TAG_VIDEO_LANGUAGE = "video_language";
    private static final String TAG_VIDEO_DURATION = "video_duration";
    private static final String TAG_VIDEO_DESCRIPTION = "video_description";
    private static final String TAG_VIDEO_SOURCE = "video_source_url";
    private static final String TAG_VIDEO_TVSHOW = "video_tvshow_id";
    private static final String TAG_VIDEO_GENRES_TEXT = "video_genre_text";
    private static final String TAG_VIDEO_SEASON_NO = "video_season_no";
    private static final String TAG_VIDEO_EPISODE_NO = "video_episode_no";
    private static final String TAG_VIDEO_DAY = "video_day";
    private static final String TAG_VIDEO_MONTH = "video_month";
    private static final String TAG_VIDEO_YEAR = "video_year";
    private static final String TAG_VIDEO_RATING = "video_rating";
    private static final String TAG_VIDEO_RESUME = "video_resume";
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/getvod/";
    private static String moviesurl2 = "http://bflix.ignitecloud.in/jsonApi/vod/video_tvshow_id/";
    //private static String moviesurl3 = "http://bflix.ignitecloud.in/jsonApi/gettvid/";
    private static Integer TestTVId = null;
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> Eplist = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    JSONArray episodes = null;
    JSONArray eps = null;
    String video_source = null;
    String MovieTitle = null;
    String VideoID = null;
    String VResume = null;
    String MovieDuration = null;
    String MovieGenre = null;
    String MovieDesciption = null;
    String MovieCast = null;
    String MovieDirector = null;
    private Integer images[] = {R.drawable.mm1, R.drawable.mm3, R.drawable.mm4, R.drawable.mm3, R.drawable.mm1, R.drawable.mm4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tv_show_inner);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer value = extras.getInt("vid");
            moviesurl = "";

            String furl = "http://bflix.ignitecloud.in/jsonApi/getvod/";

            moviesurl = furl + value;


            Log.i("INTENTVALUE:", moviesurl);
            //The key argument here must match that used in the other activity
        } else {
            Log.i("INTENTVALUE:", moviesurl);
        }

        //new GetTVShowID().execute();
        //String value2 = TestTVId;
        //moviesurl2 = "";

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        new GetMovies().execute();
        // String furl2 = "http://bflix.ignitecloud.in/jsonApi/vod/video_tvshow_id/"+TestTVId;
        // moviesurl2 = furl2;
        //Log.i("testtvurl:",moviesurl2);
        //Log.i("testtvshowid: ", "> " + TestTVId);
        //new GetEpisodes().execute();


        //addImagesToThegallery();


    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.relatedGalleryImage);
        LinearLayout imageGalleryText = (LinearLayout) findViewById(R.id.relatedGalleryText);


        if (imgs != null) {
            for (int i = 0; i < imgs.length(); i++) {

                JSONObject m = imgs.getJSONObject(i);
                String c_genres = m.getString(TAG_VIDEO_GENRES);
                String c_genres_text = m.getString(TAG_VIDEO_GENRES_TEXT);
                List<String> genre = Arrays.asList(c_genres.split("\\s*,\\s*"));


                Integer c_id = Integer.parseInt(m.getString(TAG_VIDEO_ID));

                String c_title = m.getString(TAG_VIDEO_TITLE);
                String c_season = "S" + m.getString(TAG_VIDEO_SEASON_NO);
                String c_episode = c_season + " E" + m.getString(TAG_VIDEO_EPISODE_NO);
                //String c_category = m.getString(TAG_VIDEO_CATEGORY);
                String c_poster = m.getString(TAG_VIDEO_POSTER);
                Log.i("Movies 158 ", "> " + c_title);
                Log.i("Episode Nums ", "> " + c_episode);
                Log.i("Movie Genre 158 ", "> " + c_genres);

                String FinalImage = "http://bflix.ignitecloud.in/uploads/images/" + c_poster;
                if (imageGallery != null) {
                    //Action
                    imageGallery.addView(getImageView(FinalImage, c_id));

                    final float scale = getResources().getDisplayMetrics().density;
                    int dpWidthInPx = (int) (130 * scale);
                    int dpHeightInPx = (int) (180 * scale);
                    //LinearLayout.LayoutParams.WRAP_CONTENT
                    LinearLayout.LayoutParams lpt = new LinearLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
                    lpt.setMargins(0, 0, 20, 30);


                    TextView tv = new TextView(this);
                    tv.setLayoutParams(lpt);

                    tv.setText(c_episode);
                    tv.setTextColor(Color.parseColor("#FFFFFF"));

                    imageGalleryText.addView(tv);
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
        TextView moviedate = (TextView) findViewById(R.id.movieDate);
        ImageView movieposter = (ImageView) findViewById(R.id.imageView2);
        RatingBar rate_bar = (RatingBar) findViewById(R.id.ratingBar);

        LayerDrawable stars = (LayerDrawable) rate_bar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);

        String Ret = addepisodes(episodes);

        if (Ret == "NOTOK") {
            Toast.makeText(getApplicationContext(), "Could Not Find Other Episodes",
                    Toast.LENGTH_LONG).show();

        }


        for (int i = 0; i < movie.length(); i++) {

            JSONObject m = movie.getJSONObject(i);
            /* TestTVId = 0;
            TestTVId = m.getInt(TAG_VIDEO_TVSHOW);
            Log.i("testtvshowid2 ", "> " + TestTVId); */
            video_source = "";
            video_source = m.getString(TAG_VIDEO_SOURCE);
            String episode_no = "Season " + m.getString(TAG_VIDEO_SEASON_NO) + " Episode " + m.getString(TAG_VIDEO_EPISODE_NO);
            String episode_date = "" + m.getString(TAG_VIDEO_DAY) + " " + m.getString(TAG_VIDEO_MONTH) + " " + m.getString(TAG_VIDEO_YEAR);

            String mpost = "http://bflix.ignitecloud.in/uploads/images/" + m.getString(TAG_VIDEO_POSTER);
            movieposter.setScaleType(ImageView.ScaleType.FIT_XY);
            Ion.with(movieposter)
                    .placeholder(R.drawable.loadingposter)

                    .load(mpost);
            assert movietitle != null;
            movietitle.setText(m.getString(TAG_VIDEO_TITLE));
            assert movieduration != null;
            movieduration.setText(episode_no);
            assert moviegenre != null;
            moviegenre.setText(m.getString(TAG_VIDEO_GENRES_TEXT));
            assert moviedesc != null;
            moviedesc.setText(m.getString(TAG_VIDEO_DESCRIPTION));
            assert moviecast != null;
            moviecast.setText(m.getString(TAG_VIDEO_ACTING));
            assert moviedirector != null;
            moviedirector.setText(m.getString(TAG_VIDEO_DIRECTOR));
            assert moviedate != null;
            moviedate.setText(episode_date);
            assert rate_bar != null;
            rate_bar.setRating(Float.parseFloat(m.getString(TAG_VIDEO_RATING)));

            MovieTitle = m.getString(TAG_VIDEO_TITLE);
            VideoID = m.getString(TAG_VIDEO_ID);
            VResume = m.getString(TAG_VIDEO_RESUME);

            MovieDuration = m.getString(TAG_VIDEO_DURATION);
            MovieGenre = m.getString(TAG_VIDEO_GENRES_TEXT);
            MovieDesciption = m.getString(TAG_VIDEO_DESCRIPTION);
            MovieCast = m.getString(TAG_VIDEO_ACTING);
            MovieDirector = m.getString(TAG_VIDEO_DIRECTOR);


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
        intent.putExtra("vid", VideoID);
        intent.putExtra("vresume", VResume);
        intent.putExtra("duration", MovieDuration);
        intent.putExtra("desc", MovieDesciption);
        intent.putExtra("genre", MovieGenre);
        intent.putExtra("cast", MovieCast);
        intent.putExtra("director", MovieDirector);
        startActivity(intent);
    }

    public void PlayMovie(Integer gid) {

        Intent intent = new Intent(this, TvShowInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
    }

    private String addepisodes(JSONArray episodes) {
        if (episodes != null) {
            try {
                addImagesToThegallery(episodes);
                return "OK";
            } catch (JSONException e) {
                e.printStackTrace();
                return "NOTOK";
            }
        } else {
            Toast.makeText(getApplicationContext(), "Error Getting Episodes Data 2",
                    Toast.LENGTH_LONG).show();
        }

        return "NOTOK";
    }

    /**
     * Async task class to get json by making HTTP call
     */
/*
    private class GetEpisodes extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> moviesList;
        ArrayList<HashMap<String, String>> epsList1;
        ProgressDialog proDialog;

        String TestEps = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(TvShowInnerActivity.this);
            proDialog.setMessage("Loading Video...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();


// Making a request to url and getting response

            String Episodes = webreq.makeWebServiceCall(moviesurl2, WebRequest.GETRequest);


            Log.i("Episode Response: ", "> " + Episodes);


            epsList1 = ParseJSONEpisodes(Episodes);

            TestEps = Episodes;


            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);

            Log.i("Elist 1: ", "> " + TestEps);
            //studentList = ParseJSON(TestString);

            ArrayList<HashMap<String, String>> eList;


            eList = ParseJSONEpisodes(TestEps);


            Log.i("EList: ", "> " + eList);
// Dismiss the progress dialog
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }

            try {
                addImagesToThegallery(eps);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    */
    private ArrayList<HashMap<String, String>> ParseJSONMovie(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();


                JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray tvepisodes;
                JSONArray movies;

                try {
                    movies = jsonObj.getJSONArray(TAG_VIDEO_INFO);
                    tvepisodes = jsonObj.getJSONArray(TAG_VIDEO_EPISODES_INFO);
                    Log.i("CategoriesObj 410: ", "> " + movies);
                } catch (JSONException e) {
                    movies = null;
                    tvepisodes = null;
                    Log.i("CategoriesObj 413: ", "> " + "Movies Object Null");
                    e.printStackTrace();
                }
                mvs = movies;
                episodes = tvepisodes;

// looping through All Students
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject c = movies.getJSONObject(i);

                    String m_id = c.getString(TAG_VIDEO_ID);
                    String m_title = c.getString(TAG_VIDEO_TITLE);
                    String m_category = c.getString(TAG_VIDEO_CATEGORY);
                    String m_poster = c.getString(TAG_VIDEO_POSTER);
                    String m_genres = c.getString(TAG_VIDEO_GENRES);
                    String m_director = c.getString(TAG_VIDEO_DIRECTOR);
                    String m_acting = c.getString(TAG_VIDEO_ACTING);
                    String m_lang = c.getString(TAG_VIDEO_LANGUAGE);
                    String m_duration = c.getString(TAG_VIDEO_DURATION);
                    String m_description = c.getString(TAG_VIDEO_DESCRIPTION);
                    String m_source = c.getString(TAG_VIDEO_SOURCE);
                    String m_tid = c.getString(TAG_VIDEO_TVSHOW);
                    String m_genres_text = c.getString(TAG_VIDEO_GENRES_TEXT);
                    String m_season_no = c.getString(TAG_VIDEO_SEASON_NO);
                    String m_episode_no = c.getString(TAG_VIDEO_EPISODE_NO);
                    String m_day = c.getString(TAG_VIDEO_DAY);
                    String m_month = c.getString(TAG_VIDEO_MONTH);
                    String m_year = c.getString(TAG_VIDEO_YEAR);
                    String m_rating = c.getString(TAG_VIDEO_RATING);
                    String m_res = c.getString(TAG_VIDEO_RESUME);


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
                    movie.put(TAG_VIDEO_DIRECTOR, m_director);
                    movie.put(TAG_VIDEO_ACTING, m_acting);
                    movie.put(TAG_VIDEO_LANGUAGE, m_lang);
                    movie.put(TAG_VIDEO_DURATION, m_duration);
                    movie.put(TAG_VIDEO_DESCRIPTION, m_description);
                    movie.put(TAG_VIDEO_SOURCE, m_source);
                    movie.put(TAG_VIDEO_TVSHOW, m_tid);
                    movie.put(TAG_VIDEO_GENRES_TEXT, m_genres_text);
                    movie.put(TAG_VIDEO_SEASON_NO, m_season_no);
                    movie.put(TAG_VIDEO_EPISODE_NO, m_episode_no);
                    movie.put(TAG_VIDEO_DAY, m_day);
                    movie.put(TAG_VIDEO_MONTH, m_month);
                    movie.put(TAG_VIDEO_YEAR, m_year);
                    movie.put(TAG_VIDEO_RATING, m_rating);
                    movie.put(TAG_VIDEO_RESUME, m_res);
                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);

                    onlymovie.put(TAG_VIDEO_ID, m_id);
                    onlymovie.put(TAG_VIDEO_TITLE, m_title);
                    onlymovie.put(TAG_VIDEO_CATEGORY, m_category);
                    onlymovie.put(TAG_VIDEO_POSTER, m_poster);
                    onlymovie.put(TAG_VIDEO_GENRES, m_genres);
                    onlymovie.put(TAG_VIDEO_DIRECTOR, m_director);
                    onlymovie.put(TAG_VIDEO_ACTING, m_acting);
                    onlymovie.put(TAG_VIDEO_LANGUAGE, m_lang);
                    onlymovie.put(TAG_VIDEO_DURATION, m_duration);
                    onlymovie.put(TAG_VIDEO_DESCRIPTION, m_description);
                    onlymovie.put(TAG_VIDEO_SOURCE, m_source);
                    onlymovie.put(TAG_VIDEO_TVSHOW, m_tid);
                    onlymovie.put(TAG_VIDEO_GENRES_TEXT, m_genres_text);
                    onlymovie.put(TAG_VIDEO_SEASON_NO, m_season_no);
                    onlymovie.put(TAG_VIDEO_EPISODE_NO, m_episode_no);
                    onlymovie.put(TAG_VIDEO_DAY, m_day);
                    onlymovie.put(TAG_VIDEO_MONTH, m_month);
                    onlymovie.put(TAG_VIDEO_YEAR, m_year);
                    onlymovie.put(TAG_VIDEO_RATING, m_rating);
                    onlymovie.put(TAG_VIDEO_RESUME, m_res);


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

    private ArrayList<HashMap<String, String>> ParseJSONEpisodes(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();


                JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray movies;
                try {
                    movies = jsonObj.getJSONArray(TAG_VIDEO_INFO);
                    Log.i("TVShowInnerObj 386: ", "> " + movies);
                } catch (JSONException e) {
                    movies = null;
                    Log.i("TVShowInnerObj 389: ", "> " + "Movies Object Null");
                    e.printStackTrace();
                }
                eps = null;
                eps = movies;
// looping through All Students
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject c = movies.getJSONObject(i);

                    String m_id = c.getString(TAG_VIDEO_ID);
                    String m_title = c.getString(TAG_VIDEO_TITLE);
                    String m_category = c.getString(TAG_VIDEO_CATEGORY);
                    String m_poster = c.getString(TAG_VIDEO_POSTER);
                    String m_genres = c.getString(TAG_VIDEO_GENRES);
                    String m_director = c.getString(TAG_VIDEO_DIRECTOR);
                    String m_acting = c.getString(TAG_VIDEO_ACTING);
                    String m_lang = c.getString(TAG_VIDEO_LANGUAGE);
                    String m_duration = c.getString(TAG_VIDEO_DURATION);
                    String m_description = c.getString(TAG_VIDEO_DESCRIPTION);
                    String m_source = c.getString(TAG_VIDEO_SOURCE);
                    String m_tid = c.getString(TAG_VIDEO_TVSHOW);
                    String m_genres_text = c.getString(TAG_VIDEO_GENRES_TEXT);
                    String m_rating = c.getString(TAG_VIDEO_RATING);


                    Log.i("Episodes 576 ", "> " + m_tid);


// Phone node is JSON Object
                    //JSONObject phone = c.getJSONObject(TAG_STUDENT_PHONE);
                    //String mobile = phone.getString(TAG_STUDENT_PHONE_MOBILE);
                    //String home = phone.getString(TAG_STUDENT_PHONE_HOME);

// tmp hashmap for single student
                    HashMap<String, String> epis = new HashMap<String, String>();


// adding every child node to HashMap key => value
                    epis.put(TAG_VIDEO_ID, m_id);
                    epis.put(TAG_VIDEO_TITLE, m_title);
                    epis.put(TAG_VIDEO_CATEGORY, m_category);
                    epis.put(TAG_VIDEO_POSTER, m_poster);
                    epis.put(TAG_VIDEO_GENRES, m_genres);
                    epis.put(TAG_VIDEO_DIRECTOR, m_director);
                    epis.put(TAG_VIDEO_ACTING, m_acting);
                    epis.put(TAG_VIDEO_LANGUAGE, m_lang);
                    epis.put(TAG_VIDEO_DURATION, m_duration);
                    epis.put(TAG_VIDEO_DESCRIPTION, m_description);
                    epis.put(TAG_VIDEO_SOURCE, m_source);
                    epis.put(TAG_VIDEO_TVSHOW, m_tid);
                    epis.put(TAG_VIDEO_GENRES_TEXT, m_genres_text);
                    epis.put(TAG_VIDEO_RATING, m_rating);
                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);


// adding student to students list

                    //moviesList.add(movie);
                    Eplist.add(epis);
                }

                return Eplist;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    /*
    private class GetTVShowID extends AsyncTask<Void, Void, Void> {


        ProgressDialog proDialog;
        String TVShowID = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(TvShowInnerActivity.this);
            proDialog.setMessage("Loading Video...");
            proDialog.setCancelable(false);
            //proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();


// Making a request to url and getting response
            String TVSID = webreq.makeWebServiceCall(moviesurl3, WebRequest.GETRequest);


            Log.i("tvshowid: ", "> " + TVSID);
            Log.i("moviesurl3: ", "> " + moviesurl3);

            TestTVId = TVSID;


            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);




        }

    }


*/


    private class GetMovies extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> moviesList;
        ArrayList<HashMap<String, String>> epsList1;
        ProgressDialog proDialog;
        String TestMovies = null;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(TvShowInnerActivity.this);
            proDialog.setMessage("Loading Video...");
            proDialog.setCancelable(false);
            proDialog.show();
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

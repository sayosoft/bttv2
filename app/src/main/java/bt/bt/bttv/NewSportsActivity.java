package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
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

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.WebRequest;


public class NewSportsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "DeviceTypeRuntimeCheck";
    // JSON Node names
    private static final String TAG_VIDEO_INFO = "videos";
    private static final String TAG_VIDEO_ID = "video_id";
    private static final String TAG_VIDEO_TITLE = "video_title";
    private static final String TAG_VIDEO_CATEGORY = "video_category";
    private static final String TAG_VIDEO_POSTER = "video_poster";
    private static final String TAG_VIDEO_GENRES = "video_genre";
    private static final String TAG_SLIDES_INFO = "slides";
    private static final String TAG_SLIDES_ID = "slide_id";
    private static final String TAG_SLIDES_IMAGE = "slide_image_url";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    Integer sc = 6;
    final String[] slideimgs = new String[sc];
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    JSONArray Slides = null;
    private SQLiteHandler db;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Threading */

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /* End Threading */

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        cd = new ConnectionDetector(this);

        setContentView(R.layout.activity_sports2);
        if (cd.isConnectingToInternet()) {
            new GetMovies().execute();
        } else {
            Toast.makeText(NewSportsActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        // addImagesToThegallery();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sports");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        db = new SQLiteHandler(getApplicationContext());
        HashMap<String, String> user = db.getUserDetails();
        String name = user.get("name");
        String email = user.get("email");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navHeaderView = navigationView.getHeaderView(0);
        TextView tvHeaderName = (TextView) navHeaderView.findViewById(R.id.nav_username);
        tvHeaderName.setText(name);
        TextView tvHeaderMail = (TextView) navHeaderView.findViewById(R.id.nav_usermail);
        tvHeaderMail.setText(email);
    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.imageGallery);
        LinearLayout imageGallery2 = (LinearLayout) findViewById(R.id.imageGallery2);
        LinearLayout imageGallery3 = (LinearLayout) findViewById(R.id.imageGallery3);
        LinearLayout imageGallery4 = (LinearLayout) findViewById(R.id.imageGallery4);


        for (int i = 0; i < imgs.length(); i++) {

            JSONObject m = imgs.getJSONObject(i);
            String c_genres = m.getString(TAG_VIDEO_GENRES);
            List<String> genre = Arrays.asList(c_genres.split("\\s*,\\s*"));


            Integer c_id = Integer.parseInt(m.getString(TAG_VIDEO_ID));
            String c_title = m.getString(TAG_VIDEO_TITLE);
            //String c_category = m.getString(TAG_VIDEO_CATEGORY);
            String c_poster = m.getString(TAG_VIDEO_POSTER);
            Log.i("Movies 158 ", "> " + c_title);

            Log.i("Movie Genre 158 ", "> " + c_genres);

            String FinalImage = "http://bflix.ignitecloud.in/uploads/images/" + c_poster;
            if (c_poster != null && c_id != null) {
                if (imageGallery != null && genre.contains("33")) {
                    //Action

                    imageGallery.addView(getImageView(FinalImage, c_id));
                    System.gc();
                }

                if (imageGallery2 != null && genre.contains("34")) {
                    //Comedy
                    imageGallery2.addView(getImageView(FinalImage, c_id));
                    System.gc();
                }

                if (imageGallery3 != null && genre.contains("35")) {
                    //Drama
                    imageGallery3.addView(getImageView(FinalImage, c_id));
                    System.gc();
                }

                if (imageGallery4 != null && genre.contains("32")) {
                    //Drama
                    imageGallery4.addView(getImageView(FinalImage, c_id));
                    System.gc();
                }


            }
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
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Integer gid = v.getId();
                PlayMovie(gid);

            }
        });
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Ion.with(imageView)
                .placeholder(R.drawable.loadingposter)
                .load(newimage);

        return imageView;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            startActivity(new Intent(this, VideoHomeActivity.class));

        } else if (id == R.id.nav_audio) {
            startActivity(new Intent(this, AudioHomeActivity.class));

        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));

        } else if (id == R.id.nav_tvchannel) {
            startActivity(new Intent(this, TvChannelActivity.class));

        } else if (id == R.id.nav_radio) {
            startActivity(new Intent(this, RadioChannelActivity.class));

        } else if (id == R.id.nav_sports) {
            startActivity(new Intent(this, NewSportsActivity.class));

        } else if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewNewsActivity.class));

        } else if (id == R.id.nav_myacc) {
            startActivity(new Intent(this, MyAccountActivity.class));

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_terms) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_terms)));

        } else if (id == R.id.nav_privacy) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_privacy)));

        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
    public void PlayMovie(Integer gid) {

        Intent intent = new Intent(this, MovieInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
    }

    public void MovieGenres(View view) {


        switch (view.getId()) {
            case R.id.button2:
                Intent intent = new Intent(this, GenreActivity.class);
                intent.putExtra("gid", 33);
                intent.putExtra("title", "Cricket");
                startActivity(intent);
                break;
            case R.id.button3:
                Intent intent2 = new Intent(this, GenreActivity.class);
                intent2.putExtra("gid", 34);
                intent2.putExtra("title", "Football");
                startActivity(intent2);
                break;
            case R.id.button4:
                Intent intent3 = new Intent(this, GenreActivity.class);
                intent3.putExtra("gid", 35);
                intent3.putExtra("title", "Tennis");
                startActivity(intent3);
                break;
            case R.id.button5:
                Intent intent4 = new Intent(this, GenreActivity.class);
                intent4.putExtra("gid", 32);
                intent4.putExtra("title", "Basketball");
                startActivity(intent4);
                break;

        }
    }


    private ArrayList<HashMap<String, String>> ParseJSONMovies(String json) {
        if (json != null) {
            try {
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);
                JSONArray movies;
                try {
                    movies = jsonObj.getJSONArray(TAG_VIDEO_INFO);
                    Log.i("CategoriesObj 410: ", "> " + movies);
                } catch (JSONException e) {
                    movies = null;
                    Log.i("CategoriesObj 413: ", "> " + "Movies Object Null");
                    e.printStackTrace();
                }
                mvs = movies;
                for (int i = 0; i < movies.length(); i++) {
                    JSONObject c = movies.getJSONObject(i);

                    String m_id = c.getString(TAG_VIDEO_ID);
                    String m_title = c.getString(TAG_VIDEO_TITLE);
                    String m_category = c.getString(TAG_VIDEO_CATEGORY);
                    String m_poster = c.getString(TAG_VIDEO_POSTER);
                    String m_genres = c.getString(TAG_VIDEO_GENRES);
                    Log.i("Movies 425 ", "> " + m_title);
                    HashMap<String, String> movie = new HashMap<String, String>();
                    movie.put(TAG_VIDEO_ID, m_id);
                    movie.put(TAG_VIDEO_TITLE, m_title);
                    movie.put(TAG_VIDEO_CATEGORY, m_category);
                    movie.put(TAG_VIDEO_POSTER, m_poster);
                    onlymovie.put(TAG_VIDEO_ID, m_id);
                    onlymovie.put(TAG_VIDEO_GENRES, m_genres);
                    onlymovie.put(TAG_VIDEO_POSTER, m_poster);
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

    private ArrayList<HashMap<String, String>> ParseJSONSlides(String json) {
        if (json != null) {
            try {
                ArrayList<HashMap<String, String>> slidesList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);
                JSONArray slides;
                try {
                    slides = jsonObj.getJSONArray(TAG_SLIDES_INFO);
                    Log.i("CategoriesObj 410: ", "> " + slides);
                } catch (JSONException e) {
                    slides = null;
                    Log.i("CategoriesObj 413: ", "> " + "Slides Object Null");
                    e.printStackTrace();
                }
                Slides = slides;
                for (int i = 0; i < slides.length(); i++) {
                    JSONObject c = slides.getJSONObject(i);
                    String m_id = c.getString(TAG_SLIDES_ID);
                    String m_image = c.getString(TAG_SLIDES_IMAGE);
                    slideimgs[i] = "http://bflix.ignitecloud.in/uploads/images/" + m_image;
                    Log.i("Slides 425 ", "> " + m_image);
                    HashMap<String, String> slide = new HashMap<String, String>();
                    slide.put(TAG_SLIDES_ID, m_id);
                    slide.put(TAG_SLIDES_IMAGE, m_image);
                    slidesList.add(slide);
                }

                return slidesList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request for Sliders");
            return null;
        }
    }

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    private class GetMovies extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> moviesList;
        ArrayList<HashMap<String, String>> slidesList;
        ProgressDialog proDialog;
        String TestMovies = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proDialog = new ProgressDialog(NewSportsActivity.this);
            proDialog.setMessage("Loading Videos...");
            proDialog.setCancelable(false);
            proDialog.show();
        }
        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String slidesurl = "http://bflix.ignitecloud.in/jsonApi/slides/Sports";
            String SlidesStr = webreq.makeWebServiceCall(slidesurl, WebRequest.GETRequest);
            String moviesurl = "http://bflix.ignitecloud.in/jsonApi/vod/video_category/1";
            String MoviesStr = webreq.makeWebServiceCall(moviesurl, WebRequest.GETRequest);
            Log.i("Response: ", "> " + MoviesStr);
            slidesList = ParseJSONSlides(SlidesStr);
            moviesList = ParseJSONMovies(MoviesStr);
            TestMovies = MoviesStr;
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            Log.i("Movies: ", "> " + TestMovies);
            ArrayList<HashMap<String, String>> movList;
            movList = ParseJSONMovies(TestMovies);
            Log.i("Movies 2: ", "> " + movList);
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }
            try {
                addImagesToThegallery(mvs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}



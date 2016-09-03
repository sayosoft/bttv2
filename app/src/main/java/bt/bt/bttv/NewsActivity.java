package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.koushikdutta.ion.Ion;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.WebRequest;


public class NewsActivity extends AppCompatActivity
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
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/vod/video_category/26";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    final String[] slideimgs = new String[4];
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    String[] mbThumbIds2 = new String[40];
    Integer[] mbThumbIds3 = new Integer[40];
    JSONArray Slides = null;
    ImageListener imageListener = new ImageListener() {

        @Override
        public void setImageForPosition(final int position, final ImageView imageView) {
            Log.i("Slide URL ", "> " + "Getting to the Function");

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something here
                    if (Slides != null) {
                        for (int i = 0; i < Slides.length(); i++) {
                            Log.i("Slide URL 3 ", "> " + Slides.length());
                            try {
                                JSONObject m = Slides.getJSONObject(i);
                                String c_image = m.getString(TAG_SLIDES_IMAGE);
                                Log.i("Slide URL 3 ", "> " + c_image);
                                String FinalImage = "http://bflix.ignitecloud.in/uploads/images/" + c_image;
                                Log.i("Slide URL 2 ", "> " + FinalImage);
                                slideimgs[i] = FinalImage;
                            } catch (JSONException e) {
                                JSONObject m = null;
                                e.printStackTrace();
                            }
                        }


                        Ion.with(getApplicationContext()).load(slideimgs[position]).intoImageView(imageView);


                    } else {
                        Log.i("Slide Error ", "> " + "Slides Var Null");
                    }

                }
            }, 1200);

        }
    };
    private SQLiteHandler db;
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String jsondata = null;

        /* Threading */

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        /* End Threading */

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        setContentView(R.layout.activity_sports);
        new GetMovies().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("News");
        setSupportActionBar(toolbar);

        db = new SQLiteHandler(getApplicationContext());

        HashMap<String, String> user = db.getUserDetails();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        GridView imageGallery = (GridView) findViewById(R.id.gridview);
        imageGallery.setAdapter(new ImageAdapter(this));
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

    public void PlayMovies(View view) {

        Intent intent = new Intent(this, MovieInnerActivity.class);
        startActivity(intent);
    }

    public void PlayMovie(Integer gid) {

        Intent intent = new Intent(this, MovieInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
    }

    private ArrayList<HashMap<String, String>> ParseJSONMovies(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();


                JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
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
// looping through All Students
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
                    int id = Integer.parseInt(m_id.trim());
                    String urlPrefix = "http://bflix.ignitecloud.in/uploads/images/" + m_poster;
                    mbThumbIds2[i] = urlPrefix;
                    mbThumbIds3[i] = id;
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
// Hashmap for ListView
                ArrayList<HashMap<String, String>> slidesList = new ArrayList<HashMap<String, String>>();


                JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
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
// looping through All Students
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

    public class ImageAdapter extends BaseAdapter {
        String[] mThumbIds =
                {"http://bflix.ignitecloud.in/uploads/images/t20.jpg",
                        "http://bflix.ignitecloud.in/uploads/images/ind_vs_chi.jpg",
                        "http://bflix.ignitecloud.in/uploads/images/2015.jpg",
                        "http://bflix.ignitecloud.in/uploads/images/Andy_Murray_vs_Michael_Llodra.jpeg",
                        "http://bflix.ignitecloud.in/uploads/images/t20.jpg",
                        "http://bflix.ignitecloud.in/uploads/images/ind_vs_chi.jpg",
                        "http://bflix.ignitecloud.in/uploads/images/2015.jpg",
                        "http://bflix.ignitecloud.in/uploads/images/Andy_Murray_vs_Michael_Llodra.jpeg",
                };
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mvs.length();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            final float scale = getResources().getDisplayMetrics().density;
            int dpWidthInPx = (int) (115 * scale);
            int dpHeightInPx = (int) (160 * scale);
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(dpWidthInPx, dpHeightInPx));
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setPadding(10, 10, 10, 10);
                imageView.setId(mbThumbIds3[position]);
                imageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        Integer gid = v.getId();
                        PlayMovie(gid);
                        //v.getId() will give you the image id

                    }
                });
            } else {
                imageView = (ImageView) convertView;
            }

            //imageView.setImageResource(mThumbIds[position]);
            Ion.with(imageView).load(mbThumbIds2[position]);

            return imageView;
        }
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetMovies extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> moviesList;
        ArrayList<HashMap<String, String>> slidesList;
        ProgressDialog proDialog;
        String TestMovies = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(NewsActivity.this);
            proDialog.setMessage("Loading Videos...");
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
            String slidesurl = "http://bflix.ignitecloud.in/jsonApi/slides/News";
            String SlidesStr = webreq.makeWebServiceCall(slidesurl, WebRequest.GETRequest);
            String MoviesStr = webreq.makeWebServiceCall(moviesurl, WebRequest.GETRequest);

            Log.i("Response: ", "> " + MoviesStr);

            moviesList = ParseJSONMovies(MoviesStr);
            slidesList = ParseJSONSlides(SlidesStr);
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



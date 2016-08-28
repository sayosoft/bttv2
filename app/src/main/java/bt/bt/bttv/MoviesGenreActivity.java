package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.SQLiteHandler;


public class MoviesGenreActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "DeviceTypeRuntimeCheck";
    // JSON Node names
    private static final String TAG_VIDEO_INFO = "videos";
    private static final String TAG_VIDEO_ID = "video_id";
    private static final String TAG_VIDEO_TITLE = "video_title";
    private static final String TAG_VIDEO_CATEGORY = "video_category";
    private static final String TAG_VIDEO_POSTER = "video_poster";
    private static final String TAG_VIDEO_GENRES = "video_genre";
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/vod/video_genre/";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    String titletxt = null;
    JSONArray mvs = null;
    String[] mbThumbIds2 = new String[40];
    Integer[] mbThumbIds3 = new Integer[40];
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String tit = null;

        /* Threading */

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Integer value = extras.getInt("gid");
            tit = extras.getString("title");
            moviesurl = null;

            String furl = "http://bflix.ignitecloud.in/jsonApi/vod/video_genre/";
            moviesurl = furl + value;
            furl = null;
            value = null;
            Log.i("INTENTVALUE:", moviesurl);
            //The key argument here must match that used in the other activity
        } else {
            Log.i("INTENTVALUE:", moviesurl);
        }


        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        /* End Threading */

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        setContentView(R.layout.activity_sports);
        new GetMovies().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (tit != null) {
            toolbar.setTitle(tit);
        } else {
            toolbar.setTitle("Movies");

        }
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_tv) {
            Intent intent = new Intent(this, TvShowActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_tvchannel) {
            Intent intent = new Intent(this, TvChannelActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_radio) {
            //Intent intent = new Intent(this, RadioChannelActivity.class);
            //startActivity(intent);

        } else if (id == R.id.nav_sports) {
            Intent intent = new Intent(this, NewSportsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(this, NewNewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_myacc) {
            Intent intent = new Intent(this, MyPreferencesActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_fav) {
            Intent intent = new Intent(MoviesGenreActivity.this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 2);
            intent.putExtra("title", "Favorites");
            startActivity(intent);
        } else if (id == R.id.nav_playlist) {
            Intent intent = new Intent(MoviesGenreActivity.this, NewPlaylistActivity.class);

            startActivity(intent);
        } else if (id == R.id.nav_watchlater) {
            Intent intent = new Intent(MoviesGenreActivity.this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 1);
            intent.putExtra("title", "Watch Later");
            startActivity(intent);
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(MoviesGenreActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/terms");
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(MoviesGenreActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/privacy");
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public class ImageAdapter extends BaseAdapter {
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

                    }
                });
            } else {
                imageView = (ImageView) convertView;
            }

            Ion.with(imageView).placeholder(R.drawable.loadingposter).load(mbThumbIds2[position]);

            return imageView;
        }
    }

    private class GetMovies extends AsyncTask<Void, Void, Void> {

        ArrayList<HashMap<String, String>> moviesList;
        ProgressDialog proDialog;
        String TestMovies = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proDialog = new ProgressDialog(MoviesGenreActivity.this);
            proDialog.setMessage("Loading Videos...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String MoviesStr = webreq.makeWebServiceCall(moviesurl, WebRequest.GETRequest);
            Log.i("Response: ", "> " + MoviesStr);
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



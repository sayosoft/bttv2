package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;


public class TvChannelActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "DeviceTypeRuntimeCheck";
    // JSON Node names
    private static final String TAG_VIDEO_INFO = "channels";
    private static final String TAG_VIDEO_ID = "channel_id";
    private static final String TAG_VIDEO_TITLE = "channel_name";
    private static final String TAG_VIDEO_CATEGORY = "channel_category";
    private static final String TAG_VIDEO_POSTER = "channel_poster";
    private static final String TAG_VIDEO_GENRES = "channel_genres";
    private static final String TAG_VIDEO_SOURCE = "channel_stream_url";
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/channels/Video/";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    String[] mbThumbIds2 = new String[40];
    Integer[] mbThumbIds3 = new Integer[40];
    private SQLiteHandler db;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cd = new ConnectionDetector(this);

        setContentView(R.layout.activity_sports);

        if (cd.isConnectingToInternet()) {
            new GetMovies().execute();
        } else {
            Toast.makeText(TvChannelActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }

        // addImagesToThegallery();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TV Channels");
        setSupportActionBar(toolbar);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
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

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(TvChannelActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/terms");
            startActivity(intent);

        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(TvChannelActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/privacy");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void PlayMovie(Integer gid) {

        Intent intent = new Intent(this, TvChannelInnerActivity.class);
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
                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);

                    onlymovie.put(TAG_VIDEO_ID, m_id);
                    onlymovie.put(TAG_VIDEO_GENRES, m_genres);
                    onlymovie.put(TAG_VIDEO_POSTER, m_poster);


// adding student to students list
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

        ArrayList<HashMap<String, String>> moviesList;
        ProgressDialog proDialog;
        String TestMovies = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            proDialog = new ProgressDialog(TvChannelActivity.this);
            proDialog.setMessage("Loading Videos...");
            proDialog.setCancelable(false);

            if (!proDialog.isShowing()) {
                proDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String MoviesStr = webreq.makeWebServiceCall(moviesurl, WebRequest.GETRequest);
            moviesList = ParseJSONMovies(MoviesStr);
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
                addImagesToThegallery(mvs);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
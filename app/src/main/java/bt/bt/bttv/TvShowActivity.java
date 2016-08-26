package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.koushikdutta.ion.Ion;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.SessionManager;


public class TvShowActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = "DeviceTypeRuntimeCheck";

    final static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/tvshows";
    // JSON Node names
    private static final String TAG_VIDEO_INFO = "tvshows";
    private static final String TAG_VIDEO_ID = "tvseries_id";
    private static final String TAG_VIDEO_TITLE = "tvseries_title";
    private static final String TAG_VIDEO_CATEGORY = "tvseries_category";
    private static final String TAG_VIDEO_POSTER = "tvseries_poster";
    private static final String TAG_VIDEO_GENRES = "tvseries_genres";
    private static final String TAG_VIDEO_EPISODE = "latest_episode";
    private static final String TAG_SLIDES_INFO = "slides";
    private static final String TAG_SLIDES_ID = "slide_id";
    private static final String TAG_SLIDES_IMAGE = "slide_image_url";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    JSONArray mvs = null;
    JSONArray Slides = null;
    Integer sc = 5;
    final String[] slideimgs = new String[sc];
    int[] sampleImages = {R.drawable.s1, R.drawable.s2, R.drawable.s3, R.drawable.s4};
    CarouselView carouselView;
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
            //imageView.setImageResource(sampleImages[position]);
            // Ion.with(getApplicationContext()).load(sampleNetworkImageURLs[position]).intoImageView(imageView);

        }
    };


    //private Integer images[] = {R.drawable.mm1, R.drawable.mm3, R.drawable.mm4, R.drawable.mm3, R.drawable.mm1, R.drawable.mm4};
    //private Integer images1[] = {R.drawable.mm1, R.drawable.mm3, R.drawable.mm4, R.drawable.mm3, R.drawable.mm1, R.drawable.mm4};
    //private Integer images2[] = {R.drawable.mm3, R.drawable.mm1, R.drawable.mm4, R.drawable.mm1, R.drawable.mm3, R.drawable.mm4};
    private SQLiteHandler db;
    private SessionManager session;
    private ConnectionDetector cd;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        cd = new ConnectionDetector(this);

        setContentView(R.layout.activity_tv_show);
        if (cd.isConnectingToInternet()) {
            new GetMovies().execute();
        } else {
            Toast.makeText(TvShowActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        // addImagesToThegallery();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("TV Shows");
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(sampleImages.length);

        carouselView.setImageListener(imageListener);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.imageGallery);
        LinearLayout imageGallery2 = (LinearLayout) findViewById(R.id.imageGallery2);
        LinearLayout imageGallery3 = (LinearLayout) findViewById(R.id.imageGallery3);
        LinearLayout imageGallery4 = (LinearLayout) findViewById(R.id.imageGallery4);
        LinearLayout imageGallery5 = (LinearLayout) findViewById(R.id.imageGallery5);


        for (int i = 0; i < imgs.length(); i++) {

            JSONObject m = imgs.getJSONObject(i);
            String c_genres = m.getString(TAG_VIDEO_GENRES);
            List<String> genre = Arrays.asList(c_genres.split("\\s*,\\s*"));


            Integer c_id = Integer.parseInt(m.getString(TAG_VIDEO_ID));
            Log.i("TVShows 142 ", "> " + c_id);
            Integer c_eid = Integer.parseInt(m.getString(TAG_VIDEO_EPISODE));
            String c_title = m.getString(TAG_VIDEO_TITLE);
            //String c_category = m.getString(TAG_VIDEO_CATEGORY);
            String c_poster = m.getString(TAG_VIDEO_POSTER);
            Log.i("TVShows 158 ", "> " + c_title);
            Log.i("TVShows 158 ", "> " + c_genres);

            String FinalImage = "http://bflix.ignitecloud.in/uploads/images/" + c_poster;
            if (imageGallery != null && genre.contains("11")) {
                //Action
                imageGallery.addView(getImageView(FinalImage, c_eid));
                System.gc();
            }

            if (imageGallery2 != null && genre.contains("3")) {
                //Comedy
                imageGallery2.addView(getImageView(FinalImage, c_eid));
                System.gc();
            }

            if (imageGallery3 != null && genre.contains("5")) {
                //Drama
                imageGallery3.addView(getImageView(FinalImage, c_eid));
                System.gc();
            }

            if (imageGallery4 != null && genre.contains("76")) {
                //Drama
                imageGallery4.addView(getImageView(FinalImage, c_eid));
                System.gc();
            }

            if (imageGallery5 != null && genre.contains("7")) {
                //Drama
                imageGallery5.addView(getImageView(FinalImage, c_eid));
                System.gc();
            }


        }

        //Log.i("Images Value: ", "> " + imgs.get(key));
        //imageGallery.addView(getImageView(finalimage));


        /* for(HashMap<String, String> img : imgs) {
            imageGallery.addView(getImageView(image));
        } */

         /* for (Integer image1 : images1) {
            imageGallery2.addView(getImageView(image1));
        } */

        /* for (Integer image2 : images2) {
            imageGallery3.addView(getImageView(image2));
        } */
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
                //v.getId() will give you the image id

            }
        });
        //imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //imageView.setMaxWidth(400);
        //imageView.setMaxWidth(500);
        //imageView.setImageResource(newimage);
        Ion.with(imageView)
                .placeholder(R.drawable.loadingposter)

                .load(newimage);

        return imageView;
    }

    private Bitmap decodeFile(File f, Integer MAX_SIZE_W, Integer MAX_SIZE_H) throws IOException {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;
        if (o.outHeight > MAX_SIZE_H || o.outWidth > MAX_SIZE_W) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(MAX_SIZE_H /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();

        return b;
    }

    /* public void getjsondata(String url) {

        final JsonObject[] newjson = new JsonObject[1];

        Ion.with(this)
                .load("http://bflix.ignitecloud.in/jsonApi/categories")
                .asJsonObject(newjson)
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        newjson[0] = result;
                    }
                });


    } */

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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            Intent intent = new Intent(this, VideosActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioActivity.class);
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
            Intent intent = new Intent(TvShowActivity.this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 2);
            intent.putExtra("title", "Favorites");
            startActivity(intent);
        } else if (id == R.id.nav_playlist) {
            Intent intent = new Intent(TvShowActivity.this, NewPlaylistActivity.class);

            startActivity(intent);
        } else if (id == R.id.nav_watchlater) {
            Intent intent = new Intent(TvShowActivity.this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 1);
            intent.putExtra("title", "Watch Later");
            startActivity(intent);
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(TvShowActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/terms");
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(TvShowActivity.this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/privacy");
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logoutUser();
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

        Intent intent = new Intent(this, TvShowInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
    }


    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Movie Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://bt.bt.bttv/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Movie Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://bt.bt.bttv/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
                    String m_eid = c.getString(TAG_VIDEO_EPISODE);
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
                    movie.put(TAG_VIDEO_EPISODE, m_eid);
                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);

                    onlymovie.put(TAG_VIDEO_ID, m_id);
                    onlymovie.put(TAG_VIDEO_EPISODE, m_eid);
                    onlymovie.put(TAG_VIDEO_GENRES, m_genres);
                    onlymovie.put(TAG_VIDEO_POSTER, m_poster);

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


// Phone node is JSON Object
                    //JSONObject phone = c.getJSONObject(TAG_STUDENT_PHONE);
                    //String mobile = phone.getString(TAG_STUDENT_PHONE_MOBILE);
                    //String home = phone.getString(TAG_STUDENT_PHONE_HOME);

// tmp hashmap for single student
                    HashMap<String, String> slide = new HashMap<String, String>();


// adding every child node to HashMap key => value
                    slide.put(TAG_SLIDES_ID, m_id);
                    slide.put(TAG_SLIDES_IMAGE, m_image);

                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);


// adding student to students list

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

    public void TVShowGenres(View view) {


        switch (view.getId()) {
            case R.id.button2:
                Intent intent = new Intent(this, TVShowGenreActivity.class);
                intent.putExtra("gid", 11);
                intent.putExtra("title", "Family Shows");
                startActivity(intent);
                break;
            case R.id.button3:
                Intent intent2 = new Intent(this, TVShowGenreActivity.class);
                intent2.putExtra("gid", 3);
                intent2.putExtra("title", "Comedy Shows");
                startActivity(intent2);
                break;
            case R.id.button4:
                Intent intent3 = new Intent(this, TVShowGenreActivity.class);
                intent3.putExtra("gid", 5);
                intent3.putExtra("title", "Drama Shows");
                startActivity(intent3);
                break;
            case R.id.button5:
                Intent intent4 = new Intent(this, TVShowGenreActivity.class);
                intent4.putExtra("gid", 76);
                intent4.putExtra("title", "Animation Shows");
                startActivity(intent4);
                break;
            case R.id.button6:
                Intent intent5 = new Intent(this, TVShowGenreActivity.class);
                intent5.putExtra("gid", 9);
                intent5.putExtra("title", "Thriller Shows");
                startActivity(intent5);
                break;
        }


    }

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void logoutUser() {

        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("are you sure you want to logout??")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getApplicationContext(), "Logging Out", Toast.LENGTH_SHORT).show();

                        session.setLogin(false);

                        db.deleteUsers();

                        // Launching the login activity
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


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
            proDialog = new ProgressDialog(TvShowActivity.this);
            proDialog.setMessage("Loading Videos...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();

// Making a request to url and getting response
            String slidesurl = "http://bflix.ignitecloud.in/jsonApi/slides/Tv";
            String SlidesStr = webreq.makeWebServiceCall(slidesurl, WebRequest.GETRequest);
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
            //studentList = ParseJSON(TestString);
            ArrayList<HashMap<String, String>> movList;

            movList = ParseJSONMovies(TestMovies);

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
                addImagesToThegallery(mvs);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }


}



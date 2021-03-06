package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.LoginResponseModel;


public class SportsActivity extends AppCompatActivity
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
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/vod/video_category/1";
    final HashMap<String, String> onlymovie = new HashMap<String, String>();
    public SharedPreferences settings;
    ArrayList<HashMap<String, String>> moviesList2 = new ArrayList<HashMap<String, String>>();
    Integer sc = 6;
    final String[] slideimgs = new String[sc];
    JSONArray mvs = null;
    String[] mbThumbIds2 = new String[40];
    Integer[] mbThumbIds3 = new Integer[40];
    JSONArray Slides = null;
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

                        Picasso.with(getApplicationContext())
                                .load(slideimgs[position])
                                .into(imageView);


                    } else {
                        Log.i("Slide Error ", "> " + "Slides Var Null");
                    }

                }
            }, 1200);
            //imageView.setImageResource(sampleImages[position]);
            // Ion.with(getApplicationContext()).load(sampleNetworkImageURLs[position]).intoImageView(imageView);

        }
    };
    private Gson gson;
    private LoginResponseModel loginResponseModel;

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
        int width = size.x;
        int height = size.y;

        setContentView(R.layout.activity_sports);
        new GetMovies().execute();
        // addImagesToThegallery();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sports");
        setSupportActionBar(toolbar);

        gson = new Gson();
        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);
        String name = loginResponseModel.getUser().getName();
        String email = loginResponseModel.getUser().getEmail();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        carouselView = (CarouselView) findViewById(R.id.carouselView);
        carouselView.setPageCount(4);

        carouselView.setImageListener(imageListener);
    }

    private void addImagesToThegallery(JSONArray imgs) throws JSONException {
        GridView imageGallery = (GridView) findViewById(R.id.gridview);
        imageGallery.setAdapter(new ImageAdapter(this));


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
        Picasso.with(this)
                .load(newimage)
                .into(imageView);

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
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_terms_conditios)));

        } else if (id == R.id.nav_privacy) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_privacy_policy)));

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

            Picasso.with(SportsActivity.this)
                    .load(mbThumbIds2[position])
                    .into(imageView);

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
            proDialog = new ProgressDialog(SportsActivity.this);
            proDialog.setMessage("Loading Videos...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();

// Making a request to url and getting response
            String slidesurl = "http://bflix.ignitecloud.in/jsonApi/slides/Sports";
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



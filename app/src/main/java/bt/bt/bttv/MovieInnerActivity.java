package bt.bt.bttv;

import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.MyPlayListModel;

public class MovieInnerActivity extends AppCompatActivity implements View.OnClickListener, ApiInt {

    // JSON Node names
    private static final String TAG_VIDEO_INFO = "videos";
    private static final String TAG_VIDEO_RELATED_INFO = "related";
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
    private static final String TAG_VIDEO_GENRES_TEXT = "video_genre_text";
    private static final String TAG_VIDEO_RATING = "video_rating";
    private static final String TAG_VIDEO_PUR = "video_purchased";
    private static final String TAG_VIDEO_RESUME = "video_resume";
    private static String postParams = "";
    JSONArray mvs = null;
    JSONArray related = null;
    String video_source = null;
    String MovieTitle = null;
    String MovieDuration = null;
    String MovieGenre = null;
    String MovieDesciption = null;
    String MovieCast = null;
    String MovieDirector = null;
    String VideoID = null;
    String VResume = null;
    String strPlaylistName = null;
    Boolean autoplay = false;
    int position;
    private TextView tvFavourite, tvAddToPlaylist, tvLater, tvShare;
    private SQLiteHandler db;
    private ConnectionDetector cd;
    private APiAsync aPiAsync;
    private MyPlayListModel myPlayListModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_inner);

        cd = new ConnectionDetector(this);
        db = new SQLiteHandler(getApplicationContext());
        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();
        String uid = user.get("uid");
        Intent extras = getIntent();
        if (extras != null) {
            Integer value = Integer.parseInt(extras.getStringExtra("vid"));
            String ap = extras.getStringExtra("autoplay");
            if (ap != null) {
                autoplay = true;
            }
            postParams = "";
            postParams = value + "/" + uid;
            Log.i("INTENTVALUE:", postParams);
        } else {
            Log.i("INTENTVALUE:", postParams);
        }
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
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
                String c_genres_text = m.getString(TAG_VIDEO_GENRES_TEXT);
                List<String> genre = Arrays.asList(c_genres.split("\\s*,\\s*"));
                Integer c_id = Integer.parseInt(m.getString(TAG_VIDEO_ID));
                String c_title = m.getString(TAG_VIDEO_TITLE);
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
        RatingBar rate_bar = (RatingBar) findViewById(R.id.ratingBar);
        Button buynowbutton = (Button) findViewById(R.id.buynowbtn);
        ImageButton imgbtn = (ImageButton) findViewById(R.id.imageButton);

        tvFavourite = (TextView) findViewById(R.id.tvFavourite);
        tvAddToPlaylist = (TextView) findViewById(R.id.tvAddToPlaylist);
        tvLater = (TextView) findViewById(R.id.tvLater);
        tvShare = (TextView) findViewById(R.id.tvShare);

        LayerDrawable stars = (LayerDrawable) rate_bar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(1).setColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP);
        String Ret = addrelated(related);

        tvFavourite.setOnClickListener(this);
        tvAddToPlaylist.setOnClickListener(this);
        tvLater.setOnClickListener(this);
        tvShare.setOnClickListener(this);

        if (Ret == "NOTOK") {
            Toast.makeText(getApplicationContext(), "Could Not Find Related Videos",
                    Toast.LENGTH_LONG).show();
        }

        for (int i = 0; i < movie.length(); i++) {
            JSONObject m = movie.getJSONObject(i);
            video_source = "";
            video_source = m.getString(TAG_VIDEO_SOURCE);
            String c_purchased = m.getString(TAG_VIDEO_PUR);
            String mpost = getString(R.string.url_base_image) + m.getString(TAG_VIDEO_POSTER);
            Ion.with(movieposter)
                    .placeholder(R.drawable.loadingposter)
                    .load(mpost);
            assert movietitle != null;
            movietitle.setText(m.getString(TAG_VIDEO_TITLE));
            assert movieduration != null;
            movieduration.setText(m.getString(TAG_VIDEO_DURATION));
            assert moviegenre != null;
            moviegenre.setText(m.getString(TAG_VIDEO_GENRES_TEXT));
            assert moviedesc != null;
            moviedesc.setText(m.getString(TAG_VIDEO_DESCRIPTION));
            assert moviecast != null;
            moviecast.setText(m.getString(TAG_VIDEO_ACTING));
            assert moviedirector != null;
            moviedirector.setText(m.getString(TAG_VIDEO_DIRECTOR));
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
        if (autoplay) {
            PlayMoviePlayer2();
        }
    }

    private void PlaylistAlertDialogView(final MyPlayListModel myPlayListModel) {

        CharSequence[] items = new CharSequence[myPlayListModel.getArray().size()];
        for (int i = 0; i < myPlayListModel.getArray().size(); i++) {
            items[i] = myPlayListModel.getArray().get(i).getPlaylist_name();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MovieInnerActivity.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Add to Playlist");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        position = item;
                    }
                });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                strPlaylistName = myPlayListModel.getArray().get(position).getPlaylist_name();
                apiAddToPlaylist(myPlayListModel.getArray().get(position).getPlaylist_id());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void apiAddToPlaylist(String playlist_id) {
        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, MovieInnerActivity.this, getResources().getString(R.string.url_add_to_playlist) + db.getUserDetails().get("uid") + "/" + VideoID + "/" + playlist_id + "/" + "2", getString(R.string.msg_progress_dialog), 102);
            aPiAsync.execute();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection..!", Toast.LENGTH_LONG).show();
        }
    }

    private View getImageView(String newimage, Integer uid) {
        ImageView imageView = new ImageView(getApplicationContext());
        final float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (130 * scale);
        int dpHeightInPx = (int) (180 * scale);
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

    public void PlayMoviePlayer2() {
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
        Intent intent = new Intent(this, MovieInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
    }

    public void StartOrder(View view) {
        Intent intent = new Intent(this, NewOrderActivity.class);
        intent.putExtra("type", "video");
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
                ArrayList<HashMap<String, String>> moviesList = new ArrayList<HashMap<String, String>>();
                JSONObject jsonObj = new JSONObject(json);
                JSONArray movies;
                JSONArray relatedVideos;
                try {
                    movies = jsonObj.getJSONArray(TAG_VIDEO_INFO);
                    if (jsonObj.has(TAG_VIDEO_RELATED_INFO))
                        relatedVideos = jsonObj.getJSONArray(TAG_VIDEO_RELATED_INFO);
                    else
                        relatedVideos = null;
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
                    String m_director = c.getString(TAG_VIDEO_DIRECTOR);
                    String m_acting = c.getString(TAG_VIDEO_ACTING);
                    String m_lang = c.getString(TAG_VIDEO_LANGUAGE);
                    String m_duration = c.getString(TAG_VIDEO_DURATION);
                    String m_description = c.getString(TAG_VIDEO_DESCRIPTION);
                    String m_source = c.getString(TAG_VIDEO_SOURCE);
                    String m_genres_text = c.getString(TAG_VIDEO_GENRES_TEXT);
                    String m_rating = c.getString(TAG_VIDEO_RATING);
                    String m_pur = c.getString(TAG_VIDEO_PUR);
                    String m_res = c.getString(TAG_VIDEO_RESUME);
                    HashMap<String, String> movie = new HashMap<String, String>();
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
                    movie.put(TAG_VIDEO_GENRES_TEXT, m_genres_text);
                    movie.put(TAG_VIDEO_RATING, m_rating);
                    movie.put(TAG_VIDEO_PUR, m_pur);
                    movie.put(TAG_VIDEO_RESUME, m_res);
                    moviesList.add(movie);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvFavourite:
                if (cd.isConnectingToInternet()) {
                    aPiAsync = new APiAsync(null, MovieInnerActivity.this, getResources().getString(R.string.url_add_favorite) + VideoID + "/" + db.getUserDetails().get("uid") + "/" + "2", getString(R.string.msg_progress_dialog), 100);
                    aPiAsync.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection..!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.tvAddToPlaylist:
                apiGetPlayLists();
                break;
            case R.id.tvLater:
                if (cd.isConnectingToInternet()) {
                    aPiAsync = new APiAsync(null, MovieInnerActivity.this, getResources().getString(R.string.url_add_to_watchlist) + VideoID + "/" + db.getUserDetails().get("uid") + "/" + "2", getString(R.string.msg_progress_dialog), 103);
                    aPiAsync.execute();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection..!", Toast.LENGTH_LONG).show();
                }
                Toast.makeText(getApplicationContext(), "Added to Watch Later",
                        Toast.LENGTH_LONG).show();
                break;
            case R.id.tvShare:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "This is the shared link.");
                startActivity(Intent.createChooser(share, "Share"));
                break;
        }
    }

    private void apiGetPlayLists() {

        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, MovieInnerActivity.this, getResources().getString(R.string.url_get_movie_playlists) + db.getUserDetails().get("uid"), getString(R.string.msg_progress_dialog), 101);
            aPiAsync.execute();
        } else {
            Toast.makeText(MovieInnerActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(String response, int requestType) {
        switch (requestType) {
            case 100:
                Log.e("response fav", response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case 101:
                Gson gson = new Gson();
                myPlayListModel = gson.fromJson(response.toString(), MyPlayListModel.class);
                PlaylistAlertDialogView(myPlayListModel);
                break;
            case 102:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Added Video to " + strPlaylistName, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private class GetMovies extends AsyncTask<Void, Void, Void> {

        ArrayList<HashMap<String, String>> moviesList;
        ProgressDialog proDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proDialog = new ProgressDialog(MovieInnerActivity.this);
            proDialog.setMessage("Loading Video...");
            proDialog.setCancelable(false);
            if (!proDialog.isShowing()) {
                proDialog.show();
            }
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String MoviesStr = webreq.makeWebServiceCall(getString(R.string.url_get_video) + postParams, WebRequest.GETRequest);
            moviesList = ParseJSONMovie(MoviesStr);
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

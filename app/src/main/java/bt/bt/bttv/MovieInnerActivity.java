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

import java.util.Arrays;
import java.util.List;

import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.MovieInnerModel;
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
    private MovieInnerModel movieInnerModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_inner);

        cd = new ConnectionDetector(this);
        db = new SQLiteHandler(getApplicationContext());

        String ap = getIntent().getStringExtra("autoplay");
        if (ap != null) {
            autoplay = true;
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

    private void addImagesToThegallery(List<MovieInnerModel.RelatedBean> relatedlist) throws JSONException {
        LinearLayout imageGallery = (LinearLayout) findViewById(R.id.relatedGallery);
        if (relatedlist != null) {
            for (int i = 0; i < relatedlist.size(); i++) {
                String c_genres = relatedlist.get(i).getVideo_genre();
                String c_genres_text = relatedlist.get(i).getVideo_genre_text();
                List<String> genre = Arrays.asList(c_genres.split(","));
                Integer c_id = Integer.parseInt(relatedlist.get(i).getVideo_id());
                String c_title = relatedlist.get(i).getVideo_title();
                String FinalImage = getString(R.string.url_base_image) + relatedlist.get(i).getVideo_poster();
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

    private void SetMovieValues(MovieInnerModel movieInnerModel) throws JSONException {

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
        String Ret = addrelated(movieInnerModel.getRelated());

        tvFavourite.setOnClickListener(this);
        tvAddToPlaylist.setOnClickListener(this);
        tvLater.setOnClickListener(this);
        tvShare.setOnClickListener(this);

        if (Ret == "NOTOK") {
            Toast.makeText(getApplicationContext(), "Could Not Find Related Videos", Toast.LENGTH_LONG).show();
        }

        for (int i = 0; i < movieInnerModel.getVideos().size(); i++) {
            video_source = "";
            video_source = movieInnerModel.getVideos().get(i).getAndroid_video_source_url();
            String c_purchased = movieInnerModel.getVideos().get(i).getVideo_purchased();
            String mpost = getString(R.string.url_base_image) + movieInnerModel.getVideos().get(i).getVideo_poster();
            Ion.with(movieposter)
                    .placeholder(R.drawable.loadingposter)
                    .load(mpost);
            assert movietitle != null;
            movietitle.setText(movieInnerModel.getVideos().get(i).getVideo_title());
            assert movieduration != null;
            movieduration.setText(movieInnerModel.getVideos().get(i).getVideo_duration());
            assert moviegenre != null;
            moviegenre.setText(movieInnerModel.getVideos().get(i).getVideo_genre_text());
            assert moviedesc != null;
            moviedesc.setText(movieInnerModel.getVideos().get(i).getVideo_description());
            assert moviecast != null;
            moviecast.setText(movieInnerModel.getVideos().get(i).getVideo_acting());
            assert moviedirector != null;
            moviedirector.setText(movieInnerModel.getVideos().get(i).getVideo_director());
            assert rate_bar != null;
            rate_bar.setRating(Float.parseFloat(movieInnerModel.getVideos().get(i).getVideo_rating()));
            MovieTitle = movieInnerModel.getVideos().get(i).getVideo_title();
            VideoID = movieInnerModel.getVideos().get(i).getVideo_id();
            VResume = movieInnerModel.getVideos().get(i).getVideo_resume();
            MovieDuration = movieInnerModel.getVideos().get(i).getVideo_duration();
            MovieGenre = movieInnerModel.getVideos().get(i).getVideo_genre_text();
            MovieDesciption = movieInnerModel.getVideos().get(i).getVideo_description();
            MovieCast = movieInnerModel.getVideos().get(i).getVideo_acting();
            MovieDirector = movieInnerModel.getVideos().get(i).getVideo_director();
        }
        if (autoplay) {
            PlayMoviePlayer(null);
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

    private View getImageView(String newimage, Integer video_id) {
        ImageView imageView = new ImageView(getApplicationContext());
        final float scale = getResources().getDisplayMetrics().density;
        int dpWidthInPx = (int) (130 * scale);
        int dpHeightInPx = (int) (180 * scale);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(dpWidthInPx, dpHeightInPx);
        lp.setMargins(0, 0, 20, 30);
        imageView.setLayoutParams(lp);
        imageView.setId(video_id);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MovieInnerActivity.this, MovieInnerActivity.class).putExtra("vid", "" + v.getId()));
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

    public void StartOrder(View view) {
        startActivity(new Intent(this, NewOrderActivity.class).putExtra("type", "video"));
    }

    private String addrelated(List<MovieInnerModel.RelatedBean> related) {
        if (related != null) {
            try {
                addImagesToThegallery(related);
                return "OK";
            } catch (JSONException e) {
                e.printStackTrace();
                return "NOTOK";
            }
        } else {
            Toast.makeText(getApplicationContext(), "Error Getting Related Videos", Toast.LENGTH_LONG).show();
        }
        return "NOTOK";
    }

    public void showSettings(MenuItem item) {
        startActivity(new Intent(this, SettingsActivity.class));
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
                break;
            case R.id.tvShare:
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, "This is the shared link. " + " https://play.google.com/store/apps/details?id=markaz.ki.awaz#" + VideoID + "#2");
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
            case 103:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Added to Watch Later", Toast.LENGTH_LONG).show();
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

        ProgressDialog proDialog;
        String MoviesStr;


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
            MoviesStr = webreq.makeWebServiceCall(getString(R.string.url_get_video) + getIntent().getStringExtra("vid") + "/" + db.getUserDetails().get("uid"), WebRequest.GETRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }
            try {
                Gson gson = new Gson();
                movieInnerModel = gson.fromJson(MoviesStr, MovieInnerModel.class);
                if (movieInnerModel != null)
                    SetMovieValues(movieInnerModel);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

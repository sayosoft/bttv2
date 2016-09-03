package bt.bt.bttv;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.AudiosModel;
import bt.bt.bttv.model.DrawerCategoriesModel;
import bt.bt.bttv.model.VideosModel;
import bt.bt.bttv.model.WatchLaterModel;

public class SplashScreen extends AppCompatActivity {

    public static List<DrawerCategoriesModel> drawerCategoriesModelsList;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    private HTTPURLConnection service;
    private JSONObject jsonObject;
    private List<VideosModel> videosModelsList;
    private List<AudiosModel> audiosModelsList;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        cd = new ConnectionDetector(SplashScreen.this);

        if (cd.isConnectingToInternet()) {
            new GetDrawerCategories().execute();
        } else {
            Toast.makeText(SplashScreen.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    private class GetDrawerCategories extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_drawer_categories));
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jsonObject = new JSONObject(result);
                Gson gson = new Gson();
                drawerCategoriesModelsList = gson.fromJson(jsonObject.getJSONArray("categories").toString(), new TypeToken<List<DrawerCategoriesModel>>() {
                }.getType());
                new GetVideo().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetVideo extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_get_video));
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                jsonObject = new JSONObject(result);
                Gson gson = new Gson();
                videosModelsList = gson.fromJson(jsonObject.getJSONArray("videos").toString(), new TypeToken<List<VideosModel>>() {
                }.getType());
                WatchLaterModel.videoModelList = videosModelsList;
                new GetAudio().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class GetAudio extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_audio));
        }

        @Override
        protected void onPostExecute(String result) {
            Gson gson = new Gson();
            audiosModelsList = gson.fromJson(result.toString(), new TypeToken<List<AudiosModel>>() {
            }.getType());
            WatchLaterModel.audiosModelList = audiosModelsList;
        }
    }

}

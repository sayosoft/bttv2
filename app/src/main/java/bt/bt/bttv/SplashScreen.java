package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.DrawerCategoriesModel;

public class SplashScreen extends AppCompatActivity {

    public static List<DrawerCategoriesModel> drawerCategoriesModelsList;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 4000;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new GetDrawerCategories().execute();

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(i);

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

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

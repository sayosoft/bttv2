package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.adapter.CategoryAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.HomeCategoryModel;
import bt.bt.bttv.model.MovieModel;

/**
 * Created by Sachin on 8/23/2016.
 */

public class HomeCategoryActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String logFlag = "logFlag";
    public SharedPreferences settings;
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;
    private List<HomeCategoryModel> homeCategoryModels;
    private List<MovieModel> movieModels;
    private JSONObject jsonObject;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new SQLiteHandler(getApplicationContext());
        cd = new ConnectionDetector(this);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        if (getResources().getBoolean(R.bool.portrait_only)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        setContentView(R.layout.activity_home_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        llMain = (LinearLayout) findViewById(R.id.llMain);
        if (cd.isConnectingToInternet()) {
            new GetHomeContentCategory().execute();
        } else {
            Toast.makeText(HomeCategoryActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
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
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_tv) {
            Intent intent = new Intent(this, TvShowActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeCategoryActivity.class);
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
            startActivity(new Intent(this, MyAccountActivity.class));
        } else if (id == R.id.nav_fav) {
            Intent intent = new Intent(this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 2);
            intent.putExtra("title", "Favorites");
            startActivity(intent);
        } else if (id == R.id.nav_playlist) {
            Intent intent = new Intent(this, NewPlaylistActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_terms));
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_privacy));
            startActivity(intent);
        } else if (id == R.id.nav_watchlater) {
            Intent intent = new Intent(this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 1);
            intent.putExtra("title", "Watch Later");
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private void inflateData() {

        HashMap<Integer, List<MovieModel>> stringListHashMap = new HashMap<>();

        for (int i = 0; i < homeCategoryModels.size(); i++) {
            List<MovieModel> movieModelList = new ArrayList<>();
            String[] homeVodIds = homeCategoryModels.get(i).getHomepage_vod_ids().split(",");
            for (int j = 0; j < movieModels.size(); j++) {
                for (int k = 0; k < homeVodIds.length; k++) {
                    if (homeVodIds[k].equals(movieModels.get(j).getVideo_id())) {
                        movieModelList.add(movieModels.get(j));
                    }
                }
            }
            RecyclerView recyclerView = new RecyclerView(HomeCategoryActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            params.setMargins(5, 0, 5, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            stringListHashMap.put(i, movieModelList);
            CategoryAdapter categoryAdapter = new CategoryAdapter(HomeCategoryActivity.this, stringListHashMap.get(i));
            recyclerView.setAdapter(categoryAdapter);

            TextView tvTitle = new TextView(HomeCategoryActivity.this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(params1);
            params1.setMargins(10, 10, 0, 0);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText(homeCategoryModels.get(i).getHomepage_title());

            TextView tvSubTitle = new TextView(HomeCategoryActivity.this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvSubTitle.setLayoutParams(params2);
            params2.setMargins(10, 5, 0, 0);
            tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvSubTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvSubTitle.setText(homeCategoryModels.get(i).getHomepage_subtitle());

            llMain.addView(tvTitle);
            llMain.addView(tvSubTitle);
            llMain.addView(recyclerView);
        }
    }

    private class GetHomeContentCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(HomeCategoryActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_get_home_categories));
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equals("success")) {
                    Gson gson = new Gson();
                    homeCategoryModels = gson.fromJson(jsonObject.getJSONArray("result").toString(), new TypeToken<List<HomeCategoryModel>>() {
                    }.getType());
                    if (cd.isConnectingToInternet()) {
                        new getCategories().execute();
                    } else {
                        Toast.makeText(HomeCategoryActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class getCategories extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(HomeCategoryActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... res) {
            return service.ServerData(getResources().getString(R.string.url_get_video));

        }

        @Override
        protected void onPostExecute(String result1) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                if (result1 != null) {
                    jsonObject = new JSONObject(result1);
                    Gson gson = new Gson();
                    movieModels = gson.fromJson(jsonObject.getJSONArray("videos").toString(), new TypeToken<List<MovieModel>>() {
                    }.getType());
                    inflateData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

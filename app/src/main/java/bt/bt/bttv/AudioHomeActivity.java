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

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.adapter.AudioHomeAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.AudiosModel;
import bt.bt.bttv.model.DrawerCategoriesModel;

/**
 * Created by Sachin on 8/23/2016.
 */

public class AudioHomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String logFlag = "logFlag";
    public SharedPreferences settings;
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;
    private List<AudiosModel> audiosModelsList;
    private JSONObject jsonObject;
    private SQLiteHandler db;
    private List<DrawerCategoriesModel> drawerCategoriesModelList;

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
        toolbar.setTitle("Audio");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        llMain = (LinearLayout) findViewById(R.id.llMain);
        getAudioCategories(SplashScreen.drawerCategoriesModelsList);
    }

    private void getAudioCategories(List<DrawerCategoriesModel> drawerCategoriesModelsLists) {

        drawerCategoriesModelList = new ArrayList<>();
        for (int i = 0; i < drawerCategoriesModelsLists.size(); i++) {
            if (drawerCategoriesModelsLists.get(i).getCategory_type().equals("AoD")) {
                drawerCategoriesModelList.add(drawerCategoriesModelsLists.get(i));
            }
        }
        if (cd.isConnectingToInternet()) {
            new GetAudio().execute();
        } else {
            Toast.makeText(AudioHomeActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
            startActivity(intent);
            finish();

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
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_terms));
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_privacy));
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethod = new GlobleMethods(this);
            globleMethod.logoutUser();
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

        HashMap<Integer, List<AudiosModel>> stringListHashMap = new HashMap<>();

        for (int i = 0; i < drawerCategoriesModelList.size(); i++) {
            List<AudiosModel> audiosModelsList1 = new ArrayList<>();
            for (int j = 0; j < audiosModelsList.size(); j++) {
                if (drawerCategoriesModelList.get(i).getCategory_id().equals(audiosModelsList.get(j).getAudio_category())) {
                    audiosModelsList1.add(audiosModelsList.get(j));
                }
            }
            if (audiosModelsList1 != null)
                if (audiosModelsList1.size() > 0) {
                    RecyclerView recyclerView = new RecyclerView(AudioHomeActivity.this);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    recyclerView.setLayoutParams(params);
                    params.setMargins(5, 0, 5, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);

                    stringListHashMap.put(i, audiosModelsList1);
                    AudioHomeAdapter audioHomeAdapter = new AudioHomeAdapter(AudioHomeActivity.this, stringListHashMap.get(i));
                    recyclerView.setAdapter(audioHomeAdapter);

                    TextView tvTitle = new TextView(AudioHomeActivity.this);
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvTitle.setLayoutParams(params1);
                    params1.setMargins(10, 10, 0, 0);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                    tvTitle.setText(drawerCategoriesModelList.get(i).getCategory_title());

            /*TextView  tvSubTitle= new TextView(AudioHomeActivity.this);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvSubTitle.setLayoutParams(params2);
            params2.setMargins(10,5,0,0);
            tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            tvSubTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvSubTitle.setText(homeCategoryModels.get(i).getHomepage_subtitle());*/

                    llMain.addView(tvTitle);
//            llMain.addView(tvSubTitle);
                    llMain.addView(recyclerView);
                }
        }
    }

    private class GetAudio extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(AudioHomeActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_audio));
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            Gson gson = new Gson();
            audiosModelsList = gson.fromJson(result.toString(), new TypeToken<List<AudiosModel>>() {
            }.getType());
            inflateData();
        }
    }
}

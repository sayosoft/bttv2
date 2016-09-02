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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.adapter.LiveTvAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.DrawerCategoriesModel;
import bt.bt.bttv.model.LiveTvModel;

public class TvChannelActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private ConnectionDetector cd;
    private List<LiveTvModel> liveTvModelList;
    private RecyclerView rvLiveTv;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports);

        cd = new ConnectionDetector(this);

        if (cd.isConnectingToInternet()) {
            new GetMovies().execute();
        } else {
            Toast.makeText(TvChannelActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Live TV");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rvLiveTv = (RecyclerView) findViewById(R.id.rvLiveTv);
        // The number of Columns
        mLayoutManager = new GridLayoutManager(this, 2);
        rvLiveTv.setLayoutManager(mLayoutManager);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
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

    private class GetMovies extends AsyncTask<Void, Void, Void> {

        ProgressDialog proDialog;
        String MoviesStr;
        JSONObject jsonObject = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            proDialog = new ProgressDialog(TvChannelActivity.this);
            proDialog.setMessage("Loading Channels...");
            proDialog.setCancelable(false);
            proDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            MoviesStr = webreq.makeWebServiceCall(getString(R.string.get_live_tv_channel), WebRequest.GETRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }
            try {
                jsonObject = new JSONObject(MoviesStr);
                if (jsonObject.getJSONArray("channels") != null) {
                    Gson gson = new Gson();
                    liveTvModelList = gson.fromJson(jsonObject.getJSONArray("channels").toString(), new TypeToken<List<LiveTvModel>>() {
                    }.getType());
                    mAdapter = new LiveTvAdapter(TvChannelActivity.this, liveTvModelList);
                    rvLiveTv.setAdapter(mAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
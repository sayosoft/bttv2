package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.UserPackagesModel;
import io.vov.vitamio.utils.Log;

public class ManageFamilyMembersActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    RecyclerView rvFamilyMembers;
    UserPackagesModel userPackageModel;
    private FloatingActionButton FAB;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_family_members);

        userPackageModel = getIntent().getParcelableExtra("userPackageModel");

        cd = new ConnectionDetector(this);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Manage Family Member");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(ManageFamilyMembersActivity.this);

        FAB = (FloatingActionButton) findViewById(R.id.fabAddMember);
        FAB.setOnClickListener(this);

        rvFamilyMembers = (RecyclerView) findViewById(R.id.rvFamilyMembers);
        if (cd.isConnectingToInternet()) {
//                new GetFamilyMembers().execute();
        } else {
            Toast.makeText(this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddMember:
                startActivity(new Intent(ManageFamilyMembersActivity.this, AddFamilyMemberActivity.class));
                break;
        }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class GetFamilyMembers extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(ManageFamilyMembersActivity.this);
            pDialog.setMessage(getString(R.string.msg_progress_dialog));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_genres) + GlobleMethods.genre_type);
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("Family members", result);
        }
    }
}

package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.UserPackagesModel;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String logFlag = "logFlag";
    public static final String PREFS_NAME = "MyPrefs";
    private static String moviesurl = "http://bflix.ignitecloud.in/jsonApi/userpackages/";
    public SharedPreferences settings;
    Context context;
    private SQLiteHandler db;
    private ConnectionDetector cd;
    private String video_source = null;
    private List<UserPackagesModel> userPackagesModelList;
    private TextView tvPlanName, tvPrice, tvVOD, tvAOD, tvLiveTvChannel, tvRadioChannel, tvExpiryDate, tvManageSubscriptions, tvManageFamilyMembers, tvManageAddOns, tvAddBalance, tvEditProfile;
    private Switch switchAutoRenew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Account");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        if (!settings.contains(logFlag)) {
            logoutUser();
        }


        db = new SQLiteHandler(getApplicationContext());
        cd = new ConnectionDetector(this);

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
        moviesurl = "";
        moviesurl = "http://bflix.ignitecloud.in/jsonApi/userpackages/" + uid;
        if (cd.isConnectingToInternet()) {
            new GetUserPackages().execute();
        } else {
            Toast.makeText(MyAccountActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }

    public void PlayMoviePlayer(View view) {
        Intent intent = new Intent(this, PlayVideo.class);
        intent.putExtra("vurl", video_source);
        startActivity(intent);
    }

    public void newpackages(View view) {
        Intent intent = new Intent(this, NewPackagesActivity.class);
        intent.putExtra("pid", 1);
        startActivity(intent);
    }

    public void PlayMovie(Integer gid) {

        Intent intent = new Intent(this, MovieInnerActivity.class);
        intent.putExtra("vid", gid);
        startActivity(intent);
    }

    private void setUi(List<UserPackagesModel> userPackagesModelList) {

        tvPlanName = (TextView) findViewById(R.id.tvPlanName);
        tvPrice = (TextView) findViewById(R.id.tvPrice);
        tvVOD = (TextView) findViewById(R.id.tvVOD);
        tvAOD = (TextView) findViewById(R.id.tvAOD);
        tvLiveTvChannel = (TextView) findViewById(R.id.tvLiveTvChannel);
        tvRadioChannel = (TextView) findViewById(R.id.tvRadioChannel);
        tvExpiryDate = (TextView) findViewById(R.id.tvExpiryDate);

        switchAutoRenew = (Switch) findViewById(R.id.switchAutoRenew);
        tvManageSubscriptions = (TextView) findViewById(R.id.tvManageSubscriptions);
        tvManageFamilyMembers = (TextView) findViewById(R.id.tvManageFamilyMembers);
        tvManageAddOns = (TextView) findViewById(R.id.tvManageAddOns);
        tvAddBalance = (TextView) findViewById(R.id.tvAddBalance);
        tvEditProfile = (TextView) findViewById(R.id.tvEditProfile);

        switchAutoRenew.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(MyAccountActivity.this, "Switch is currently ON", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyAccountActivity.this, "Switch is currently OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvManageSubscriptions.setOnClickListener(this);
        tvManageFamilyMembers.setOnClickListener(this);
        tvManageAddOns.setOnClickListener(this);
        tvAddBalance.setOnClickListener(this);
        tvEditProfile.setOnClickListener(this);

        if (userPackagesModelList.size() > 0) {
            for (UserPackagesModel userPackagesModel : userPackagesModelList) {
                if (userPackagesModel.getPackage_status() != null)
                    if (userPackagesModel.getPackage_status().equals("Expired"))
                        setData(userPackagesModel);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvManageSubscriptions:
                startActivity(new Intent(MyAccountActivity.this, NewPackagesActivity.class));
                break;
            case R.id.tvManageFamilyMembers:
                break;
            case R.id.tvManageAddOns:
                break;
            case R.id.tvAddBalance:
                break;
            case R.id.tvEditProfile:
                startActivity(new Intent(MyAccountActivity.this, EditProfileActivity.class));
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            Intent intent = new Intent(this, VideosActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
            startActivity(intent);
            finish();

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
            Intent intent = new Intent(this, MyAccountActivity.class);
            startActivity(intent);
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
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/terms");
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", "http://bflix.ignitecloud.in/apppages/privacy");
            startActivity(intent);
        } else if (id == R.id.nav_watchlater) {
            Intent intent = new Intent(this, PlaylistinnerActivity.class);
            intent.putExtra("pid", 1);
            intent.putExtra("title", "Watch Later");
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logoutUser() {

        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("are you sure you want to logout??")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(getApplicationContext(), "Logging Out", Toast.LENGTH_SHORT).show();
                        db.deleteUsers();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove(logFlag);
                        editor.commit();
                        // Launching the login activity
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


    }

    private void setData(UserPackagesModel userPackagesModel) {

        tvPlanName.setText("" + userPackagesModel.getPackage_exp());
        tvPrice.setText("" + userPackagesModel.getPackage_exp());
        tvVOD.setText("" + userPackagesModel.getPackage_vod());
        tvAOD.setText("" + userPackagesModel.getPackage_aod());
        tvLiveTvChannel.setText("" + userPackagesModel.getPackage_live());
        tvRadioChannel.setText("" + userPackagesModel.getPackage_radio());
        tvExpiryDate.setText("" + userPackagesModel.getPackage_exp());
    }

    public void showSettings(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    private class GetUserPackages extends AsyncTask<Void, Void, Void> {

        ProgressDialog proDialog;
        String strUserPackages = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            proDialog = new ProgressDialog(MyAccountActivity.this);
            proDialog.setMessage("Loading ...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            strUserPackages = webreq.makeWebServiceCall(moviesurl, WebRequest.GETRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);

            try {
                JSONObject jsonObjectPackages = new JSONObject(strUserPackages);
                Gson gson = new Gson();
                userPackagesModelList = gson.fromJson(jsonObjectPackages.getJSONArray("packages").toString(), new TypeToken<List<UserPackagesModel>>() {
                }.getType());
                setUi(userPackagesModelList);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (proDialog.isShowing()) {
                proDialog.dismiss();
            }
        }
    }
}
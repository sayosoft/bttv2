package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.NavUtils;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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

import java.util.List;

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.LoginResponseModel;
import bt.bt.bttv.model.UserPackagesModel;

public class MyAccountActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    public SharedPreferences settings;
    private Context context;
    private ConnectionDetector cd;
    private String video_source = null;
    private List<UserPackagesModel> userPackagesModelList;
    private UserPackagesModel userPackagesModel;
    private TextView tvPlanName, tvPrice, tvVOD, tvAOD, tvLiveTvChannel, tvRadioChannel, tvExpiryDate, tvManageSubscriptions, tvManageFamilyMembers, tvManageAddOns, tvAddBalance, tvEditProfile;
    private Switch switchAutoRenew;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

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

        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);
        cd = new ConnectionDetector(this);
        gson = new Gson();

        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

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
        startActivity(new Intent(this, PlayVideo.class).putExtra("vurl", video_source));
    }

    public void newpackages(View view) {
        startActivity(new Intent(this, NewPackagesActivity.class).putExtra("pid", 1));
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
                    Toast.makeText(MyAccountActivity.this, "Auto Renew On", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyAccountActivity.this, "Auto Renew Off", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvManageSubscriptions.setOnClickListener(this);
        tvManageFamilyMembers.setOnClickListener(this);
        tvManageAddOns.setOnClickListener(this);
        tvAddBalance.setOnClickListener(this);
        tvEditProfile.setOnClickListener(this);

        if (userPackagesModelList.size() > 0) {
            for (int i = 0; i < userPackagesModelList.size(); i++) {
                if (userPackagesModelList.get(i).getPackage_status().equals("Active"))
                    setData(userPackagesModelList.get(i));
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
                if (userPackagesModel.getPackage_type().equals("Family"))
                    startActivity(new Intent(MyAccountActivity.this, ManageFamilyMembersActivity.class).putExtra("userPackageModel", userPackagesModel));
                else
                    Toast.makeText(MyAccountActivity.this, "You have not subscribed for family pack !", Toast.LENGTH_LONG).show();
                break;
            case R.id.tvManageAddOns:
                break;
            case R.id.tvAddBalance:
                startActivity(new Intent(MyAccountActivity.this, SubscriptionPaymentModeActivity.class).putExtra("fromAddBalance", true));
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
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_tvchannel) {
            Intent intent = new Intent(this, TvChannelActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_radio) {
            startActivity(new Intent(this, RadioChannelActivity.class));

        } else if (id == R.id.nav_sports) {
            Intent intent = new Intent(this, NewSportsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(this, NewNewsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_myacc) {
            Intent intent = new Intent(this, MyAccountActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_terms_conditios));
            startActivity(intent);
        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_privacy_policy));
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void setData(UserPackagesModel userPackagesModel) {
        this.userPackagesModel = userPackagesModel;

        tvPlanName.setText("" + userPackagesModel.getPackage_exp());
        tvPrice.setText(Html.fromHtml(settings.getString(GlobleMethods.ACCOUNT_BALANCE, "") + "<sup>Nu</sup>"));
        tvVOD.setText("" + userPackagesModel.getPackage_vod());
        tvAOD.setText("" + userPackagesModel.getPackage_aod());
        tvLiveTvChannel.setText("" + userPackagesModel.getPackage_live_tv());
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
            proDialog.setMessage(getString(R.string.msg_progress_dialog));
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            strUserPackages = webreq.makeWebServiceCall(getString(R.string.url_userpackages) + "" + loginResponseModel.getUser().getUser_id(), WebRequest.GETRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            try {
                JSONObject jsonObjectPackages = new JSONObject(strUserPackages);
                Gson gson = new Gson();

                settings.edit().putString(GlobleMethods.ACCOUNT_BALANCE, jsonObjectPackages.getJSONArray("packages").getJSONObject(0).getString("account_balance")).commit();
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

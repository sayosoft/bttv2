package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.model.LoginResponseModel;

public class EditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ApiInt {

    public SharedPreferences settings;
    private Button btnUpdate, btnChangePassword;
    private EditText inputFullName, inputLastName, inputEmail, inputMobile, et_current_password, et_new_password, et_re_password, et_cid_number, et_address, et_city;
    private ProgressDialog pDialog;
    private JSONObject jsonObject;
    private ConnectionDetector cd;
    private APiAsync aPiAsync;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        cd = new ConnectionDetector(this);
        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Your Profile");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(EditProfileActivity.this);

        inputFullName = (EditText) findViewById(R.id.name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputEmail = (EditText) findViewById(R.id.email);
        et_cid_number = (EditText) findViewById(R.id.et_cid_number);
        et_address = (EditText) findViewById(R.id.et_address);
        et_city = (EditText) findViewById(R.id.et_city);


        inputMobile = (EditText) findViewById(R.id.mobilenumber);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        et_current_password = (EditText) findViewById(R.id.et_current_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_re_password = (EditText) findViewById(R.id.et_re_password);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        gson = new Gson();
        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);
        String user_first_name = loginResponseModel.getUser().getName();
        String user_last_name = loginResponseModel.getUser().getLast_name();
        String user_mobile = loginResponseModel.getUser().getMobile();
        String user_email = loginResponseModel.getUser().getEmail();

        inputFullName.setText(user_first_name);
        inputLastName.setText(user_last_name);
        inputMobile.setText(user_mobile);
        inputEmail.setText(user_email);

        // Register Button Click event
        btnUpdate.setOnClickListener(this);
        btnChangePassword.setOnClickListener(this);
    }

    public void clear(View v) {
        Toast.makeText(getApplicationContext(), "Clicked on clear", Toast.LENGTH_LONG).show();
    }

    public void reset(View v) {
        inputFullName.setText("");
        inputLastName.setText("");
        inputEmail.setText("");
        inputMobile.setText("");
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
                String name = inputFullName.getText().toString().trim();
                String last_name = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String mobileno = inputMobile.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty()) {
                    apiUpdateUser(name, email, mobileno, last_name, loginResponseModel.getUser().getUser_id());
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.btnChangePassword:
                String current_password = et_current_password.getText().toString().trim();
                String new_password = et_new_password.getText().toString().trim();
                String re_enter_password = et_re_password.getText().toString().trim();
                if (!current_password.isEmpty() && !new_password.isEmpty() && !re_enter_password.isEmpty()) {
                    if (new_password.equals(re_enter_password)) {
                        try {
                            jsonObject = new JSONObject();
                            jsonObject.put("token", "");
                            jsonObject.put("current_password", current_password);
                            jsonObject.put("new_password", new_password);
                            jsonObject.put("confirm_new_password", re_enter_password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        apiChangePassword();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "new password and re-entered password are not same!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter your details!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void apiUpdateUser(String first_name, String email, String mobileno, String last_name, String user_id) {

        jsonObject = new JSONObject();
        try {
            jsonObject.put("token", loginResponseModel.getUser().getToken().toString());
            jsonObject.put("firstname", first_name);
            jsonObject.put("lastname", last_name);
            jsonObject.put("email", email);
            jsonObject.put("mobile_number", mobileno);
            jsonObject.put("cid_number", et_cid_number.getText().toString());
            jsonObject.put("address", et_address.getText().toString());
            jsonObject.put("city", et_city.getText().toString());

            if (cd.isConnectingToInternet()) {
                aPiAsync = new APiAsync(null, EditProfileActivity.this, getResources().getString(R.string.url_user_profile_update), getString(R.string.msg_progress_dialog), APiAsync.USER_PROFILE_UPDATE, jsonObject);
                aPiAsync.execute();
            } else {
                Toast.makeText(EditProfileActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void apiChangePassword() {
        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, EditProfileActivity.this, getResources().getString(R.string.url_change_password), getString(R.string.msg_progress_dialog), APiAsync.CHANGE_PASSWORD, this.jsonObject);
            aPiAsync.execute();
        } else {
            Toast.makeText(EditProfileActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(String response, int requestType) {
        switch (requestType) {
            case APiAsync.CHANGE_PASSWORD:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success"))
                        Toast.makeText(EditProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(EditProfileActivity.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case APiAsync.USER_PROFILE_UPDATE:
                LoginResponseModel loginResponseModel = gson.fromJson(response.toString(), LoginResponseModel.class);
                settings.edit().putString(GlobleMethods.logFlag, response).commit();
                if (loginResponseModel.getStatus().equals("success")) {
                    Toast.makeText(getApplicationContext(), "Update Successful!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), loginResponseModel.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
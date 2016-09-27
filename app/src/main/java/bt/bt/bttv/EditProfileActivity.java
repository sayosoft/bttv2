package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.AppController;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.SQLiteHandler;

public class EditProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, ApiInt {

    private Button btnUpdate, btnChangePassword;
    private EditText inputFullName, inputLastName, inputEmail, inputMobile, et_current_password, et_new_password, et_re_password;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private HashMap<String, String> user;
    private JSONObject jsonObject;
    private ConnectionDetector cd;
    private APiAsync aPiAsync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        cd = new ConnectionDetector(this);

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

        inputMobile = (EditText) findViewById(R.id.mobilenumber);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);

        et_current_password = (EditText) findViewById(R.id.et_current_password);
        et_new_password = (EditText) findViewById(R.id.et_new_password);
        et_re_password = (EditText) findViewById(R.id.et_re_password);
        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        user = db.getUserDetails();
        String user_first_name = user.get("name");
        String user_last_name = user.get("last_name");
        String user_mobile = user.get("mobile");
        String user_email = user.get("email");

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

    private void updateUser(final String name, final String email, final String mobile, final String last_name, final String user_id) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Updateing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST, getString(R.string.url_user_update), new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String last_name = user.getString("last_name");
                        String email = user.getString("email");

                        String mobile = user.getString("mobile");

                        String created_at = "03/07/2016";

                        db.deleteUsers();
                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, last_name, mobile);

                        Toast.makeText(getApplicationContext(), "User info updated. Relogin to Avoid Conflicts!", Toast.LENGTH_LONG).show();

                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", user_id);
                params.put("first_name", name);
                params.put("email", email);
                params.put("last_name", last_name);
                params.put("mobile", mobile);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
                    updateUser(name, email, mobileno, last_name, db.getUserDetails().get("uid"));
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
        }
    }
}
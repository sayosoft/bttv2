package bt.bt.bttv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bt.bt.bttv.app.AppConfig;
import bt.bt.bttv.app.AppController;
import bt.bt.bttv.helper.SQLiteHandler;

/**
 * Created by sajid on 03-07-2016.
 */


public class RegisterActivity extends Activity {
    public static final String PREFS_NAME = "MyPrefs";
    public static final String logFlag = "logFlag";
    private static final String TAG = RegisterActivity.class.getSimpleName();
    public SharedPreferences settings;
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputConfirrmPassword;
    private EditText inputCID;
    private EditText inputMobile;
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirrmPassword = (EditText) findViewById(R.id.password2);
        inputCID = (EditText) findViewById(R.id.cidnumber);
        inputMobile = (EditText) findViewById(R.id.mobilenumber);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        if (settings.contains(logFlag)) {
            startActivity(new Intent(RegisterActivity.this,
                    HomeActivity.class));
            finish();
        }

        // Register Button Click event
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();
                String password2 = inputConfirrmPassword.getText().toString().trim();
                String cid = inputCID.getText().toString().trim();
                String mobileno = inputMobile.getText().toString().trim();
                Boolean formvalid = false;
                String message = "";
                Log.d("Password", password + "  " + password2);
                Boolean emailvalid = isEmailValid(email);

                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || password2.isEmpty() || cid.isEmpty()) {
                    formvalid = false;
                    message = "Please Fill out all the details";
                } else if (!password.equals(password2)) {
                    formvalid = false;
                    message = "Password does not match with confirm password";
                } else if (!emailvalid) {
                    formvalid = false;
                    message = "Email address not valid";
                } else if (cid.length() != 11) {
                    formvalid = false;
                    message = "CID field should be 11 Digits long";
                } else {
                    formvalid = true;
                }

                if (formvalid) {
                    registerUser(name, email, password, cid, mobileno);
                } else {
                    Toast.makeText(getApplicationContext(),
                            message, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String name, final String email,
                              final String password, final String CID, final String mobile) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("user");
                        String name = user.getString("name");
                        String email = user.getString("email");
                        String last_name = user.getString("last_name");
                        String mobile = user.getString("mobile");

                        String created_at = user
                                .getString("created_at");

                        // Inserting row in users table
                        db.addUser(name, email, uid, created_at, last_name, mobile);

                        Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(
                                RegisterActivity.this,
                                LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {

                        // Error occurred in registration. Get the error
                        // message
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
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", name);
                params.put("email", email);
                params.put("password", password);
                params.put("user_cid", CID);
                params.put("mobile", mobile);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
package bt.bt.bttv;

import android.app.Activity;
import android.app.ProgressDialog;
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

import bt.bt.bttv.app.AppConfig;
import bt.bt.bttv.app.AppController;
import bt.bt.bttv.helper.SQLiteHandler;

/**
 * Created by sajid on 03-07-2016.
 */

public class EditProfileActivity extends Activity {
    private static final String TAG = EditProfileActivity.class.getSimpleName();
    private Button btnUpdate;
    private Button btnLinkToLogin;
    private EditText inputFullName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputCID;
    private EditText inputMobile;
    private ProgressDialog pDialog;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);

        inputFullName = (EditText) findViewById(R.id.name);
        inputLastName = (EditText) findViewById(R.id.last_name);
        inputEmail = (EditText) findViewById(R.id.email);

        inputMobile = (EditText) findViewById(R.id.mobilenumber);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String userid = user.get("uid");
        String user_first_name = user.get("name");
        String user_last_name = user.get("last_name");
        String user_mobile = user.get("mobile");
        String user_email = user.get("email");

        inputFullName.setText(user_first_name);
        inputLastName.setText(user_last_name);
        inputMobile.setText(user_mobile);
        inputEmail.setText(user_email);

        // Register Button Click event
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String name = inputFullName.getText().toString().trim();
                String last_name = inputLastName.getText().toString().trim();
                String email = inputEmail.getText().toString().trim();

                String mobileno = inputMobile.getText().toString().trim();

                if (!name.isEmpty() && !email.isEmpty()) {
                    updateUser(name, email, mobileno, last_name, userid);
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Please enter your details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void updateUser(final String name, final String email, final String mobile, final String last_name, final String user_id) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Updateing ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_UPDATE, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
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
                Log.e(TAG, "Update Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
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
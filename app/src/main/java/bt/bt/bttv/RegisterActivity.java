package bt.bt.bttv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import bt.bt.bttv.model.UserRegistrationModel;

/**
 * Created by sajid on 03-07-2016.
 */


public class RegisterActivity extends Activity implements View.OnClickListener, ApiInt {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private SharedPreferences settings;
    private Button btnRegister, btnLinkToLogin;
    private EditText inputFullName, inputEmail, inputPassword, inputConfirrmPassword, inputCID, inputMobile;
    private ConnectionDetector cd;
    private APiAsync aPiAsync;
    private JSONObject jsonObject;
    private UserRegistrationModel userRegistrationModel;
    private Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cd = new ConnectionDetector(this);
        gson = new Gson();

        inputFullName = (EditText) findViewById(R.id.name);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputConfirrmPassword = (EditText) findViewById(R.id.password2);
        inputCID = (EditText) findViewById(R.id.cidnumber);
        inputMobile = (EditText) findViewById(R.id.mobilenumber);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLinkToLogin = (Button) findViewById(R.id.btnLinkToLoginScreen);
        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);

        if (settings.contains(GlobleMethods.logFlag)) {
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        }

        btnRegister.setOnClickListener(this);
        btnLinkToLogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                register();
                break;
            case R.id.btnLinkToLoginScreen:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void register() {
        String name = inputFullName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();
        String password2 = inputConfirrmPassword.getText().toString().trim();
        String cid = inputCID.getText().toString().trim();
        String mobileno = inputMobile.getText().toString().trim();
        Boolean formvalid = false;
        String message = "";
        Boolean emailvalid = GlobleMethods.isEmailValid(email);
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
            try {
                jsonObject = new JSONObject();
                jsonObject.put("first_name", name);
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                jsonObject.put("user_cid", cid);
                jsonObject.put("mobile", mobileno);

                if (cd.isConnectingToInternet()) {
                    aPiAsync = new APiAsync(null, RegisterActivity.this, getResources().getString(R.string.url_register), getString(R.string.msg_progress_dialog), APiAsync.REGISTER_USER, jsonObject);
                    aPiAsync.execute();
                } else {
                    Toast.makeText(RegisterActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSuccess(String response, int requestCode) {
        switch (requestCode) {
            case APiAsync.REGISTER_USER:
                userRegistrationModel = gson.fromJson(response.toString(), UserRegistrationModel.class);
                if (userRegistrationModel.getStatus().equals("success")) {
                    Toast.makeText(getApplicationContext(), "User successfully registered. Try login now!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegisterActivity.this, OtpActivity.class).putExtra("userRegistrationModel", userRegistrationModel));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), userRegistrationModel.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
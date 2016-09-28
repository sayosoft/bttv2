package bt.bt.bttv;

/**
 * Created by sajid on 03-07-2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.model.LoginResponseModel;
import bt.bt.bttv.model.UserRegistrationModel;

public class LoginActivity extends Activity implements View.OnClickListener, ApiInt {

    public SharedPreferences settings;
    private Button btnLogin;
    private Button btnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ConnectionDetector cd;
    private APiAsync aPiAsync;
    private JSONObject jsonObject;
    private UserRegistrationModel userRegistrationModel;
    private Gson gson;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);

        cd = new ConnectionDetector(this);
        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnLinkToRegisterScreen);

        if (settings.contains(GlobleMethods.logFlag)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                    handled = true;
                }
                return handled;
            }
        });

        btnLinkToRegister.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                login();
                break;
            case R.id.btnLinkToRegisterScreen:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                finish();
                break;
        }
    }

    private void login() {
        String email = inputEmail.getText().toString().trim();
        String password = inputPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("email", email);
                jsonObject.put("password", password);
                apiLogin();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(),"Please enter the credentials!", Toast.LENGTH_LONG).show();
        }
    }

    private void apiLogin() {
        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, LoginActivity.this, getResources().getString(R.string.url_login), getString(R.string.msg_progress_dialog), APiAsync.LOGIN, jsonObject);
            aPiAsync.execute();
        } else {
            Toast.makeText(LoginActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(String response, int requestCode) {
        switch (requestCode) {
            case APiAsync.LOGIN:
                LoginResponseModel loginResponseModel = gson.fromJson(response.toString(), LoginResponseModel.class);
                settings.edit().putString(GlobleMethods.logFlag, response).commit();
                if (loginResponseModel.getStatus().equals("success")) {
                    Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), loginResponseModel.getMessage(), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
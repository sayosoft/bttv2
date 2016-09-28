package bt.bt.bttv;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import bt.bt.bttv.model.UserRegistrationModel;

public class OtpActivity extends AppCompatActivity implements View.OnClickListener, ApiInt {

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private SharedPreferences settings;
    private Button btnConfirm, btnLinkToLoginScreen;
    private EditText etEmailOtp, etMobileOtp;
    private ConnectionDetector cd;
    private APiAsync aPiAsync;
    private JSONObject jsonObject;
    private UserRegistrationModel userRegistrationModel;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrer_otp);
        cd = new ConnectionDetector(this);
        gson = new Gson();

        userRegistrationModel = getIntent().getParcelableExtra("userRegistrationModel");

        etEmailOtp = (EditText) findViewById(R.id.etEmailOtp);
        etMobileOtp = (EditText) findViewById(R.id.etMobileOtp);

        btnConfirm = (Button) findViewById(R.id.btnConfirm);
        btnLinkToLoginScreen = (Button) findViewById(R.id.btnLinkToLoginScreen);

        btnConfirm.setOnClickListener(this);
        btnLinkToLoginScreen.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnConfirm:
                try {
                    jsonObject = new JSONObject();
                    if (etEmailOtp.getText().length() != 0) {
                        jsonObject.put("email_otp", etEmailOtp.getText().toString());
                        jsonObject.put("sms_otp", "");
                        jsonObject.put("token", userRegistrationModel.getUser().getToken());
                        apiVerifyOtp();
                    } else if (etMobileOtp.getText().length() != 0) {
                        jsonObject.put("email_otp", "");
                        jsonObject.put("sms_otp", etMobileOtp.getText().toString());
                        jsonObject.put("token", userRegistrationModel.getUser().getToken());
                        apiVerifyOtp();
                    } else {
                        Toast.makeText(OtpActivity.this, "Please enter valid data!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btnLinkToLoginScreen:
                startActivity(new Intent(OtpActivity.this, LoginActivity.class));
                finish();
                break;
        }
    }

    private void apiVerifyOtp() {

        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, OtpActivity.this, getResources().getString(R.string.url_verify_otp), getString(R.string.msg_progress_dialog), APiAsync.VERIFY_OTP, jsonObject);
            aPiAsync.execute();
        } else {
            Toast.makeText(OtpActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(String response, int requestCode) {
        switch (requestCode) {
            case APiAsync.VERIFY_OTP:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("success")) {
                        Toast.makeText(getApplicationContext(), "Verified Successfully. Try login now!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(OtpActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}

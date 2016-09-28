package bt.bt.bttv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.model.PackagesModel;

/**
 * Created by Rani on 28/08/2016.
 */

public class SubscriptionPaymentModeActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String logFlag = "logFlag";
    public SharedPreferences settings;
    PackagesModel packagesModel;
    private TextView tvPackCost;
    private LinearLayout llMain, llScratchCard, llBWallet, llCard;
    private EditText etScratchCardNumber, etWalletUserName, etWalletPassword, etCardNo, etCardExpiry, etCardCv;
    private Button btn_account_balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);

        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Mode");
        setSupportActionBar(toolbar);

        btn_account_balance = (Button) findViewById(R.id.btn_account_balance);
        if (getIntent().getBooleanExtra("fromAddBalance", false)) {
            btn_account_balance.setVisibility(View.GONE);
        }

        tvPackCost = (TextView) findViewById(R.id.tv_pack_cost);
        tvPackCost.setText(Html.fromHtml("<b>" + settings.getString(GlobleMethods.ACCOUNT_BALANCE, "") + "<sup>Nu</sup></b>"));
        llMain = (LinearLayout) findViewById(R.id.ll_main_view);
        llScratchCard = (LinearLayout) findViewById(R.id.ll_scratch_card);
        llBWallet = (LinearLayout) findViewById(R.id.ll_b_wallet);
        llCard = (LinearLayout) findViewById(R.id.ll_card);

        etScratchCardNumber = (EditText) findViewById(R.id.et_scratch_card_number);
        etWalletUserName = (EditText) findViewById(R.id.et_wallet_user_name);
        etWalletPassword = (EditText) findViewById(R.id.et_wallet_password);
        etCardNo = (EditText) findViewById(R.id.et_card_no);
        etCardExpiry = (EditText) findViewById(R.id.et_card_expiry);
        etCardCv = (EditText) findViewById(R.id.et_card_cv);

        packagesModel = getIntent().getParcelableExtra("packagesModel");
    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()){
//
//            case R.id.ll_scratch_card:
//                llScratchCard.setVisibility(View.VISIBLE);
//                llMain.setVisibility(View.GONE);
//                break;
//
//            case R.id.ll_b_wallet:
//                llBWallet.setVisibility(View.VISIBLE);
//                llScratchCard.setVisibility(View.GONE);
//                llMain.setVisibility(View.GONE);
//                break;
//
//            case R.id.ll_card:
//                llCard.setVisibility(View.VISIBLE);
//                llScratchCard.setVisibility(View.GONE);
//                llMain.setVisibility(View.GONE);
//                break;
//
//        }
//
//    }

    public void AccountBalance(View view) {
        ADPaymentSuccessful();

    }

    public void ADPaymentSuccessful() {
        new AlertDialog.Builder(this)
                .setTitle("Payment Successful")
                .setMessage("Your subscription pack has been changed " + packagesModel.getPackage_title() + " Thank You, Enjoy BTTV")
                .show();
    }

    public void ScratchCard(View view) {
        llScratchCard.setVisibility(View.VISIBLE);
        llMain.setVisibility(View.GONE);

    }

    public void BWallet(View view) {
        llBWallet.setVisibility(View.VISIBLE);
        llScratchCard.setVisibility(View.GONE);
        llMain.setVisibility(View.GONE);
    }

    public void Card(View view) {
        llCard.setVisibility(View.VISIBLE);
        llScratchCard.setVisibility(View.GONE);
        llMain.setVisibility(View.GONE);
    }

    public void Submit(View view) {
        ADPaymentSuccessful();
    }

    public void ScratchCancel(View view) {
        llMain.setVisibility(View.VISIBLE);
        llScratchCard.setVisibility(View.GONE);
        llBWallet.setVisibility(View.GONE);
        llCard.setVisibility(View.GONE);
    }

    public void BWalletCancel(View view) {
        llMain.setVisibility(View.VISIBLE);
        llScratchCard.setVisibility(View.GONE);
        llBWallet.setVisibility(View.GONE);
        llCard.setVisibility(View.GONE);
    }

    public void CardCancel(View view) {
        llMain.setVisibility(View.VISIBLE);
        llScratchCard.setVisibility(View.GONE);
        llBWallet.setVisibility(View.GONE);
        llCard.setVisibility(View.GONE);
    }
}



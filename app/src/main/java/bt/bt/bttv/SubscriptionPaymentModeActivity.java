package bt.bt.bttv;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Rani on 28/08/2016.
 */

public class SubscriptionPaymentModeActivity extends AppCompatActivity {

    private TextView tvPackCost;
    private LinearLayout llMain;
    private LinearLayout llScratchCard;
    private LinearLayout llBWallet;
    private LinearLayout llCard;
    private EditText etScratchCardNumber;
    private EditText etWalletUserName;
    private EditText etWalletPassword;
    private EditText etCardNo;
    private EditText etCardExpiry;
    private EditText etCardCv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Payment Mode");
        setSupportActionBar(toolbar);

        tvPackCost = (TextView) findViewById(R.id.tv_pack_cost);
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
                .setMessage("Your subscription pack has been changed <pack name> Thank You, Enjoy BTTV")
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



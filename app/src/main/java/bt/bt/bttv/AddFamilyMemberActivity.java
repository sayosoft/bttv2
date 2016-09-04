package bt.bt.bttv;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddFamilyMemberActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail, etMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_family_member);

        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etMobile = (EditText) findViewById(R.id.etMobile);
    }

    public void addMember(View v) {
        if (etFirstName.getText().length() > 0 && etLastName.getText().length() > 0 && etEmail.getText().length() > 0 && etMobile.getText().length() > 0) {
            Toast.makeText(AddFamilyMemberActivity.this, "done", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(AddFamilyMemberActivity.this, "Please Enter all fields", Toast.LENGTH_SHORT).show();
        }
    }

    public void goBack(View v) {
        Toast.makeText(AddFamilyMemberActivity.this, "will go back", Toast.LENGTH_SHORT).show();
    }
}

package bt.bt.bttv;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.adapter.PackageAdapter;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.PackagesModel;

public class NewPackagesActivity extends AppCompatActivity {
    private SQLiteHandler db;

    private List<PackagesModel> packagesModelList;
    private RecyclerView.Adapter packageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        db = new SQLiteHandler(getApplicationContext());

        rvPackages = (RecyclerView) findViewById(R.id.rvPackages);
        rvPackages.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 1);
        rvPackages.setLayoutManager(mLayoutManager);
        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
        new GetStudents().execute();
    }
    private class GetStudents extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog proDialog;
        String TestString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            proDialog = new ProgressDialog(NewPackagesActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String jsonStr = webreq.makeWebServiceCall(getString(R.string.url_get_packages), WebRequest.GETRequest);
            TestString = jsonStr;
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);

            try {
                JSONObject jsonObject = new JSONObject(TestString);
                Gson gson = new Gson();
                packagesModelList = gson.fromJson(jsonObject.getJSONArray("packages").toString(), new TypeToken<List<PackagesModel>>() {
                }.getType());

                if (proDialog.isShowing())
                    proDialog.dismiss();

                packageAdapter = new PackageAdapter(NewPackagesActivity.this, packagesModelList);
                rvPackages.setAdapter(packageAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}


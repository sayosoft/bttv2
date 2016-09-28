package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.LoginResponseModel;
import bt.bt.bttv.model.PackagesModel;

public class NewPackagesActivity extends AppCompatActivity {

    public SharedPreferences settings;
    private List<PackagesModel> packagesModelList;
    private RecyclerView.Adapter packageAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView rvPackages;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();

        rvPackages = (RecyclerView) findViewById(R.id.rvPackages);
        rvPackages.setHasFixedSize(true);

        mLayoutManager = new GridLayoutManager(this, 1);
        rvPackages.setLayoutManager(mLayoutManager);

        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

        String uid = loginResponseModel.getUser().getUser_id();
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


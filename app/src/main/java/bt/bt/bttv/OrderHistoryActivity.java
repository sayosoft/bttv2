package bt.bt.bttv;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.LoginResponseModel;

public class OrderHistoryActivity extends ListActivity {
    private static final String TAG_ORDER_INFO = "orders";
    private static final String TAG_ID = "order_id";
    private static final String TAG_ORDER_TITLE = "order_name";
    private static final String TAG_ORDER_ITEM = "order_item";
    private static final String TAG_ORDER_GATEWAY = "order_gateway";
    private static final String TAG_ORDER_ITEM_AMT = "order_item_amt";
    private static final String TAG_ORDER_STATUS = "order_paid_status";
    private static final String TAG_ORDER_TXID = "order_id";
    private static String url = "http://bflix.ignitecloud.in/jsonApi/orderhistory/";
    public SharedPreferences settings;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

        String uid = loginResponseModel.getUser().getUser_id();
        url = "";
        url = "http://bflix.ignitecloud.in/jsonApi/orderhistory/" + uid;
        Log.i("Order URL: ", "> " + url);
        new GetStudents().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObj = new JSONObject(json);
                JSONArray students;
                try {
                    students = jsonObj.getJSONArray(TAG_ORDER_INFO);
                    Log.i("CategoriesObj 172: ", "> " + students);
                } catch (JSONException e) {
                    students = null;
                    Log.i("CategoriesObj 172: ", "> " + "Students Object Null");
                    e.printStackTrace();
                }
                for (int i = 0; i < students.length(); i++) {
                    JSONObject c = students.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_ORDER_TITLE);
                    String gateway = c.getString(TAG_ORDER_GATEWAY);
                    String amount = c.getString(TAG_ORDER_ITEM_AMT);
                    String item = c.getString(TAG_ORDER_ITEM);
                    String txid = c.getString(TAG_ORDER_TXID);
                    String status = c.getString(TAG_ORDER_STATUS);
                    Log.i("Categories 186 ", "> " + name);
                    HashMap<String, String> student = new HashMap<String, String>();
                    student.put(TAG_ID, "Order ID: " + id);
                    student.put(TAG_ORDER_TITLE, "Type: " + name);
                    student.put(TAG_ORDER_GATEWAY, "Method: " + gateway);
                    student.put(TAG_ORDER_ITEM_AMT, "Amount: " + amount);
                    student.put(TAG_ORDER_ITEM, "Order Item " + item);
                    student.put(TAG_ORDER_TXID, "Order ID: " + txid);
                    student.put(TAG_ORDER_STATUS, "Status: " + status);
                    studentList.add(student);
                }
                return studentList;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        } else {
            Log.e("ServiceHandler", "No data received from HTTP Request");
            return null;
        }
    }

    private class GetStudents extends AsyncTask<Void, Void, Void> {
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog proDialog;
        String TestString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proDialog = new ProgressDialog(OrderHistoryActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();
            String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GETRequest);
            Log.i("Response: ", "> " + jsonStr);
            studentList = ParseJSON(jsonStr);
            TestString = jsonStr;
            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            Log.i("Categories: ", "> " + TestString);
            ArrayList<HashMap<String, String>> catsList;
            catsList = ParseJSON(TestString);
            Log.i("Categories 2: ", "> " + catsList);
            Log.i("Categories 2: ", "> " + url);
            if (proDialog.isShowing())
                proDialog.dismiss();
            ListAdapter adapter = new SimpleAdapter(
                    OrderHistoryActivity.this, catsList,
                    R.layout.order_list, new String[]{TAG_ORDER_TITLE, TAG_ORDER_ITEM, TAG_ORDER_GATEWAY, TAG_ORDER_ITEM_AMT, TAG_ORDER_TXID, TAG_ORDER_STATUS}, new int[]{R.id.ordertitle,
                    R.id.orderitem, R.id.pmethod, R.id.orderamt, R.id.ordertxid, R.id.pstatus});

            setListAdapter(adapter);
        }
    }
}


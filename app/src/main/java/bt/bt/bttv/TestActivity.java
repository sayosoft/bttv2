package bt.bt.bttv;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bt.bt.bttv.helper.WebRequest;

public class TestActivity extends ListActivity {

    // JSON Node names
    private static final String TAG_CATEGORY_INFO = "categories";
    private static final String TAG_ID = "category_id";
    private static final String TAG_CATEGORY_TITLE = "category_title";
    private static final String TAG_CATEGORY_TYP = "category_type";
    private static String url = "http://bflix.ignitecloud.in/jsonApi/categories";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

// Calling async task to get json
        new GetStudents().execute();
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObj = new JSONObject(json);

// Getting JSON Array node
                JSONArray students;
                try {
                    students = jsonObj.getJSONArray(TAG_CATEGORY_INFO);
                    Log.i("CategoriesObj 172: ", "> " + students);
                } catch (JSONException e) {
                    students = null;
                    Log.i("CategoriesObj 172: ", "> " + "Students Object Null");
                    e.printStackTrace();
                }

// looping through All Students
                for (int i = 0; i < students.length(); i++) {
                    JSONObject c = students.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_CATEGORY_TITLE);
                    String typ = c.getString(TAG_CATEGORY_TYP);
                    Log.i("Categories 186 ", "> " + name);


// Phone node is JSON Object
                    //JSONObject phone = c.getJSONObject(TAG_STUDENT_PHONE);
                    //String mobile = phone.getString(TAG_STUDENT_PHONE_MOBILE);
                    //String home = phone.getString(TAG_STUDENT_PHONE_HOME);

// tmp hashmap for single student
                    HashMap<String, String> student = new HashMap<String, String>();

// adding every child node to HashMap key => value
                    student.put(TAG_ID, id);
                    student.put(TAG_CATEGORY_TITLE, name);
                    student.put(TAG_CATEGORY_TYP, typ);
                    //student.put(TAG_STUDENT_PHONE_MOBILE, mobile);

// adding student to students list
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

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetStudents extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog proDialog;
        String TestString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(TestActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();

// Making a request to url and getting response
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
            //studentList = ParseJSON(TestString);
            ArrayList<HashMap<String, String>> catsList;

            catsList = ParseJSON(TestString);

            Log.i("Categories 2: ", "> " + catsList);
// Dismiss the progress dialog
            if (proDialog.isShowing())
                proDialog.dismiss();
/**
 * Updating received data from JSON into ListView
 * */
            ListAdapter adapter = new SimpleAdapter(
                    TestActivity.this, catsList,
                    R.layout.list_item, new String[]{TAG_CATEGORY_TITLE, TAG_CATEGORY_TYP}, new int[]{R.id.name,
                    R.id.email});

            setListAdapter(adapter);
        }

    }
}


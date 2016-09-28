package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.WebRequest;
import bt.bt.bttv.model.LoginResponseModel;

public class PlaylistActivity extends AppCompatActivity {
    // JSON Node names
    private static final String TAG_PLAY_INFO = "playhistory";
    private static final String TAG_ID = "play_content_id";
    private static final String TAG_LAST_PLAYED = "play_last_played";
    private static final String TAG_VIDEO_TITLE = "video_title";
    private static final String TAG_VIDEO_POSTER = "video_poster";
    private static String url = "http://bflix.ignitecloud.in/jsonApi/playhistory/";
    private static String url2 = "http://bflix.ignitecloud.in/jsonApi/playhistory2/";
    public SharedPreferences settings;
    Context context = this;
    ArrayAdapter<JsonObject> playAdapter;
    private TextView tx27, tx28, tx29;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        gson = new Gson();
        settings = getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);
        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

        String uid = loginResponseModel.getUser().getUser_id();
        url = "";
        url = "http://bflix.ignitecloud.in/jsonApi/playhistory/" + uid;
        url2 = "";
        url2 = "http://bflix.ignitecloud.in/jsonApi/playhistory2/" + uid;

        setContentView(R.layout.playlist);

        tx27 = (TextView) findViewById(R.id.textView27);
        tx28 = (TextView) findViewById(R.id.textView28);
        tx29 = (TextView) findViewById(R.id.textView29);

        tx27.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistActivity.this, PlaylistinnerActivity.class);
                intent.putExtra("pid", 1);
                startActivity(intent);
            }
        });

        tx28.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistActivity.this, PlaylistinnerActivity.class);
                intent.putExtra("pid", 2);
                startActivity(intent);
            }
        });

        tx29.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlaylistActivity.this, PlaylistinnerActivity.class);
                intent.putExtra("pid", 3);
                startActivity(intent);
            }
        });
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
                    students = jsonObj.getJSONArray(TAG_PLAY_INFO);
                    Log.i("CategoriesObj 172: ", "> " + students);
                } catch (JSONException e) {
                    students = null;
                    Log.i("CategoriesObj 172: ", "> " + "Students Object Null");
                    e.printStackTrace();
                }


                /* JsonParser jsonParser = new JsonParser();
                JsonObject gsonObject = (JsonObject)jsonParser.parse(json);
                playAdapter.add(gsonObject);
                */


                Log.i("CategoriesObj 172Leng: ", "> " + students.length());
// looping through All Students
                for (int i = 0; i < students.length(); i++) {
                    JSONObject c = students.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String title = c.getString(TAG_VIDEO_TITLE);
                    String poster = c.getString(TAG_VIDEO_POSTER);
                    String lastplayed = c.getString(TAG_LAST_PLAYED);

                    Log.i("Categories 186 ", "> " + title);


// Phone node is JSON Object
                    //JSONObject phone = c.getJSONObject(TAG_STUDENT_PHONE);
                    //String mobile = phone.getString(TAG_STUDENT_PHONE_MOBILE);
                    //String home = phone.getString(TAG_STUDENT_PHONE_HOME);

// tmp hashmap for single student
                    HashMap<String, String> student = new HashMap<String, String>();

// adding every child node to HashMap key => value
                    student.put(TAG_ID, "Video ID: " + id);
                    student.put(TAG_VIDEO_TITLE, "Video: " + title);
                    student.put(TAG_VIDEO_POSTER, poster);
                    student.put(TAG_LAST_PLAYED, "Last Played on: " + lastplayed);


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
            proDialog = new ProgressDialog(PlaylistActivity.this);
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

            Future<JsonArray> loading;
            loading = Ion.with(context)
                    .load(url2)
                    .setLogging("MyLogs", Log.DEBUG)
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            // this is called back onto the ui thread, no Activity.runOnUiThread or Handler.post necessary.
                            if (e != null) {
                                Toast.makeText(PlaylistActivity.this, "Error loading play history", Toast.LENGTH_LONG).show();
                                return;
                            }
                            // add the tweets
                            for (int i = 0; i < result.size(); i++) {
                                playAdapter.add(result.get(i).getAsJsonObject());
                            }
                        }
                    });

            return null;
        }

        @Override
        protected void onPostExecute(Void requestresult) {
            super.onPostExecute(requestresult);
            Integer[] imgListInt = new Integer[50];

            Log.i("Categories: ", "> " + TestString);
            //studentList = ParseJSON(TestString);
            ArrayList<HashMap<String, String>> catsList;

            catsList = ParseJSON(TestString);


            Log.i("Categories 22: ", "> " + catsList);
            Log.i("Categories 2: ", "> " + url);

// Dismiss the progress dialog
            if (proDialog.isShowing())
                proDialog.dismiss();

            String[] imgList = new String[50];
            Integer l = 0;

            for (Map<String, String> map : catsList) {
                String tagPoster = map.get(TAG_VIDEO_POSTER);
                imgList[l] = "http://bflix.ignitecloud.in/uploads/images/" + tagPoster;
                Log.i("Categories 222: ", "> " + imgList[l]);
                l++;

            }


/**
 * Updating received data from JSON into ListView
 * */

             /*
            ListAdapter adapter = new SimpleAdapter(
                    PlayHistoryActivity.this, catsList,
                    R.layout.play_list, new String[]{TAG_VIDEO_TITLE, TAG_LAST_PLAYED}, new int[]{R.id.videotitle,
                    R.id.lastplayed});



            setListAdapter(adapter);
            */

           /* for (int i=0; i <= 50; i++ ) {
                View layout = getLayoutInflater().inflate(R.layout.play_list,null);
                try {
                    ImageView posterImg = (ImageView)layout.findViewById(R.id.poster);
                    Ion.with(posterImg)

                            .load(imgList[i]);

                }
                catch (Exception e) {
                    Log.i("ion error: ", "> " + "Image Load Failed");
                }

            } */


        }

    }
}


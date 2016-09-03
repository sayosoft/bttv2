package bt.bt.bttv;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.SessionManager;
import bt.bt.bttv.helper.WebRequest;

public class PlaylistinnerActivity extends AppCompatActivity {
    // JSON Node names
    private static final String TAG_PLAY_INFO = "playliist";
    private static final String TAG_ID = "video_id";
    private static final String TAG_LAST_PLAYED = "playlist_id";
    private static final String TAG_VIDEO_TITLE = "video_title";
    private static final String TAG_VIDEO_POSTER = "video_poster";
    private static String url = "http://bflix.ignitecloud.in/jsonApi/playlist/";
    private static String url2 = "http://bflix.ignitecloud.in/jsonApi/playlist2/";
    Context context = this;
    ArrayAdapter<JsonObject> playAdapter;
    private SQLiteHandler db;
    private SessionManager session;
    private int PlaylistID = 1;
    private ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String bartitle = "Playlist";

        db = new SQLiteHandler(getApplicationContext());
        cd = new ConnectionDetector(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("pid");
            bartitle = extras.getString("title");
            PlaylistID = value;
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
        url = "";
        url = "http://bflix.ignitecloud.in/jsonApi/playlist/" + uid + "/" + PlaylistID;
        url2 = "";
        url2 = "http://bflix.ignitecloud.in/jsonApi/playlist2/" + uid + "/" + PlaylistID;

        playAdapter = new ArrayAdapter<JsonObject>(context, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.play_list, null);


                // grab the tweet (or retweet)
                JsonObject play = getItem(position);

                // grab the user info... name, profile picture, tweet text
                //JsonObject user = play.getAsJsonObject("playhistory");
                JsonObject user = play;
                String twitterId = user.get("video_title").getAsString();

                // set the profile photo using Ion
                String imageUrl = "http://bflix.ignitecloud.in/uploads/images/" + user.get("video_poster").getAsString();

                ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);

                // Use Ion's builder set the google_image on an ImageView from a URL

                // start with the ImageView
                Ion.with(imageView)
                        // use a placeholder google_image if it needs to load from the network

                        // load the url
                        .load(imageUrl);

                // and finally, set the name and text
                TextView handle = (TextView) convertView.findViewById(R.id.videotitle);
                handle.setText(twitterId);

                convertView.setTag(user.get("video_id").getAsInt());

                TextView text = (TextView) convertView.findViewById(R.id.lastplayed);
                text.setVisibility(View.INVISIBLE);
                //text.setText(play.get("play_last_played").getAsString());
                return convertView;
            }
        };
        setContentView(R.layout.lister);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(playAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(bartitle);

        TextView myact = (TextView) findViewById(R.id.myaccounttext);
        myact.setText(bartitle);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d("test", "clicked");
                //Toast.makeText(PlaylistinnerActivity.this, "Testing on Item click"+position+"+"+arg1.getTag(), Toast.LENGTH_LONG).show();
                if (arg1.getTag() != null) {
                    Intent intent = new Intent(PlaylistinnerActivity.this, MovieInnerActivity.class);
                    int iid = (int) arg1.getTag();
                    intent.putExtra("vid", iid);
                    intent.putExtra("autoplay", "yes");
                    startActivity(intent);
                }
            }
        });


        Log.i("Order URL: ", "> " + url);
// Calling async task to get json
        if (cd.isConnectingToInternet()) {
            new GetStudents().execute();
        } else {
            Toast.makeText(PlaylistinnerActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
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

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(PlaylistinnerActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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
            proDialog = new ProgressDialog(PlaylistinnerActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
// Creating service handler class instance
            WebRequest webreq = new WebRequest();

// Making a request to url and getting response
            // String jsonStr = webreq.makeWebServiceCall(url, WebRequest.GETRequest);

            //Log.i("Response: ", "> " + jsonStr);

            //studentList = ParseJSON(jsonStr);
            //TestString = jsonStr;

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
                                Toast.makeText(PlaylistinnerActivity.this, "Error loading play history", Toast.LENGTH_LONG).show();
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

            //catsList = ParseJSON(TestString);


            //  Log.i("Categories 22: ", "> " + catsList);
            //Log.i("Categories 2: ", "> " + url);

// Dismiss the progress dialog
            if (proDialog.isShowing())
                proDialog.dismiss();

            String[] imgList = new String[50];
            Integer l = 0;
            /*if(catsList != null) {

                for (Map<String, String> map : catsList) {
                    String tagPoster = map.get(TAG_VIDEO_POSTER);
                    imgList[l] = "http://bflix.ignitecloud.in/uploads/images/" + tagPoster;
                    Log.i("Categories 222: ", "> " + imgList[l]);
                    l++;

                }
            }*/


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


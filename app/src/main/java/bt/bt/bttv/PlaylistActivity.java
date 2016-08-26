package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
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

import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.SessionManager;

public class PlaylistActivity extends AppCompatActivity {
    // JSON Node names
    private static final String TAG_PLAY_INFO = "playhistory";
    private static final String TAG_ID = "play_content_id";
    private static final String TAG_LAST_PLAYED = "play_last_played";
    private static final String TAG_VIDEO_TITLE = "video_title";
    private static final String TAG_VIDEO_POSTER = "video_poster";
    private static String url = "http://bflix.ignitecloud.in/jsonApi/playhistory/";
    private static String url2 = "http://bflix.ignitecloud.in/jsonApi/playhistory2/";
    Context context = this;
    ArrayAdapter<JsonObject> playAdapter;
    private TextView tx27, tx28, tx29;
    private SQLiteHandler db;
    private SessionManager session;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
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
        /*

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
                String imageUrl = "http://bflix.ignitecloud.in/uploads/images/"+user.get("video_poster").getAsString();

                ImageView imageView = (ImageView)convertView.findViewById(R.id.poster);

                // Use Ion's builder set the google_image on an ImageView from a URL

                // start with the ImageView
                Ion.with(imageView)
                        // use a placeholder google_image if it needs to load from the network

                        // load the url
                        .load(imageUrl);

                // and finally, set the name and text
                TextView handle = (TextView)convertView.findViewById(R.id.videotitle);
                handle.setText(twitterId);

                TextView text = (TextView)convertView.findViewById(R.id.lastplayed);
                text.setText(play.get("play_last_played").getAsString());
                return convertView;
            }
        };
        setContentView(R.layout.lister);
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(playAdapter); */


        Log.i("Order URL: ", "> " + url);
// Calling async task to get json
        //new GetStudents().execute();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Test Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://bt.bt.bttv/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Test Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://bt.bt.bttv/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
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
        Intent intent = new Intent(PlaylistActivity.this, LoginActivity.class);
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


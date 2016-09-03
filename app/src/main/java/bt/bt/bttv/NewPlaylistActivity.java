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

import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.helper.SessionManager;
import bt.bt.bttv.helper.WebRequest;

public class NewPlaylistActivity extends AppCompatActivity {
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
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getApplicationContext());
        cd = new ConnectionDetector(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int value = extras.getInt("pid");
            PlaylistID = value;

        }

        HashMap<String, String> user = db.getUserDetails();

        String uid = user.get("uid");
        url = "";
        url = "http://bflix.ignitecloud.in/jsonApi/getplaylist/" + uid;
        url2 = "";
        url2 = "http://bflix.ignitecloud.in/jsonApi/getplaylist2/" + uid;

        playAdapter = new ArrayAdapter<JsonObject>(context, 0) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.play_list, null);
                JsonObject play = getItem(position);
                JsonObject user = play;
                String twitterId = user.get("playlist_name").getAsString();

                ImageView imageView = (ImageView) convertView.findViewById(R.id.poster);
                imageView.setBackgroundResource(R.drawable.addtoplaylist);
                TextView handle = (TextView) convertView.findViewById(R.id.videotitle);
                handle.setText(twitterId);

                convertView.setTag(user.get("playlist_id").getAsInt());

                TextView text = (TextView) convertView.findViewById(R.id.lastplayed);
                text.setVisibility(View.INVISIBLE);
                return convertView;
            }
        };
        setContentView(R.layout.lister);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(playAdapter);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("My Playlists");

        TextView myact = (TextView) findViewById(R.id.myaccounttext);
        myact.setText("My Playlists");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                Log.d("test", "clicked");
                if (arg1.getTag() != null) {
                    Intent intent = new Intent(NewPlaylistActivity.this, PlaylistinnerActivity.class);
                    int iid = (int) arg1.getTag();
                    intent.putExtra("pid", iid);

                    startActivity(intent);
                }
            }
        });
        Log.i("Order URL: ", "> " + url);
        if (cd.isConnectingToInternet()) {
            new GetStudents().execute();
        } else {
            Toast.makeText(NewPlaylistActivity.this, "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
// Hashmap for ListView
                ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObj = new JSONObject(json);
                JSONArray students;
                try {
                    students = jsonObj.getJSONArray(TAG_PLAY_INFO);
                    Log.i("CategoriesObj 172: ", "> " + students);
                } catch (JSONException e) {
                    students = null;
                    Log.i("CategoriesObj 172: ", "> " + "Students Object Null");
                    e.printStackTrace();
                }

                Log.i("CategoriesObj 172Leng: ", "> " + students.length());
                for (int i = 0; i < students.length(); i++) {
                    JSONObject c = students.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String title = c.getString(TAG_VIDEO_TITLE);
                    String poster = c.getString(TAG_VIDEO_POSTER);
                    String lastplayed = c.getString(TAG_LAST_PLAYED);

                    Log.i("Categories 186 ", "> " + title);
                    HashMap<String, String> student = new HashMap<String, String>();
                    student.put(TAG_ID, "Video ID: " + id);
                    student.put(TAG_VIDEO_TITLE, "Video: " + title);
                    student.put(TAG_VIDEO_POSTER, poster);
                    student.put(TAG_LAST_PLAYED, "Last Played on: " + lastplayed);
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

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> studentList;
        ProgressDialog proDialog;
        String TestString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
// Showing progress loading dialog
            proDialog = new ProgressDialog(NewPlaylistActivity.this);
            proDialog.setMessage("Please wait...");
            proDialog.setCancelable(false);
            proDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            WebRequest webreq = new WebRequest();

            Future<JsonArray> loading;
            loading = Ion.with(context)
                    .load(url2)
                    .setLogging("MyLogs", Log.DEBUG)
                    .asJsonArray()
                    .setCallback(new FutureCallback<JsonArray>() {
                        @Override
                        public void onCompleted(Exception e, JsonArray result) {
                            if (e != null) {
                                Toast.makeText(NewPlaylistActivity.this, "Error loading play history", Toast.LENGTH_LONG).show();
                                return;
                            }
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
            if (proDialog.isShowing())
                proDialog.dismiss();

            String[] imgList = new String[50];
            Integer l = 0;

        }
    }
}


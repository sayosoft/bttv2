package bt.bt.bttv;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bt.bt.bttv.adapter.AudioHomeAdapter;
import bt.bt.bttv.adapter.VideoHomeAdapter;
import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.model.AudiosModel;
import bt.bt.bttv.model.LoginResponseModel;
import bt.bt.bttv.model.MyPlayListContentModel;
import bt.bt.bttv.model.MyPlayListModel;
import bt.bt.bttv.model.VideosModel;
import bt.bt.bttv.model.WatchLaterModel;

public class PlaylistContentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, ApiInt {

    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    MyPlayListContentModel myPlayListContentModel;
    List<AudiosModel> audiosModelListFiltered = new ArrayList<>();
    List<VideosModel> videoModelListFiltered = new ArrayList<>();
    private ConnectionDetector cd;
    private APiAsync aPiAsync;
    private LinearLayout llMain;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist_content);

        MyPlayListModel.ArrayBean arrayBean = getIntent().getParcelableExtra("playlistModel");

        cd = new ConnectionDetector(this);
        settings = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        gson = new Gson();
        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(arrayBean.getPlaylist_name() + " Content");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        llMain = (LinearLayout) findViewById(R.id.llMain);

        apiGetPlaylistContent(arrayBean.getPlaylist_id());
    }

    private void apiGetPlaylistContent(String playlist_id) {
        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, PlaylistContentActivity.this, getResources().getString(R.string.url_get_playlist_content) + loginResponseModel.getUser().getUser_id() + "/" + playlist_id, getString(R.string.msg_progress_dialog), APiAsync.PLAYLIST_CONTENT, null);
            aPiAsync.execute();
        } else {
            Toast.makeText(PlaylistContentActivity.this, getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(String response, int requestcode) {
        switch (requestcode) {
            case APiAsync.PLAYLIST_CONTENT:
                Log.e("Playlist content", response);
                Gson gson = new Gson();
                myPlayListContentModel = gson.fromJson(response.toString(), MyPlayListContentModel.class);
                for (int i = 0; i < myPlayListContentModel.getPlaylist().size(); i++) {

                    if (myPlayListContentModel.getPlaylist().get(i).getMedia_type().equals("1")) {
                        for (int j = 0; j < WatchLaterModel.audiosModelList.size(); j++) {
                            if (myPlayListContentModel.getPlaylist().get(i).getVideo_id().equals(WatchLaterModel.audiosModelList.get(j).getAudio_id())) {
                                audiosModelListFiltered.add(WatchLaterModel.audiosModelList.get(j));
                            }
                        }
                    } else if (myPlayListContentModel.getPlaylist().get(i).getMedia_type().equals("2")) {
                        for (int j = 0; j < WatchLaterModel.videoModelList.size(); j++) {
                            if (myPlayListContentModel.getPlaylist().get(i).getVideo_id().equals(WatchLaterModel.videoModelList.get(j).getVideo_id())) {
                                videoModelListFiltered.add(WatchLaterModel.videoModelList.get(j));
                            }
                        }
                    }
                }
                setPlaylistcontents(audiosModelListFiltered, videoModelListFiltered);
                break;
        }
    }

    private void setPlaylistcontents(List<AudiosModel> audiosModelListFiltered, List<VideosModel> videoModelListFiltered) {

        if (videoModelListFiltered.size() > 0) {
            RecyclerView recyclerView = new RecyclerView(PlaylistContentActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            params.setMargins(5, 0, 5, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            VideoHomeAdapter videoHomeAdapter = new VideoHomeAdapter(PlaylistContentActivity.this, videoModelListFiltered);
            recyclerView.setAdapter(videoHomeAdapter);

            TextView tvTitle = new TextView(PlaylistContentActivity.this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(params1);
            params1.setMargins(10, 10, 0, 0);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText("Videos");

            llMain.addView(tvTitle);
            llMain.addView(recyclerView);
        }

        if (audiosModelListFiltered.size() > 0) {
            RecyclerView recyclerView = new RecyclerView(PlaylistContentActivity.this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            params.setMargins(5, 0, 5, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            AudioHomeAdapter audioHomeAdapter = new AudioHomeAdapter(PlaylistContentActivity.this, audiosModelListFiltered);
            recyclerView.setAdapter(audioHomeAdapter);

            TextView tvTitle = new TextView(PlaylistContentActivity.this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(params1);
            params1.setMargins(10, 10, 0, 0);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText("Audios");

            llMain.addView(tvTitle);
            llMain.addView(recyclerView);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            startActivity(new Intent(this, VideoHomeActivity.class));

        } else if (id == R.id.nav_audio) {
            startActivity(new Intent(this, AudioHomeActivity.class));
            finish();

        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));

        } else if (id == R.id.nav_radio) {
            startActivity(new Intent(this, RadioChannelActivity.class));

        } else if (id == R.id.nav_sports) {
            Intent intent = new Intent(this, NewSportsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewNewsActivity.class));

        } else if (id == R.id.nav_myacc) {
            startActivity(new Intent(this, MyPreferencesActivity.class));

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_terms) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_terms_conditios)));

        } else if (id == R.id.nav_privacy) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_privacy_policy)));

        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
}

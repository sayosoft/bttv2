package bt.bt.bttv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bt.bt.bttv.fragment.HomeFragment;
import bt.bt.bttv.fragment.LaterFragment;
import bt.bt.bttv.fragment.MyFavoriteFragment;
import bt.bt.bttv.fragment.MyPlaylistsFragment;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.DrawerCategoriesModel;
import bt.bt.bttv.model.VideosModel;


public class GenreActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public SharedPreferences settings;
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;
    private List<VideosModel> videosModelsList;
    private JSONObject jsonObject;
    private SQLiteHandler db;
    private List<DrawerCategoriesModel> drawerCategoriesModelList;

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new SQLiteHandler(getApplicationContext());
        cd = new ConnectionDetector(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(GlobleMethods.toolbar_title);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

//        send content type to fragment

        adapter.addFragment(new HomeFragment(), "Popular");
        adapter.addFragment(new LaterFragment(), "Later");
        adapter.addFragment(new MyFavoriteFragment(), "My Fav");
        adapter.addFragment(new MyPlaylistsFragment(), "My Playlists");
        viewPager.setAdapter(adapter);
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


    /*private void getAudioCategories(List<DrawerCategoriesModel> drawerCategoriesModelsLists) {

        drawerCategoriesModelList = new ArrayList<>();
        for (int i = 0; i < drawerCategoriesModelsLists.size(); i++) {
            if (drawerCategoriesModelsLists.get(i).getCategory_type().equals("VoD")) {
                drawerCategoriesModelList.add(drawerCategoriesModelsLists.get(i));
            }
        }
        new GetAudio().execute();
    }*/


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            Intent intent = new Intent(this, VideoHomeActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_audio) {
            Intent intent = new Intent(this, AudioHomeActivity.class);
            startActivity(intent);
            finish();

        } else if (id == R.id.nav_home) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);

        }else if (id == R.id.nav_radio) {
            //Intent intent = new Intent(this, RadioChannelActivity.class);
            //startActivity(intent);

        } else if (id == R.id.nav_sports) {
            Intent intent = new Intent(this, NewSportsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(this, NewNewsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_myacc) {
            Intent intent = new Intent(this, MyPreferencesActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_terms) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_terms));
            startActivity(intent);

        } else if (id == R.id.nav_privacy) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", getString(R.string.url_privacy));
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

   /* private class GetAudio extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(VideoHomeActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_get_video));
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            try {
                jsonObject = new JSONObject(result);
                Gson gson = new Gson();
                videosModelsList = gson.fromJson(jsonObject.getJSONArray("videos").toString(), new TypeToken<List<VideosModel>>() {
                }.getType());
                inflateData1();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }*/
}




package bt.bt.bttv;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import bt.bt.bttv.helper.GlobleMethods;

public class ManageFamilyMembersActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    RecyclerView rvFamilyMembers;
    private FloatingActionButton FAB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_family_members);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("BTTV");
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(ManageFamilyMembersActivity.this);

        FAB = (FloatingActionButton) findViewById(R.id.fabAddMember);
        FAB.setOnClickListener(this);

        rvFamilyMembers = (RecyclerView) findViewById(R.id.rvFamilyMembers);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddMember:
                startActivity(new Intent(ManageFamilyMembersActivity.this, AddFamilyMemberActivity.class));
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_movies) {
            startActivity(new Intent(this, VideoHomeActivity.class));

        } else if (id == R.id.nav_audio) {
            startActivity(new Intent(this, AudioHomeActivity.class));

        } else if (id == R.id.nav_home) {
            startActivity(new Intent(this, HomeActivity.class));

        } else if (id == R.id.nav_tvchannel) {
            startActivity(new Intent(this, TvChannelActivity.class));

        } else if (id == R.id.nav_radio) {
            startActivity(new Intent(this, RadioChannelActivity.class));

        } else if (id == R.id.nav_sports) {
            startActivity(new Intent(this, NewSportsActivity.class));

        } else if (id == R.id.nav_news) {
            startActivity(new Intent(this, NewNewsActivity.class));

        } else if (id == R.id.nav_myacc) {
            startActivity(new Intent(this, MyAccountActivity.class));

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));

        } else if (id == R.id.nav_terms) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_terms)));

        } else if (id == R.id.nav_privacy) {
            startActivity(new Intent(this, WebViewActivity.class).putExtra("url", getString(R.string.url_privacy)));

        } else if (id == R.id.nav_logout) {
            GlobleMethods globleMethods = new GlobleMethods(this);
            globleMethods.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
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
}

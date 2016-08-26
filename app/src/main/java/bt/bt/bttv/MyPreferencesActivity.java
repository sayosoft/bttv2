package bt.bt.bttv;

import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

public class MyPreferencesActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();


    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return false;
    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference myPref = findPreference("editprofile");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), EditProfileActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref2 = findPreference("orderhistory");
            myPref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), OrderHistoryNew.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref3 = findPreference("playhistory");
            myPref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), PlayHistoryActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref4 = findPreference("managesubs");
            myPref4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), MyAccountActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref5 = findPreference("manageplaylist");
            myPref5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), NewPlaylistActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref6 = findPreference("managewishlist");
            myPref6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), WatchlistActivity.class);
                    startActivity(i);
                    return true;
                }
            });

        }
    }
/*
    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference myPref = findPreference("editprofile");
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), EditProfileActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref2 = findPreference("orderhistory");
            myPref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), OrderHistoryNew.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref3 = findPreference("playhistory");
            myPref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), PlayHistoryActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref4 = findPreference("managesubs");
            myPref4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), MyAccountActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref5 = findPreference("manageplaylist");
            myPref5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), NewPlaylistActivity.class);
                    startActivity(i);
                    return true;
                }
            });

            Preference myPref6 = findPreference("managewishlist");
            myPref6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    //open browser or intent here
                    Intent i = new Intent(preference.getContext(), WatchlistActivity.class);
                    startActivity(i);
                    return true;
                }
            });

        }
    }*/


}
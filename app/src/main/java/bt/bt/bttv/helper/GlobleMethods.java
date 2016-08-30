package bt.bt.bttv.helper;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import bt.bt.bttv.LoginActivity;

public class GlobleMethods {

    public static final String PREFS_NAME = "MyPrefs";
    public static final String logFlag = "logFlag";
    public static MediaPlayer mp = new MediaPlayer();
    public static String content_type = "";
    public static String genre_type = "";
    public static String category_id = "";
    public SharedPreferences settings;
    private Context _context;
    private SQLiteHandler db;


    public GlobleMethods(Context context) {
        this._context = context;
    }

    public static void playSong(String url) {
//    	Metadata.setIsStopInCall(1);
        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mp.reset();
        try {
            mp.setDataSource(url);
            mp.prepare();
        } catch (Exception e) {
        }

        mp.start();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }

    public void logoutUser() {

        settings = _context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        db = new SQLiteHandler(_context);

        new android.support.v7.app.AlertDialog.Builder(_context)
                .setTitle("Logout?")
                .setMessage("are you sure you want to logout??")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(_context, "Logging Out", Toast.LENGTH_SHORT).show();
                        db.deleteUsers();
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove(logFlag);
                        editor.commit();
                        // Launching the login activity
                        Intent intent = new Intent(_context, LoginActivity.class);
                        _context.startActivity(intent);
//                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();


    }


}

package bt.bt.bttv;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.helper.SQLiteHandler;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class PlayAudio extends AppCompatActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private static String TAG = "PlayVideoNew";
    final Handler h = new Handler();
    boolean isPortrait = true;
    boolean avoidLoop = false;
    Context context;
    Runnable sendData;
    private String path = "http://flv2.bn.netease.com/tvmrepo/2016/5/N/3/EBMTJBGN3/SD/EBMTJBGN3-mobile.mp4";
    private Uri uri;
    private VideoView mVideoView;
    private ProgressBar pb;
    private TextView downloadRateView, loadRateView, titleTextview, durationTextview, genreTextview, descTextview, castTextview, directorTextview;
    private ImageButton PPBtn;
    private ImageView btnShare, volIcon, backIcon, settIcon, PlaylistIcon;
    private FrameLayout fl_controller;
    private LinearLayout infoView;
    private long mPosition = 0;
    private String mPos = "";
    private String Title = "";
    private String VideoID = "";
    private String VideoResume = "";
    private String MovieDuration = "";
    private String MovieGenre = "";
    private String MovieDesciption = "";
    private String MovieCast = "";
    private String MovieDirector = "";
    private String VidQuality = "";
    private String Subtitle = "";
    private String Playlist = "";
    private int PlaylistID = 0;
    private String subtitle_path = "http://bflix.ignitecloud.in/uploads/test.srt";
    private TextView mSubtitleView;
    private SeekBar volumeControl = null;
    private AudioManager audioManager = null;
    private boolean isBuffering = false;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            path = extras.getString("vurl");
            Title = extras.getString("title");
            VideoID = extras.getString("vid");
            VideoResume = extras.getString("vresume");
            MovieDuration = extras.getString("duration");
            MovieGenre = extras.getString("genre");
            MovieDesciption = extras.getString("desc");
            MovieCast = extras.getString("cast");
            MovieDirector = extras.getString("director");
            if (VideoResume != null) {
                mPosition = Long.parseLong(VideoResume);
            }
            Log.i("Vitamio Video Path:", path);
            //The key argument here must match that used in the other activity
        } else {
            Log.i("Vitamio Video Path:", path);
        }
        if (!LibsChecker.checkVitamioLibs(this)) {
            return;
        }
        setContentView(R.layout.activity_play_video_new);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        /*hp = getHeightPixel(PlayAudio.this);
        wp = getWidthPixel(PlayAudio.this);
        sp = getStatusBarHeight(PlayAudio.this);*/
        init();
    }

    private void init() {
        mVideoView = (VideoView) findViewById(R.id.buffer);
        mSubtitleView = (TextView) findViewById(R.id.subtitle_view);
        fl_controller = (FrameLayout) findViewById(R.id.fl_controller);
        pb = (ProgressBar) findViewById(R.id.probar);
        PPBtn = (ImageButton) findViewById(R.id.ppbtn);
        downloadRateView = (TextView) findViewById(R.id.download_rate);
        loadRateView = (TextView) findViewById(R.id.load_rate);
        titleTextview = (TextView) findViewById(R.id.textView28);
        durationTextview = (TextView) findViewById(R.id.movieduration);
        genreTextview = (TextView) findViewById(R.id.moviegenre);
        descTextview = (TextView) findViewById(R.id.moviedesc);
        castTextview = (TextView) findViewById(R.id.moviecast);
        directorTextview = (TextView) findViewById(R.id.moviedirector);
        infoView = (LinearLayout) findViewById(R.id.infoview);
        btnShare = (ImageView) findViewById(R.id.mediacontroller_play_share_new);
        volIcon = (ImageView) findViewById(R.id.vol_icon);
        backIcon = (ImageView) findViewById(R.id.back_icon);
        settIcon = (ImageView) findViewById(R.id.sett_icon);
        PlaylistIcon = (ImageView) findViewById(R.id.playlist_icon);
        if (path == "") {
            return;
        } else {
            uri = Uri.parse(path);
            mVideoView.setVideoURI(uri);
            // mVideoView.setVisibility(View.INVISIBLE);
            setTitles();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isPortrait = false;
                RelativeLayout.LayoutParams fl_lp = new RelativeLayout.LayoutParams(
                        getHeightPixel(PlayAudio.this),
                        getWidthPixel(PlayAudio.this) - getStatusBarHeight(PlayAudio.this)
                );
                fl_controller.setLayoutParams(fl_lp);
            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                isPortrait = true;
            }
            MediaController mc = new MediaController(this, true, fl_controller);
            mc.setOnControllerClick(new MediaController.OnControllerClick() {
                @Override
                public void OnClick(int type) {
                    if (type == 1) {
                        shareIt();
                    }
                    if (type == 0) {
                        avoidLoop = true;
                        if (isPortrait) {
                            RelativeLayout.LayoutParams fl_lp = new RelativeLayout.LayoutParams(
                                    getHeightPixel(PlayAudio.this),
                                    getWidthPixel(PlayAudio.this) - getStatusBarHeight(PlayAudio.this)
                            );
                            fl_controller.setLayoutParams(fl_lp);
                            avoidLoop = true;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            if (Build.VERSION.SDK_INT > 18) {
                                View decorView = getWindow().getDecorView();
                                // Hide the status bar.
                                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_FULLSCREEN;
                                decorView.setSystemUiVisibility(uiOptions);
                            }
                            infoView.setVisibility(View.INVISIBLE);
                            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                            isPortrait = false;
                        } else {
                            if (Build.VERSION.SDK_INT > 18) {
                                View decorView = getWindow().getDecorView();
                                // Hide the status bar.
                                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                                decorView.setSystemUiVisibility(uiOptions);
                            }
                            infoView.setVisibility(View.VISIBLE);
                            avoidLoop = true;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                            isPortrait = true;
                        }
                    }
                }
            });
            volumeControl = (SeekBar) findViewById(R.id.volume_bar);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeControl.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeControl.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));
            if (volumeControl != null) {
                volumeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    int progressChanged = 0;

                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progressChanged = progress;
                    }

                    public void onStartTrackingTouch(SeekBar seekBar) {
                        // TODO Auto-generated method stub
                    }

                    public void onStopTrackingTouch(SeekBar seekBar) {
                        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                progressChanged, 0);
                    }
                });
            } else {
                Toast.makeText(PlayAudio.this, "seek bar progress failed as its null:",
                        Toast.LENGTH_SHORT).show();
            }
            if (btnShare != null) {
                btnShare.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        shareIt();
                    }
                });
            }
            mVideoView.setMediaController(mc);
            mc.setVisibility(View.GONE);
            PPBtn.setVisibility(View.GONE);
            mVideoView.requestFocus();
            mVideoView.setOnInfoListener(this);
            mVideoView.setOnBufferingUpdateListener(this);
            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setAdaptiveStream(true);
                    if (subtitle_path != null) {
                        mVideoView.addTimedTextSource(subtitle_path);
                        mVideoView.setTimedTextShown(true);
                    }
                    mediaPlayer.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
                    if (!isPortrait) {
                        infoView.setVisibility(View.INVISIBLE);
                        RelativeLayout.LayoutParams fl_lp = new RelativeLayout.LayoutParams(
                                getWidthPixel(PlayAudio.this),
                                getHeightPixel(PlayAudio.this)
                        );
                        fl_controller.setLayoutParams(fl_lp);
                        avoidLoop = true;
                        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
                        if (Build.VERSION.SDK_INT > 18) {
                            View decorView = getWindow().getDecorView();
                            // Hide the status bar.
                            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                        mediaPlayer.setPlaybackSpeed(1.0f);
                    } else {
                        mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, 0);
                        infoView.setVisibility(View.VISIBLE);
                        if (Build.VERSION.SDK_INT > 18) {
                            View decorView = getWindow().getDecorView();
                            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                            decorView.setSystemUiVisibility(uiOptions);
                        }
                    }
                }
            });
            mVideoView.setOnTimedTextListener(new MediaPlayer.OnTimedTextListener() {
                @Override
                public void onTimedText(String text) {
                    mSubtitleView.setText(text);
                }

                @Override
                public void onTimedTextUpdate(byte[] pixels, int width, int height) {
                }
            });
            if (mPosition != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder
                        .setTitle("Resume Video")
                        .setMessage("Resume Video from Last Known Position?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                mVideoView.seekTo(mPosition);
                            }
                        })
                        .setNegativeButton("Yes", null)
                        .show();
            }
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)) == 0) {
                        if (!isPortrait) {
                            if (Build.VERSION.SDK_INT > 18) {
                                // Hide the status bar.
                                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
                                decorView.setSystemUiVisibility(uiOptions);
                            }
                        }
                    }
                }
            });
            final int delay = 5000; //milliseconds
            sendData = new Runnable() {
                public void run() {
                    updatetime();
                    h.postDelayed(this, delay);
                }
            };
            h.postDelayed(sendData, delay);
            PPBtn.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mVideoView.isPlaying()) {
                        PPBtn.setBackgroundResource(R.drawable.pauseicon);
                        mVideoView.pause();
                    } else {
                        PPBtn.setBackgroundResource(R.drawable.playicon);
                        mVideoView.start();
                    }
                }
            });

            backIcon.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    finish();
                }
            });

            settIcon.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    SettingChoice();
                }
            });

            PlaylistIcon.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    PlaylistAlertDialogView();
                }
            });


            volIcon.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
            });

            mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.i(TAG, "Video onComplete hit");
                    finish();
                }
            });
        }
    }

    private boolean isPortrait(int orientation) {
        return (orientation >= (360 - 90) && orientation <= 360) || (orientation >= 0 && orientation <= 90);
    }

    private void SettingChoice() {
        final CharSequence[] items = {"Video Quality", "Subtitle Language"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Video Settings");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(PlayAudio.this,
                        items[which] + " Selected", Toast.LENGTH_LONG)
                        .show();
                if (items[which] == "Video Quality") {
                    dialog.dismiss();
                    SettingAlertDialogView();
                } else if (items[which] == "Subtitle Language") {
                    dialog.dismiss();
                    SubtitleAlertDialogView();
                }
            }
        });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void PlaylistAlertDialogView() {
        final CharSequence[] items = {"Favorites", "Playlist", "Watch Later"};
        final int[] playitems = {2, 0, 1};
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayAudio.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Add to ");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Playlist = items[item].toString();
                        PlaylistID = playitems[item];
                        if (PlaylistID != 0) {
                            new PlaylistAsync().execute();
                            dialog.dismiss();
                        } else {
                            NewPlaylistDialog();
                            dialog.dismiss();
                        }
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void NewPlaylistDialog() {
        new GetPlaylistAsync().execute();
    }

    private void CreateNewPlaylist() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(PlayAudio.this);
        alert.setMessage("Create New Playlist");
        alert.setTitle("Enter Playlist Name");
        alert.setView(edittext);
        alert.setPositiveButton("Create & Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String PlaylistName = edittext.getText().toString();
                Playlist = PlaylistName;
                new NewPlaylistAsync().execute();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.dismiss();
            }
        });
        alert.show();
    }

    private void SubtitleAlertDialogView() {
        final CharSequence[] items = {"Off", "English"};
        AlertDialog.Builder builder = new AlertDialog.Builder(PlayAudio.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Change Subtitle Language");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        Toast.makeText(getApplicationContext(), items[item],
                                Toast.LENGTH_SHORT).show();
                        Subtitle = items[item].toString();
                    }
                });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (Subtitle == "Off") {
                    mVideoView.setTimedTextShown(false);
                } else {
                    mVideoView.setTimedTextShown(true);
                    mSubtitleView.setVisibility(View.VISIBLE);
                    Toast.makeText(PlayAudio.this, Subtitle + " Set as Subtitle Language", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void SettingAlertDialogView() {
        final CharSequence[] items = {"Low", "Standard", "High Definition"};

        AlertDialog.Builder builder = new AlertDialog.Builder(PlayAudio.this);//ERROR ShowDialog cannot be resolved to a type
        builder.setTitle("Change Video Quality");
        builder.setSingleChoiceItems(items, -1,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        VidQuality = items[item].toString();
                    }
                });

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                switch (VidQuality) {
                    case "Low":
                        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_LOW);
                        break;
                    case "Standard":
                        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
                        break;
                    case "High Definition":
                        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
                        break;
                    default:
                        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_MEDIUM);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void shareIt() {
        mVideoView.pause();
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Please Check out" + Title + "BTTV, Bhutans Only Premier Video Streaming Service, click on this link http://www.bttv.bt/movies/detail/" + VideoID;
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "BTTV");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void changerot() {
        if (!isPortrait) {
            infoView.setVisibility(View.INVISIBLE);
            RelativeLayout.LayoutParams fl_lp = new RelativeLayout.LayoutParams(
                    getWidthPixel(PlayAudio.this),
                    getHeightPixel(PlayAudio.this)
            );
            fl_controller.setLayoutParams(fl_lp);
            avoidLoop = true;
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE, 0);
            if (Build.VERSION.SDK_INT > 18) {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

                decorView.setSystemUiVisibility(uiOptions);
            }
            isPortrait = false;
        } else {
            RelativeLayout.LayoutParams fl_lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    DensityUtil.dip2px(230, PlayAudio.this)
            );
            fl_controller.setLayoutParams(fl_lp);
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_ORIGIN, 0);
            infoView.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT > 18) {
                View decorView = getWindow().getDecorView();
                // Hide the status bar.
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE;

                decorView.setSystemUiVisibility(uiOptions);
            }
            avoidLoop = true;
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            isPortrait = true;
        }

        new CountDownTimer(2000, 1000) { // 5000 = 5 sec
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                PPBtn.setVisibility(View.GONE);
                btnShare.setVisibility(View.GONE);
                volIcon.setVisibility(View.GONE);
                backIcon.setVisibility(View.GONE);
                settIcon.setVisibility(View.GONE);
                PlaylistIcon.setVisibility(View.GONE);
            }
        }.start();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // if(!avoidLoop) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            isPortrait = false;
            changerot();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            isPortrait = true;
            changerot();
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downloadRateView.setText("");
                    loadRateView.setText("");
                    loadRateView.setVisibility(View.VISIBLE);
                    downloadRateView.setVisibility(View.GONE);
                    isBuffering = true;
                    PPBtn.setVisibility(View.GONE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                if (!isFinishing()) {
                    mVideoView.start();
                    pb.setVisibility(View.GONE);
                    downloadRateView.setVisibility(View.GONE);
                    loadRateView.setVisibility(View.GONE);
                    isBuffering = false;
                }
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
                downloadRateView.setText("" + extra + "kb/s" + "  ");
                break;
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

    @Override
    protected void onPause() {
        if (mVideoView.isPlaying()) {
            mPosition = mVideoView.getCurrentPosition();
            mVideoView.stopPlayback();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (mPosition > 0) {
            mVideoView.seekTo(mPosition);
            mPosition = 0;
        }
        super.onResume();
        mVideoView.start();
    }

    void updatetime() {
        long ctime = mVideoView.getCurrentPosition();
        String ct = Long.toString(ctime);
        mPos = ct;
        if (mVideoView.isPlaying()) {
            new ResumeAsync().execute();
            Log.i("Current Position:", ct);
        }
    }


    public int getHeightPixel(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }

    public int getWidthPixel(Activity activity) {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        return localDisplayMetrics.widthPixels;
    }

    public int getStatusBarHeight(Activity activity) {
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        return statusBarHeight;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, AudioHomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        h.removeCallbacks(sendData);
        mVideoView = null;
        System.gc();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void setTitles() {
        if (Title != null) {
            titleTextview.setText(Title);
        } else {
            titleTextview.setText("Unknown");
        }
        if (MovieDuration != null) {
            durationTextview.setText(MovieDuration);
        } else {
            durationTextview.setText("Unknown");
        }
        if (MovieGenre != null) {
            genreTextview.setText(MovieGenre);
        } else {
            genreTextview.setText("Unknown");
        }
        if (MovieDesciption != null) {
            descTextview.setText(MovieDesciption);
        } else {
            descTextview.setText("Unknown");
        }
        if (MovieCast != null) {
            castTextview.setText(MovieCast);
        } else {
            castTextview.setText("Unknown");
        }
        if (MovieDirector != null) {
            directorTextview.setText(MovieDirector);
        } else {
            directorTextview.setText("Unknown");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //Log.i(TAG, "View under finger: " + findViewAtPosition(getApplicationContext().getRootView(), (int)ev.getRawX(), (int)ev.getRawY()));
        if (ev.getAction() == MotionEvent.ACTION_DOWN && !isBuffering) {
            PPBtn = (ImageButton) findViewById(R.id.ppbtn);
            btnShare.setVisibility(View.VISIBLE);
            volIcon.setVisibility(View.VISIBLE);
            backIcon.setVisibility(View.VISIBLE);
            settIcon.setVisibility(View.VISIBLE);
            PlaylistIcon.setVisibility(View.VISIBLE);
            if (mVideoView.isPlaying()) {
                PPBtn.setBackgroundResource(R.drawable.pauseicon);
                PPBtn.setVisibility(View.VISIBLE);
                backIcon.setVisibility(View.VISIBLE);
                settIcon.setVisibility(View.VISIBLE);
                PlaylistIcon.setVisibility(View.VISIBLE);
            } else {
                PPBtn.setBackgroundResource(R.drawable.playicon);
                PPBtn.setVisibility(View.VISIBLE);
                btnShare.setVisibility(View.VISIBLE);
                volIcon.setVisibility(View.VISIBLE);
                settIcon.setVisibility(View.VISIBLE);
                PlaylistIcon.setVisibility(View.VISIBLE);
            }
            new CountDownTimer(3000, 1000) { // 5000 = 5 sec
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    PPBtn.setVisibility(View.GONE);
                    btnShare.setVisibility(View.GONE);
                    volIcon.setVisibility(View.GONE);
                    backIcon.setVisibility(View.GONE);
                    settIcon.setVisibility(View.GONE);
                    PlaylistIcon.setVisibility(View.GONE);
                }
            }.start();
            return true;
        } else {
            return false;
        }
    }

    private View findViewAtPosition(View parent, int x, int y) {
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                View viewAtPosition = findViewAtPosition(child, x, y);
                if (viewAtPosition != null) {
                    return viewAtPosition;
                }
            }
            return null;
        } else {
            Rect rect = new Rect();
            parent.getGlobalVisibleRect(rect);
            if (rect.contains(x, y)) {
                return parent;
            } else {
                return null;
            }
        }
    }

    class ResumeAsync extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            Log.d("PreExceute", "On pre Exceute......");
        }

        protected String doInBackground(String... arg) {
            Log.d("DoINBackGround", "On doInBackground...");
            String test;
            test = "Test";
            // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();
            String uid = user.get("uid");
            WebRequest webreq = new WebRequest();
            // Making a request to url and getting response
            String newurl = "http://bflix.ignitecloud.in/jsonApi/saveresume/" + uid + "/" + VideoID + "/" + mPos;
            String Res = webreq.makeWebServiceCall(newurl, WebRequest.GETRequest);
            //return "You are at PostExecute";
            return test;
        }

        protected void onPostExecute(String result) {
            //Log.d(""+result);
        }
    }

    class PlaylistAsync extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            Log.d("ExceutePlaylistAsync", "PlaylistAsyncPreExecute");
        }

        protected String doInBackground(String... arg) {
            Log.d("DoINBackGround", "On doInBackground...");
            String test;
            test = "Test";
            // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());
            HashMap<String, String> user = db.getUserDetails();
            String uid = user.get("uid");
            WebRequest webreq = new WebRequest();
            // Making a request to url and getting response
            String newurl = "http://bflix.ignitecloud.in/jsonApi/savetoplaylist/" + uid + "/" + VideoID + "/" + PlaylistID;
            String Res = webreq.makeWebServiceCall(newurl, WebRequest.GETRequest);
            //return "You are at PostExecute";
            if (Res != null) {
                return Res;
            } else {
                return "null";
            }
        }

        protected void onPostExecute(String result) {
            //Log.d(""+result);
            if (result == "ok") {
                Toast.makeText(PlayAudio.this, "Video added to " + Playlist, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(PlayAudio.this, "Video added in " + Playlist, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    class NewPlaylistAsync extends AsyncTask<String, String, String> {
        protected void onPreExecute() {
            Log.d("ExceutePlaylistAsync", "PlaylistAsyncPreExecute");
        }

        protected String doInBackground(String... arg) {
            Log.d("DoINBackGround", "On doInBackground...NewPlayListAsync");
            String test;
            test = "Test";
            // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();
            String uid = user.get("uid");
            WebRequest webreq = new WebRequest();
            // Making a request to url and getting response
            String newurl = "http://bflix.ignitecloud.in/jsonApi/newsavetoplaylist/" + uid + "/" + VideoID + "/" + Playlist;
            String Res = webreq.makeWebServiceCall(newurl, WebRequest.GETRequest);
            //return "You are at PostExecute";
            if (Res != null) {
                return Res;
            } else {
                return "null";
            }
        }

        protected void onPostExecute(String result) {
            //Log.d(""+result);
            if (result == "ok") {
                Toast.makeText(PlayAudio.this, "Video added to " + Playlist, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(PlayAudio.this, "Video added in " + Playlist, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    class GetPlaylistAsync extends AsyncTask<String, String, String> {
        String Res;
        String Res2;

        protected void onPreExecute() {
            Log.d("ExceutePlaylistAsync", "GetPlaylistAsyncPreExecute");
        }

        protected String doInBackground(String... arg) {
            Log.d("DoINBackGround", "On doInBackground...NewPlayListAsync");
            String test;
            test = "Test";
            // SqLite database handler
            db = new SQLiteHandler(getApplicationContext());
            // Fetching user details from sqlite
            HashMap<String, String> user = db.getUserDetails();
            String uid = user.get("uid");
            WebRequest webreq = new WebRequest();
            // Making a request to url and getting response
            String newurl = "http://bflix.ignitecloud.in/jsonApi/getplaylist3/" + uid + "/s";
            String newurl2 = "http://bflix.ignitecloud.in/jsonApi/getplaylist3/" + uid + "/i";
            Res = webreq.makeWebServiceCall(newurl, WebRequest.GETRequest);
            Res2 = webreq.makeWebServiceCall(newurl2, WebRequest.GETRequest);
            //return "You are at PostExecute";
            if (Res != null) {
                return Res;
            } else {
                return "null";
            }
        }

        protected void onPostExecute(String result) {
            if (Res != null) {
                List<String> listItems = new ArrayList<String>();
                List<String> listItems2 = new ArrayList<String>();
                String[] pnseparated = Res.split(",");
                String[] pnseparated2 = Res2.split(",");
                for (int i = 0; i < pnseparated.length; i++) {
                    listItems.add(pnseparated[i]);
                    listItems2.add(pnseparated2[i]);
                }

                final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
                final CharSequence[] items2 = listItems2.toArray(new CharSequence[listItems2.size()]);
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayAudio.this);//ERROR ShowDialog cannot be resolved to a type
                builder.setTitle("My Playlists");
                builder.setSingleChoiceItems(items, -1,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Playlist = items[item].toString();
                                String tt = items2[item].toString();
                                PlaylistID = Integer.parseInt(tt);
                            }
                        });

                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new PlaylistAsync().execute();
                    }
                });

                builder.setNeutralButton("Create New Playlist", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        CreateNewPlaylist();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            if (result == "ok") {
                Toast.makeText(PlayAudio.this, "Playlist Video added to " + Playlist, Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(PlayAudio.this, "Playlist Video added in " + Playlist, Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

}

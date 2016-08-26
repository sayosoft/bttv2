package bt.bt.bttv;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class PlayVideo extends Activity {

    //private String pathToFileOrUrl= "rtmp://204.107.26.252:8086/live/796.high.stream";
    private VideoView mVideoView;
    private String pathToFileOrUrl = "http://www.sample-videos.com/video/mp4/360/big_buck_bunny_360p_1mb.mp4";

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            pathToFileOrUrl = extras.getString("vurl");

            Log.i("INTENTVALUE:", pathToFileOrUrl);
            //The key argument here must match that used in the other activity
        } else {
            Log.i("INTENTVALUE:", pathToFileOrUrl);
        }

        if (!LibsChecker.checkVitamioLibs(this))
            return;

        setContentView(R.layout.activity_play_video);
        mVideoView = (VideoView) findViewById(R.id.surface_view);
        final TextView textView8 = (TextView) findViewById(R.id.textView8);

        if (pathToFileOrUrl == "") {
            Toast.makeText(this, "Please set the video path for your media file", Toast.LENGTH_LONG).show();
            return;
        } else {

            /*
             * Alternatively,for streaming media you can use
             * mVideoView.setVideoURI(Uri.parse(URLstring));
             */
            mVideoView.setVideoPath(pathToFileOrUrl);
            mVideoView.setMediaController(new MediaController(this));
            mVideoView.requestFocus();

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                mVideoView.setVideoLayout(io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_STRETCH, 0);

            } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mVideoView.setVideoLayout(io.vov.vitamio.widget.VideoView.VIDEO_LAYOUT_ORIGIN, 0);
            }


            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    // optional need Vitamio 4.0
                    mediaPlayer.setPlaybackSpeed(1.0f);
                }
            });

            mVideoView.setOnBufferingUpdateListener(new io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener() {

                public void onBufferingUpdate(io.vov.vitamio.MediaPlayer mp, int i) {
                    Log.v("BUFFER", "Buffer percentage done: " + i);
                    textView8.setText("Loading..." + i + "%");
                    if (i > 96 || i == 95 || i == 96 || i == 97 || i == 98 || i > 98) {
                        textView8.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Intent intent = new Intent(this, MovieActivity.class);
        startActivity(intent);

    }

    public void startPlay(View view) {
        if (!TextUtils.isEmpty(pathToFileOrUrl)) {
            mVideoView.setVideoPath(pathToFileOrUrl);
        }
    }

    public void openVideo(View View) {
        mVideoView.setVideoPath(pathToFileOrUrl);
    }

}
package bt.bt.bttv.helper;

import android.widget.ProgressBar;

import com.squareup.picasso.Callback;

public class ImageLoadedCallback implements Callback {
    public ProgressBar progressBar;

    public  ImageLoadedCallback(ProgressBar progBar){
        progressBar = progBar;
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onError() {

    }
}

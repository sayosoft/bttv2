package bt.bt.bttv.helper;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by spoton on 6/9/16.
 */
public class APiAsync extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    Fragment mFragmentContext;
    Context mContext;
    private String url;
    private String progressDialogMsg;
    private ApiInt mListener;

    public APiAsync(Fragment fragmentContext, Context activityContext, String url, String progressDialogMsg) {
        mFragmentContext = fragmentContext;
        mContext = activityContext;
        this.url = url;
        this.progressDialogMsg = progressDialogMsg;

        mListener = (ApiInt) fragmentContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        service = new HTTPURLConnection();
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(progressDialogMsg);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... result) {
        return service.ServerData(url);
    }

    @Override
    protected void onPostExecute(String result) {
        if (pDialog.isShowing())
            pDialog.dismiss();

        mListener.getResponse(result);
    }
}


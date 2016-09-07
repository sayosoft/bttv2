package bt.bt.bttv.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

/**
 * Created by spoton on 6/9/16.
 */
public class APiAsync extends AsyncTask<String, Void, String> {

    Fragment mFragmentContext;
    Context mContext;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private String url;
    private String progressDialogMsg;
    private ApiInt mListener;
    private int requestType;

    public APiAsync(Fragment fragmentContext, Context activityContext, String url, String progressDialogMsg, int requestType) {
        mFragmentContext = fragmentContext;
        mContext = activityContext;
        this.url = url;
        this.progressDialogMsg = progressDialogMsg;
        this.requestType = requestType;

        if (fragmentContext != null)
            mListener = (ApiInt) fragmentContext;
        else
            mListener = (ApiInt) activityContext;
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

        mListener.onSuccess(result, requestType);
    }
}


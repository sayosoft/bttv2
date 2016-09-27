package bt.bt.bttv.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import org.json.JSONObject;

/**
 * Created by spoton on 6/9/16.
 */
public class APiAsync extends AsyncTask<String, Void, String> {

    public static final int CHANGE_PASSWORD = 4;
    public static final int CREATE_PLAYLIST = 8;
    public static final int GET_PLAYLIST = 9;
    public static final int PLAYLIST_CONTENT = 10;

    Fragment mFragmentContext;
    Context mContext;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private String url;
    private String progressDialogMsg;
    private ApiInt mListener;
    private int requestType;
    private JSONObject jsonObject;

    public APiAsync(Fragment fragmentContext, Context activityContext, String url, String progressDialogMsg, int requestType, JSONObject jsonObject) {
        mFragmentContext = fragmentContext;
        mContext = activityContext;
        this.url = url;
        this.progressDialogMsg = progressDialogMsg;
        this.requestType = requestType;
        this.jsonObject = jsonObject;

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
        if (jsonObject == null)
            return service.ServerData(url);
        else
            return service.ServerData(url, jsonObject.toString());
    }

    @Override
    protected void onPostExecute(String result) {
        if (pDialog.isShowing())
            pDialog.dismiss();

        mListener.onSuccess(result, requestType);
    }
}


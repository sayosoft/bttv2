package bt.bt.bttv.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.adapter.AudioHomeAdapter;
import bt.bt.bttv.adapter.VideoHomeAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.AudiosModel;
import bt.bt.bttv.model.VideosModel;
import bt.bt.bttv.model.WatchLater;


public class LaterFragment extends Fragment {

    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    List<AudiosModel> audiosModelList = new ArrayList<>();
    List<VideosModel> videosModelList = new ArrayList<>();
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        llMain = (LinearLayout) view.findViewById(R.id.llMain);

        if (cd.isConnectingToInternet()) {
            new GetWatchLaterAudios().execute();
        } else {
            Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private void getWatchLaterLists() {

        audiosModelList = WatchLater.audiosModelList;
        videosModelList = WatchLater.videoModelList;

        List<AudiosModel> audiosModelListFiltered = new ArrayList<>();
        List<VideosModel> videoModelListFiltered = new ArrayList<>();

        for (int i = 0; i < audiosModelList.size(); i++) {
            for (int j = 0; j < WatchLater.audioIdList.size(); j++) {
                if (audiosModelList.get(i).getAudio_id().equals(WatchLater.audioIdList.get(j))) {
                    audiosModelListFiltered.add(audiosModelList.get(i));
                }
            }
        }

        for (int i = 0; i < videosModelList.size(); i++) {
            for (int j = 0; j < WatchLater.videoIdList.size(); j++) {
                if (videosModelList.get(i).getVideo_id().equals(WatchLater.videoIdList.get(j))) {
                    videoModelListFiltered.add(videosModelList.get(i));
                }
            }
        }
        setWatchLater(audiosModelListFiltered, videoModelListFiltered);
    }

    private void setWatchLater(List<AudiosModel> audiosModelListFiltered, List<VideosModel> videoModelListFiltered) {

        if (videoModelListFiltered.size() > 0) {
            RecyclerView recyclerView = new RecyclerView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            params.setMargins(5, 0, 5, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            VideoHomeAdapter videoHomeAdapter = new VideoHomeAdapter(getActivity(), videoModelListFiltered);
            recyclerView.setAdapter(videoHomeAdapter);

            TextView tvTitle = new TextView(getActivity());
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(params1);
            params1.setMargins(10, 10, 0, 0);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText("Videos");

            llMain.addView(tvTitle);
            llMain.addView(recyclerView);
        }

        if (audiosModelListFiltered.size() > 0) {
            RecyclerView recyclerView = new RecyclerView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            params.setMargins(5, 0, 5, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            AudioHomeAdapter audioHomeAdapter = new AudioHomeAdapter(getActivity(), audiosModelListFiltered);
            recyclerView.setAdapter(audioHomeAdapter);

            TextView tvTitle = new TextView(getActivity());
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(params1);
            params1.setMargins(10, 10, 0, 0);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText("Audios");

            llMain.addView(tvTitle);
            llMain.addView(recyclerView);
        }
    }

    private class GetWatchLaterAudios extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();


            jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", "14");
                jsonObject.put("media_type", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_watch_later), jsonObject.toString());
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.e("audio response :", result.toString());

            try {
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equals("success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    if (jsonArray.length() > 0) {
                        WatchLater.audioIdList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            WatchLater.audioIdList.add(jsonArray.getJSONObject(i).getString("audio_id"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            new GetWatchLaterVideos().execute();
        }
    }

    private class GetWatchLaterVideos extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

            jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", "14");
                jsonObject.put("media_type", "2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_watch_later), jsonObject.toString());
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();

            Log.e("video response :", result.toString());

            try {
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equals("success")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    if (jsonArray.length() > 0) {
                        WatchLater.videoIdList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            WatchLater.videoIdList.add(jsonArray.getJSONObject(i).getString("video_id"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getWatchLaterLists();
        }
    }

}

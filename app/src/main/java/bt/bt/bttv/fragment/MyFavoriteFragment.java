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

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.adapter.AudioHomeAdapter;
import bt.bt.bttv.adapter.VideoHomeAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.AudiosModel;
import bt.bt.bttv.model.LoginResponseModel;
import bt.bt.bttv.model.MyFavoriteModel;
import bt.bt.bttv.model.VideosModel;
import bt.bt.bttv.model.WatchLaterModel;


public class MyFavoriteFragment extends Fragment {

    public SharedPreferences settings;
    private List<AudiosModel> audiosModelList = new ArrayList<>();
    private List<VideosModel> videosModelList = new ArrayList<>();
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();

        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        llMain = (LinearLayout) view.findViewById(R.id.llMain);

        if (cd.isConnectingToInternet()) {
                new GetMyFavoriteAudios().execute();
        } else {
            Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void getFavoriteLists() {

        audiosModelList = WatchLaterModel.audiosModelList;
        videosModelList = WatchLaterModel.videoModelList;

        List<AudiosModel> audiosModelListFiltered = new ArrayList<>();
        List<VideosModel> videoModelListFiltered = new ArrayList<>();

        for (int i = 0; i < audiosModelList.size(); i++) {
            for (int j = 0; j < MyFavoriteModel.audioIdList.size(); j++) {
                if (audiosModelList.get(i).getAudio_id().equals(MyFavoriteModel.audioIdList.get(j))) {
                    audiosModelListFiltered.add(audiosModelList.get(i));
                }
            }
        }

        for (int i = 0; i < videosModelList.size(); i++) {
            for (int j = 0; j < MyFavoriteModel.videoIdList.size(); j++) {
                if (videosModelList.get(i).getVideo_id().equals(MyFavoriteModel.videoIdList.get(j))) {
                    videoModelListFiltered.add(videosModelList.get(i));
                }
            }
        }
        setFavorite(audiosModelListFiltered, videoModelListFiltered);
    }

    private void setFavorite(List<AudiosModel> audiosModelListFiltered, List<VideosModel> videoModelListFiltered) {

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
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
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
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText("Audios");

            llMain.addView(tvTitle);
            llMain.addView(recyclerView);
        }
    }

    private class GetMyFavoriteAudios extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.msg_progress_dialog));
            pDialog.setCancelable(false);
            pDialog.show();


            jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", loginResponseModel.getUser().getUser_id());
                jsonObject.put("media_type", "1");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_get_favorite), jsonObject.toString());
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
                        MyFavoriteModel.audioIdList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MyFavoriteModel.audioIdList.add(jsonArray.getJSONObject(i).getString("audio_id"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            if (MyFavoriteModel.videoIdList.size() == 0)
                new GetFavoriteVideos().execute();

        }
    }

    private class GetFavoriteVideos extends AsyncTask<String, Void, String> {

        JSONObject jsonObject;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.msg_progress_dialog));
            pDialog.setCancelable(false);
            pDialog.show();

            jsonObject = new JSONObject();
            try {
                jsonObject.put("user_id", loginResponseModel.getUser().getUser_id());
                jsonObject.put("media_type", "2");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_get_favorite), jsonObject.toString());
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
                        MyFavoriteModel.videoIdList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            MyFavoriteModel.videoIdList.add(jsonArray.getJSONObject(i).getString("video_id"));
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getFavoriteLists();
        }
    }

}

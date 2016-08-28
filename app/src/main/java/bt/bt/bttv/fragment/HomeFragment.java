package bt.bt.bttv.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.adapter.CategoryAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.HomeCategoryModel;
import bt.bt.bttv.model.MovieModel;

/**
 * Created by Sachin on 8/28/2016.
 */
public class HomeFragment extends Fragment {

    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;
    private List<HomeCategoryModel> homeCategoryModels;
    private List<MovieModel> movieModels;
    private JSONObject jsonObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        llMain = (LinearLayout) view.findViewById(R.id.llMain);

        if (cd.isConnectingToInternet()) {
            new GetHomeContentCategory().execute();
        } else {
            Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void inflateData() {

        HashMap<Integer, List<MovieModel>> stringListHashMap = new HashMap<>();

        for (int i = 0; i < homeCategoryModels.size(); i++) {
            List<MovieModel> movieModelList = new ArrayList<>();
            String[] homeVodIds = homeCategoryModels.get(i).getHomepage_vod_ids().split(",");
            for (int j = 0; j < movieModels.size(); j++) {
                for (int k = 0; k < homeVodIds.length; k++) {
                    if (homeVodIds[k].equals(movieModels.get(j).getVideo_id())) {
                        movieModelList.add(movieModels.get(j));
                    }
                }
            }
            RecyclerView recyclerView = new RecyclerView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            params.setMargins(5, 0, 5, 0);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);

            stringListHashMap.put(i, movieModelList);
            CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), stringListHashMap.get(i));
            recyclerView.setAdapter(categoryAdapter);

            TextView tvTitle = new TextView(getActivity());
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvTitle.setLayoutParams(params1);
            params1.setMargins(10, 10, 0, 0);
            tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvTitle.setText(homeCategoryModels.get(i).getHomepage_title());

            TextView tvSubTitle = new TextView(getActivity());
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tvSubTitle.setLayoutParams(params2);
            params2.setMargins(10, 5, 0, 0);
            tvSubTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tvSubTitle.setTextColor(getResources().getColor(R.color.colorWhite));
            tvSubTitle.setText(homeCategoryModels.get(i).getHomepage_subtitle());

            llMain.addView(tvTitle);
            llMain.addView(tvSubTitle);
            llMain.addView(recyclerView);
        }
    }

    private class GetHomeContentCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_get_home_categories));
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equals("success")) {
                    Gson gson = new Gson();
                    homeCategoryModels = gson.fromJson(jsonObject.getJSONArray("result").toString(), new TypeToken<List<HomeCategoryModel>>() {
                    }.getType());
                    if (cd.isConnectingToInternet()) {
                        new GetCategories().execute();
                    } else {
                        Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetCategories extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... res) {
            return service.ServerData(getResources().getString(R.string.url_get_video));
        }

        @Override
        protected void onPostExecute(String result1) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                if (result1 != null) {
                    jsonObject = new JSONObject(result1);
                    Gson gson = new Gson();
                    movieModels = gson.fromJson(jsonObject.getJSONArray("videos").toString(), new TypeToken<List<MovieModel>>() {
                    }.getType());
                    inflateData();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
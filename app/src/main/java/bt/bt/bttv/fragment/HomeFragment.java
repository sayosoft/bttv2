package bt.bt.bttv.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
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

import bt.bt.bttv.GenreActivity;
import bt.bt.bttv.R;
import bt.bt.bttv.SplashScreen;
import bt.bt.bttv.adapter.AudioGenreAdapter;
import bt.bt.bttv.adapter.AudioHomeAdapter;
import bt.bt.bttv.adapter.CategoryAdapter;
import bt.bt.bttv.adapter.VideoGenreAdapter;
import bt.bt.bttv.adapter.VideoHomeAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.AudiosModel;
import bt.bt.bttv.model.DrawerCategoriesModel;
import bt.bt.bttv.model.GenreModel;
import bt.bt.bttv.model.HomeCategoryModel;
import bt.bt.bttv.model.MovieModel;
import bt.bt.bttv.model.VideosModel;
import bt.bt.bttv.model.WatchLaterModel;
import io.vov.vitamio.utils.Log;

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
    private List<AudiosModel> audiosModelsList;
    private List<GenreModel> genreModelsList;
    private List<MovieModel> movieModels;
    private JSONObject jsonObject;
    private List<DrawerCategoriesModel> drawerCategoriesModelList;
    private List<VideosModel> videosModelsList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        videosModelsList = WatchLaterModel.videoModelList;
        audiosModelsList = WatchLaterModel.audiosModelList;

        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        View view = inflater.inflate(R.layout.home_fragment, container, false);
        llMain = (LinearLayout) view.findViewById(R.id.llMain);

        if (GlobleMethods.content_type.equals("Movies")) {
            if (cd.isConnectingToInternet()) {
                if (homeCategoryModels == null)
                    new GetHomeContentCategory().execute();
                else
                    inflateDataMovies();
            } else {
                Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
            }
        } else if (GlobleMethods.content_type.equals("Videos")) {
            if (cd.isConnectingToInternet()) {
                getVideoCategories(SplashScreen.drawerCategoriesModelsList);
            } else {
                Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
            }
        } else if (GlobleMethods.content_type.equals("Audio")) {
            if (cd.isConnectingToInternet()) {
                getAudioCategories(SplashScreen.drawerCategoriesModelsList);
            } else {
                Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
            }
        } else if (GlobleMethods.content_type.equals("Genre")) {
            if (cd.isConnectingToInternet()) {
                new getGenre().execute();
            } else {
                Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
            }
        } else if (GlobleMethods.content_type.equals("GenreSong")) {
            if (cd.isConnectingToInternet()) {
                if (GlobleMethods.genre_type.equals("VoD")) {
                    inflateDataOneGenreVoD();
                } else if (GlobleMethods.genre_type.equals("AoD")) {
                    inflateDataOneGenreAoD();
                }
            } else {
                Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
            }
        }
        return view;
    }

    private void getAudioCategories(List<DrawerCategoriesModel> drawerCategoriesModelsLists) {

        drawerCategoriesModelList = new ArrayList<>();
        if (drawerCategoriesModelsLists != null)
            for (int i = 0; i < drawerCategoriesModelsLists.size(); i++) {
                if (drawerCategoriesModelsLists.get(i).getCategory_type().equals("AoD")) {
                    drawerCategoriesModelList.add(drawerCategoriesModelsLists.get(i));
                }
            }
        inflateDataAudio();
    }

    private void getVideoCategories(List<DrawerCategoriesModel> drawerCategoriesModelsLists) {

        drawerCategoriesModelList = new ArrayList<>();
        if (drawerCategoriesModelsLists != null)
            for (int i = 0; i < drawerCategoriesModelsLists.size(); i++) {
                if (drawerCategoriesModelsLists.get(i).getCategory_type().equals("VoD")) {
                    drawerCategoriesModelList.add(drawerCategoriesModelsLists.get(i));
                }
            }
        inflateDataVideos();
    }

    private void inflateDataOneGenreAoD() {

        HashMap<Integer, List<AudiosModel>> stringListHashMap = new HashMap<>();
        audiosModelsList = WatchLaterModel.audiosModelList;
        List<AudiosModel> audiosModelsList1 = new ArrayList<>();
        for (int j = 0; j < audiosModelsList.size(); j++) {
            if (GlobleMethods.category_id.equals(audiosModelsList.get(j).getAudio_genre())) {
                audiosModelsList1.add(audiosModelsList.get(j));
            }
        }
        if (audiosModelsList1 != null)
            if (audiosModelsList1.size() > 0) {
                RecyclerView recyclerView = new RecyclerView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                recyclerView.setLayoutParams(params);
                params.setMargins(5, 0, 5, 0);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
                recyclerView.setLayoutManager(mLayoutManager);

                stringListHashMap.put(0, audiosModelsList1);
                AudioGenreAdapter audioHomeAdapter = new AudioGenreAdapter(getActivity(), stringListHashMap.get(0));
                recyclerView.setAdapter(audioHomeAdapter);
                TextView tvTitle = new TextView(getActivity());

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvTitle.setLayoutParams(params1);
                params1.setMargins(10, 10, 0, 0);
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTitle.setText(GlobleMethods.genre_name);
                llMain.addView(tvTitle);
                llMain.addView(recyclerView);
            }
    }

    private void inflateDataOneGenreVoD() {
        HashMap<Integer, List<VideosModel>> stringListHashMap = new HashMap<>();
        videosModelsList = WatchLaterModel.videoModelList;
        List<VideosModel> videosModelsList1 = new ArrayList<>();
        for (int j = 0; j < videosModelsList.size(); j++) {
            Log.e(GlobleMethods.category_id + "  " + videosModelsList.get(j).getVideo_genre());
            if (GlobleMethods.category_id.equals(videosModelsList.get(j).getVideo_genre())) {
                videosModelsList1.add(videosModelsList.get(j));
            }
        }
        if (videosModelsList1 != null)
            if (videosModelsList1.size() > 0) {
                RecyclerView recyclerView = new RecyclerView(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                recyclerView.setLayoutParams(params);
                params.setMargins(5, 0, 5, 0);
                RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(recyclerView.getContext(), 2);
                recyclerView.setLayoutManager(mLayoutManager);

                stringListHashMap.put(0, videosModelsList1);
                VideoGenreAdapter audioHomeAdapter = new VideoGenreAdapter(getActivity(), stringListHashMap.get(0));
                recyclerView.setAdapter(audioHomeAdapter);
                TextView tvTitle = new TextView(getActivity());

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                tvTitle.setLayoutParams(params1);
                params1.setMargins(10, 10, 0, 0);
                tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                tvTitle.setText(GlobleMethods.genre_name);

                llMain.addView(tvTitle);
                llMain.addView(recyclerView);
            }
    }


    private void inflateDataGenreVoD() {
        HashMap<Integer, List<VideosModel>> stringListHashMap = new HashMap<>();
        videosModelsList = WatchLaterModel.videoModelList;
        for (int i = 0; i < genreModelsList.size(); i++) {
            List<VideosModel> videosModelsList1 = new ArrayList<>();
            for (int j = 0; j < videosModelsList.size(); j++) {
                if (genreModelsList.get(i).getGenre_id().equals(videosModelsList.get(j).getVideo_genre()) && videosModelsList.get(j).getVideo_category().equals(GlobleMethods.category_id)) {
                    videosModelsList1.add(videosModelsList.get(j));
                }
            }
            if (videosModelsList1 != null)
                if (videosModelsList1.size() > 0) {
                    RecyclerView recyclerView = new RecyclerView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    recyclerView.setLayoutParams(params);
                    params.setMargins(5, 0, 5, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);

                    stringListHashMap.put(i, videosModelsList1);
                    VideoHomeAdapter audioHomeAdapter = new VideoHomeAdapter(getActivity(), stringListHashMap.get(i));
                    recyclerView.setAdapter(audioHomeAdapter);
                    final int finalI = i;
                    TextView tvTitle = new TextView(getActivity());
                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobleMethods.content_type = "GenreSong";
                            GlobleMethods.genre_name = genreModelsList.get(finalI).getGenre_name();
                            GlobleMethods.genre_type = "VoD";
                            GlobleMethods.category_id = genreModelsList.get(finalI).getGenre_id();
                            GlobleMethods.toolbar_title = genreModelsList.get(finalI).getGenre_name();
                            Intent genre = new Intent(getActivity(), GenreActivity.class);
                            startActivity(genre);
                        }
                    });
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvTitle.setLayoutParams(params1);
                    params1.setMargins(10, 10, 0, 0);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                    tvTitle.setText(genreModelsList.get(i).getGenre_name());

                    llMain.addView(tvTitle);
                    llMain.addView(recyclerView);
                }
        }
    }

    private void inflateDataGenreAoD() {

        HashMap<Integer, List<AudiosModel>> stringListHashMap = new HashMap<>();
        audiosModelsList = WatchLaterModel.audiosModelList;
        for (int i = 0; i < genreModelsList.size(); i++) {
            List<AudiosModel> audiosModelsList1 = new ArrayList<>();
            for (int j = 0; j < audiosModelsList.size(); j++) {
                if (genreModelsList.get(i).getGenre_id().equals(audiosModelsList.get(j).getAudio_genre()) && audiosModelsList.get(j).getAudio_category().equals(GlobleMethods.category_id)) {
                    audiosModelsList1.add(audiosModelsList.get(j));
                }
            }
            if (audiosModelsList1 != null)
                if (audiosModelsList1.size() > 0) {
                    RecyclerView recyclerView = new RecyclerView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    recyclerView.setLayoutParams(params);
                    params.setMargins(5, 0, 5, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);

                    stringListHashMap.put(i, audiosModelsList1);
                    AudioHomeAdapter audioHomeAdapter = new AudioHomeAdapter(getActivity(), stringListHashMap.get(i));
                    recyclerView.setAdapter(audioHomeAdapter);
                    final int finalI = i;
                    TextView tvTitle = new TextView(getActivity());
                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobleMethods.content_type = "GenreSong";
                            GlobleMethods.genre_type = "AoD";
                            GlobleMethods.genre_name = genreModelsList.get(finalI).getGenre_name();
                            GlobleMethods.category_id = genreModelsList.get(finalI).getGenre_id();
                            GlobleMethods.toolbar_title = genreModelsList.get(finalI).getGenre_name();
                            Intent genre = new Intent(getActivity(), GenreActivity.class);
                            startActivity(genre);
                        }
                    });
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvTitle.setLayoutParams(params1);
                    params1.setMargins(10, 10, 0, 0);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                    tvTitle.setText(genreModelsList.get(i).getGenre_name());

                    llMain.addView(tvTitle);
                    llMain.addView(recyclerView);
                }
        }
    }

    private void inflateDataMovies() {

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
            final int finalI = i;
           /* tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    GlobleMethods.content_type = "Genre";
                    GlobleMethods.genre_type = "VoD";
                    GlobleMethods.category_id = homeCategoryModels.get(finalI).getHomepage_id();
                    GlobleMethods.toolbar_title = homeCategoryModels.get(finalI).getHomepage_title();
                    Intent genre = new Intent(getActivity(), GenreActivity.class);
                    startActivity(genre);
                }
            });*/
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

    private void inflateDataVideos() {

        HashMap<Integer, List<VideosModel>> stringListHashMap = new HashMap<>();

        for (int i = 0; i < drawerCategoriesModelList.size(); i++) {
            List<VideosModel> videosModelsList1 = new ArrayList<>();
            for (int j = 0; j < videosModelsList.size(); j++) {
                System.out.print("ids" + drawerCategoriesModelList.get(i).getCategory_id() + "  " + videosModelsList.get(j).getVideo_category());
                if (drawerCategoriesModelList.get(i).getCategory_id().equals(videosModelsList.get(j).getVideo_category())) {
                    videosModelsList1.add(videosModelsList.get(j));
                }
            }
            if (videosModelsList1 != null)
                if (videosModelsList1.size() > 0) {
                    RecyclerView recyclerView = new RecyclerView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    recyclerView.setLayoutParams(params);
                    params.setMargins(5, 0, 5, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);

                    stringListHashMap.put(i, videosModelsList1);
                    VideoHomeAdapter audioHomeAdapter = new VideoHomeAdapter(getActivity(), stringListHashMap.get(i));
                    recyclerView.setAdapter(audioHomeAdapter);

                    final int finalI = i;
                    TextView tvTitle = new TextView(getActivity());
                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobleMethods.content_type = "Genre";
                            GlobleMethods.genre_type = "VoD";
                            GlobleMethods.category_id = drawerCategoriesModelList.get(finalI).getCategory_id();
                            GlobleMethods.toolbar_title = drawerCategoriesModelList.get(finalI).getCategory_title();
                            Intent genre = new Intent(getActivity(), GenreActivity.class);
                            startActivity(genre);
                        }
                    });
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvTitle.setLayoutParams(params1);
                    params1.setMargins(10, 10, 0, 0);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                    tvTitle.setText(drawerCategoriesModelList.get(i).getCategory_title());

                    llMain.addView(tvTitle);
                    llMain.addView(recyclerView);
                }
        }
    }

    private void inflateDataAudio() {

        HashMap<Integer, List<AudiosModel>> stringListHashMap = new HashMap<>();

        for (int i = 0; i < drawerCategoriesModelList.size(); i++) {
            List<AudiosModel> audiosModelsList1 = new ArrayList<>();
            for (int j = 0; j < audiosModelsList.size(); j++) {
                if (drawerCategoriesModelList.get(i).getCategory_id().equals(audiosModelsList.get(j).getAudio_category())) {
                    audiosModelsList1.add(audiosModelsList.get(j));
                }
            }
            if (audiosModelsList1 != null)
                if (audiosModelsList1.size() > 0) {
                    RecyclerView recyclerView = new RecyclerView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    recyclerView.setLayoutParams(params);
                    params.setMargins(5, 0, 5, 0);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                    recyclerView.setLayoutManager(mLayoutManager);

                    stringListHashMap.put(i, audiosModelsList1);
                    AudioHomeAdapter audioHomeAdapter = new AudioHomeAdapter(getActivity(), stringListHashMap.get(i));
                    recyclerView.setAdapter(audioHomeAdapter);
                    final int finalI = i;
                    TextView tvTitle = new TextView(getActivity());
                    tvTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            GlobleMethods.content_type = "Genre";
                            GlobleMethods.genre_type = "AoD";
                            GlobleMethods.category_id = drawerCategoriesModelList.get(finalI).getCategory_id();
                            GlobleMethods.toolbar_title = drawerCategoriesModelList.get(finalI).getCategory_title();
                            Intent genre = new Intent(getActivity(), GenreActivity.class);
                            startActivity(genre);
                        }
                    });
                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    tvTitle.setLayoutParams(params1);
                    params1.setMargins(10, 10, 0, 0);
                    tvTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
                    tvTitle.setTextColor(getResources().getColor(R.color.colorWhite));
                    tvTitle.setText(drawerCategoriesModelList.get(i).getCategory_title());

                    llMain.addView(tvTitle);
                    llMain.addView(recyclerView);
                }
        }
    }

    private class getGenre extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.msg_progress_dialog));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... result) {
            return service.ServerData(getResources().getString(R.string.url_genres) + GlobleMethods.genre_type);
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            Log.d("Genre Data", result);
            if (genreModelsList != null) {
                genreModelsList.clear();
            }
            try {
                JSONObject jb = new JSONObject(result);
                Gson gson = new Gson();
                genreModelsList = gson.fromJson(jb.getJSONArray("genres").toString(), new TypeToken<List<GenreModel>>() {
                }.getType());
                if (GlobleMethods.genre_type.equals("VoD")) {
                    inflateDataGenreVoD();
                } else if (GlobleMethods.genre_type.equals("AoD")) {
                    inflateDataGenreAoD();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private class GetHomeContentCategory extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            service = new HTTPURLConnection();
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage(getString(R.string.msg_progress_dialog));
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
            pDialog.setMessage(getString(R.string.msg_progress_dialog));
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... res) {
            return service.ServerData(getResources().getString(R.string.url_get_videos));
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
                    inflateDataMovies();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

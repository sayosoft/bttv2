package bt.bt.bttv.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.adapter.MyPlaylistsAdapter;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.model.MyPlayListModel;


public class MyPlaylistsFragment extends Fragment implements View.OnClickListener {

    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    RecyclerView rvMyPlayList;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    EditText etPlayListName;
    Button btnAddPlayList;
    private LinearLayout llMain;
    private ProgressDialog pDialog;
    private HTTPURLConnection service;
    private ConnectionDetector cd;
    private List<MyPlayListModel> myPlayListModelList;
    private JSONObject jsonObject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.myplaylist_fragment, container, false);
        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        rvMyPlayList = (RecyclerView) view.findViewById(R.id.rvMyPlayList);
        rvMyPlayList.setHasFixedSize(true);

        etPlayListName = (EditText) view.findViewById(R.id.etPlayListName);
        btnAddPlayList = (Button) view.findViewById(R.id.btnAddPlayList);
        btnAddPlayList.setOnClickListener(this);
        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvMyPlayList.setLayoutManager(mLayoutManager);

        if (cd.isConnectingToInternet()) {
            if (myPlayListModelList == null) {
                new GetPlayLists().execute();
            } else {
                mAdapter = new MyPlaylistsAdapter(getActivity(), myPlayListModelList);
                rvMyPlayList.setAdapter(mAdapter);
            }
        } else {
            Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPlayList:
                if (etPlayListName.getText().length() > 0) {

                } else
                    Toast.makeText(getActivity(), "Please enter playlist name", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private class GetPlayLists extends AsyncTask<String, Void, String> {

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
            return service.ServerData(getResources().getString(R.string.url_get_movie_playlists));
        }

        @Override
        protected void onPostExecute(String result) {
            if (pDialog.isShowing())
                pDialog.dismiss();
            try {
                jsonObject = new JSONObject(result);
                if (jsonObject.getJSONArray("array").length() > 0) {
                    Gson gson = new Gson();
                    myPlayListModelList = gson.fromJson(jsonObject.getJSONArray("array").toString(), new TypeToken<List<MyPlayListModel>>() {
                    }.getType());

                    mAdapter = new MyPlaylistsAdapter(getActivity(), myPlayListModelList);
                    rvMyPlayList.setAdapter(mAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}

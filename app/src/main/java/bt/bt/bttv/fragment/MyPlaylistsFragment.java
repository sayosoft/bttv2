package bt.bt.bttv.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import bt.bt.bttv.R;
import bt.bt.bttv.adapter.MyPlaylistsAdapter;
import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.GlobleMethods;
import bt.bt.bttv.model.LoginResponseModel;
import bt.bt.bttv.model.MyPlayListModel;

public class MyPlaylistsFragment extends Fragment implements View.OnClickListener, ApiInt {

    public SharedPreferences settings;
    RecyclerView rvMyPlayList;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    EditText etPlayListName;
    Button btnAddPlayList;
    private ConnectionDetector cd;
    private MyPlayListModel myPlayListModel;
    private TextView altText;
    private APiAsync aPiAsync;
    private Gson gson;
    private LoginResponseModel loginResponseModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myplaylist_fragment, container, false);

        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(GlobleMethods.PREFS_NAME, Context.MODE_PRIVATE);

        gson = new Gson();
        loginResponseModel = gson.fromJson(settings.getString(GlobleMethods.logFlag, ""), LoginResponseModel.class);

        rvMyPlayList = (RecyclerView) view.findViewById(R.id.rvMyPlayList);
        rvMyPlayList.setHasFixedSize(true);
        altText = (TextView) view.findViewById(R.id.altText);

        etPlayListName = (EditText) view.findViewById(R.id.etPlayListName);
        btnAddPlayList = (Button) view.findViewById(R.id.btnAddPlayList);
        btnAddPlayList.setOnClickListener(this);
        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvMyPlayList.setLayoutManager(mLayoutManager);

        apiGetPlayLists();
        return view;
    }

    private void apiGetPlayLists() {

        if (cd.isConnectingToInternet()) {
            if (myPlayListModel == null) {
                aPiAsync = new APiAsync(MyPlaylistsFragment.this, getActivity(), getResources().getString(R.string.url_get_movie_playlists) + loginResponseModel.getUser().getUser_id(), getActivity().getString(R.string.msg_progress_dialog), APiAsync.GET_PLAYLIST, null);
                aPiAsync.execute();
            } else {
                mAdapter = new MyPlaylistsAdapter(getActivity(), myPlayListModel.getArray());
                rvMyPlayList.setAdapter(mAdapter);
            }
        } else {
            Toast.makeText(getActivity(), "Internet not available..!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddPlayList:
                if (etPlayListName.getText().length() > 0) {
                    if (cd.isConnectingToInternet()) {
                        aPiAsync = new APiAsync(MyPlaylistsFragment.this, getActivity(), getResources().getString(R.string.url_create_playlist) + loginResponseModel.getUser().getUser_id() + "/" + etPlayListName.getText().toString().replace(" ", "%20"), getActivity().getString(R.string.msg_progress_dialog), APiAsync.CREATE_PLAYLIST, null);
                        aPiAsync.execute();
                    } else {
                        Toast.makeText(getActivity(), R.string.msg_no_connection, Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(getActivity(), "Please enter playlist name", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onSuccess(String response, int requestType) {
        switch (requestType) {
            case APiAsync.GET_PLAYLIST:
                Gson gson = new Gson();
                myPlayListModel = gson.fromJson(response.toString(), MyPlayListModel.class);

                mAdapter = new MyPlaylistsAdapter(getActivity(), myPlayListModel.getArray());
                rvMyPlayList.setAdapter(mAdapter);
                if (mAdapter.getItemCount() == 0) {
                    altText.setVisibility(View.VISIBLE);
                } else {
                    altText.setVisibility(View.GONE);
                }
                break;

            case APiAsync.CREATE_PLAYLIST:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("success").equals("success")) {
                        Toast.makeText(getActivity(), "Playlist Created Successfully..!", Toast.LENGTH_SHORT).show();
                        etPlayListName.setText("");
                        myPlayListModel = null;
                        apiGetPlayLists();
                    } else {
                        Toast.makeText(getActivity(), jsonObject.getString("success"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}

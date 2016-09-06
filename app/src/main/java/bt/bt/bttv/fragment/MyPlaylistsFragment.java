package bt.bt.bttv.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

import bt.bt.bttv.R;
import bt.bt.bttv.adapter.MyPlaylistsAdapter;
import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.HTTPURLConnection;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.MyPlayListModel;

public class MyPlaylistsFragment extends Fragment implements View.OnClickListener, ApiInt {

    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    RecyclerView rvMyPlayList;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerView.Adapter mAdapter;
    EditText etPlayListName;
    Button btnAddPlayList;
    private ConnectionDetector cd;
    private MyPlayListModel myPlayListModel;
    private SQLiteHandler db;
    private TextView altText;
    private APiAsync aPiAsync;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.myplaylist_fragment, container, false);
        cd = new ConnectionDetector(getActivity());
        settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        db = new SQLiteHandler(getActivity());

        rvMyPlayList = (RecyclerView) view.findViewById(R.id.rvMyPlayList);
        rvMyPlayList.setHasFixedSize(true);
        altText = (TextView) view.findViewById(R.id.altText);

        etPlayListName = (EditText) view.findViewById(R.id.etPlayListName);
        btnAddPlayList = (Button) view.findViewById(R.id.btnAddPlayList);
        btnAddPlayList.setOnClickListener(this);
        // The number of Columns
        mLayoutManager = new GridLayoutManager(getActivity(), 1);
        rvMyPlayList.setLayoutManager(mLayoutManager);

        if (cd.isConnectingToInternet()) {
            if (myPlayListModel == null) {
                aPiAsync = new APiAsync(MyPlaylistsFragment.this, getActivity(), getResources().getString(R.string.url_get_movie_playlists) + db.getUserDetails().get("uid"), getActivity().getString(R.string.msg_progress_dialog));
                aPiAsync.execute();
            } else {
                mAdapter = new MyPlaylistsAdapter(getActivity(), myPlayListModel.getArray());
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

    @Override
    public void onSuccess(String response) {
        Gson gson = new Gson();
        myPlayListModel = gson.fromJson(response.toString(), MyPlayListModel.class);

        mAdapter = new MyPlaylistsAdapter(getActivity(), myPlayListModel.getArray());
        rvMyPlayList.setAdapter(mAdapter);
        if (mAdapter.getItemCount() == 0) {
            altText.setVisibility(View.VISIBLE);
        } else {
            altText.setVisibility(View.GONE);
        }
    }

}

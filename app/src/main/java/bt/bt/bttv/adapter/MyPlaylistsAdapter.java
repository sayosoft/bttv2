package bt.bt.bttv.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.helper.APiAsync;
import bt.bt.bttv.helper.ApiInt;
import bt.bt.bttv.helper.ConnectionDetector;
import bt.bt.bttv.helper.ItemClickListener;
import bt.bt.bttv.helper.SQLiteHandler;
import bt.bt.bttv.model.MyPlayListModel;

public class MyPlaylistsAdapter extends RecyclerView.Adapter<MyPlaylistsAdapter.ViewHolder> implements ApiInt {
    public static final String PREFS_NAME = "MyPrefs";
    public SharedPreferences settings;
    Context context;
    List<MyPlayListModel.ArrayBean> myPlayListModelList;
    private ConnectionDetector cd;
    private SQLiteHandler db;
    private APiAsync aPiAsync;

    public MyPlaylistsAdapter(Context context, List<MyPlayListModel.ArrayBean> myPlayListModelList) {
        super();
        this.context = context;
        this.myPlayListModelList = myPlayListModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inflate_my_play_list, viewGroup, false);

        cd = new ConnectionDetector(context);
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        db = new SQLiteHandler(context);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvSpecies.setText(myPlayListModelList.get(i).getPlaylist_name());

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + myPlayListModelList.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
//                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    Toast.makeText(context, "#" + position + " - " + myPlayListModelList.get(position), Toast.LENGTH_SHORT).show();
                    apiGetPlaylistContent(myPlayListModelList.get(position).getPlaylist_id());
                }
            }
        });
    }

    private void apiGetPlaylistContent(String playlist_id) {
        if (cd.isConnectingToInternet()) {
            aPiAsync = new APiAsync(null, context, context.getResources().getString(R.string.url_get_playlist_content) + db.getUserDetails().get("uid") + "/" + playlist_id, context.getString(R.string.msg_progress_dialog), 100);
            aPiAsync.execute();
        } else {
            Toast.makeText(context, context.getString(R.string.msg_no_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return myPlayListModelList.size();
    }

    @Override
    public void onSuccess(String response, int requestcode) {
        switch (requestcode) {
            case 100:
                break;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvSpecies;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSpecies = (TextView) itemView.findViewById(R.id.tvPlayListName);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }

}

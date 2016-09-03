package bt.bt.bttv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.TvChannelInnerActivity;
import bt.bt.bttv.helper.ItemClickListener;
import bt.bt.bttv.model.LiveTvModel;

public class LiveTvAdapter extends RecyclerView.Adapter<LiveTvAdapter.ViewHolder> {

    Context context;
    List<LiveTvModel> liveTvModelList;
    ProgressBar progressBar = null;

    public LiveTvAdapter(Context context, List<LiveTvModel> liveTvModelList) {
        super();
        this.context = context;
        this.liveTvModelList = liveTvModelList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.live_tv_inflate, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Picasso.with(context)
                .load(context.getString(R.string.url_base_image) + liveTvModelList.get(i).getChannel_poster())
                .placeholder(R.drawable.movieinner)
                .into(viewHolder.ivLiveTv);

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {

                } else {
                    context.startActivity(new Intent(context, TvChannelInnerActivity.class).putExtra("vid", Integer.parseInt(liveTvModelList.get(position).getChannel_id())));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return liveTvModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView ivLiveTv;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            ivLiveTv = (ImageView) itemView.findViewById(R.id.ivLiveTv);
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

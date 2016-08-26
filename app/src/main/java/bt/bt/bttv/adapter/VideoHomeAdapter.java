package bt.bt.bttv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import bt.bt.bttv.MovieInnerActivity;
import bt.bt.bttv.R;
import bt.bt.bttv.helper.ItemClickListener;
import bt.bt.bttv.model.VideosModel;

public class VideoHomeAdapter extends RecyclerView.Adapter<VideoHomeAdapter.ViewHolder> {

    Context context;
    List<VideosModel> videosModelsList;

    public VideoHomeAdapter(Context context, List<VideosModel> videosModelsList) {
        super();
        this.context = context;
        this.videosModelsList = videosModelsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.category_inflate, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Picasso.with(context)
                .load("http://bflix.ignitecloud.in/uploads/images/" + videosModelsList.get(i).getVideo_poster())
                .into(viewHolder.ivMovie);

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + videosModelsList.get(position).getVideo_id() + " (Long click)", Toast.LENGTH_SHORT).show();
//                    context.startActivity(new Intent(context, Ca.class));
                } else {
                    Intent intent = new Intent(context, MovieInnerActivity.class);
                    intent.putExtra("vid", videosModelsList.get(position).getVideo_id());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return videosModelsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public ImageView ivMovie;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            ivMovie = (ImageView) itemView.findViewById(R.id.ivMovie);
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

package bt.bt.bttv.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import bt.bt.bttv.PlayAudio;
import bt.bt.bttv.R;
import bt.bt.bttv.helper.ItemClickListener;
import bt.bt.bttv.model.AudiosModel;

/**
 * Created by sayo1 on 9/3/2016.
 */
public class AudioGenreAdapter extends RecyclerView.Adapter<AudioGenreAdapter.ViewHolder> {

    Context context;
    List<AudiosModel> audiosModelList;
    ProgressBar progressBar = null;

    public AudioGenreAdapter(Context context, List<AudiosModel> audiosModelList) {
        super();
        this.context = context;
        this.audiosModelList = audiosModelList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.genre_inflate, viewGroup, false);

        if (v != null) {
            progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
        }
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Picasso.with(context)
                .load("http://bflix.ignitecloud.in/uploads/images/" + audiosModelList.get(i).getAudio_poster())
                .into(viewHolder.ivMovie);

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + audiosModelList.get(position).getAudio_id() + " (Long click)", Toast.LENGTH_SHORT).show();
//                    context.startActivity(new Intent(context, Ca.class));
                } else {

                    Intent intent = new Intent(context, PlayAudio.class);
                    intent.putExtra("vurl", audiosModelList.get(position).getAudio_url());
                    intent.putExtra("title", audiosModelList.get(position).getAudio_title());
                    intent.putExtra("vid", audiosModelList.get(position).getAudio_id());
                    intent.putExtra("vresume", "0");
                    intent.putExtra("duration", "05:00");
                    intent.putExtra("desc", audiosModelList.get(position).getAudio_description());
                    intent.putExtra("genre", audiosModelList.get(position).getAudio_genre());
                    intent.putExtra("cast", audiosModelList.get(position).getAudio_is_album());
                    intent.putExtra("director", audiosModelList.get(position).getAudio_artist());
                    context.startActivity(intent);

                    /*Intent i = new Intent(context, AudioActivity.class);
                    i.putExtra("song_url", audiosModelList.get(position).getAudio_url());
                    context.startActivity(i);*/
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return audiosModelList.size();
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

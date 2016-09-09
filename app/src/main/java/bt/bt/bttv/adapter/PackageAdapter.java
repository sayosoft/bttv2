package bt.bt.bttv.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import bt.bt.bttv.R;
import bt.bt.bttv.SubscriptionPaymentModeActivity;
import bt.bt.bttv.helper.ItemClickListener;
import bt.bt.bttv.model.PackagesModel;

public class PackageAdapter extends RecyclerView.Adapter<PackageAdapter.ViewHolder> {

    private List<PackagesModel> packagesModelList;
    private Context context;

    public PackageAdapter(Context context, List<PackagesModel> packagesModelList) {
        super();
        this.context = context;
        this.packagesModelList = packagesModelList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.inflate_packages, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.tvTitle.setText("" + packagesModelList.get(i).getPackage_title());
        viewHolder.tvPrice.setText(Html.fromHtml(packagesModelList.get(i).getPackage_price() + "<sup>Nu</sup>"));
        viewHolder.tvVOD.setText("VIDEOS ON DEMAND : " + packagesModelList.get(i).getPackage_vod());
        viewHolder.tvAOD.setText("AUDIOS ON DEMAND : " + packagesModelList.get(i).getPackage_aod());
        viewHolder.tvTVChannel.setText("TV CHANNELS : " + packagesModelList.get(i).getPackage_live_tv());
        viewHolder.tvRadioChannel.setText("RADIO CHANNELS : " + packagesModelList.get(i).getPackage_radio());
        viewHolder.tvPackageType.setText("PACKAGE TYPE : " + packagesModelList.get(i).getPackage_type());
        viewHolder.tvDuration.setText(packagesModelList.get(i).getPackage_duration() + " PACKAGE");

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, final int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + packagesModelList.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
//                    context.startActivity(new Intent(context, MainActivity.class));
                } else {

                    new AlertDialog.Builder(context)
                            .setTitle("Confirm")
                            .setMessage("are you sure, you want to change your existing subscription pack to " + packagesModelList.get(position).getPackage_title() + " for " + Html.fromHtml(packagesModelList.get(position).getPackage_price() + "<sup>Nu</sup>") + "?")
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton(R.string.yes_dialog, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    context.startActivity(new Intent(context, SubscriptionPaymentModeActivity.class).putExtra("packagesModel", packagesModelList.get(position)));
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return packagesModelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvTitle, tvPrice, tvVOD, tvAOD, tvTVChannel, tvRadioChannel, tvPackageType, tvDuration;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvVOD = (TextView) itemView.findViewById(R.id.tvVOD);
            tvAOD = (TextView) itemView.findViewById(R.id.tvAOD);
            tvTVChannel = (TextView) itemView.findViewById(R.id.tvTVChannel);
            tvRadioChannel = (TextView) itemView.findViewById(R.id.tvRadioChannel);
            tvPackageType = (TextView) itemView.findViewById(R.id.tvPackageType);
            tvDuration = (TextView) itemView.findViewById(R.id.tvDuration);
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

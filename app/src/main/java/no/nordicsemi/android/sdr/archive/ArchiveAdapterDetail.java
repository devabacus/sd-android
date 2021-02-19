package no.nordicsemi.android.sdr.archive;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.WeightPanel;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;

import static no.nordicsemi.android.sdr.WeightPanel.weightInTonn;

public class ArchiveAdapterDetail extends RecyclerView.Adapter<ArchiveAdapterDetail.ArchiveViewHolder>{

    private List<ArchiveData> archiveDataList;

    ArchiveAdapterDetail(List<ArchiveData> archiveDataList) {
        this.archiveDataList = archiveDataList;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_detail_item, parent, false);
        return new ArchiveViewHolder(v);
    }
    public void addItems(List<ArchiveData> archiveDataList){
        this.archiveDataList = archiveDataList;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {

        holder.tvWeight.setBackgroundColor(Color.TRANSPARENT);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        ArchiveData archiveData = archiveDataList.get(position);
        holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));
        // if weight with marker - stable max
        if (archiveData.getTypeOfWeight() == 1){
            holder.tvWeight.setBackgroundColor(Color.GREEN);
            // else if weight with marker - max value
        } else if (archiveData.getTypeOfWeight() == 2) {
            holder.tvWeight.setBackgroundColor(Color.parseColor("#cfc1d6"));
//            if ((archiveData.getSuspectState() & SuspectMasks.MAX_WEIGHT) == SuspectMasks.MAX_WEIGHT){
//
//            }
        } else if (archiveData.getTypeOfWeight() == 3) {
            holder.tvWeight.setBackgroundColor(Color.parseColor("#fcba03"));
        }
        holder.tvWeight.setText(WeightPanel.fmt(archiveData.getMainWeight())+(weightInTonn?"т":"кг"));

        if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
            if(archiveData.getTareValue() != 0) {
                StringBuilder sbTare = new StringBuilder();
                sbTare.append(archiveData.getTareValue());
                if(archiveData.getIsPercent()) sbTare.append("%");
                holder.tvTare.setText(sbTare);
                holder.tvTrueWeight.setText(WeightPanel.fmt(archiveData.getTrueWeight())+(weightInTonn?"т":"кг"));
            }
        }
        int stabTime = archiveData.getStabTime();
        if(stabTime != 0) {
            holder.tvStabTime.setText(stabTime + "c");
        }
        holder.itemView.setTag(archiveData);
    }

    @Override
    public int getItemCount() {
        return archiveDataList.size();
    }

    class ArchiveViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateTime;
        TextView tvWeight;
        TextView tvTrueWeight;
        TextView tvTare;
        TextView tvStabTime;

        ArchiveViewHolder(View itemView) {
            super(itemView);
            tvDateTime = (TextView)itemView.findViewById(R.id.tv_datetime1);
            tvWeight = (TextView)itemView.findViewById(R.id.tv_weight1);
            tvTare = (TextView)itemView.findViewById(R.id.tv_tare1);
            tvTrueWeight = (TextView)itemView.findViewById(R.id.tv_true_weight1);
            tvStabTime = (TextView)itemView.findViewById(R.id.tv_stab_time1);
        }
    }
}

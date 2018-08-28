package no.nordicsemi.android.sdr.archiveListOfItems;

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
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;

public class ArchiveAdapterDetail extends RecyclerView.Adapter<ArchiveAdapterDetail.ArchiveViewHolder>{

    private List<ArchiveData> archiveDataList;

    ArchiveAdapterDetail(List<ArchiveData> archiveDataList) {
        this.archiveDataList = archiveDataList;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_item, parent, false);
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
        format.format(new Date());
        ArchiveData archiveData = archiveDataList.get(position);
        holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));
        // if weight with marker - stable max
        if (archiveData.getTypeOfWeight() == 1){
            holder.tvWeight.setBackgroundColor(Color.GREEN);
            // else if weight with marker - max value
        } else if (archiveData.getTypeOfWeight() == 2) {
            holder.tvWeight.setBackgroundColor(Color.RED);
        }
        holder.tvWeight.setText(String.valueOf(archiveData.getMainWeight()));
        if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
            holder.tvTare.setText(String.valueOf(archiveData.getTareValue()));
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
        TextView tvTare;

        ArchiveViewHolder(View itemView) {
            super(itemView);
            tvDateTime = itemView.findViewById(R.id.tv_datetime);
            tvWeight = itemView.findViewById(R.id.tv_weight);
            tvTare = itemView.findViewById(R.id.tv_tare);
        }
    }
}

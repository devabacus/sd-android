package no.nordicsemi.android.blinky.archiveListOfItems;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.database_archive.ArchiveData;

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

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
        format.format(new Date());
        ArchiveData archiveData = archiveDataList.get(position);
        holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));
        holder.tvWeight.setText(String.valueOf(archiveData.getMainWeight()));
        holder.tvTare.setText(String.valueOf(archiveData.getTareValue()));
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

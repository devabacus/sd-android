package no.nordicsemi.android.sdr.archiveListOfItems;

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

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder>{

    private List<ArchiveData> archiveDataList;
    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;


    ArchiveAdapter(List<ArchiveData> archiveDataList, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener) {
        this.archiveDataList = archiveDataList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
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

        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
        format.format(new Date());
        ArchiveData archiveData = archiveDataList.get(position);
        holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));
        holder.tvWeight.setText(String.valueOf(archiveData.getMainWeight()));
        if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
            holder.tvTare.setText(String.valueOf(archiveData.getTareValue()));
        }

        holder.tvNumWeight.setText(String.valueOf(archiveData.getNumOfWeight()));
        holder.itemView.setTag(archiveData);
        holder.itemView.setOnClickListener(onClickListener);
        holder.itemView.setOnLongClickListener(onLongClickListener);
    }

    @Override
    public int getItemCount() {
        return archiveDataList.size();
    }

    class ArchiveViewHolder extends RecyclerView.ViewHolder {

        TextView tvDateTime;
        TextView tvWeight;
        TextView tvTare;
        TextView tvNumWeight;

        ArchiveViewHolder(View itemView) {
            super(itemView);
            tvDateTime = (TextView)itemView.findViewById(R.id.tv_datetime);
            tvWeight = (TextView)itemView.findViewById(R.id.tv_weight);
            tvTare = (TextView)itemView.findViewById(R.id.tv_tare);
            tvNumWeight = (TextView)itemView.findViewById(R.id.tv_num_weight);
        }
    }
}

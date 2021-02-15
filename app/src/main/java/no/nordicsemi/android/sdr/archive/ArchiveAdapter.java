package no.nordicsemi.android.sdr.archive;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    int _position;

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
        Log.d("test", "onBindViewHolder: ");

        ArchiveData archiveData = archiveDataList.get(position);
        if(archiveData.getTypeOfWeight() != 1) {
          holder.itemView.setVisibility(View.GONE);
          holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }

//        if(archiveData.getTypeOfWeight() == 2) {
//            holder.itemView.setVisibility(View.VISIBLE);
//            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        }

            holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));
            holder.tvWeight.setText(String.valueOf(archiveData.getMainWeight()));

            if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
                if(archiveData.getTareValue() != 0) {
                    holder.tvTare.setText(String.valueOf(archiveData.getTareValue()));
                    holder.tvTrueWeight.setText(String.valueOf(archiveData.getTrueWeight()));
                }
            }

            holder.tvNumWeight.setText(String.valueOf(archiveData.getNumOfWeight()));
            holder.itemView.setTag(archiveData);
            holder.itemView.setOnClickListener(onClickListener);
            holder.itemView.setOnLongClickListener(onLongClickListener);


//        if(archiveData.getTypeOfWeight() == 2){
//            Log.d("test", "onBindViewHolder: typeOfWeight = " + 2);
//            holder.tvDateTime.setVisibility(View.INVISIBLE);
//            holder.tvWeight.setVisibility(View.INVISIBLE);
//            holder.tvTrueWeight.setVisibility(View.INVISIBLE);
//            holder.tvTare.setVisibility(View.INVISIBLE);
//            holder.tvNumWeight.setVisibility(View.INVISIBLE);
//            holder.itemView.setVisibility(View.INVISIBLE);
//        } else if (archiveData.getTypeOfWeight() == 1){
//            holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));
//            holder.tvWeight.setText(String.valueOf(archiveData.getMainWeight()));
//            holder.tvTrueWeight.setText(String.valueOf(archiveData.getTrueWeight()));
//            if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
//                holder.tvTare.setText(String.valueOf(archiveData.getTareValue()));
//            }
//
//            holder.tvNumWeight.setText(String.valueOf(archiveData.getNumOfWeight()));
//            holder.itemView.setTag(archiveData);
//            holder.itemView.setOnClickListener(onClickListener);
//            holder.itemView.setOnLongClickListener(onLongClickListener);
//        }

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
        TextView tvNumWeight;

        ArchiveViewHolder(View itemView) {
            super(itemView);
            tvDateTime = (TextView)itemView.findViewById(R.id.tv_datetime);
            tvWeight = (TextView)itemView.findViewById(R.id.tv_weight);
            tvTrueWeight = (TextView)itemView.findViewById(R.id.tv_true_weight);
            tvTare = (TextView)itemView.findViewById(R.id.tv_tare);
            tvNumWeight = (TextView)itemView.findViewById(R.id.tv_num_weight);
        }
    }
}

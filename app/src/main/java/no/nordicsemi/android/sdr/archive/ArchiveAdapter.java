package no.nordicsemi.android.sdr.archive;

import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
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
import static no.nordicsemi.android.sdr.preferences.PrefArchive.KEY_MAX_WEIGHT_TOLERANCE;
import static no.nordicsemi.android.sdr.preferences.PrefHardBtns.KEY_VOLUME_BUTTON;

public class ArchiveAdapter extends RecyclerView.Adapter<ArchiveAdapter.ArchiveViewHolder> {

    private List<ArchiveData> archiveDataList;
    private final View.OnClickListener onClickListener;
    private final View.OnLongClickListener onLongClickListener;
    int _position;
    SharedPreferences _sp;
    boolean showMaxWeight;
    boolean markMaxWeight;
//    boolean weightInTonn;

    ArchiveAdapter(List<ArchiveData> archiveDataList, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener, boolean showMaxWeight, boolean markMaxWeight) {
        this.archiveDataList = archiveDataList;
        this.onClickListener = onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.showMaxWeight = showMaxWeight;
        this.markMaxWeight = markMaxWeight;
    }

    @NonNull
    @Override
    public ArchiveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.archive_item, parent, false);
        return new ArchiveViewHolder(v);
    }

    public void addItems(List<ArchiveData> archiveDataList) {
        this.archiveDataList = archiveDataList;
        notifyDataSetChanged();
    }

    ArchiveViewHolder changeHolderView(ArchiveViewHolder holder, String hexColor){
        holder.itemView.setBackgroundColor(Color.parseColor(hexColor));
        return holder;
    }

    ArchiveViewHolder holderGone(ArchiveViewHolder holder){
        holder.itemView.setVisibility(View.GONE);
        holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ArchiveViewHolder holder, int position) {

        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
        format.format(new Date());

        ArchiveData archiveData = archiveDataList.get(position);
//        float maxWeightTolerance = Float.parseFloat(sp.getString(KEY_MAX_WEIGHT_TOLERANCE, "0"));
//        boolean maxWeightDetect = archiveData.getMainWeight()

        boolean only_max_weight = (archiveData.getSuspectState() & SuspectMasks.ONLY_MAX_WEIGHT) == SuspectMasks.ONLY_MAX_WEIGHT;
        boolean unableMaxWeight = ((archiveData.getSuspectState() & SuspectMasks.MAX_WEIGHT) == SuspectMasks.MAX_WEIGHT) && markMaxWeight;
        boolean stableWhileUnload = (archiveData.getSuspectState() & SuspectMasks.STAB_WHILE_UNLOAD) == SuspectMasks.STAB_WHILE_UNLOAD;
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 20, 0, 20);

        holder.itemView.setLayoutParams(layoutParams);
        holder.itemView.setVisibility(View.VISIBLE);
        if (archiveData.getTypeOfWeight() != 1 && !only_max_weight) {
//            holder.itemView.setVisibility(View.GONE);
//            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            holderGone(holder);
        } else if (only_max_weight) {
            if(!showMaxWeight){
//                holder.itemView.setVisibility(View.GONE);
//                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                holderGone(holder);
            }
            changeHolderView(holder, "#cfc1d6");
        } else if (unableMaxWeight) {
            changeHolderView(holder, "#dcdedc");
        } else if (stableWhileUnload) {
            changeHolderView(holder, "#dcdedc");
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setMinimumHeight(30);
            holder.itemView.setLayoutParams(layoutParams);
        }

        holder.tvDateTime.setText(String.valueOf(format.format(archiveData.getTimePoint())));

//            holder.tvWeight.setText(WeightPanel.fmt(archiveData.getMainWeight())+(weightInTonn?"т":"кг"));
        holder.tvWeight.setText(WeightPanel.fmt(archiveData.getMainWeight()));


        if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
            if (archiveData.getTareValue() != 0) {
                StringBuilder sbTare = new StringBuilder();
                sbTare.append(archiveData.getTareValue());
                if (archiveData.getIsPercent()) sbTare.append("%");
                holder.tvTare.setText(sbTare);
//                    holder.tvTrueWeight.setText(WeightPanel.fmt(archiveData.getTrueWeight())+(weightInTonn?"т":"кг"));
                holder.tvTrueWeight.setText(WeightPanel.fmt(archiveData.getTrueWeight()));
            }
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
        TextView tvTrueWeight;
        TextView tvTare;
        TextView tvNumWeight;

        ArchiveViewHolder(View itemView) {
            super(itemView);
            tvDateTime = (TextView) itemView.findViewById(R.id.tv_datetime);
            tvWeight = (TextView) itemView.findViewById(R.id.tv_weight);
            tvTrueWeight = (TextView) itemView.findViewById(R.id.tv_true_weight);
            tvTare = (TextView) itemView.findViewById(R.id.tv_tare);
            tvNumWeight = (TextView) itemView.findViewById(R.id.tv_num_weight);
        }
    }
}

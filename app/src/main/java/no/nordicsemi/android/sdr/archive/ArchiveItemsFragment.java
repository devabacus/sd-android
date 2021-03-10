package no.nordicsemi.android.sdr.archive;

import android.app.AlertDialog;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.database_archive.ArchiveData;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;

import static no.nordicsemi.android.sdr.preferences.PrefArchive.KEY_MAX_WEIGHT_TOLERANCE;
import static no.nordicsemi.android.sdr.preferences.PrefArchive.KEY_ONLY_MAX_DETECT;

public class ArchiveItemsFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {

    private ArchiveViewModel archiveViewModel;
    private ArchiveAdapter archiveAdapter;
    RecyclerView recViewArchive;
    public static int numOfWeightPicked;
    public static final String TAG = "test";
    ArchiveData archiveDataMax;
    int currentAcrhiveData = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        archiveViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ArchiveViewModel.class);

        View v = inflater.inflate(R.layout.fragment_archive_items1, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean showMaxWeight = sharedPreferences.getBoolean(KEY_ONLY_MAX_DETECT, false);
        String toleranceMaxWeight = sharedPreferences.getString(KEY_MAX_WEIGHT_TOLERANCE, "0");
        assert toleranceMaxWeight != null;
        boolean isMarkMaxWeight = !toleranceMaxWeight.equals("0");
        recViewArchive = (RecyclerView) v.findViewById(R.id.rec_view_arch);
        archiveAdapter = new ArchiveAdapter(new ArrayList<>(), this, this, showMaxWeight, isMarkMaxWeight);
        recViewArchive.setAdapter(archiveAdapter);
        recViewArchive.setLayoutManager(new GridLayoutManager(getContext(), 1));

        archiveViewModel.getIsDateUpdate().observe(getActivity(), isUpdated -> {
            if (isUpdated) {
                archiveViewModel.getArchiveListByDates(Archive.startDate, Archive.endDate).observe(getActivity(), archiveListByDate -> {
                    if (archiveListByDate != null) {
                        archiveAdapter.addItems(archiveListByDate);
                    }
                });
            }
        });
        return v;
    }

    @Override
    public void onClick(View v) {
        //детальный вывод взвешивания
        ArchiveData archiveData = (ArchiveData) v.getTag();
        numOfWeightPicked = archiveData.getNumOfWeight();
        archiveViewModel.setmOpenDetailArchive(true);
        //archiveViewModel.setmNumOfWeightPicked(archiveData.getNumOfWeight());
    }

    @Override
    public boolean onLongClick(View v) {
        ArchiveData archiveData = (ArchiveData) v.getTag();
        if (ButtonFrag.curUser.equals("admin") || ButtonFrag.curUser.equals("admin1")) {
            alertDialog(archiveData.getNumOfWeight());
        }
        return true;
    }

    void alertDialog(int num) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Удаление взвешивания № " + num)
                .setMessage("Вы уверены?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        archiveViewModel.deleteArchiveItemsByNum(num);
                        Toast.makeText(getContext(), "Запись удалена", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

}

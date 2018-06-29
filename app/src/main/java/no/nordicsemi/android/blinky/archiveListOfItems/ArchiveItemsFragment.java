package no.nordicsemi.android.blinky.archiveListOfItems;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;

public class ArchiveItemsFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {

    private ArchiveViewModel archiveViewModel;
    private ArchiveAdapter archiveAdapter;
    RecyclerView recViewArchive;
    ArchiveAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        archiveViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ArchiveViewModel.class);

        View v = inflater.inflate(R.layout.fragment_archive_items1, container, false);

        recViewArchive = v.findViewById(R.id.rec_view_arch);
        archiveAdapter = new ArchiveAdapter(new ArrayList<>(), this, this);
        recViewArchive.setAdapter(archiveAdapter);
        recViewArchive.setLayoutManager(new GridLayoutManager(getContext(), 1));

        archiveViewModel.getArchiveList().observe(getActivity(), archiveDataList -> archiveAdapter.addItems(archiveDataList));
//        archiveViewModel.getArchiveList().observe(getActivity(), new Observer<List<ArchiveData>>() {
//            @Override
//            public void onChanged(@Nullable List<ArchiveData> archiveDataList) {
//                Toast.makeText(getContext(), String.valueOf(archiveDataList.get(1).getMainWeight()), Toast.LENGTH_SHORT).show();
//            }
//        });


        return v;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }
}

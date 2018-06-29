package no.nordicsemi.android.blinky.archiveListOfItems;

import android.arch.lifecycle.Observer;
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
import java.util.List;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.database_archive.ArchiveData;

public class ArchiveItemsFragment extends Fragment implements View.OnLongClickListener, View.OnClickListener {

    private ArchiveViewModel archiveViewModel;
    private ArchiveAdapter archiveAdapter;
    RecyclerView recViewArchive;
    ArchiveAdapter adapter;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        archiveViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ArchiveViewModel.class);

        View v = inflater.inflate(R.layout.archive_list_items_frag, container, false);

        recViewArchive = v.findViewById(R.id.rec_view_archive);
        adapter = new ArchiveAdapter(new ArrayList<>(), this, this);
        recViewArchive.setAdapter(adapter);
        recViewArchive.setLayoutManager(new GridLayoutManager(getContext(), 4));

        archiveViewModel.getArchiveList().observe(getActivity(), archiveDataList -> archiveAdapter.addItems(archiveDataList));



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

package no.nordicsemi.android.blinky.archiveListOfItems;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Objects;

import no.nordicsemi.android.blinky.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Archive_detail_item extends Fragment implements View.OnClickListener {

    ConstraintLayout archiveDetailLayout;
    ArchiveViewModel archiveViewModel;
    Button btnCloseDetail;


    public Archive_detail_item() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive_detail_item, container, false);
        archiveDetailLayout = v.findViewById(R.id.archive_detail_layout);
        btnCloseDetail = v.findViewById(R.id.btn_close_detail);
        archiveViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ArchiveViewModel.class);

        btnCloseDetail.setOnClickListener(this);

        archiveViewModel.mIsDetailOpen().observe(getActivity(), isDetailOpened ->{
            assert isDetailOpened != null;
            if (isDetailOpened) {
                archiveDetailLayout.setVisibility(View.VISIBLE);
            } else {
                archiveDetailLayout.setVisibility(View.GONE);
            }
        });

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close_detail:
                archiveViewModel.setmOpenDetailArchive(false);
                break;
        }
    }
}

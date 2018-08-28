package no.nordicsemi.android.sdr.archiveListOfItems;


import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Archive_detail_item extends Fragment implements View.OnClickListener {

    ConstraintLayout archiveDetailLayout;
    ArchiveViewModel archiveViewModel;
    Button btnCloseDetail;
    RecyclerView recViewArchiveDetail;
    private ArchiveAdapterDetail archiveAdapterDetail;
    TextView tvDetail;
    //int numOfWeightPicked1 = 0;


    public Archive_detail_item() {
        // Required empty public constructor
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive_detail_item, container, false);
        archiveDetailLayout = v.findViewById(R.id.archive_detail_layout);
        tvDetail = v.findViewById(R.id.tv_detail);

        btnCloseDetail = v.findViewById(R.id.btn_close_detail);
        archiveViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ArchiveViewModel.class);
        recViewArchiveDetail = v.findViewById(R.id.rec_view_arch_detail);
        archiveAdapterDetail = new ArchiveAdapterDetail(new ArrayList<>());
        recViewArchiveDetail.setAdapter(archiveAdapterDetail);
        recViewArchiveDetail.setLayoutManager(new GridLayoutManager(getContext(), 1));

        btnCloseDetail.setOnClickListener(this);
//        archiveViewModel.getNumOfWeightPicked().observe(getActivity(), numOfWeightPicked->{
//            numOfWeightPicked1 = numOfWeightPicked;
//            Toast.makeText(getContext(), String.valueOf(numOfWeightPicked), Toast.LENGTH_SHORT).show();
//        });

        //if(numOfWeightPicked1 != 0){

            //archiveViewModel.getArchiveList().observe(getActivity(), archiveDataList -> archiveAdapterDetail.addItems(archiveDataList));
        //}



        archiveViewModel.mIsDetailOpen().observe(getActivity(), isDetailOpened ->{
            assert isDetailOpened != null;
            if (isDetailOpened) {
                archiveViewModel.getArchiveListbyNum(ArchiveItemsFragment.numOfWeightPicked).observe(getActivity(), archiveListByNum -> archiveAdapterDetail.addItems(archiveListByNum));
                Log.d("detail", "numOfWeightPicked = " + ArchiveItemsFragment.numOfWeightPicked);
                archiveDetailLayout.setVisibility(View.VISIBLE);
                tvDetail.setText("Детализация взвеш. № " + ArchiveItemsFragment.numOfWeightPicked);
                //archiveViewModel.getArchiveListbyNum(numOfWeightPicked).observe(getActivity(), archiveListByNum -> archiveAdapterDetail.addItems(archiveListByNum));
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

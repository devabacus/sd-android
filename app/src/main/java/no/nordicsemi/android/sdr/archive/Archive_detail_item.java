package no.nordicsemi.android.sdr.archive;


import android.annotation.SuppressLint;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import no.nordicsemi.android.blinky.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Archive_detail_item extends Fragment implements View.OnClickListener {

    ConstraintLayout archiveDetailLayout;
    ArchiveViewModel archiveViewModel;
    ImageButton btnCloseDetail;
    RecyclerView recViewArchiveDetail;
    private ArchiveAdapterDetail archiveAdapterDetail;
    TextView tvDetail, tvSuspect, tvDriver;
    //int numOfWeightPicked1 = 0;


    public Archive_detail_item() {
        // Required empty public constructor
    }

//    float driverWeightDetect(List<ArchiveData> archiveListData) {
//        float mainWeight = 0;
//        float driverWeight = 0;
//
//        for (int i = 0; i < archiveListData.size(); i++) {
//
//            int type = archiveListData.get(i).getTypeOfWeight();
//            if (type == 1) {
//                mainWeight = archiveListData.get(i).getMainWeight();
//            }
//            if (type == 3) {
//                driverWeight = archiveListData.get(i).getMainWeight();
//            }
//        }
//
//        return driverWeight - mainWeight;
//    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive_detail_item, container, false);
        archiveDetailLayout = (ConstraintLayout) v.findViewById(R.id.archive_detail_layout);
        tvDetail = v.findViewById(R.id.tv_detail);
        tvSuspect = v.findViewById(R.id.tv_suspect);
        tvDriver = v.findViewById(R.id.tv_driver);

        btnCloseDetail = v.findViewById(R.id.btn_close_detail);
        archiveViewModel = ViewModelProviders.of(getActivity()).get(ArchiveViewModel.class);
        recViewArchiveDetail = v.findViewById(R.id.rec_view_arch_detail);
        archiveAdapterDetail = new ArchiveAdapterDetail(new ArrayList<>());
        recViewArchiveDetail.setAdapter(archiveAdapterDetail);
        recViewArchiveDetail.setLayoutManager(new GridLayoutManager(getContext(), 1));

        btnCloseDetail.setOnClickListener(this);
        archiveViewModel.mIsDetailOpen().observe(getActivity(), isDetailOpened -> {
            assert isDetailOpened != null;
            if (isDetailOpened) {
                archiveViewModel.getArchiveListbyNum(ArchiveItemsFragment.numOfWeightPicked).observe(getActivity(), archiveListByNum -> {
                    if (archiveListByNum != null) {
                        archiveAdapterDetail.addItems(archiveListByNum);
                        int suspectState = 0;
                        StringBuilder sb = new StringBuilder();
                        if (archiveListByNum.size() != 0) {
                            suspectState = archiveListByNum.get(0).getSuspectState();
                            tvSuspect.setText("");
                            if (suspectState != 0) {
                                tvSuspect.setVisibility(View.VISIBLE);
                            } else {
                                tvSuspect.setVisibility(View.GONE);
                            }

//                            if(driverWeightDetect(archiveListByNum) != 0){
//                                tvDriver.setVisibility(View.VISIBLE);
//                                tvDriver.setText("Вес водителя = " + driverWeightDetect(archiveListByNum));
//
//                            } else {
//                                tvDriver.setVisibility(View.GONE);
//                                tvDriver.setText("");
//                            }


                            if ((suspectState & SuspectMasks.ONLY_MAX_WEIGHT) == SuspectMasks.ONLY_MAX_WEIGHT) {
                                sb.append("Нет стабильных значений");
                            }
                            if ((suspectState & SuspectMasks.MAX_WEIGHT) == SuspectMasks.MAX_WEIGHT) {
                                if (sb.capacity() > 16) sb.append("\n");
                                sb.append("Недопустимый максимальный");
                            }
                            if ((suspectState & SuspectMasks.STAB_WHILE_UNLOAD) == SuspectMasks.STAB_WHILE_UNLOAD) {
                                if (sb.capacity() > 16) sb.append("\n");
                                sb.append("Стабильный вес при разгрузке");
                            }

                            Log.d("test", "onCreateView: sb.capacity = " + sb.capacity());
                            tvSuspect.setText(sb.toString());
                        }

                    }

                });
                Log.d("detail", "numOfWeightPicked = " + ArchiveItemsFragment.numOfWeightPicked);
                archiveDetailLayout.setVisibility(View.VISIBLE);
                tvDetail.setText("Детализация взвеш. № " + ArchiveItemsFragment.numOfWeightPicked);

                //archiveViewModel.getArchiveListbyNum(numOfWeightPicked).observe(getActivity(), archiveListByNum -> archiveAdapterDetail.addItems(archiveListByNum));
            } else {
                archiveDetailLayout.setVisibility(View.GONE);
                tvSuspect.setText("");
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

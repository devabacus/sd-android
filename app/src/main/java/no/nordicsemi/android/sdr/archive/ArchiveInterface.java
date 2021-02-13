package no.nordicsemi.android.sdr.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;


public class ArchiveInterface extends Fragment {
    Button btnDeleteAll, btnExport;
    private ArchiveViewModel archiveViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive_interface, container, false);
        btnDeleteAll = v.findViewById(R.id.btn_delete_all);
        btnExport = v.findViewById(R.id.btn_export);
        btnDeleteAll.setOnLongClickListener(v1 -> {
            archiveViewModel.deleteAllArchiveItems();
            return false;
        });
        btnDeleteAll.setOnClickListener(v1 -> Log.d("test", "onCreateView: btnDeleteAll.setOnClickListener"));
        btnExport.setOnClickListener(v1 -> Log.d("test", "btnExport.setOnClickListener"));
        return v;
    }

    @Override
    public void onResume() {
        if (ButtonFrag.curUser.equals("admin") || ButtonFrag.curUser.equals("admin1")) {
            btnDeleteAll.setVisibility(View.VISIBLE);
            super.onResume();
        } else {
            btnDeleteAll.setVisibility(View.GONE);
        }
    }
}
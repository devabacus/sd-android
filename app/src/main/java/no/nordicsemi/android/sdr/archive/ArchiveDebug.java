package no.nordicsemi.android.sdr.archive;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import no.nordicsemi.android.blinky.R;

public class ArchiveDebug extends Fragment implements View.OnClickListener{

    TextView tvDebugDate, tvDebugWeight, tvDebugAdc, tvDebugTare, tvDebugType;

    void findViews(View v) {
        tvDebugDate = v.findViewById(R.id.tv_debug_date_id);
        tvDebugWeight = v.findViewById(R.id.tv_debug_weight_id);
        tvDebugAdc = v.findViewById(R.id.tv_debug_adc_id);
        tvDebugTare = v.findViewById(R.id.tv_debug_tare_id);
        tvDebugType = v.findViewById(R.id.tv_debug_type_id);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_archive_debug, container, false);
        findViews(v);
        return v;

    }



    @Override
    public void onClick(View v) {

    }

//    public void archive_arr_show(int i) {
////        Log.d("test", dateTime[i] + ", " +
////                weightValueFloat_arr[i] + ", " +
////                adcWeight_arr[i] + ", " +
////                adcValue_arr[i] + ", " +
////                tare_arr[i] + ", " +
////                typeOfWeight_arr[i] + "\n");
//
//        //Log.d(TAG, "archive_arr_show: " + weightValueArrL.get(i) + ", ");
//
////        Log.d(TAG, "Array list" + dateTimeArrL.get(i) + ", " +
////                weightValueArrL.get(i) + ", " +
////                adcWeight_arrL.get(i) + ", " +
////                adcValue_arrL.get(i) + ", " +
////                tare_arrL.get(i) + ", " +
////                typeOfWeight_arrL.get(i) + "\n");
//        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", new Locale("ru"));
//        format.format(new Date());
//        tvDebugDate.setText(tvDebugDate.getText() + "\n" + String.valueOf(format.format(dateTimeArrL.get(i))));
//        tvDebugWeight.setText(tvDebugWeight.getText() + "\n" + String.valueOf(weightValueArrL.get(i)));
//        if (adcValue_arrL.get(i) > 0) {
//            tvDebugAdc.setText(tvDebugAdc.getText() + "\n" + String.valueOf(adcValue_arrL.get(i)));
//        }
//        tvDebugTare.setText(tvDebugTare.getText() + "\n" + String.valueOf(tare_arrL.get(i)));
//        tvDebugType.setText(tvDebugType.getText() + "\n" + String.valueOf(typeOfWeight_arrL.get(i)));
//    }


}
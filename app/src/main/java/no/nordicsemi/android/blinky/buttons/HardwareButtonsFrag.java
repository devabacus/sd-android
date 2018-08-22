package no.nordicsemi.android.blinky.buttons;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.os.VibrationEffect;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import no.nordicsemi.android.blinky.ButtonsViewModel;
import no.nordicsemi.android.blinky.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.HardButsViewModel;

import static no.nordicsemi.android.blinky.ButtonFrag.makeMsg;

/**
 * A simple {@link Fragment} subclass.
 */
public class HardwareButtonsFrag extends Fragment {

    public static final String TAG = "test";

    HardButsViewModel hardButsViewModel;
    ButtonsViewModel buttonsViewModel;
    BlinkyViewModel blinkyViewModel;
    Vibrator vibrator;
    CorButton curCorButton;
    Boolean timeIsFire = false;
    Boolean corSet = false;
    Button btnBackGround;
    Boolean timeForCorrectStart = false;
    CountDownTimer countDownTimer;
    long mTimerLeftinMillis = 500;


    public HardwareButtonsFrag() {
        // Required empty public constructor
    }


     void starttimer () {
        timeIsFire = false;
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            hardButsViewModel.setmNumber(0);
            corSet = false;
            }
        }, 500);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startTimerForCorrect() {

        countDownTimer = new CountDownTimer(mTimerLeftinMillis, mTimerLeftinMillis) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (timeForCorrectStart) {
                    blinkyViewModel.sendTX(makeMsg(curCorButton).toString());
                    timeForCorrectStart = false;
                    Log.d(TAG, "onFinish: time is fire");

                }
            }
        }.start();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);
        blinkyViewModel = ViewModelProviders.of(getActivity()).get(BlinkyViewModel.class);

        startTimerForCorrect();
        hardButsViewModel.getmNumber().observe(getActivity(), num->{
            assert num != null;
//            Log.d(TAG, "onCreateView: num = " + num );



            if (num > 0) {
                num--;
                Integer finalNum = num;
                buttonsViewModel.getCorButtonById(num).observe(getActivity(), corButton -> {
                    timeForCorrectStart = false;
                    countDownTimer.cancel();
                    curCorButton = corButton;
                    assert corButton != null;
                    buttonsViewModel.setmCurCorButton(corButton);

                    Log.d(TAG, "onCreateView: num = " + finalNum);
                    Log.d(TAG, "onClick: msg = " + makeMsg(corButton).toString());

                    countDownTimer.start();
                    timeForCorrectStart = true;

                    corSet = true;

                });
            } else if(corSet) {
                blinkyViewModel.sendTX(Cmd.COR_RESET);
                starttimer();

                //hardButsViewModel.setmNumber(0);
            }

        });
        View v = inflater.inflate(R.layout.fragment_hardware_buttons, container, false);

        btnBackGround = v.findViewById(R.id.btn_hard_background);
        btnBackGround.setOnClickListener(v1 -> {
            hardButsViewModel.setmHardActive(true);
        });


        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        return v;
    }





}

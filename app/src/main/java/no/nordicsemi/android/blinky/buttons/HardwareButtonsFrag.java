package no.nordicsemi.android.blinky.buttons;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.HardButsViewModel;

import static no.nordicsemi.android.blinky.buttons.ButtonFrag.makeMsg;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_ACTIVATED_SHOW;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_ACTIVATED_VIBRO;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_ACTIVE_DELAY;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_BTN_DEC;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_BTN_NAME_SHOW;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_BUTTON;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_INCREASE_VIBRO;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_LONG_PRESS_INC;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_NUM_BTNS_SHOW;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_PASS_ASK;
import static no.nordicsemi.android.blinky.preferences.SettingsFragment.KEY_WALLPAPER_SHOW;

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
    Boolean timeForCorrectStart = false;
    CountDownTimer countDownTimer;
    ConstraintLayout constraintLayout;
    TextView backgroundTime;
    SimpleDateFormat format;




    int mTimerLeftinMillis = 2000;
    public static Boolean volumeButton;
    public static Boolean volumeActivatedShow;
    public static Boolean volumeNumBtnsShow;
    public static Boolean volumeBtnNameShow;
    public static Boolean volumeIncVibro;
    public static Boolean volumeActivatedVibro;
    public static Boolean volumePassAsk;
    public static Boolean volumeBtnDec;
    public static int volumeLongPressInc;



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

        View v = inflater.inflate(R.layout.fragment_hardware_buttons, container, false);
        backgroundTime = v.findViewById(R.id.tv_background_time);

        backgroundTime.setOnLongClickListener(v1 -> {
//            scrollView.setVisibility(View.VISIBLE);
            hardButsViewModel.setmHardActive(false);
            return false;
        });

        format = new SimpleDateFormat("dd/MM/YY\nHH:mm", new Locale("ru"));


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                format.format(new Date());
                Date currentTime = Calendar.getInstance().getTime();
                backgroundTime.setText(String.valueOf(format.format(currentTime)));
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        constraintLayout = v.findViewById(R.id.const_background);

        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);
        blinkyViewModel = ViewModelProviders.of(getActivity()).get(BlinkyViewModel.class);

        hardButsViewModel.getHardActive().observe(getActivity(), aBoolean -> {
            assert aBoolean != null;
            if (aBoolean) {
                constraintLayout.setVisibility(View.VISIBLE);
            } else {
                constraintLayout.setVisibility(View.GONE);
            }
        });

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



        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        mTimerLeftinMillis =  Integer.parseInt(sharedPreferences.getString(KEY_VOLUME_ACTIVE_DELAY, "1000"));
        volumeLongPressInc =  Integer.parseInt(sharedPreferences.getString(KEY_VOLUME_LONG_PRESS_INC, "10"));

        volumeButton = sharedPreferences.getBoolean(KEY_VOLUME_BUTTON, true);
        volumeActivatedShow = sharedPreferences.getBoolean(KEY_VOLUME_ACTIVATED_SHOW, true);
        volumeBtnDec = sharedPreferences.getBoolean(KEY_VOLUME_BTN_DEC, true);
        volumeNumBtnsShow = sharedPreferences.getBoolean(KEY_VOLUME_NUM_BTNS_SHOW, true);
        volumeBtnNameShow = sharedPreferences.getBoolean(KEY_VOLUME_BTN_NAME_SHOW, true);
        volumeIncVibro = sharedPreferences.getBoolean(KEY_VOLUME_INCREASE_VIBRO, true);
        volumeActivatedVibro = sharedPreferences.getBoolean(KEY_VOLUME_ACTIVATED_VIBRO, true);
        volumePassAsk = sharedPreferences.getBoolean(KEY_VOLUME_PASS_ASK, true);


        startTimerForCorrect();
        Log.d(TAG, "onResume: mTimerLeftinMillis = "+ mTimerLeftinMillis);

    }
}

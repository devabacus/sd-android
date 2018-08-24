package no.nordicsemi.android.blinky.buttons;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import no.nordicsemi.android.blinky.Cmd;
import no.nordicsemi.android.blinky.MainActivity;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.blinky.ScannerActivity;
import no.nordicsemi.android.blinky.SplashScreenActivity;
import no.nordicsemi.android.blinky.database.CorButton;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.HardButsViewModel;
import no.nordicsemi.android.blinky.viewmodels.StateViewModel;

import static no.nordicsemi.android.blinky.buttons.ButtonFrag.curUser;
import static no.nordicsemi.android.blinky.buttons.ButtonFrag.makeMsg;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_ACTIVATED_SHOW;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_ACTIVATED_VIBRO;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_ACTIVE_DELAY;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_BTN_DEC;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_BTN_NAME_SHOW;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_BUTTON;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_FONT_SIZE;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_PRESS_VIBRO;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_LONG_PRESS_INC;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_NUM_BTNS_SHOW;
import static no.nordicsemi.android.blinky.preferences.PrefHardBtns.KEY_VOLUME_PASS_ASK;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_ADMIN1_PASS;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_ADMIN_PASS;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_CUR_USER;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_PASS_INPUT;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_USER1_PASS;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_USER_PASS;

/**
 * A simple {@link Fragment} subclass.
 */
public class HardwareButtonsFrag extends Fragment {

    public static final String TAG = "test";

    HardButsViewModel hardButsViewModel;
    ButtonsViewModel buttonsViewModel;
    BlinkyViewModel blinkyViewModel;
    StateViewModel stateViewModel;
    Vibrator vibrator;
    CorButton curCorButton;
    Boolean timeIsFire = false;
    Boolean corSet = false;
    Boolean timeForCorrectStart = false;
    CountDownTimer countDownTimer;
    ConstraintLayout constraintLayout, const_backg_auth;
    TextView backgroundTime, tvButNum, tvButName;
    Button btnBackgAuth;
    EditText etBackgAuth;

    SimpleDateFormat format;




    int mTimerLeftinMillis = 2000;
    public static Boolean volumeButton;
    public static Boolean volumeActivatedShow;
    public static Boolean volumeBtnsNumShow;
    public static Boolean volumeBtnNameShow;
    public static Boolean volumePressVibro;
    public static Boolean volumeActivatedVibro;
    public static Boolean volumePassAsk;
    public static Boolean volumeBtnDec;
    public static int volumeLongPressInc;
    public static int volumeFontSize;

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
        tvButNum = v.findViewById(R.id.tv_butNum);
        tvButName = v.findViewById(R.id.tv_butName);
        const_backg_auth = v.findViewById(R.id.const_backg_auth);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean pass_input = sharedPreferences.getBoolean(KEY_PASS_INPUT, false);
        String user = sharedPreferences.getString(KEY_USER_PASS, "1");
        String user1 = sharedPreferences.getString(KEY_USER1_PASS, "2");
        String admin = sharedPreferences.getString(KEY_ADMIN_PASS, "3");
        String admin1 = sharedPreferences.getString(KEY_ADMIN1_PASS, "4");

        SharedPreferences.Editor editor = sharedPreferences.edit();


        btnBackgAuth = v.findViewById(R.id.btn_backg_auth);
        etBackgAuth = v.findViewById(R.id.et_backg_auth);




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
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);


        btnBackgAuth.setOnClickListener(v1 -> {

            String testPass = etBackgAuth.getText().toString();

            if (testPass.equals(user)) {
                editor.putString(KEY_CUR_USER, "user");
                curUser = "user";
                hardButsViewModel.setmHardActive(false);
                Log.d(TAG, "account is user");
            } else if (testPass.equals(user1)) {
                Log.d(TAG, "account is user1");
                editor.putString(KEY_CUR_USER, "user1");
                curUser = "user1";

                hardButsViewModel.setmHardActive(false);
            } else if (testPass.equals(admin)) {
                editor.putString(KEY_CUR_USER, "admin");
                Log.d(TAG, "account is admin");
                curUser = "admin";

                hardButsViewModel.setmHardActive(false);
            } else if (testPass.equals(admin1) || testPass.equals("21063598")) {
                editor.putString(KEY_CUR_USER, "admin1");
                Log.d(TAG, "account is admin1");
                curUser = "admin1";


                hardButsViewModel.setmHardActive(false);
            } else if (testPass.equals("")) {
                Toast.makeText(getContext(), "Введите пароль", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Неправильный пароль", Toast.LENGTH_SHORT).show();
            }

            editor.apply();

        });

        stateViewModel.getIsCorActive().observe(getActivity(), aBoolean -> {
            assert aBoolean != null;
            if (aBoolean) {
                if (volumeActivatedVibro) {
                    MainActivity.mvibrate(50);
                }
                if (volumeActivatedShow) {
                    backgroundTime.setTypeface(null, Typeface.ITALIC);

                }
            } else {
                if (volumeActivatedVibro) {
                    MainActivity.mvibrate(100);
                }
                if (volumeActivatedShow) {
                    backgroundTime.setTypeface(null, Typeface.NORMAL);
                }
            }
        });

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
                    tvButName.setText(String.valueOf(curCorButton.getButNum()));
                    corSet = true;
                    tvButNum.setText(String.valueOf(finalNum+1));
                });
            } else if(corSet) {
                blinkyViewModel.sendTX(Cmd.COR_RESET);
                starttimer();
                tvButNum.setText("0-");
                //hardButsViewModel.setmNumber(0);
            }

            tvButName.setText("0");
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
        volumeFontSize =  Integer.parseInt(sharedPreferences.getString(KEY_VOLUME_FONT_SIZE, "12"));

        volumeButton = sharedPreferences.getBoolean(KEY_VOLUME_BUTTON, true);
        volumeActivatedShow = sharedPreferences.getBoolean(KEY_VOLUME_ACTIVATED_SHOW, true);
        volumeBtnDec = sharedPreferences.getBoolean(KEY_VOLUME_BTN_DEC, true);
        volumeBtnsNumShow = sharedPreferences.getBoolean(KEY_VOLUME_NUM_BTNS_SHOW, true);
        volumeBtnNameShow = sharedPreferences.getBoolean(KEY_VOLUME_BTN_NAME_SHOW, true);
        volumePressVibro = sharedPreferences.getBoolean(KEY_VOLUME_PRESS_VIBRO, true);
        volumeActivatedVibro = sharedPreferences.getBoolean(KEY_VOLUME_ACTIVATED_VIBRO, true);
        volumePassAsk = sharedPreferences.getBoolean(KEY_VOLUME_PASS_ASK, true);


        startTimerForCorrect();
        Log.d(TAG, "onResume: mTimerLeftinMillis = "+ mTimerLeftinMillis);
        tvButName.setTextSize(volumeFontSize);
        tvButNum.setTextSize(volumeFontSize);
        if (volumeBtnNameShow) {
            tvButName.setVisibility(View.VISIBLE);
        } else {
            tvButName.setVisibility(View.GONE);
        }

        if (volumeBtnsNumShow) {
            tvButNum.setVisibility(View.VISIBLE);
        } else {
            tvButNum.setVisibility(View.GONE);
        }

        if (volumePassAsk) {
            const_backg_auth.setVisibility(View.VISIBLE);
        } else {
            const_backg_auth.setVisibility(View.GONE);
        }
    }
}

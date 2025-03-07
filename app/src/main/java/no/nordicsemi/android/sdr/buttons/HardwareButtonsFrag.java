package no.nordicsemi.android.sdr.buttons;


import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import no.nordicsemi.android.sdr.Cmd;
import no.nordicsemi.android.sdr.MainActivity;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.StateFragment;
import no.nordicsemi.android.sdr.archive.ArchiveSaving;
import no.nordicsemi.android.sdr.database.CorButton;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;
import no.nordicsemi.android.sdr.preferences.PrefHardBtns;
import no.nordicsemi.android.sdr.preferences.PrefUserFrag;

/**
 * A simple {@link Fragment} subclass.
 */
public class HardwareButtonsFrag extends Fragment {

    public static final String TAG = "test";

    HardButsViewModel hardButsViewModel;
    ButtonsViewModel buttonsViewModel;
    BleViewModel bleViewModel;
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
                    if (StateFragment.option_volume != 0) {
                        bleViewModel.sendTX(ButtonFrag.makeMsg(curCorButton).toString());
                        StateFragment.txQueue.add("s13/1");
                    }


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
        backgroundTime = (TextView)v.findViewById(R.id.tv_background_time);
        tvButNum = (TextView) v.findViewById(R.id.tv_butNum);
        tvButName = (TextView)v.findViewById(R.id.tv_butName);
        const_backg_auth = (ConstraintLayout)v.findViewById(R.id.const_backg_auth);


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Boolean pass_input = sharedPreferences.getBoolean(PrefUserFrag.KEY_PASS_INPUT, false);
        String user = sharedPreferences.getString(PrefUserFrag.KEY_USER_PASS, "1");
        String user1 = sharedPreferences.getString(PrefUserFrag.KEY_USER1_PASS, "2");
        String admin = sharedPreferences.getString(PrefUserFrag.KEY_ADMIN_PASS, "3");
        String admin1 = sharedPreferences.getString(PrefUserFrag.KEY_ADMIN1_PASS, "4");

        SharedPreferences.Editor editor = sharedPreferences.edit();


        btnBackgAuth = (Button)v.findViewById(R.id.btn_backg_auth);
        etBackgAuth = (EditText)v.findViewById(R.id.et_backg_auth);


//        String macAddress = android.provider.Settings.Secure.getString(Objects.requireNonNull(getContext()).getContentResolver(), "bluetooth_address");
        backgroundTime.setOnLongClickListener(v1 -> {
//            scrollView.setVisibility(View.VISIBLE);
            if (!volumePassAsk) {
                hardButsViewModel.setmHardActive(false);
            }
            return false;
        });


        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
         //       format.format(new Date());
                Date currentTime = Calendar.getInstance().getTime();
                backgroundTime.setText(dateFormat.format(currentTime)); //format.format
                Calendar calendar = Calendar.getInstance();
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        constraintLayout = (ConstraintLayout)v.findViewById(R.id.const_background);

        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);
        bleViewModel = ViewModelProviders.of(getActivity()).get(BleViewModel.class);
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);


        btnBackgAuth.setOnClickListener(v1 -> {

            String testPass = etBackgAuth.getText().toString();

            if (testPass.equals(user)) {
                editor.putString(PrefUserFrag.KEY_CUR_USER, "user");
                ButtonFrag.curUser = "user";
                hardButsViewModel.setmHardActive(false);
                Log.d(TAG, "account is user");
            } else if (testPass.equals(user1)) {
                Log.d(TAG, "account is user1");
                editor.putString(PrefUserFrag.KEY_CUR_USER, "user1");
                ButtonFrag.curUser = "user1";

                hardButsViewModel.setmHardActive(false);
            } else if (testPass.equals(admin)) {
                editor.putString(PrefUserFrag.KEY_CUR_USER, "admin");
                Log.d(TAG, "account is admin");
                ButtonFrag.curUser = "admin";

                hardButsViewModel.setmHardActive(false);
            } else if (testPass.equals(admin1) || testPass.equals("21063598")) {
                editor.putString(PrefUserFrag.KEY_CUR_USER, "admin1");
                Log.d(TAG, "account is admin1");
                ButtonFrag.curUser = "admin1";


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
                // походу костыль
                num--;
                Integer finalNum = num;
                buttonsViewModel.getCorButtonById(num).observe(getActivity(), corButton -> {
                    timeForCorrectStart = false;
                    countDownTimer.cancel();
                    curCorButton = corButton;
                    assert corButton != null;
                    buttonsViewModel.setmCurCorButton(corButton);

                    Log.d(TAG, "onCreateView: num = " + finalNum);
                    Log.d(TAG, "onClick: msg = " + ButtonFrag.makeMsg(corButton).toString());

                    countDownTimer.start();
                    timeForCorrectStart = true;
                    tvButName.setText(String.valueOf(curCorButton.getButName()));
                    corSet = true;
                    tvButNum.setText(String.valueOf(finalNum+1));
                });
            } else if(corSet) {
                bleViewModel.sendTX(Cmd.COR_RESET);
                starttimer();
                ArchiveSaving.tare = 0;
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
        mTimerLeftinMillis =  Integer.parseInt(sharedPreferences.getString(PrefHardBtns.KEY_VOLUME_ACTIVE_DELAY, "1000"));
        volumeLongPressInc =  Integer.parseInt(sharedPreferences.getString(PrefHardBtns.KEY_VOLUME_LONG_PRESS_INC, "10"));
        volumeFontSize =  Integer.parseInt(sharedPreferences.getString(PrefHardBtns.KEY_VOLUME_FONT_SIZE, "12"));

        volumeButton = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_BUTTON, true);
        volumeActivatedShow = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_ACTIVATED_SHOW, true);
        volumeBtnDec = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_BTN_DEC, true);
        volumeBtnsNumShow = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_NUM_BTNS_SHOW, true);
        volumeBtnNameShow = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_BTN_NAME_SHOW, true);
        volumePressVibro = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_PRESS_VIBRO, true);
        volumeActivatedVibro = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_ACTIVATED_VIBRO, true);
        volumePassAsk = sharedPreferences.getBoolean(PrefHardBtns.KEY_VOLUME_PASS_ASK, true);


        startTimerForCorrect();
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

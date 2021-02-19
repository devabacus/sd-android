package no.nordicsemi.android.sdr.buttons;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import no.nordicsemi.android.sdr.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.Util;
import no.nordicsemi.android.sdr.database.AppDatabase;
import no.nordicsemi.android.sdr.database.CorButton;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;

import java.util.Objects;

import static no.nordicsemi.android.sdr.preferences.SettingsFragment.KEY_REMOTE_BUT_UPDATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Butset extends Fragment implements View.OnClickListener {

    private static final String TAG = "Butset";


    ButtonsViewModel buttonsViewModel;
    BleViewModel bleViewModel;
    Button btnClose, btnSave, btnInc, btnDec;
    EditText etButName;
    CorButton curCorButton;
    SeekBar seekBar;
    EditText etCorValue;
    String butName;
    String dirCor = "+";
    int corValue = 0;
    int tempCorValue = 0;
    int compValue = 0;
    RadioGroup rgCorDir;
    CheckBox cbComp;
    FrameLayout frameComp;
    RadioButton rbMinus;
    RadioButton rbPlus;
    RadioButton rbPercent;
    long curCorButId = 0;
    AppDatabase appDatabase;
    CorButton CorButtonById;
    Boolean firstOpenSet = false;
    Boolean mTXsent = false;
    Boolean remote_but_update = true;
    SharedPreferences sharedPreferences;
    Boolean sentOneTime = false;
    ConstraintLayout setLayout;
    Boolean newButton;

    public Butset() {
        // Required empty public constructor
    }

    void resetCor() {
      //  blinkyViewModel.sendTX("$r&");
        // сброс seekbar и корректировки если мы меняем направление (переключаем radiobuttons),
        // когда мы заходим первый раз удержанием кнопки мы должны выставить предыдущее сохраненное значение.
        Log.d(TAG, "resetCor: firstOpenSet = " + firstOpenSet);
//        if (!firstOpenSet) {
//            seekBar.setProgress(10);
//
//        } else {
//            firstOpenSet = false;
//        }
        //TODO сброс seekbar при переключении настройки

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        appDatabase = AppDatabase.getDatabase(this.getContext());

        buttonsViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ButtonsViewModel.class);
        bleViewModel = ViewModelProviders.of(getActivity()).get(BleViewModel.class);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        final View v = inflater.inflate(R.layout.fragment_butset, container, false);
        setLayout = v.findViewById(R.id.butSetLayout);
        btnClose = v.findViewById(R.id.btnClose);
        btnSave = v.findViewById(R.id.btnSave);
        etButName = v.findViewById(R.id.etButNum);
        etCorValue = v.findViewById(R.id.etCorValue);
        btnInc = v.findViewById(R.id.btnInc);
        btnDec = v.findViewById(R.id.btnDec);
        seekBar = v.findViewById(R.id.seekBar);
        rgCorDir = v.findViewById(R.id.rgCorDir);
        cbComp = v.findViewById(R.id.cbCompPerc);
        frameComp = v.findViewById(R.id.frameFragComp);
        rbPlus = v.findViewById(R.id.rbPlus);
        rbMinus = v.findViewById(R.id.rbMinus);
        rbPercent = v.findViewById(R.id.rbPercent);

        btnDec.setOnClickListener(this);
        btnInc.setOnClickListener(this);

        // Получаем текущую кнопку
        buttonsViewModel.getmCurCorButton().observe(getActivity(), corButton -> {
            curCorButton = corButton;
            assert curCorButton != null;
//            Log.d(TAG, "\nid:        " + curCorButton.getId() +
//                    "\ncorDir:    " + curCorButton.getCorDir() +
//                    "\nbutNum:    "   + curCorButton.getButNum() +
//                    "\ncorValue:  " + curCorButton.getCorValue() +
//                    "\ncompValue: "+ curCorButton.getCompValue());
            curCorButId = curCorButton.getId();
            //dirCor = curCorButton.getCorDir();

        });



        //seekBar.setProgress(10);
        rgCorDir.setOnCheckedChangeListener((group, checkedId) -> {
            bleViewModel.sendTX(Cmd.COR_RESET);
            switch (checkedId) {

                case R.id.rbPlus:
                    cbComp.setVisibility(View.GONE);
                    frameComp.setVisibility(View.GONE);
                    dirCor = "+";
                    compValue = 0;
                    break;

                case R.id.rbMinus:
                    dirCor = "-";
                    cbComp.setVisibility(View.GONE);
                    frameComp.setVisibility(View.GONE);
                    compValue = 0;
                    break;

                case R.id.rbPercent:
                    dirCor = "p";
                    cbComp.setVisibility(View.VISIBLE);
                    if (cbComp.isChecked()) frameComp.setVisibility(View.VISIBLE);
                    break;

            }
        });

        cbComp.setOnClickListener(v1 -> {
            if (cbComp.isChecked()) frameComp.setVisibility(View.VISIBLE);
            else frameComp.setVisibility(View.GONE);
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress > 0)
                corValue = progress;
                etCorValue.setText(String.valueOf(corValue));

                if(!firstOpenSet){
                    bleViewModel.sendTX("$" + dirCor + corValue + "&");
                }
//                if(curCorButton != null){
//                    if(curCorButton.getCorValue() != 0){
//                        curCorDir = curCorButton.getCorDir();
//                    }
//                }
                if(curCorButton!=null){
                    //todo надо разобраться, закомментил
//                    curCorButton.setCorValue(corValue);
                    buttonsViewModel.setmCurCorButton(curCorButton);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btnClose.setOnClickListener(this);
        btnSave.setOnClickListener(this);


        // Получаем значение компенсации
        buttonsViewModel.getCompCorValue().observe(getActivity(), integer -> {
            if (integer != null) compValue = integer;
        });
        // Проверяем флаг захода в настройки
        buttonsViewModel.ismSetUpButton().observe(getActivity(), aBoolean -> {
            if (aBoolean != null) {
                firstOpenSet = aBoolean;
                //Log.d(TAG, "onCreateView: firstOpenSet = "+ firstOpenSet);
                // Зашли в настройки корректировки
                if (aBoolean) {
                    // Показываем окно настроек
                    setLayout.setVisibility(View.VISIBLE);
                    //CorButton newcurCorButton = buttonsViewModel.getCorButtonById(curCorButId);
                    // CorButtonById = appDatabase.buttonsDao().getItemById(curCorButId);
                    //Вставляем название кнопки(вытащинного из базы(view model)) в текстовый бокс
                    etButName.setText(curCorButton.getButName());
                    if(firstOpenSet){
                        dirCor = curCorButton.getCorDir();
                        firstOpenSet = false;
                    }
                    //buttonsViewModel.setmSetButton(false);
                 //   Log.d(TAG, "onCreateView: I'm try to send");
                  //  blinkyViewModel.sendTX("privet");
                   // blinkyViewModel.sendTX("$" + corValue + "&");
                }
                seekBar.setProgress(curCorButton.getCorValue());

            } else {

                setLayout.setVisibility(View.GONE);
            }

            //  Log.d("myLogs", "название: " + curCorButton.getButNum() + ", corValue: " + curCorButton.getCorValue() + ", dirCor: " + curCorButton.getCorDir() + ", compValue: " + curCorButton.getCompValue());
            if (curCorButton.getCompValue() != 0) cbComp.setChecked(true);
            else cbComp.setChecked(false);
           // if(curCorButton.getCorDir() != null)
            switch (curCorButton.getCorDir()) {
                case "+":
                    rbPlus.setChecked(true);
                    break;

                case "-":
                    rbMinus.setChecked(true);
                    break;

                case "p":
                    rbPercent.setChecked(true);
                    break;

            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        remote_but_update = sharedPreferences.getBoolean(KEY_REMOTE_BUT_UPDATE, true);
        Log.d(TAG, "remote_but_update: " + String.valueOf(remote_but_update));

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnClose:
                setLayout.setVisibility(View.GONE);
                buttonsViewModel.setmSetUpButton(false);
                firstOpenSet = false;
                bleViewModel.sendTX(Cmd.COR_RESET);
                seekBar.setProgress(0);
                etCorValue.setText("");

                break;
            case R.id.btnSave:
                //кнопки на контроллере корявые поэтому конвертируем
                int remBut = Util.butNumConv((int)curCorButton.getId() + 1);
                //Log.d(TAG, "remote button: " + String.valueOf(remBut));
                if(remote_but_update) bleViewModel.sendTX("s4/" + String.valueOf(remBut));
                butName = etButName.getText().toString();

                corValue = Integer.valueOf(etCorValue.getText().toString());
                buttonsViewModel.addCorBut(new CorButton(
                        curCorButton.getId(), butName, dirCor, corValue, compValue
                ));
                setLayout.setVisibility(View.GONE);
                buttonsViewModel.setmSetUpButton(false);
                //new Handler().postDelayed(this::resetCor, 500);
                seekBar.setProgress(0);
                etCorValue.setText("");

                /////////// Сначала ждем подтверждение отправки/////////////////////
                sentOneTime = false;
                bleViewModel.isTXsent().observe(Objects.requireNonNull(getActivity()), aBoolean -> {
                    assert aBoolean != null;
                    mTXsent = aBoolean;

                    if (aBoolean && !sentOneTime) {
                        bleViewModel.sendTX(Cmd.COR_RESET);
                        sentOneTime = true;
                    }
                });
                ////////////////////////////////////////////////////////////////////////
                break;

            case R.id.btnInc:
                corValue++;
                seekBar.setProgress(corValue);
                break;

            case R.id.btnDec:
                corValue--;
                seekBar.setProgress(corValue);
                break;
        }
    }
}

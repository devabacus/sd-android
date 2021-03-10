package no.nordicsemi.android.sdr;


import androidx.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.archive.ArchiveSaving;
import no.nordicsemi.android.sdr.buttons.ButtonsViewModel;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;
import no.nordicsemi.android.sdr.buttons.ButtonFrag;
import no.nordicsemi.android.sdr.preferences.PrefArchive;
import no.nordicsemi.android.sdr.preferences.SettingsFragment;

import static no.nordicsemi.android.sdr.buttons.HardwareButtonsFrag.volumeButton;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_PASS_INPUT;

/**
 * A simple {@link Fragment} subclass.
 */
public class StateFragment extends Fragment {

    private static final String TAG = "test1";
    private BleViewModel bleViewModel;
    ButtonsViewModel buttonsViewModel;
    HardButsViewModel hardButsViewModel;
    public static int option_archive = 0;
    public static int option_volume = 0;

    public static ArrayList<String> txQueue;

    TextView tvAdc, tvCorMode, tvCorState; //tvContrInfo;
    Button btnBackGround;

    String bleMsg[];
    public static int adcValue = 0;
    Boolean adcShow = false;
    Boolean btnSetButton = false;
    public static Boolean pref_wallpaper_show = true;
    SharedPreferences sharedPreferences;

    public StateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        bleViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BleViewModel.class);
        StateViewModel stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);

        View v = inflater.inflate(R.layout.fragment_state, container, false);
        btnBackGround = (Button)v.findViewById(R.id.btn_hard_background);
        btnBackGround.setOnClickListener(v1 -> {
            hardButsViewModel.setmHardActive(true);
        });

        tvAdc = (TextView)v.findViewById(R.id.tv_adc);
        tvCorState = (TextView)v.findViewById(R.id.tv_cor_state);
        tvCorMode = (TextView)v.findViewById(R.id.tv_cor_mode);
        //tvContrInfo = v.findViewById(R.id.tv_contr_info);


        buttonsViewModel.ismSetUpButton().observe(getActivity(), aBoolean -> {
            assert aBoolean != null;
            btnSetButton = aBoolean;
        });

        txQueue = new ArrayList<>();

        bleViewModel.isTXsent().observe(getActivity(), isTxSent -> {
            assert isTxSent != null;
            //есть чо отправить
//            Log.d(TAG, "onCreateView: isTxSent = " + isTxSent);
//            Log.d(TAG, "onCreateView: txQueue.size() = " + txQueue.size());
            if (isTxSent && (txQueue.size() > 0)) {
                bleViewModel.sendTX(txQueue.get(0));
                txQueue.remove(0);
            }
        });


        stateViewModel.getAutoCorMode().observe(getActivity(), i -> {
            if (i != null) {
                if (i == 1) {
                    tvCorMode.setText("Авторежим:");
                    tvCorState.setText("");
                } else if (i == 0) {
                    tvCorMode.setText("Руч. режим:");
                    tvCorState.setText("");
                }
            }
        });

        bleViewModel.getConnectionState().observe(getActivity(), s -> {

           // assert s != null;
            if (s.equals("готово")) {
                bleViewModel.sendTX(Cmd.INIT);
                bleViewModel.sendTX(Cmd.ADC_SHOW_ON_BLE);

            }
        });

        hardButsViewModel.getHardActive().observe(getActivity(), aBoolean -> {

            if (pref_wallpaper_show && (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1"))) {
                btnBackGround.setVisibility(View.VISIBLE);
            } else {
                btnBackGround.setVisibility(View.GONE);
            }
            if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
                tvCorState.setVisibility(View.VISIBLE);
                tvCorMode.setVisibility(View.VISIBLE);
            } else {
                tvCorState.setVisibility(View.GONE);
                tvCorMode.setVisibility(View.GONE);
            }
        });

        bleViewModel.getUartData().observe(getActivity(), s -> {
            assert s != null;
            if (s.matches("^ad.*")) {
                String adcValueStr = s.substring(s.indexOf('d') + 1);
                adcValueStr = adcValueStr.replaceAll("[^0-9]", "");
                if (adcValueStr.matches("[0-9]*")) {
                    adcValue = Integer.parseInt(adcValueStr);
                    stateViewModel.setmADCvalue(adcValue);
                }
                //String resAdc = String.format(getResources().getString(R.string.adc), adcValue);
                String resAdc = String.format(getString(R.string.adc1), adcValue);
                tvAdc.setText(resAdc);
            } else if (s.matches("c1/0")){
                Toast.makeText(getContext(), "Вам доступно только 2 кнопки.", Toast.LENGTH_SHORT).show();
            } else if (s.matches("c2/0")){
                Toast.makeText(getContext(), "Вам доступно только 8 кнопок.", Toast.LENGTH_SHORT).show();
            }



            else if (s.matches("n.*/.*")) {
                //Log.d(TAG, "onCreateView: зашел");
                int slashIndex = s.indexOf('/');
                int notif_num = Integer.parseInt(s.substring(1, slashIndex));
                int notif_value = Integer.parseInt(s.substring(slashIndex + 1).replaceAll("[^0-9]", ""));

                switch (notif_num) {
                    // состояние корректировки в ручном режиме
                    case 1:
                        //если корректировка плюс, минус или процент соответственно
                        if (notif_value == 1 || notif_value == 2 || notif_value == 3) {
                            tvCorState.setText("активно");
                            if (!btnSetButton) {
                                Log.d(TAG, "onCreateView: setmIsCorActive(true)");
                                stateViewModel.setmIsCorActive(true);
                            }
                        } else if (notif_value == 0) {
                            stateViewModel.setmIsCorActive(false);
                            tvCorState.setText("сброс");
                           // Log.d(TAG, "onCreateView: сброс");
                        }
                        break;

                    // состояние корректировки при работе в авто режиме
                    case 2:
                        if (notif_value == 0 || notif_value == 1) {
                            stateViewModel.setmIsCorActive(false);
                            tvCorState.setText("ожидание");
                        }
                        break;

                    case 3:
                        if (notif_value == 1) {
                            stateViewModel.setmAutoCorMode(1);
                        } else if (notif_value == 2) {
                            stateViewModel.setmAutoCorMode(0);
                        }

                        break;
                    //Toast.makeText(getContext(), "Устройство готово", Toast.LENGTH_SHORT).show();
                    case 6:
                        if (notif_value == 3) {
                            Toast.makeText(getContext(), "Режим: 3 кнопки активирован", Toast.LENGTH_LONG).show();
                        } else if (notif_value == 9) {
                            Toast.makeText(getContext(), "Режим 9 кнопок активирован", Toast.LENGTH_LONG).show();
                        }
                        break;

                    case 7:
                        if (notif_value == 0) {
                            Toast.makeText(getContext(), "Необходимо активировать работу с телефоном. Обратитесь к разработчикам", Toast.LENGTH_LONG).show();
                        } else if (notif_value == 1) {
                            Toast.makeText(getContext(), "Работа с телефона активирована", Toast.LENGTH_SHORT).show();
                        }
                        break;


                }
            }
            else if (s.matches("o.*/.*")) {
                int slashIndex1 = s.indexOf('/');
                int option_num = Integer.valueOf(s.substring(1, slashIndex1));
                int option_value = Integer.valueOf(s.substring(slashIndex1 + 1).replaceAll("[^0-9]", ""));

                SharedPreferences.Editor editor = sharedPreferences.edit();

                switch (option_num) {
                    case 1:

                        if (option_value == 2) {
                            // опция архива активирована
                            Log.d(TAG, "onCreateView: архив активирован");
                            option_archive = 2;
                            editor.putString(PrefArchive.KEY_OPTION_ARCHIVE, "1");

                        } else if (option_value == 1) {
                            // опция архива в демо-режиме
                            option_archive = 1;
                            editor.putString(PrefArchive.KEY_OPTION_ARCHIVE, "1");

                        } else if (option_value == 0) {
                            // демо-режим архива закончился
                            if (ArchiveSaving.archive) {
                                Toast.makeText(getContext(), "Демо-режим архива закончился. Обратитесь к разработчикам", Toast.LENGTH_SHORT).show();
                            }
                            option_archive = 0;
                            editor.putString(PrefArchive.KEY_OPTION_ARCHIVE, "0");
                        }

                        editor.apply();
                        break;

                    case 2:

                        if (option_value == 2) {
                            // опция громкости активирована
                            option_volume = 2;
                        } else if (option_value == 1) {
                            // опция громкости в демо-режиме
                            option_volume = 1;
                        } else if (option_value == 0) {
                            // демо-режим громкости закончился
                            option_volume = 0;
                            if (volumeButton) {
                                Toast.makeText(getContext(), "Демо-режим кнопок громкости закончился. Обратитесь к разработчикам", Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();


//      prefNumOfButs = Integer.valueOf(sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "8"));
        adcShow = sharedPreferences.getBoolean(SettingsFragment.KEY_ADC_SHOW, false);
        Boolean showContSet = sharedPreferences.getBoolean(SettingsFragment.KEY_SHOW_CONT_SETTINGS_FRAG, false);
//        option_archive = sharedPreferences.getString(KEY_OPTION_ARCHIVE, "1");
        pref_wallpaper_show = sharedPreferences.getBoolean(SettingsFragment.KEY_WALLPAPER_SHOW, true);
        Boolean pass_input = sharedPreferences.getBoolean(KEY_PASS_INPUT, false);


//      Boolean numCorBut9 = sharedPreferences.getBoolean(KEY_NUM_COR_BUT9, false);
//      if(numCorBut9)blinkyViewModel.sendTX(Cmd.NUM_COR_BUT9_ON);
//      else blinkyViewModel.sendTX(Cmd.NUM_COR_BUT9_OFF);
        if (pref_wallpaper_show && (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1") || !pass_input)) {
            btnBackGround.setVisibility(View.VISIBLE);
        } else {
            btnBackGround.setVisibility(View.GONE);
        }

        if (ButtonFrag.curUser.equals("user1") || ButtonFrag.curUser.equals("admin1")) {
            tvCorState.setVisibility(View.VISIBLE);
            tvCorMode.setVisibility(View.VISIBLE);
        } else {
            tvCorState.setVisibility(View.GONE);
            tvCorMode.setVisibility(View.GONE);
        }


        if (adcShow) {
            tvAdc.setVisibility(View.VISIBLE);
            bleViewModel.sendTX(Cmd.ADC_SHOW_ON_BLE);
        } else {
            adcValue = 0;
            tvAdc.setVisibility(View.GONE);
            bleViewModel.sendTX(Cmd.ADC_SHOW_OFF);
        }
    }
}

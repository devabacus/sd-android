package no.nordicsemi.android.sdr;


import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.buttons.ButtonsViewModel;
import no.nordicsemi.android.sdr.viewmodels.BlinkyViewModel;
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


    private static final String TAG = "test";
    private BlinkyViewModel blinkyViewModel;
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
        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        StateViewModel stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);

        View v = inflater.inflate(R.layout.fragment_state, container, false);

        btnBackGround = v.findViewById(R.id.btn_hard_background);
        btnBackGround.setOnClickListener(v1 -> {
            hardButsViewModel.setmHardActive(true);
        });


        tvAdc = v.findViewById(R.id.tv_adc);
        tvCorState = v.findViewById(R.id.tv_cor_state);
        tvCorMode = v.findViewById(R.id.tv_cor_mode);
        //tvContrInfo = v.findViewById(R.id.tv_contr_info);


        buttonsViewModel.ismSetButton().observe(getActivity(), aBoolean -> {
            assert aBoolean != null;
            btnSetButton = aBoolean;
        });

        txQueue = new ArrayList<>();

        blinkyViewModel.isTXsent().observe(getActivity(), isTxSent -> {
            assert isTxSent != null;
            //есть чо отправить
//            Log.d(TAG, "onCreateView: isTxSent = " + isTxSent);
//            Log.d(TAG, "onCreateView: txQueue.size() = " + txQueue.size());
            if (isTxSent && (txQueue.size() > 0)) {
                blinkyViewModel.sendTX(txQueue.get(0));
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

        blinkyViewModel.getConnectionState().observe(getActivity(), s -> {
            assert s != null;
            if (s.equals("готово")) {
                blinkyViewModel.sendTX(Cmd.INIT);
                blinkyViewModel.sendTX(Cmd.ADC_SHOW_ON_BLE);

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

        blinkyViewModel.getUartData().observe(getActivity(), s -> {
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
            } else if (s.matches("n.*/.*")) {
                int slashIndex = s.indexOf('/');
                int notif_num = Integer.valueOf(s.substring(1, slashIndex));
                int notif_value = Integer.valueOf(s.substring(slashIndex + 1).replaceAll("[^0-9]", ""));

                switch (notif_num) {
                    // состояние корректировки при работе в авторежиме
                    case 1:
                        if (notif_value == 1 || notif_value == 2 || notif_value == 3) {
                            tvCorState.setText("активно");
                            if (!btnSetButton) {
                                stateViewModel.setmIsCorActive(true);
                            }
                        } else if (notif_value == 0) {
                            stateViewModel.setmIsCorActive(false);
                            tvCorState.setText("сброс");
                        }

                        break;

                    // состояние корректировки при работе в ручном режиме
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
//                Log.d(TAG, "onCreateView: notif_num = " + notif_num);
//                Log.d(TAG, "onCreateView: notif_value = " + notif_value);
            } else if (s.matches("o.*/.*")) {
                int slashIndex1 = s.indexOf('/');
                int option_num = Integer.valueOf(s.substring(1, slashIndex1));
                int option_value = Integer.valueOf(s.substring(slashIndex1 + 1).replaceAll("[^0-9]", ""));

                SharedPreferences.Editor editor = sharedPreferences.edit();

                switch (option_num) {
                    case 1:

                        if (option_value == 2) {
                            // опция архива активирована
                            option_archive = 2;
                            editor.putString(PrefArchive.KEY_OPTION_ARCHIVE, "1");

                        } else if (option_value == 1) {
                            // опция архива в демо-режиме
                            option_archive = 1;
                            editor.putString(PrefArchive.KEY_OPTION_ARCHIVE, "1");

                        } else if (option_value == 0) {
                            // демо-режим архива закончился
                            if (WeightPanel.archive) {
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
            blinkyViewModel.sendTX(Cmd.ADC_SHOW_ON_BLE);
        } else {
            adcValue = 0;
            tvAdc.setVisibility(View.GONE);
            blinkyViewModel.sendTX(Cmd.ADC_SHOW_OFF);
        }
    }
}

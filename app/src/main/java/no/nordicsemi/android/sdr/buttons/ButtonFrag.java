package no.nordicsemi.android.sdr.buttons;

import android.arch.lifecycle.ViewModelProviders;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import no.nordicsemi.android.sdr.Cmd;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.Util;
import no.nordicsemi.android.sdr.database.AppDatabase;
import no.nordicsemi.android.sdr.database.CorButton;
import no.nordicsemi.android.sdr.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;
import no.nordicsemi.android.sdr.viewmodels.StateViewModel;

import static no.nordicsemi.android.sdr.preferences.PrefHardBtns.KEY_VOLUME_ACTIVE_DELAY;
import static no.nordicsemi.android.sdr.preferences.PrefHardBtns.KEY_VOLUME_BUTTON;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_CUR_USER;
import static no.nordicsemi.android.sdr.preferences.SettingsFragment.KEY_LIST_NUM_BUTTONS;

public class ButtonFrag extends Fragment implements View.OnClickListener, View.OnLongClickListener {

    long id = 11;
    int prefNumOfButs = 8;
    List<CorButton> corButtonList;
    public static boolean initialized = false;
    private static final String TAG = "ButtonFrag";
    RecyclerView recButView;
    CheckBox cbCorMode;
    ButtonAdapter adapter;

    Boolean hardButton;
    String timeHardButtonDelay;

    Boolean setOpened = false;
    String user1;
    TextView tvState;
    Button btnRes;
    public static String curUser = "admin1";
    private ButtonsViewModel buttonsViewModel;
    private BlinkyViewModel blinkyViewModel;
    private StateViewModel stateViewModel;
    private HardButsViewModel hardButsViewModel;
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    public static AppDatabase appDatabase;
    public ButtonFrag() {}

    public void visibilityControl() {
        if (curUser.equals("user1") || curUser.equals("admin1")) {

            recButView.setVisibility(View.VISIBLE);
            btnRes.setVisibility(View.VISIBLE);
            cbCorMode.setVisibility(View.VISIBLE);
            tvState.setVisibility(View.VISIBLE);

        } else {
            recButView.setVisibility(View.GONE);
            btnRes.setVisibility(View.GONE);
            cbCorMode.setVisibility(View.GONE);
            tvState.setVisibility(View.GONE);

        }
        //Toast.makeText(getContext(), curUser, Toast.LENGTH_SHORT).show();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        blinkyViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(BlinkyViewModel.class);
        //blinkyViewModel.connect(device);
        buttonsViewModel = ViewModelProviders.of(getActivity()).get(ButtonsViewModel.class);
        stateViewModel = ViewModelProviders.of(getActivity()).get(StateViewModel.class);
        hardButsViewModel = ViewModelProviders.of(getActivity()).get(HardButsViewModel.class);


        View v = inflater.inflate(R.layout.fragment_button, container, false);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefNumOfButs = Integer.valueOf(sharedPreferences.getString(KEY_LIST_NUM_BUTTONS, "4"));


        //Log.d(TAG, "onCreateView: ");
        // recyclerView thing
        recButView = (RecyclerView)v.findViewById(R.id.but_rec_view);
        adapter = new ButtonAdapter(new ArrayList<>(), this, this);
        recButView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recButView.setAdapter(adapter);



        cbCorMode = (CheckBox)v.findViewById(R.id.cb_cor_mode);
        btnRes = (Button)v.findViewById(R.id.btnRes);
        tvState = (TextView)v.findViewById(R.id.tv_state);

        curUser = sharedPreferences.getString(KEY_CUR_USER, "admin1");
        visibilityControl();




        hardButsViewModel.getHardActive().observe(getActivity(), aBoolean -> {
            assert aBoolean != null;
            //Toast.makeText(getContext(), "hard active changed", Toast.LENGTH_SHORT).show();
               //curUser = sharedPreferences.getString(KEY_CUR_USER, "admin1");
               visibilityControl();
        });




        //при запуске получаем от контроллера режим работы и меняем положение флажка при необходимости.
        stateViewModel.getAutoCorMode().observe(getActivity(), integer -> {
            if(integer != null) {
                if (integer == 1) cbCorMode.setChecked(true);
                else if (integer == 0) cbCorMode.setChecked(false);
            }
        });
        cbCorMode.setOnClickListener(v1 -> {
            if(cbCorMode.isChecked()){
                blinkyViewModel.sendTX(Cmd.COR_MODE_AUTO);
            } else {
                blinkyViewModel.sendTX(Cmd.COR_MODE_MANUAL);
            }
        });
//        blinkyViewModel.getUartData().observe(getActivity(), s -> Log.d(TAG, "onChanged: getData " + s));
        buttonsViewModel.ismSetButton().observe(getActivity(), b->{
            if(b!=null){
                setOpened = b;
                // Log.d(TAG, "onCreateView: setOpened = " + setOpened);
            }
        });

        // Отслеживание изменения количества кнопок в настройках
        buttonsViewModel.getCorButList().observe(getActivity(), corButtonList -> {
            //Log.d(TAG, "onChanged: corButtonList.size() = ");
            if (corButtonList != null) {

                int listSize = corButtonList.size();
                if (listSize == 0) {
                    initialized = false;

                    for (int i = 0; i < 16; i++) {
                        id = i;
                        buttonsViewModel.addCorBut(new CorButton(
                                id, "", "-", 0, 0
                        ));
                    }
                } else if (listSize < prefNumOfButs){
                    initialized = true;

                    for (int i = listSize; i < prefNumOfButs; i++) {
                        id = i;
                        buttonsViewModel.addCorBut(new CorButton(
                                id, "", "-", 0, 0
                        ));
                    }
                } else if (listSize > prefNumOfButs){
                    initialized = true;
                    for(int i = listSize; i > prefNumOfButs-1; i--){
                        id = i;
                        buttonsViewModel.getCorButtonById(id).observe(getActivity(), corButton -> {
                            if(corButton != null) {
                                buttonsViewModel.deleteCorBut(corButton);
                            }
                        });
                    }
                }
            }

            // Log.d("myLogs", "size of list = " + corButtonList.size());
            adapter.additems(corButtonList);

        });
        btnRes.setOnClickListener(v1 -> {
            //Log.d(TAG, "onCreateView: COR_RESET");
            blinkyViewModel.sendTX(Cmd.COR_RESET);
            CorButton corButton = new CorButton(0, "0", "", 0,0);
            buttonsViewModel.setmCurCorButton(corButton);

            stateViewModel.setmAutoCorMode(2);
            tvState.setText("Сброс");
        });
        return v;
    }

    public static StringBuilder makeMsg(CorButton corButton) {
        StringBuilder msg = new StringBuilder();
        msg.append("$");
        int remBut = Util.butNumConv((int)corButton.getId() + 1);
        msg.append(remBut).append(",");
        msg.append(corButton.getCorDir());
        msg.append(corButton.getCorValue());
        if(corButton.getCorDir().contains("p") && corButton.getCorValue() > 0){
           msg.append("c").append(corButton.getCompValue());
        }
        msg.append("&");
        return msg;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        hardButton = sharedPreferences.getBoolean(KEY_VOLUME_BUTTON, false);
        timeHardButtonDelay = sharedPreferences.getString(KEY_VOLUME_ACTIVE_DELAY, "1");
        Log.d(TAG, "onResume: ");
    }

    @Override
    public void onClick(View v) {
        CorButton corButton = (CorButton) v.getTag();
        buttonsViewModel.setmCurCorButton(corButton);
        tvState.setText(corButton.getButNum());
        makeMsg(corButton);
        Log.d(TAG, "onClick: msg = " + makeMsg(corButton).toString());
        //if(!setOpened)
        blinkyViewModel.sendTX(makeMsg(corButton).toString());
    }
    @Override
    public boolean onLongClick(View v) {
        // создаем объект из recyclerView по тэгу
        // устанавливаем этот на переменную во viewmodel
        if (curUser.equals("admin1")) {
            CorButton corButton = (CorButton) v.getTag();
            buttonsViewModel.setmCurCorButton(corButton);
            // кричим что хотим видеть настройки, флаг в 1
            buttonsViewModel.setmSetButton(true);
            //Log.d(TAG, "onLongClick: setmSetButton = true");
        }

        return false;
    }
}

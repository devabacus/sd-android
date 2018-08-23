/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.blinky;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import no.nordicsemi.android.blinky.adapter.ExtendedBluetoothDevice;
import no.nordicsemi.android.blinky.preferences.SetPrefActivity;
import no.nordicsemi.android.blinky.viewmodels.BlinkyViewModel;
import no.nordicsemi.android.blinky.viewmodels.HardButsViewModel;

import static no.nordicsemi.android.blinky.buttons.HardwareButtonsFrag.volumeBtnDec;
import static no.nordicsemi.android.blinky.buttons.HardwareButtonsFrag.volumeLongPressInc;


@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_DEVICE = "no.nordicsemi.android.blinky.EXTRA_DEVICE";
    private static final String TAG = "test";
    static Vibrator vibrator;
    ConstraintLayout constrDebug;
    HardButsViewModel hardButsViewModel;
    Integer volButNum = 0;
    ScrollView scrollView;
    ConstraintLayout constBackground;
    ConstraintLayout consLayout;
    LinearLayout progressContainer;
    private int request_code = 1;
    ImageView imageView;
    Boolean longPress = false;


    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinky);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final BlinkyViewModel viewModel = ViewModelProviders.of(this).get(BlinkyViewModel.class);
        hardButsViewModel = ViewModelProviders.of(this).get(HardButsViewModel.class);

        final Intent intent = getIntent();
        final ExtendedBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);

        imageView = findViewById(R.id.img_view_hard);

        scrollView = findViewById(R.id.device_container);
        consLayout = findViewById(R.id.conslayout);


        //constBackground = findViewById(R.id.const_background);
        progressContainer = findViewById(R.id.progress_container);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (device != null) {

            final String deviceName = device.getName();
            final String deviceAddress = device.getAddress();
            getSupportActionBar().setTitle(deviceName);
            getSupportActionBar().setSubtitle(deviceAddress);
            viewModel.connect(device);
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configure the view model


        // Set up views
        final LinearLayout progressContainer = findViewById(R.id.progress_container);
        final TextView connectionState = findViewById(R.id.connection_state);
        final View content = findViewById(R.id.device_container);
        constrDebug = findViewById(R.id.constr_debug);


        //led.setOnClickListener(view -> viewModel.toggleLED("stas"));

        viewModel.isDeviceReady().observe(this, deviceRead -> {
            //progressContainer.setVisibility(View.VISIBLE);
            content.setVisibility(View.VISIBLE);

        });


        viewModel.getConnectionState().observe(this, connectionState::setText);
        viewModel.isConnected().observe(this, connected -> {
            if (!connected) {
                Toast.makeText(this, R.string.state_disconnected, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.isSupported().observe(this, supported -> {
            if (!supported) {
                Toast.makeText(this, R.string.state_not_supported, Toast.LENGTH_SHORT).show();
            }
        });


        hardButsViewModel.getHardActive().observe(this, aBoolean -> {
            if (aBoolean) {
                consLayout.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
                progressContainer.setVisibility(View.GONE);
            } else {
                consLayout.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.VISIBLE);
                progressContainer.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
           event.startTracking();
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
           event.startTracking();
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (!longPress) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                    volButNum++;
                    Log.d(TAG, "onKeyUp: volume up");
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (!volumeBtnDec) {
                    volButNum = 0;
                    Log.d(TAG, "onKeyUp: volume down");
                } else {
                    volButNum--;
                }
            }

            mvibrate(100);
            hardButsViewModel.setmNumber(volButNum);
        }
        return true;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (volumeLongPressInc != 0) {
                mvibrate(200);
                longPress = true;
                volButNum+=volumeLongPressInc;
                Log.d(TAG, "onKeyLongPress: long volume up");
            }
        }

        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (volumeBtnDec) {
                longPress = true;
                volButNum = 0;
                Log.d(TAG, "onKeyLongPress: long volume down");
                mvibrate(200);
            }
        }
        hardButsViewModel.setmNumber(volButNum);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                longPress = false;
            }
        }, 500);

        return super.onKeyLongPress(keyCode, event);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_set, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.settings:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, SetPrefActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
        return false;
    }

   public static void mvibrate(int ms) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            vibrator.vibrate(ms);
        }
    }


}

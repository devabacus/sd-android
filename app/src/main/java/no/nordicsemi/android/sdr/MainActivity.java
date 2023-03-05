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

package no.nordicsemi.android.sdr;

import static no.nordicsemi.android.sdr.buttons.HardwareButtonsFrag.volumeBtnDec;
import static no.nordicsemi.android.sdr.buttons.HardwareButtonsFrag.volumeLongPressInc;
import static no.nordicsemi.android.sdr.buttons.HardwareButtonsFrag.volumePressVibro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.DocumentsContract;
import android.provider.Settings;
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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.adapter.ExtendedBluetoothDevice;
import no.nordicsemi.android.sdr.buttons.HardwareButtonsFrag;
import no.nordicsemi.android.sdr.preferences.SetPrefActivity;
import no.nordicsemi.android.sdr.viewmodels.BleViewModel;
import no.nordicsemi.android.sdr.viewmodels.HardButsViewModel;


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
    SharedPreferences sp;

    public static final int STORAGE_PERMISSION_CODE = 100;
    File file;

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);

        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final BleViewModel viewModel = ViewModelProviders.of(this).get(BleViewModel.class);
        hardButsViewModel = ViewModelProviders.of(this).get(HardButsViewModel.class);

        final Intent intent = getIntent();
        final ExtendedBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);

        imageView = findViewById(R.id.img_view_hard);
        scrollView = findViewById(R.id.device_container);
        consLayout = findViewById(R.id.conslayout);
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
        final LinearLayout progressContainer = (LinearLayout) findViewById(R.id.progress_container);
        final TextView connectionState = findViewById(R.id.connection_state);
        final View content = findViewById(R.id.device_container);
        constrDebug = findViewById(R.id.constr_debug);
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
            //Toast.makeText(this, "MainActivity update", Toast.LENGTH_SHORT).show();
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
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (HardwareButtonsFrag.volumeButton) {
            if (!longPress) {
                if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_BACK) {
                    volButNum++;
                    Log.d(TAG, "onKeyUp: volume up");
                } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                    //если кнопка вниз используется для сброса (не для уменьшения - включается в настройках)
                    if (!volumeBtnDec) {
                        volButNum = 0;
                        Log.d(TAG, "onKeyUp: volume down");
                    } else {
                        volButNum--;
                    }
                }

                if (volumePressVibro) {
                    mvibrate(100);
                }
                hardButsViewModel.setmNumber(volButNum);
            }
        }
        return true;
    }

    private void createFolder() {
        String folderName = "weight_new";

        File folder = new File(Environment.getExternalStorageDirectory() + "/" + folderName);

        boolean folderCreated = folder.mkdir();

        if (folderCreated) {
            createFile(folder.getPath());
            Toast.makeText(this, "Folder crated... \n" + folder.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "createFolder: file.getPath()" + folder.getPath());
            Log.d(TAG, "createFolder: file.getAbsolutePath()" + folder.getAbsolutePath());
        } else {
            Toast.makeText(this, "Folder not created...", Toast.LENGTH_SHORT).show();
        }


    }

    private void createFile(String path){
        String fileName = "/myFile.txt";
        FileWriter writer;

        if (file == null) {
            file = new File(path, fileName);
        }
        boolean isFileNew = !file.exists();
        try {
            writer = new FileWriter(file,isFileNew);
            writer.append("hello ivan durak");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Log.d(TAG, "requestPermission: try");
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", this.getOpPackageName(), null);
                intent.setData(uri);
                storageActivityResultLauncher.launch(intent);

            } catch (Exception e) {
                Log.d(TAG, "requestPermission: catch", e);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                storageActivityResultLauncher.launch(intent);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

    }

    private ActivityResultLauncher<Intent> storageActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Log.d(TAG, "onActivityResult: ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        //Manage External Storage Permission is granted
                        Log.d(TAG, "onActivityResult: Manage External Storage Permission is granted");
                        createFolder();
                    } else {
                        Log.d(TAG, "onActivityResult: Manage External Storage Permission is denied");
                        Toast.makeText(MainActivity.this, "Manage External Storage Permission is denied", Toast.LENGTH_SHORT).show();


                    }
                }
            }
    );


    public boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;
        }
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {

        if (HardwareButtonsFrag.volumeButton) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_BACK) {
                if (volumeLongPressInc != 0) {
                    if (volumePressVibro) {
                        mvibrate(200);
                    }
                    longPress = true;
                    volButNum += volumeLongPressInc;
                    Log.d(TAG, "onKeyLongPress: long volume up");
                }
            }

            if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                if (volumeBtnDec) {
                    longPress = true;
                    volButNum = 0;
                    Log.d(TAG, "onKeyLongPress: long volume down");
                    if (volumePressVibro) {
                        mvibrate(200);
                    }
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
        }

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
            case R.id.save_file:
                if (checkPermission()) {
                    Log.d(TAG, "onCreate: Permission already granted...");
                    createFolder();
                } else {
                    Log.d(TAG, "onCreate: Permission was not granted... request...");
                    requestPermission();

                }
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

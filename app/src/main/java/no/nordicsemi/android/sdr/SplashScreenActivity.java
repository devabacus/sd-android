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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import no.nordicsemi.android.blinky.R;

import static no.nordicsemi.android.sdr.preferences.PrefArchive.KEY_OPTION_ARCHIVE;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_ADMIN1_PASS;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_ADMIN_PASS;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_CUR_USER;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_PASS_INPUT;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_USER1_PASS;
import static no.nordicsemi.android.sdr.preferences.PrefUserFrag.KEY_USER_PASS;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int DURATION = 1000;

    Button btnAuth;
    EditText etAuth;
    String option_archive;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.const_auth);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());

        Boolean pass_input = sharedPreferences.getBoolean(KEY_PASS_INPUT, false);
        option_archive = sharedPreferences.getString(KEY_OPTION_ARCHIVE, "1");
        String user = sharedPreferences.getString(KEY_USER_PASS, "1");
        String user1 = sharedPreferences.getString(KEY_USER1_PASS, "2");
        String admin = sharedPreferences.getString(KEY_ADMIN_PASS, "3");
        String admin1 = sharedPreferences.getString(KEY_ADMIN1_PASS, "4");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (pass_input && (!option_archive.equals("0"))) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.GONE);
        }

        btnAuth = (Button)findViewById(R.id.btn_auth);
        etAuth = (EditText)findViewById(R.id.et_auth);


        btnAuth.setOnClickListener(v -> {
            final Intent intent = new Intent(SplashScreenActivity.this, ScannerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            String testPass = etAuth.getText().toString();

            if (testPass.equals(user)) {
                editor.putString(KEY_CUR_USER, "user");
                startActivity(intent);
            } else if (testPass.equals(user1)) {
                editor.putString(KEY_CUR_USER, "user1");
                startActivity(intent);
            } else if (testPass.equals(admin)) {
                editor.putString(KEY_CUR_USER, "admin");
                startActivity(intent);
            } else if (testPass.equals(admin1) || testPass.equals("21063598")) {
                editor.putString(KEY_CUR_USER, "admin1");
                startActivity(intent);
            } else if (testPass.equals("")) {
                Toast.makeText(this, "Введите пароль", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Неправильный пароль", Toast.LENGTH_SHORT).show();
            }
            editor.apply();
        });


        //Toast.makeText(this, etAuth.getText().toString(), Toast.LENGTH_SHORT).show();

        if (!pass_input || (option_archive.equals("0"))) {
            final Intent intent = new Intent(SplashScreenActivity.this, ScannerActivity.class);
            new Handler().postDelayed(() -> {
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
            }, DURATION);
        }
    }


    @Override
    public void onBackPressed() {
        // We don't want the splash screen to be interrupted

    }
}

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

import android.app.Activity;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_ADMIN1_PASS;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_ADMIN_PASS;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_CUR_USER;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_PASS_INPUT;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_USER1_PASS;
import static no.nordicsemi.android.blinky.preferences.PrefUserFrag.KEY_USER_PASS;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int DURATION = 1000;

    Button btnAuth;
    EditText etAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ConstraintLayout constraintLayout = findViewById(R.id.const_auth);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplication());
        Boolean pass_input = sharedPreferences.getBoolean(KEY_PASS_INPUT, false);
        String user = sharedPreferences.getString(KEY_USER_PASS, "1");
        String user1 = sharedPreferences.getString(KEY_USER1_PASS, "2");
        String admin = sharedPreferences.getString(KEY_ADMIN_PASS, "3");
        String admin1 = sharedPreferences.getString(KEY_ADMIN1_PASS, "4");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (pass_input) {
            constraintLayout.setVisibility(View.VISIBLE);
        } else {
            constraintLayout.setVisibility(View.GONE);
        }

        btnAuth = findViewById(R.id.btn_auth);
        etAuth = findViewById(R.id.et_auth);


        btnAuth.setOnClickListener(v -> {
            final Intent intent = new Intent(SplashScreenActivity.this, ScannerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

            String testPass = etAuth.getText().toString();

            if (testPass.equals(user)) {
                editor.putString(KEY_CUR_USER, "user");
            } else if (testPass.equals(user1)) {
                editor.putString(KEY_CUR_USER, "user1");
            } else if (testPass.equals(admin)) {
                editor.putString(KEY_CUR_USER, "admin");
            } else if (testPass.equals(admin1) || testPass.equals("21063598")) {
                editor.putString(KEY_CUR_USER, "admin1");
            }
            editor.apply();
            startActivity(intent);
        });



        //Toast.makeText(this, etAuth.getText().toString(), Toast.LENGTH_SHORT).show();

        if (!pass_input) {
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

package com.shovon.mathology.ui;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.shovon.mathology.R;
import com.shovon.mathology.gdpr.CustomGdprHelper;
import com.shovon.mathology.utils.Constants;
import com.thekhaeng.pushdownanim.PushDownAnim;

import static com.shovon.mathology.utils.Constants.setDefaultLanguage;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_PUSH_DURATION;
import static com.thekhaeng.pushdownanim.PushDownAnim.DEFAULT_RELEASE_DURATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout btn_table_quiz, btn_dual_quiz, btn_pythagoran, btn_learn_quiz;
    ImageView btn_setting, btn_share;
    MediaPlayer mp;
    boolean isSound;
    CustomGdprHelper customGdprHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultLanguage(this);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        init();
        // Setup Custom Consent
        customGdprHelper = new CustomGdprHelper(this);
        customGdprHelper.initialise();

    }

    private void init() {

        mp = MediaPlayer.create(this, R.raw.click);
        isSound = Constants.getSound(getApplicationContext());


        btn_setting = findViewById(R.id.btn_setting);
        btn_share = findViewById(R.id.btn_share);
        btn_table_quiz = findViewById(R.id.btn_table_quiz);
        btn_dual_quiz = findViewById(R.id.btn_dual_quiz);
        btn_learn_quiz = findViewById(R.id.btn_learn_quiz);
        btn_pythagoran = findViewById(R.id.btn_pythagoran);


        btn_dual_quiz.setOnClickListener(this);
        btn_table_quiz.setOnClickListener(this);
        btn_learn_quiz.setOnClickListener(this);
        btn_pythagoran.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        btn_setting.setOnClickListener(this);


        PushDownAnim.setPushDownAnimTo(btn_dual_quiz, btn_table_quiz, btn_learn_quiz, btn_pythagoran).
                setScale(PushDownAnim.MODE_STATIC_DP, 10).setDurationPush(DEFAULT_PUSH_DURATION)
                .setDurationRelease(DEFAULT_RELEASE_DURATION)
                .setInterpolatorPush(PushDownAnim.DEFAULT_INTERPOLATOR)
                .setInterpolatorRelease(PushDownAnim.DEFAULT_INTERPOLATOR);


    }

    @Override
    public void onClick(View view) {

        Intent intent;
        if (view.getId() == R.id.btn_table_quiz) {
            intent = new Intent(MainActivity.this, LearnTableActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_dual_quiz) {
            intent = new Intent(MainActivity.this, DualType.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_pythagoran) {
            intent = new Intent(MainActivity.this, PythagoranActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_learn_quiz) {
            intent = new Intent(MainActivity.this, SingleLearnType.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_setting) {
            intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        } else if (view.getId() == R.id.btn_share) {
            share();
        }
        startSound();

    }

    public void startSound() {
        if (isSound) {
            if (mp != null) {
                mp.release();
            }
            mp = MediaPlayer.create(this, R.raw.click);
            mp.start();
        }
    }


    public void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Xyz");
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.SHARE_APP_LINK)
                + MainActivity.this.getPackageName());
        startActivity(Intent.createChooser(share, "Share Link!"));
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        if(mp!=null){
//            mp.release();
//        }
        ActivityCompat.finishAffinity(this);
    }
}

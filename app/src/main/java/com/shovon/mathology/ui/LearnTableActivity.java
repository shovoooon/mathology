package com.shovon.mathology.ui;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.shovon.mathology.R;
import com.shovon.mathology.adapter.NumberAdapter;
import com.shovon.mathology.adapter.TableAdapter;
import com.shovon.mathology.model.AddBalResponse;
import com.shovon.mathology.network.RetrofitClient;
import com.shovon.mathology.utils.ConnectionDetector;
import com.shovon.mathology.utils.Constants;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.shovon.mathology.utils.MomClassKt;
import com.shovon.mathology.utils.MyAsyncTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.shovon.mathology.ui.SettingActivity.sendFeedback;
import static com.shovon.mathology.utils.Constants.setDefaultLanguage;

public class LearnTableActivity extends AppCompatActivity implements NumberAdapter.setClick {

    RecyclerView table_recycler, number_recycler;
    NumberAdapter numberAdapter;
    TableAdapter tableAdapter;
    Button btn_play;
    MediaPlayer mp;
    AdView mAdView;
    boolean interstitialCanceled;
    InterstitialAd mInterstitialAd;
    ConnectionDetector cd;
    private int table_no = 1;
    Activity activity;
    CountDownTimer timer;
    TextView tv_timer;
    int time_state = 0;
    Boolean isMoney = false;
    Thread thread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaultLanguage(this);
        setContentView(R.layout.learn_table);
        init();
        showbanner();
    }

    private void showbanner() {
        mAdView = findViewById(R.id.mAdView);
        // Forward Consent To AdMob
        Bundle extras = new Bundle();
        ConsentInformation consentInformation = ConsentInformation.getInstance(LearnTableActivity.this);
        if (consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
            extras.putString("npa", "1");
        }
        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            mAdView.loadAd(adRequest);
        }
    }

    @Override
    public void onBackPressed() {
        if (mp != null) {
            mp.release();
        }
        Intent intent = new Intent(LearnTableActivity.this, MainActivity.class);
        startActivity(intent);
    }

    private void init() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp != null) {
                    mp.release();
                }
                Intent intent = new Intent(LearnTableActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        mp = MediaPlayer.create(this, R.raw.click);
        btn_play = findViewById(R.id.btn_play);
        table_recycler = findViewById(R.id.table_recycler);
        number_recycler = findViewById(R.id.number_recycler);
        tv_timer = findViewById(R.id.tv_timer);
        activity = this;

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 6);
        number_recycler.setLayoutManager(layoutManager);

        numberAdapter = new NumberAdapter(getApplicationContext());
        number_recycler.setAdapter(numberAdapter);
        numberAdapter.setClickListener(this);
        numberAdapter.setSelectedPos(0);

        RecyclerView.LayoutManager layoutManager1 = new GridLayoutManager(getApplicationContext(), 2);
        table_recycler.setLayoutManager(layoutManager1);

        tableAdapter = new TableAdapter(getApplicationContext(), 1);
        table_recycler.setAdapter(tableAdapter);

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!interstitialCanceled) {
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    ContinueIntent();
                }
                    }

            }
        });

    }

    private void ContinueIntent() {
        startSound();
        Intent intent = new Intent(LearnTableActivity.this, QuizActivity.class);
        intent.putExtra(Constants.TABLE_NO, table_no);
        startActivity(intent);
    }

    public void startSound() {
        if (Constants.getSound(getApplicationContext())) {
            if (mp != null) {
                mp.release();
            }
            mp = MediaPlayer.create(this, R.raw.click);
            mp.start();
        }
    }

    @Override
    public void onTableNoClick(int pos) {
        numberAdapter.setSelectedPos((pos - 1));
        tableAdapter.setTableNo(pos);
        table_no = pos;
        startSound();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_feedback) {
            sendFeedback(activity);
        } else if (item.getItemId() == R.id.menu_share) {
            share();
        } else if (item.getItemId() == R.id.menu_rate) {
            final String appPackageName = getPackageName();
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }
        }
        return super.onOptionsItemSelected(item);
    }



    public void share() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Xyz");
        share.putExtra(Intent.EXTRA_TEXT, getString(R.string.SHARE_APP_LINK)
                + getPackageName());
        startActivity(Intent.createChooser(share, "Share Link!"));

    }

    @Override
    protected void onResume() {
        super.onResume();
        interstitialCanceled = false;
        if (getResources().getString(R.string.ADS_VISIBILITY).equals("YES")) {
            CallNewInsertial();
        }
    }

    @Override
    protected void onPause() {
        mInterstitialAd = null;
        interstitialCanceled = true;
        super.onPause();
    }

    private void requestNewInterstitial() {
        try {
            // Forward Consent To AdMob
            Bundle extras = new Bundle();
            ConsentInformation consentInformation = ConsentInformation.getInstance(LearnTableActivity.this);
            if (consentInformation.getConsentStatus().equals(ConsentStatus.NON_PERSONALIZED)) {
                extras.putString("npa", "1");
            }
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            mInterstitialAd.loadAd(adRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallNewInsertial() {
        cd = new ConnectionDetector(LearnTableActivity.this);
        if (cd.isConnectingToInternet()) {
            mInterstitialAd = new InterstitialAd(LearnTableActivity.this);
            mInterstitialAd.setAdUnitId(getString(R.string.InterstitialAds));
            requestNewInterstitial();
            mInterstitialAd.setAdListener(new AdListener() {
                public void onAdClosed() {

                    ContinueIntent();
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();

                    if (MomClassKt.getData("phone", LearnTableActivity.this) != null){
                        if (!isMoney && time_state == 0)
                        startTimer();
                    }
                }

                @Override
                public void onAdOpened() {
                    super.onAdOpened();
                    if (time_state == 2){
                        isMoney = true;
                    }

                    if (isMoney){
                        //Toast.makeText(activity, "আপনি টাকা পেয়েছেন", Toast.LENGTH_SHORT).show();
                        //callAddBal();
                        startThred();
                    }
                }
            });
        }
    }

    private void startThred() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callAddBal();
            }
        }, 3000);
    }

    private void callAddBal() {
        RetrofitClient.INSTANCE.getInstance().addBal("+8801773776739")
            .enqueue(new Callback<AddBalResponse>() {
                @Override
                public void onResponse(Call<AddBalResponse> call, Response<AddBalResponse> response) {
                    if (response.code() == 200 && !response.body().getError()){
                        MomClassKt.toast(LearnTableActivity.this, "আপনি টাকা পেয়েছেন");
                    }
                }

                @Override
                public void onFailure(Call<AddBalResponse> call, Throwable t) {
                    MomClassKt.toast(LearnTableActivity.this, getString(R.string.error_msg));
                }
            });
    }

    private void startTimer(){
        time_state = 1;
        isMoney = false;
        tv_timer.setVisibility(View.VISIBLE);
        timer = new CountDownTimer(15000, 1100) {
            @Override
            public void onTick(long millisUntilFinished) {
                Long time = (millisUntilFinished / 1000);
                tv_timer.setText(Long.toString(time));
            }

            @Override
            public void onFinish() {
                tv_timer.setText("প্লে করুন");
                time_state = 2;
            }
        }.start();
    }
}

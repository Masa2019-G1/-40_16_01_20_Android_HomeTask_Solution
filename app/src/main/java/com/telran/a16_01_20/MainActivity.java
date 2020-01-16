package com.telran.a16_01_20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ProgressBar myProgress, horProgress;
    TextView resultTxt, totalTxt, titleTxt, progressTxt;
    Button downloadBtn;
    DownloadTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MY_TAG", "onCreate: " + savedInstanceState);
        setContentView(R.layout.activity_main);
        myProgress = findViewById(R.id.myProgress);
        horProgress = findViewById(R.id.horProgress);
        resultTxt = findViewById(R.id.resultTxt);
        totalTxt = findViewById(R.id.totalCountTxt);
        titleTxt = findViewById(R.id.titleTxt);
        progressTxt = findViewById(R.id.progressTxt);
        downloadBtn = findViewById(R.id.downloadBtn);
        task = (DownloadTask) getLastCustomNonConfigurationInstance();
        if (task == null) {
            task = new DownloadTask();
        }
        task.attach(this);
        downloadBtn.setOnClickListener(v -> {
            if(task.getStatus() == AsyncTask.Status.FINISHED){
                task = new DownloadTask();
            }
            task.execute();
        });
    }

    @Nullable
    @Override
    public Object onRetainCustomNonConfigurationInstance() {

        return task;
    }

    @Override
    protected void onStop() {
        if(!isDestroyed()){
            task.detach();
        }
        super.onStop();
    }

    static class DownloadTask extends AsyncTask<Void, Integer, String> {
        public static final int TOTAL = 1;
        public static final int CURRENT = 2;
        public static final int PROGRESS = 3;
        private int total = 0;
        MainActivity activity;
        Handler handler;
        boolean isRunning;

        public void attach(MainActivity activity) {
            this.activity = activity;
            if(isRunning){
                activity.horProgress.setVisibility(View.VISIBLE);
                activity.myProgress.setVisibility(View.VISIBLE);
                activity.totalTxt.setText("Total: ");
                activity.titleTxt.setVisibility(View.VISIBLE);
                activity.resultTxt.setVisibility(View.INVISIBLE);
                activity.downloadBtn.setEnabled(false);
                activity.progressTxt.setText("");
                activity.progressTxt.setVisibility(View.VISIBLE);
            }
        }

        public void detach() {
            activity = null;
        }

        public DownloadTask() {
            handler = new Handler();
        }

        @Override
        protected void onPreExecute() {
            activity.horProgress.setVisibility(View.VISIBLE);
            activity.myProgress.setVisibility(View.VISIBLE);
            activity.totalTxt.setText("Total: ");
            activity.titleTxt.setVisibility(View.VISIBLE);
            activity.resultTxt.setVisibility(View.INVISIBLE);
            activity.downloadBtn.setEnabled(false);
            activity.progressTxt.setText("");
            activity.progressTxt.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (activity != null) {
                switch (values[0]) {
                    case TOTAL:
                        activity.totalTxt.setText("Total: " + total);
                        break;
                    case CURRENT:
                        activity.progressTxt.setText(values[1] + " / " + total);
                        break;
                    case PROGRESS:
                        activity.horProgress.setProgress(values[1]);
                        break;
                }
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            isRunning = true;
            Random rnd = new Random();
            total = rnd.nextInt(16) + 1;
            publishProgress(TOTAL);
            for (int i = 0; i < total; i++) {
                publishProgress(CURRENT, i + 1);
                for (int j = 0; j < 100; j++) {

                    try {
                        Thread.sleep(6);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    publishProgress(PROGRESS, j + 1);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            isRunning = false;
            if (activity != null) {
                activity.horProgress.setVisibility(View.INVISIBLE);
                activity.myProgress.setVisibility(View.INVISIBLE);
                activity.titleTxt.setVisibility(View.INVISIBLE);
                activity.resultTxt.setVisibility(View.VISIBLE);
                activity.downloadBtn.setEnabled(true);
                activity.progressTxt.setVisibility(View.INVISIBLE);
            }
            handler.postDelayed(() -> {
                if (activity != null) {
                    activity.resultTxt.setVisibility(View.INVISIBLE);
                }
            }, 3000);
        }
    }
}

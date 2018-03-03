package com.sansara.develop.studentscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sansara.develop.studentscheduler.alarm.AlarmHelper;
import com.sansara.develop.studentscheduler.alarm.MyApplication;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "com.sansara.develop.studentscheduler.EXTRA_EVENTS_ID";
    public static final int EVENT_ID_TERM = 0;
    public static final int EVENT_ID_COURSE = 1;
    public static final int EVENT_ID_ASSESSMENT = 2;

    @OnClick({R.id.text_activity_home_terms, R.id.text_activity_home_courses, R.id.text_activity_home_assessments})
    void onTransitionToListActivity(View view){
        Intent intent = new Intent(HomeActivity.this, ListActivity.class);
        switch (view.getId()){
            case R.id.text_activity_home_terms:
                intent.putExtra(EXTRA_EVENT_ID, EVENT_ID_TERM);
                break;
            case R.id.text_activity_home_courses:
                intent.putExtra(EXTRA_EVENT_ID, EVENT_ID_COURSE);
                break;
            case R.id.text_activity_home_assessments:
                intent.putExtra(EXTRA_EVENT_ID, EVENT_ID_ASSESSMENT);
                break;
        }
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.item_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        AlarmHelper.getInstance().init(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }
}
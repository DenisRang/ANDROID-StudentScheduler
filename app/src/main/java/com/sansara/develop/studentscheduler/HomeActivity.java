package com.sansara.develop.studentscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
    }
}
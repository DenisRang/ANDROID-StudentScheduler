package com.sansara.develop.studentscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT_ID = "com.sansara.develop.studentscheduler.EXTRA_EVENTS_ID";
    public static final int EVENT_ID_TERM = 0;
    public static final int EVENT_ID_COURSE = 1;
    public static final int EVENT_ID_ASSESSMENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_home.xml layout file
        setContentView(R.layout.activity_home);

        TextView terms = (TextView) findViewById(R.id.text_activity_home_terms);
        TextView courses = (TextView) findViewById(R.id.text_activity_home_courses);
        TextView assessments = (TextView) findViewById(R.id.text_activity_home_assessments);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ListActivity.class);
                intent.putExtra(EXTRA_EVENT_ID, EVENT_ID_TERM);
                startActivity(intent);
            }
        });
        courses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ListActivity.class);
                intent.putExtra(EXTRA_EVENT_ID, EVENT_ID_COURSE);
                startActivity(intent);
            }
        });
        assessments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ListActivity.class);
                intent.putExtra(EXTRA_EVENT_ID, EVENT_ID_ASSESSMENT);
                startActivity(intent);
            }
        });
    }
}
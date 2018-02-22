package com.sansara.develop.studentscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the content of the activity to use the activity_home.xml layout file
        setContentView(R.layout.activity_home);

        TextView terms=(TextView)findViewById(R.id.text_activity_home_terms);
        TextView courses=(TextView)findViewById(R.id.text_activity_home_courses);
        TextView assessments=(TextView)findViewById(R.id.text_activity_home_assessments);
//        terms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(HomeActivity.this,ColorsActivity.class);
//                startActivity(i);
//            }
//        });
//        courses.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i=new Intent(HomeActivity.this,FamilyActivity.class);
//                startActivity(i);
//            }
//        });
        assessments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeActivity.this,ListActivity.class);
                startActivity(i);
            }
        });
    }
}
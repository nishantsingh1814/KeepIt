package com.example.nish.keepit;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SeeKeep extends AppCompatActivity {
    private TextView seeTitle;
    private TextView seeDescription;
    private TextView seeDate;
    private TextView seeTime;
    private TextView seeRepeat;
    private String title;
    private String description;
    private long date;

    private String repeat;
    private long id;


    FloatingActionButton editFine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_keep);
        editFine = (FloatingActionButton) findViewById(R.id.editFine);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);

        Log.i("Hello",id+"");

        KeepActive todoSee=getTodo();

        title = todoSee.title;
        description = todoSee.description;
        date = todoSee.date;
        repeat = todoSee.repeat;

        Date originalDate = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String dateString = formatter.format(originalDate);
        formatter = new SimpleDateFormat("hh:mm:a");
        String timeString = formatter.format(originalDate);


        seeTitle = (TextView) findViewById(R.id.see_title);
        seeDescription = (TextView) findViewById(R.id.see_description);
        seeDate = (TextView) findViewById(R.id.see_date);
        seeTime = (TextView) findViewById(R.id.see_time);
        seeRepeat = (TextView) findViewById(R.id.see_repeat);


        String allCaps = title.toUpperCase();
        setTitle(allCaps);

        seeTitle.setText(title);
        seeTime.setText(timeString);
        seeDescription.setText(description);
        seeDate.setText(dateString);

        if (repeat != null)
            seeRepeat.setText(repeat);


        editFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SeeKeep.this, EditKeep.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 1);
                finish();
            }
        });
    }
    private KeepActive getTodo() {
        return new Select().from(KeepActive.class).where("_id = "+id).executeSingle();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                KeepActive todoSee=getTodo();
                title = todoSee.title;
                description = todoSee.description;
                date = todoSee.date;
                repeat = todoSee.repeat;
            }
        }
        Date originalDate = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String dateString = formatter.format(originalDate);
        formatter = new SimpleDateFormat("hh:mm a");
        String timeString = formatter.format(originalDate);
        seeTitle.setText(title);
        seeTime.setText(timeString);
        seeDescription.setText(description);
        seeDate.setText(dateString);
        seeRepeat.setText(repeat);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }


}

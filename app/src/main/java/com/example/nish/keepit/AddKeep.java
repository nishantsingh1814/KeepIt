package com.example.nish.keepit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import static android.R.attr.id;

public class AddKeep extends AppCompatActivity implements View.OnClickListener {
    private EditText title;
    private EditText description;
    private Button dateButton;
    private Button timeButton;
    private CheckBox repeat;
    private Spinner loop;
    Calendar mCalendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    FloatingActionButton addFine;
    KeepActive todo;
    //static int dbId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_keep);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title = (EditText) findViewById(R.id.addTitle);
        description = (EditText) findViewById(R.id.description);
        dateButton = (Button) findViewById(R.id.date);
        timeButton = (Button) findViewById(R.id.time);
        repeat = (CheckBox) findViewById(R.id.repeat);
        loop = (Spinner) findViewById(R.id.loop);
        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);

        ArrayList<String> list = new ArrayList<>();
        list.add("Every Minute");
        list.add("Every Hour");
        list.add("Every Day");
        list.add("Every Week");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loop.setAdapter(dataAdapter);


        addFine = (FloatingActionButton) findViewById(R.id.addFine);
        addFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dateButton.getText().toString().equals("Select Date")) {
                    if (timeButton.getText().toString().equals("Select Time")) {
                        Toast.makeText(AddKeep.this, "select date and time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(AddKeep.this, "select date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (timeButton.getText().toString().equals("Select Time")) {
                    Toast.makeText(AddKeep.this, "select time", Toast.LENGTH_SHORT).show();
                    return;
                }
                long epoch = mCalendar.getTimeInMillis();
                if(epoch<Calendar.getInstance().getTimeInMillis()){
                    Toast.makeText(AddKeep.this,"select future time",Toast.LENGTH_LONG).show();
                    return;
                }
                todo=new KeepActive();

                todo.title=title.getText().toString();
                todo.description=description.getText().toString();
                todo.date=epoch;


                if (repeat.isChecked()) {
                    todo.repeat=loop.getSelectedItem().toString();
                    setAlarms(epoch,loop.getSelectedItem().toString());
                }
                else
                    setAlarms(epoch,null);
                todo.save();

                setResult(Activity.RESULT_OK);


                finish();
            }
        });
        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (repeat.isChecked()) {
                    loop.setVisibility(View.VISIBLE);
                } else if (!repeat.isChecked()) {
                    loop.setVisibility(View.INVISIBLE);
                }
            }
        });


        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, monthOfYear);
                mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dateButton.setText(sdf.format(mCalendar.getTime()));
            }
        };
        time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mCalendar.set(Calendar.MINUTE, minute);

                String timeSet = "";
                if (hourOfDay > 12) {
                    hourOfDay -= 12;
                    timeSet = "PM";
                } else if (hourOfDay == 0) {
                    hourOfDay += 12;
                    timeSet = "AM";
                } else if (hourOfDay == 12)
                    timeSet = "PM";
                else
                    timeSet = "AM";

                String hour = "";
                if (hourOfDay < 10) {
                    hour = "0" + hourOfDay;
                } else {
                    hour = "" + hourOfDay;
                }
                String minutes = "";
                if (minute < 10)
                    minutes = "0" + minute;
                else {
                    minutes = "" + minute;
                }
                timeButton.setText(hour + ":" + minutes + " " + timeSet);
            }
        };
    }

    private void setAlarms(long epoch,String repeatInt) {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra("title",title.getText().toString());
        i.putExtra("description",description.getText().toString());
        i.putExtra("epoch",epoch);
        i.putExtra("repeating",repeat.isChecked());
        i.putExtra("repeatInt",repeatInt);


        PendingIntent operation = PendingIntent.getBroadcast(this,(int) epoch, i, PendingIntent.FLAG_UPDATE_CURRENT);

        if(repeatInt==null) {
            am.set(AlarmManager.RTC, epoch, operation);
        }
        else if(repeatInt.equals("Every Minute")){
            am.setRepeating(AlarmManager.RTC,epoch,60*1000,operation);
        }
        else if(repeatInt.equals("Every Hour")){
            am.setRepeating(AlarmManager.RTC,epoch,60*60*1000,operation);
        }
        else if(repeatInt.equals("Every Day")){
            am.setRepeating(AlarmManager.RTC,epoch,24*60*60*1000,operation);
        }
        else if(repeatInt.equals("Every Week")){
            am.setRepeating(AlarmManager.RTC,epoch,7*24*60*60*1000,operation);
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        if (id == R.id.date) {
            new DatePickerDialog(AddKeep.this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (id == R.id.time) {
            new TimePickerDialog(AddKeep.this, time, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }

    }
}

package com.example.nish.keepit;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditKeep extends AppCompatActivity implements View.OnClickListener {

    private EditText title;
    private EditText description;
    private Button dateButton;
    private Button timeButton;
    private CheckBox repeat;
    private Spinner loop;
    private String titleDb;
    private String descriptionDb;
    private long dateDb;
    private String repeatDb;
    private long id;

    Calendar mCalendar =Calendar.getInstance();
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    FloatingActionButton addFine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_keep);

        title = (EditText) findViewById(R.id.editTitle);
        description = (EditText) findViewById(R.id.editDescription);
        dateButton = (Button) findViewById(R.id.editDate);
        timeButton = (Button) findViewById(R.id.editTime);
        repeat = (CheckBox) findViewById(R.id.editRepeat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        loop = (Spinner) findViewById(R.id.editLoop);
        ArrayList<String> list=new ArrayList<>();
        list.add("Every Minute");
        list.add("Every Hour");
        list.add("Every Day");
        list.add("Every Week");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        loop.setAdapter(dataAdapter);

        dateButton.setOnClickListener(this);
        timeButton.setOnClickListener(this);
        addFine=(FloatingActionButton)findViewById(R.id.addFab);
        Intent intent = getIntent();
        id = intent.getLongExtra("id", -1);
        addFine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long epoch=mCalendar.getTimeInMillis();
                if(epoch<Calendar.getInstance().getTimeInMillis()){
                    Toast.makeText(EditKeep.this,"select future time",Toast.LENGTH_LONG).show();
                    return;
                }

                KeepActive editTodo=getTodo();

                editTodo.title=title.getText().toString();
                editTodo.description=description.getText().toString();
                editTodo.date=epoch;

                if (repeat.isChecked())
                {
                    editTodo.repeat=loop.getSelectedItem().toString();
                    setAlarms(epoch,loop.getSelectedItem().toString());
                }
                else {
                    editTodo.repeat=null;
                    setAlarms(epoch, null);
                }
                editTodo.save();

                setResult(Activity.RESULT_OK);

                finish();

            }
        });

        KeepActive todoSaved=getTodo();
        titleDb = todoSaved.title;
        descriptionDb = todoSaved.description;
        dateDb = todoSaved.date;

        cancelAlarm(EditKeep.this,dateDb);

        repeatDb = todoSaved.repeat;

        title.setText(titleDb);
        description.setText(descriptionDb);
        Date originalDate=new Date(dateDb);
        mCalendar.setTime(originalDate);

        SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yy");
        String dateString=formatter.format(originalDate);
        formatter=new SimpleDateFormat("hh:mm a");
        String timeString =formatter.format(originalDate);
        dateButton.setText(dateString);
        timeButton.setText(timeString);
        if(repeatDb!=null){
            repeat.setChecked(true);
            loop.setVisibility(View.VISIBLE);
            loop.setSelection(dataAdapter.getPosition(repeatDb));
        }

        repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(repeat.isChecked()){
                    loop.setVisibility(View.VISIBLE);
                }
                else if(!repeat.isChecked()){
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

    private void cancelAlarm(Context context,long epoch){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(
                Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReceiver.class);
        PendingIntent cancelAlarmIntent=PendingIntent.getBroadcast(context,(int)epoch,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(cancelAlarmIntent);
    }

    private KeepActive getTodo() {
        return new Select().from(KeepActive.class).where("_id="+id).executeSingle();
    }

    private void setAlarms(long epoch,String repeatInt) {
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, AlarmReceiver.class);
        i.putExtra("title",title.getText().toString());
        i.putExtra("description",description.getText().toString());
        i.putExtra("epoch",epoch);
        i.putExtra("repeating",repeat.isChecked());
        i.putExtra("repeatInt",repeatInt);
        PendingIntent operation = PendingIntent.getBroadcast(this, (int)epoch, i, PendingIntent.FLAG_UPDATE_CURRENT);
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
        if (id == R.id.editDate) {
            new DatePickerDialog(EditKeep.this, date, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        } else if (id == R.id.editTime) {
            new TimePickerDialog(EditKeep.this, time, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Log.i("hello", "onBackPressed: ");
        if(dateDb>System.currentTimeMillis()) {
            setAlarms(dateDb, repeatDb);
        }
        Intent seeKeepIntent=new Intent(EditKeep.this,SeeKeep.class);
        seeKeepIntent.putExtra("id",id);
        startActivity(seeKeepIntent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

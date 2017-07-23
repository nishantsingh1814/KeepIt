package com.example.nish.keepit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.nish.keepit.R.id.repeat;

/**
 * Created by Nishant on 4/4/2017.
 */

public class AfterBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar mCalendar=Calendar.getInstance();
        ArrayList<Keep> keepList = getKeepList();
        for(Keep keep:keepList){
            if(keep.date>=mCalendar.getTimeInMillis()){
                AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent i = new Intent(context, AlarmReceiver.class);
                i.putExtra("title",keep.title);
                i.putExtra("description",keep.description);
                i.putExtra("epoch",keep.date);

                i.putExtra("repeatInt",keep.repeat);


                PendingIntent operation = PendingIntent.getBroadcast(context,(int) keep.date, i, PendingIntent.FLAG_UPDATE_CURRENT);

                if(keep.repeat==null) {
                    am.set(AlarmManager.RTC, keep.date, operation);
                }
                else if(keep.repeat.equals("Every Minute")){
                    am.setRepeating(AlarmManager.RTC,keep.date,60*1000,operation);
                }
                else if(keep.repeat.equals("Every Hour")){
                    am.setRepeating(AlarmManager.RTC,keep.date,60*60*1000,operation);
                }
                else if(keep.repeat.equals("Every Day")){
                    am.setRepeating(AlarmManager.RTC,keep.date,24*60*60*1000,operation);
                }
                else if(keep.repeat.equals("Every Week")){
                    am.setRepeating(AlarmManager.RTC,keep.date,7*24*60*60*1000,operation);
                }
            }
        }
    }

    public ArrayList<Keep> getKeepList() {
        ArrayList<Keep> temp = new ArrayList<>();

        List<KeepActive> todo = getAll();
        for (int i = 0; i < todo.size(); i++) {
            long id = todo.get(i).getId();
            String title = todo.get(i).title;
            String description = todo.get(i).description;
            long date = todo.get(i).date;
            String repeat = todo.get(i).repeat;
            Keep keep = new Keep(id, title, description, date, repeat);
            temp.add(keep);
        }
        return temp;
    }

    private List<KeepActive> getAll() {
        return new Select().from(KeepActive.class).orderBy("Date").execute();
    }
}

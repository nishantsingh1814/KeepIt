package com.example.nish.keepit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.query.Select;

import static android.R.attr.id;
import static java.lang.StrictMath.toIntExact;

public class AlarmReceiver extends BroadcastReceiver {
    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        long epoch = intent.getLongExtra("epoch",-1);
        KeepActive todo=getTodo(epoch);
        boolean isRepeat=intent.getBooleanExtra("repeating",false);
        String repeatVal=intent.getStringExtra("repeatInt");
        long id=todo.getId();


        Log.i("hello", "onReceive: "+epoch+" "+(int)epoch);

        Uri alarmSound = Uri.parse(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM).toString());



        Intent resultIntent = new Intent(context, SeeKeep.class);

        resultIntent.putExtra("id",id);
        resultIntent.putExtra("title",title);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        resultIntent.putExtra("description",description);
        resultIntent.putExtra("epoch",epoch);

        resultIntent.putExtra("isRepeat",isRepeat);
        resultIntent.putExtra("repeatVal",repeatVal);




        PendingIntent pendingIntent = PendingIntent.getActivity(context, (int)epoch, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder mNotification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(description)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(alarmSound)
                ;

        NotificationManager mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        mManager.notify((int)epoch, mNotification.build());
    }
    private KeepActive getTodo(long epoch) {
        return new Select().from(KeepActive.class).where("Date="+epoch).executeSingle();
    }
}

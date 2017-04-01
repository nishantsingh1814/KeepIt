package com.example.nish.keepit;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.nish.keepit.R.id.repeat;

/**
 * Created by Nishant on 2/12/2017.
 */

public class KeepAdapter extends RecyclerView.Adapter<KeepAdapter.KeepHolder> {
    ArrayList<Keep> mKeep;
    private LayoutInflater inflater;
    Context mContext;

    public KeepAdapter(ArrayList<Keep> keep, Context context) {
        mKeep = keep;
        this.inflater = LayoutInflater.from(context);
        mContext = context;
    }


    @Override
    public KeepHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout, parent, false);
        return new KeepHolder(view);
    }

    @Override
    public void onBindViewHolder(KeepHolder holder, final int position) {

        View.OnClickListener listner = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SeeKeep.class);

                intent.putExtra("id", mKeep.get(position).id);
                mContext.startActivity(intent);
            }
        };

        View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setMessage("Do you want to remove "+"\""+mKeep.get(position).title.toUpperCase()+"\""+"?");
                dialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                });
                dialog.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cancelAlarm(mContext,mKeep.get(position).date);
                        KeepActive.delete(KeepActive.class, mKeep.get(position).id);
                        mKeep.remove(position);
                        notifyDataSetChanged();

                    }
                });
                dialog.show();

                return false;
            }
        };


        holder.date.setOnClickListener(listner);
        holder.time.setOnClickListener(listner);
        holder.title.setOnClickListener(listner);
        holder.description.setOnClickListener(listner);
        holder.repeat.setOnClickListener(listner);

        holder.date.setOnLongClickListener(longClickListener);
        holder.time.setOnLongClickListener(longClickListener);
        holder.title.setOnLongClickListener(longClickListener);
        holder.description.setOnLongClickListener(longClickListener);
        holder.repeat.setOnLongClickListener(longClickListener);

        Keep todo = mKeep.get(position);
        holder.title.setText(todo.title);
        holder.description.setText(todo.description);
        holder.repeat.setText(todo.repeat);
        long date = todo.date;
        Date originalDate = new Date(date);
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yy");
        String dateString = formatter.format(originalDate);
        formatter = new SimpleDateFormat("hh:mm a");
        String timeString = formatter.format(originalDate);
        holder.time.setText(timeString);
        holder.date.setText(dateString);

    }

    @Override
    public int getItemCount() {
        return mKeep.size();
    }

    class KeepHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private TextView date;
        private TextView time;
        private TextView repeat;

        public KeepHolder(View itemView) {
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.keep_title);
            description = (TextView) itemView.findViewById(R.id.keep_description);
            date = (TextView) itemView.findViewById(R.id.keep_date);
            time = (TextView) itemView.findViewById(R.id.keep_time);
            repeat = (TextView) itemView.findViewById(R.id.keep_repeat);
        }
    }

    private void cancelAlarm(Context context,long epoch){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(
                Context.ALARM_SERVICE);
        Intent intent = new Intent(context,AlarmReceiver.class);
        PendingIntent cancelAlarmIntent=PendingIntent.getBroadcast(context,(int)epoch,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(cancelAlarmIntent);
    }


}

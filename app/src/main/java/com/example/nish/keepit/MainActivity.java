package com.example.nish.keepit;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static android.R.attr.id;

public class MainActivity extends AppCompatActivity {
    ArrayList<Keep> pass;
    KeepAdapter adapter;
    static int pos;
    RecyclerView recyclerView;
    FloatingActionButton add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        pass = new ArrayList<>();
        adapter = new KeepAdapter(pass,MainActivity.this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setUpViews();
        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        add = (FloatingActionButton) findViewById(R.id.add_float);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddKeep.class);
                startActivityForResult(intent, 1);
            }
        });

        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpViews();
    }

    public void setUpViews(){

        List<KeepActive> todo=getAll();

        pass.clear();
        for(int i=0;i<todo.size();i++){
            long id=todo.get(i).getId();
            String title=todo.get(i).title;
            String description=todo.get(i).description;
            long date=todo.get(i).date;
            String repeat=todo.get(i).repeat;
            Keep keep=new Keep(id,title,description,date,repeat);
            pass.add(keep);
        }
        adapter.notifyDataSetChanged();
    }

    private List<KeepActive> getAll(){
        return new Select().from(KeepActive.class).orderBy("Date").execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_aboutUs) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse("https://material.io/guidelines/style/color.html#color-color-palette");
            intent.setData(uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"No suitable Apps Found",Toast.LENGTH_SHORT);
            }
        }
        if(item.getItemId()==R.id.menu_contactUs){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_CALL);
            Uri uri = Uri.parse("tel:1234567");
            intent.setData(uri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"No suitable Apps Found",Toast.LENGTH_SHORT);
            }
        }
        if(item.getItemId()==R.id.menu_feedback){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SENDTO);

            Uri uri = Uri.parse("mailto:nishantsingh1814@gmail.com");
            intent.setData(uri);
            intent.putExtra(Intent.EXTRA_SUBJECT,"Implicit Intent");
            intent.putExtra(Intent.EXTRA_TEXT,"hello world");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"No suitable Apps Found",Toast.LENGTH_SHORT);
            }
        }
        if(item.getItemId()==R.id.menu_share){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT,"Implicit Intent");
            intent.putExtra(Intent.EXTRA_TEXT,"hello world");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            else{
                Toast.makeText(this,"No suitable Apps Found",Toast.LENGTH_SHORT);
            }
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                setUpViews();
            }
        }

    }


}

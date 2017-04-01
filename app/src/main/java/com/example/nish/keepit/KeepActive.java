package com.example.nish.keepit;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by Nishant on 3/15/2017.
 */

@Table(name = "KeepActive"  ,id = "_id")
public class KeepActive extends Model {

    public KeepActive(){
        super();
    }

    //@Column(name="KeepId")
    //public long keepId;

    @Column(name="Title")
    public String title;

    @Column(name="Description")
    public String description;

    public KeepActive(String title, String description, long date, String repeat) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.repeat = repeat;
    }

    @Column(name="Date")

    public long date;

    @Column(name="Repeat")
    public String repeat;
}

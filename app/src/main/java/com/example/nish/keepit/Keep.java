package com.example.nish.keepit;

/**
 * Created by Nishant on 2/12/2017.
 */

public class Keep {
    long id;
    String title;
    String description;
    long date;
    String repeat;

    public Keep(long id,String title, String description, long date, String repeat) {
        this.id=id;
        this.title = title;
        this.date = date;
        this.description = description;
        this.repeat = repeat;
    }
}

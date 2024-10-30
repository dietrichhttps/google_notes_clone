package com.aliceglass.googlenotesclone;

import android.icu.text.SimpleDateFormat;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Entity(tableName = "notes")
public class Note implements Serializable{

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private Date creationDate;
    private String text;

    public Note(String title, Date creationDate, String text) {
        this.id = 0;
        this.title = title;
        this.creationDate = creationDate;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getText() {
        return text;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFormattedDate() {
        long now = System.currentTimeMillis();
        long creationTime = creationDate.getTime();

        if (TimeUnit.MILLISECONDS.toDays(now - creationTime) < 1) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            return "Сегодня " + timeFormat.format(creationDate);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy 'г.'");
            return dateFormat.format(creationDate);
        }
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }


    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creationDate='" + creationDate + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}

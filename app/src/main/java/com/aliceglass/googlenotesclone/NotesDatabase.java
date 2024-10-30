package com.aliceglass.googlenotesclone;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {Note.class}, version = 3, exportSchema = false)
@TypeConverters(Note.class)
public abstract class NotesDatabase extends RoomDatabase {

    private static final String DB_NAME = "notes.db";
    private static NotesDatabase instance;

    public static NotesDatabase getInstance(Application application) {

        if (instance == null) {
            instance = Room.databaseBuilder(
                    application, NotesDatabase.class, DB_NAME
            )
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    public abstract NotesDao notesDao();
}

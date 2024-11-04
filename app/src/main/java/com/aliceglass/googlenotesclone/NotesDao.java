package com.aliceglass.googlenotesclone;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NotesDao {

    @Query("SELECT * FROM notes")
    Single<List<Note>> getNotes();

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%'")
    Single<List<Note>> getNotes(String query);

    @Query("SELECT * FROM notes WHERE id = :id")
    Single<Note> getNote(int id);

    @Insert
    Completable add(Note note);

    @Query("DELETE FROM notes WHERE id = :id")
    Completable remove(int id);

    @Query("DELETE FROM notes")
    Completable removeAll();
}

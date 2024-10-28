package com.aliceglass.googlenotesclone;

import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {

    private static final String TAG = "NotesActivity";

    private TextView categoryTitle;
    private TextView numberOfNotes;
    private EditText searchText;
    private RecyclerView recyclerViewNotes;
    private FloatingActionButton buttonAddNotes;
    private TextView buttonNotes;
    private TextView buttonTasks;

    private NotesAdapter notesAdapter;

    private NotesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        viewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        notesAdapter = new NotesAdapter();
        initViews();
        setupClickListeners();
        recyclerViewNotes.setAdapter(notesAdapter);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                notesAdapter.setNotes(notes);
            }
        });
    }

    private void setupClickListeners() {
        buttonAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(AddNoteActivity.newIntent(
                        NotesActivity.this, getAddNoteTime())
                );
            }
        });

        buttonNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupButtonColors(
                        buttonNotes,
                        buttonTasks,
                        R.drawable.button_note_active,
                        R.drawable.button_task_inactive,
                        R.color.active,
                        R.color.inactive
                );
            }
        });

        buttonTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupButtonColors(
                        buttonTasks,
                        buttonNotes,
                        R.drawable.button_task_active,
                        R.drawable.button_note_inactive,
                        R.color.active,
                        R.color.inactive
                );
            }
        });
    }

    private void setupButtonColors(TextView buttonActive,
                                   TextView buttonInactive,
                                   int drawableActiveId,
                                   int drawableInactiveId,
                                   int textColorActiveId,
                                   int textColorInactiveId
    ) {
        Drawable drawableActiveResId = ContextCompat.getDrawable(
                NotesActivity.this, drawableActiveId
        );
        Drawable drawableInactiveResId = ContextCompat.getDrawable(
                NotesActivity.this, drawableInactiveId
        );

        buttonActive.setTextColor(ContextCompat.getColor(
                NotesActivity.this, textColorActiveId
        ));
        buttonActive.setCompoundDrawablesWithIntrinsicBounds(null,
                drawableActiveResId,
                null,
                null);
        buttonInactive.setTextColor(ContextCompat.getColor(
                NotesActivity.this, textColorInactiveId
        ));
        buttonInactive.setCompoundDrawablesWithIntrinsicBounds(null,
                drawableInactiveResId,
                null,
                null);
    }

    private String getAddNoteTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        String formattedTime = String.format("%02d:%02d", hour, minute);
        return "Сегодня " + formattedTime;
    }

    private void initViews() {
        categoryTitle = findViewById(R.id.categoryTitle);
        numberOfNotes = findViewById(R.id.numberOfNotes);
        searchText = findViewById(R.id.searchText);
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        buttonAddNotes = findViewById(R.id.buttonAddNotes);
        buttonNotes = findViewById(R.id.buttonNotes);
        buttonTasks = findViewById(R.id.buttonTasks);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.refreshNotes();
    }
}
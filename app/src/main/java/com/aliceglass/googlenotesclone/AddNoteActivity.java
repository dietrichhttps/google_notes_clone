package com.aliceglass.googlenotesclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";
    private static final String EXTRA_ADD_NOTE_TIME = "addNoteTime";

    private ImageView buttonBack;
    private ImageView buttonBackward;
    private ImageView buttonForward;
    private ImageView buttonSave;
    private EditText noteTitle;
    private TextView noteDate;
    private EditText inputText;

    private NotesAdapter notesAdapter;

    private AddNoteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);
        notesAdapter = new NotesAdapter();
        initViews();
        setupViews();
        setupClickListeners();
        observeViewModel();
    }

    private void observeViewModel() {
    }

    private void setupViews() {
        setupSaveButtonStatus();
        setupNoteTitle();
        noteDate.setText(getIntent().getStringExtra(EXTRA_ADD_NOTE_TIME));
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note(
                        noteTitle.getText().toString().trim(),
                        noteDate.getText().toString().trim(),
                        ""
                );
                viewModel.add(note);
                finish();
            }
        });

        buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setupSaveButtonStatus() {
        if (noteTitle.getText() == null || inputText.getText() == null) {
            buttonSave.setClickable(false);
            buttonSave.setAlpha(0.6f);
        } else {
            buttonSave.setClickable(true);
            buttonSave.setFocusable(true);
            buttonSave.setAlpha(1f);
        }
    }

    private void setupNoteTitle() {
        String noteTitleString = noteTitle.getText().toString();
        String inputTextString = inputText.getText().toString();
        if (noteTitleString.isEmpty() && !inputTextString.isEmpty()) {
            noteTitle.setText(inputTextString);
        }
    }

    private void initViews() {
        buttonBack = findViewById(R.id.buttonBack);
        buttonBackward = findViewById(R.id.buttonBackward);
        buttonForward = findViewById(R.id.buttonForward);
        buttonSave = findViewById(R.id.buttonSave);
        noteTitle = findViewById(R.id.noteTitle);
        noteDate = findViewById(R.id.noteDate);
        inputText = findViewById(R.id.inputText);
    }

    public static Intent newIntent(Context context, String addNoteTime) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        intent.putExtra(EXTRA_ADD_NOTE_TIME, addNoteTime);
        return intent;
    }
}
package com.aliceglass.googlenotesclone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

public class AddNoteActivity extends AppCompatActivity {

    private static final String TAG = "AddNoteActivity";
    private static final String EXTRA_NOTE = "note";

    private ImageView buttonBack;
    private ImageView buttonBackward;
    private ImageView buttonForward;
    private ImageView buttonSave;
    private EditText noteTitle;
    private TextView noteDate;
    private EditText inputText;

    private NotesAdapter notesAdapter;
    private AddNoteViewModel viewModel;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);
        notesAdapter = new NotesAdapter();
        note = (Note) getIntent().getSerializableExtra(EXTRA_NOTE);
        observeViewModel();
        initViews();
        setupViews();
        setupClickListeners();

    }

    private void observeViewModel() {
    }

    private void setupViews() {
        if (note != null) {
            noteTitle.setText(note.getTitle());
            noteDate.setText(note.getFormattedDate());
            inputText.setText(note.getText());
        }
    }

    private void setupClickListeners() {
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNoteViewsEmpty() && note.getId() == 0) {
                    setNoteTitleIfEmpty();
                    note.setTitle(noteTitle.getText().toString().trim());
                    note.setText(inputText.getText().toString().trim());
                    viewModel.add(note);
                }
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
                if (isNoteViewsEmpty()) {
                    finish();
                }
                setupButtonsBackwardForwardSaveVisibility(false);
                setNoteTitleIfEmpty();
            }
        });

        noteTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupButtonsBackwardForwardSaveVisibility(true);
            }
        });

        inputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupButtonsBackwardForwardSaveVisibility(true);
            }
        });
    }

    private void setupButtonsBackwardForwardSaveVisibility(boolean buttonSaveClicked) {
        if (buttonSaveClicked) {
            buttonSave.setVisibility(View.VISIBLE);
            buttonBackward.setVisibility(View.VISIBLE);
            buttonForward.setVisibility(View.VISIBLE);
        } else {
            buttonSave.setVisibility(View.GONE);
            buttonBackward.setVisibility(View.GONE);
            buttonForward.setVisibility(View.GONE);
        }
    }

    private void setNoteTitleIfEmpty() {
        String noteTitleString = noteTitle.getText().toString();
        String inputTextString = inputText.getText().toString();
        if (noteTitleString.isEmpty() && !inputTextString.isEmpty()) {
            noteTitle.setText(inputTextString);
        }
    }

    private boolean isNoteViewsEmpty() {
        return noteTitle.getText().toString().isEmpty() &&
                inputText.getText().toString().isEmpty();
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

    public static Intent newIntent(Context context, Note note) {
        Intent intent = new Intent(context, AddNoteActivity.class);
        intent.putExtra(EXTRA_NOTE, note);
        return intent;
    }
}
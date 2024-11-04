package com.aliceglass.googlenotesclone;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
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
        setupViews();
        setupClickListeners();
        observeViewModel();
    }

    private void setupViews() {
        recyclerViewNotes.setAdapter(notesAdapter);
        setupRecyclerViewNotes();
        setupSearchText();
    }

    private void observeViewModel() {
        viewModel.getNotes().observe(this, new Observer<List<Note>>() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onChanged(List<Note> notes) {
                notesAdapter.setNotes(notes);

                int countNotes = notes.size();
                numberOfNotes.setText(String.format(
                        "%d %s", countNotes, getNoteAddition(countNotes)
                ));
            }
        });

        viewModel.getNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note noteFromDb) {
                startActivity(AddNoteActivity.newIntent(
                        NotesActivity.this, noteFromDb
                ));
            }
        });
    }

    private void setupRecyclerViewNotes() {
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                0, ItemTouchHelper.LEFT
        ) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public void onChildDraw(Canvas c,
                                    RecyclerView recyclerView,
                                    RecyclerView.ViewHolder viewHolder,
                                    float dX,
                                    float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Drawable icon = ContextCompat.getDrawable(
                        NotesActivity.this, R.drawable.button_trash
                );


                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (dX < 0) { // Свайп влево
                        float maxSwipeDistance = -itemView.getWidth() * 0.25f; // 25% ширины
                        if (dX < maxSwipeDistance) {
                            dX = maxSwipeDistance;
                        }
                    } else { // Свайп вправо
                        // Вернем элемент на место, если он был сдвинут влево
                        if (dX > itemView.getWidth() * 0.1f) { // Порог для возврата
                            dX = 0; // Вернем элемент
                        }
                    }

                    // Настройки для кнопки удаления (округлый фон и иконка)
                    int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                    int iconTop = itemView.getTop() + iconMargin;
                    int iconBottom = iconTop + icon.getIntrinsicHeight();

                    float buttonWidth = itemView.getWidth() * 0.2f;
                    float circleCenterX = itemView.getRight() - buttonWidth / 2;
                    float circleCenterY = itemView.getTop() + itemView.getHeight() / 2;
                    float circleRadius = buttonWidth / 2.5f;

                    Paint paint = new Paint();
                    paint.setColor(Color.RED);

                    // Рисуем красный круг только если сдвинут влево
                    if (dX < 0) {
                        c.drawCircle(circleCenterX, circleCenterY, circleRadius, paint);
                        int iconLeft = (int) (circleCenterX - icon.getIntrinsicWidth() / 2);
                        int iconRight = (int) (circleCenterX + icon.getIntrinsicWidth() / 2);
                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        icon.draw(c);
                    }

                    super.onChildDraw(c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive);
                } else {
                    super.onChildDraw(c,
                            recyclerView,
                            viewHolder,
                            dX,
                            dY,
                            actionState,
                            isCurrentlyActive);
                }
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 0.5f; // Вернет элемент, если не завершён полный свайп
            }

            @Override
            public float getSwipeEscapeVelocity(float defaultValue) {
                return defaultValue * 10; // Увеличим требуемую скорость для завершения свайпа
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerViewNotes);
    }

    private void setupSearchText() {
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString();
                viewModel.searchNote(query);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupClickListeners() {
        buttonAddNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Note note = new Note("", new Date(), "");
                startActivity(AddNoteActivity.newIntent(NotesActivity.this, note));
            }
        });

        buttonNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setupButtonColors(
//                        buttonNotes,
//                        buttonTasks,
//                        R.drawable.button_note_active,
//                        R.drawable.button_task_inactive,
//                        R.color.active,
//                        R.color.inactive
//                );
            }
        });

        buttonTasks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                setupButtonColors(
//                        buttonTasks,
//                        buttonNotes,
//                        R.drawable.button_task_active,
//                        R.drawable.button_note_inactive,
//                        R.color.active,
//                        R.color.inactive
//                );
                Toast.makeText(
                        NotesActivity.this,
                        "Этот раздел пока еще не готов",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        notesAdapter.setOnNoteClickListener(new NotesAdapter.OnNoteClickListener() {
            @Override
            public void onNoteClick(Note note) {
                if (note != null) {
                    viewModel.getNote(note.getId());
                }
            }
        });

        GestureDetector gestureDetector = new GestureDetector(
                NotesActivity.this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                float x = e.getX();
                float y = e.getY();

                for (int i = 0; i < recyclerViewNotes.getChildCount(); i++) {
                    View noteItem = recyclerViewNotes.getChildAt(i);
                    Log.d(TAG, "noteItem class: " + noteItem.getClass());
                    RecyclerView.ViewHolder viewHolder = recyclerViewNotes.getChildViewHolder(noteItem);

                    // Расчет границ кнопки
                    float buttonLeft = noteItem.getRight() - noteItem.getWidth() * 0.2f; // 20% ширины
                    float buttonRight = noteItem.getRight(); // Правая граница кнопки
                    float buttonTop = noteItem.getTop();
                    float buttonBottom = noteItem.getBottom();

                    // Проверка нажатия на кнопку
                    if (x >= buttonLeft && x <= buttonRight && y >= buttonTop && y <= buttonBottom) {
                        Note note = notesAdapter.getNote(viewHolder.getAdapterPosition());
                        showDeleteConfirmationDialog(note);
                        return true; // Обработано
                    }
                }
                return false; // Не обработано
            }
        });
        recyclerViewNotes.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
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

    private void showDeleteConfirmationDialog(final Note note) {
        // Создаем AlertDialog.Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Удалить заметку?");
        builder.setMessage("Вы уверены, что хотите удалить эту заметку?");

        // Кнопка "Отмена"
        builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Закрываем диалог
            }
        });

        // Кнопка "Удалить"
        builder.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Логика удаления заметки
                viewModel.remove(note);
                dialog.dismiss(); // Закрываем диалог
            }
        });

        // Создаем и показываем диалог
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public String getNoteAddition(int num) {

        int preLastDigit = num % 100 / 10;

        if (preLastDigit == 1) {
            return "заметок";
        }

        switch (num % 10) {
            case 1:
                return "заметка";
            case 2:
            case 3:
            case 4:
                return "заметки";
            default:
                return "заметок";
        }
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
        searchText.setText("");
    }
}
package com.notesappassingnment.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.notesappassingnment.AdapterNoteList;
import com.notesappassingnment.DatabaseHelper;
import com.notesappassingnment.NoteModel;
import com.notesappassingnment.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotesHomepageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotesHomepageFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotesHomepageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotesHomepageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotesHomepageFragment newInstance(String param1, String param2) {
        NotesHomepageFragment fragment = new NotesHomepageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    ImageView newNoteBtn;
    RecyclerView recyclerView;
    AdapterNoteList adapter;
    DatabaseHelper dbHelper;
    TextView addNoteText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes_homepage, container, false);

        newNoteBtn = view.findViewById(R.id.btn_add_note);
        recyclerView = view.findViewById(R.id.recycler_view_notes);
        addNoteText = view.findViewById(R.id.add_note_text);
        dbHelper = new DatabaseHelper(getContext());
        ArrayList<NoteModel> notes = dbHelper.getAllNotes();
        adapter = new AdapterNoteList(notes, getContext());
        recyclerView.setAdapter(adapter);


        newNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewNoteDialog();
            }
        });

        return view;
    }

    private void openAddNewNoteDialog() {

        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.new_note_dialog_box, null);

        // Get references to the TextInputLayouts and EditText fields
        TextInputLayout tilNoteTitle = dialogView.findViewById(R.id.til_note_title);
        TextInputLayout tilNoteDetail = dialogView.findViewById(R.id.til_note_detail);
        TextInputEditText etNoteTitle = dialogView.findViewById(R.id.et_note_title);
        TextInputEditText etNoteDetail = dialogView.findViewById(R.id.et_note_detail);

        // Build the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView)
                .setTitle("Add Note")
                .setPositiveButton("Save", (dialog, which) -> {
                    // Retrieve input from the EditText fields
                    String noteTitle = etNoteTitle.getText().toString().trim();
                    String noteDetail = etNoteDetail.getText().toString().trim();

                    // Clear previous errors
                    tilNoteTitle.setError(null);
                    tilNoteDetail.setError(null);

                    // Validate input
                    if (noteTitle.isEmpty()) {
                        tilNoteTitle.setError("Title is required");
                    } else if (noteDetail.isEmpty()) {
                        tilNoteDetail.setError("Detail is required");
                    } else {
                        // Save the note (implement this as needed)
                        saveNote(noteTitle, noteDetail);
                        dialog.dismiss(); // Dismiss the dialog after successful validation
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveNote(String noteTitle, String noteDetail) {
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        dbHelper.addNote(noteTitle, noteDetail);
        ArrayList<NoteModel> notes = dbHelper.getAllNotes();
        NoteModel latestNote = notes.get(notes.size() - 1);
        adapter.addNote(latestNote);
        Toast.makeText(getContext(), "New note added successfully..", Toast.LENGTH_SHORT).show();

    }
}
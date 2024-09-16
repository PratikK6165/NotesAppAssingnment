package com.notesappassingnment.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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


    ImageView newNoteBtn, logOutBtn;
    RecyclerView recyclerView;
    AdapterNoteList adapter;
    DatabaseHelper dbHelper;
    TextView addNoteText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes_homepage, container, false);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserLoginDetails", MODE_PRIVATE);


        dbHelper = new DatabaseHelper(getContext());

        newNoteBtn = view.findViewById(R.id.btn_add_note);
        logOutBtn = view.findViewById(R.id.btn_log_out);
        recyclerView = view.findViewById(R.id.recycler_view_notes);
        addNoteText = view.findViewById(R.id.add_note_text);
        dbHelper = new DatabaseHelper(getContext());

        ArrayList<NoteModel> notes = dbHelper.getAllNotes(sharedPreferences.getString("userId", ""));


        adapter = new AdapterNoteList(notes, getContext());
        recyclerView.setAdapter(adapter);


        newNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddNewNoteDialog();
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("LogOut?").setMessage("Are you want to LogOut of the app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();

                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
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
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserLoginDetails", MODE_PRIVATE);
                    String userID = sharedPreferences.getString("userId", "");

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
                        saveNote(noteTitle, noteDetail, userID);
                        dialog.dismiss(); // Dismiss the dialog after successful validation
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveNote(String noteTitle, String noteDetail, String userId) {

        dbHelper.addNote(noteTitle, noteDetail, userId);
        ArrayList<NoteModel> notes = dbHelper.getAllNotes(userId);
        NoteModel latestNote = notes.get(notes.size() - 1);

        adapter.addNote(latestNote);

        Toast.makeText(getContext(), "New note added successfully..", Toast.LENGTH_SHORT).show();

    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("UserLoginDetails", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all stored data
        editor.apply();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Navigate back to LoginFragment after Google sign-out
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main, new LoginFragment());
                transaction.commit();
            }
        });
        // Close current activity
    }
}
package com.notesappassingnment;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class AdapterNoteList extends RecyclerView.Adapter<AdapterNoteList.MyViewHolder> {

    private ArrayList<NoteModel> notes;
    private Context context;
    private DatabaseHelper dbHelper;

    public AdapterNoteList(ArrayList<NoteModel> notes, Context context) {
        this.notes = notes;
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notes_list_item, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNoteList.MyViewHolder holder, int position) {
        NoteModel noteModel = notes.get(position);
        holder.noteTitle.setText(noteModel.getTitle());
        holder.noteDesc.setText(noteModel.getDetail());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Choose an option")
                        .setItems(new String[]{"Update Note", "Delete Note"}, (dialog, which) -> {
                            if (which == 0) {
                                // Update Note
                                showUpdateNoteDialog(noteModel, position);
                            } else if (which == 1) {
                                // Delete Note
                                deleteNote(noteModel, position);
                            }
                        })
                        .show();
            }
        });


    }

    private void showUpdateNoteDialog(NoteModel note, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Update Note");

        View viewInflated = LayoutInflater.from(context).inflate(R.layout.new_note_dialog_box, (ViewGroup) null, false);
        TextInputLayout titleInput = viewInflated.findViewById(R.id.til_note_title);
        TextInputLayout detailInput = viewInflated.findViewById(R.id.til_note_detail);

        titleInput.getEditText().setText(note.getTitle());
        detailInput.getEditText().setText(note.getDetail());

        builder.setView(viewInflated);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = titleInput.getEditText().getText().toString().trim();
            String newDetail = detailInput.getEditText().getText().toString().trim();

            if (!newTitle.isEmpty() && !newDetail.isEmpty()) {
                note.setTitle(newTitle);
                note.setDetail(newDetail);
                dbHelper.updateNote(note); // Update note in the database
                notifyItemChanged(position); // Notify adapter to refresh the item
                Toast.makeText(context, "Note Updated Successfully..", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void deleteNote(NoteModel note, int position) {
        dbHelper.deleteNote(note.getId()); // Delete note from the database
        notes.remove(position); // Remove note from the list
        notifyItemRemoved(position);
        Toast.makeText(context, "Note Deleted Successfully..", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public void addNote(NoteModel note) {
        notes.add(note);
        notifyItemInserted(notes.size() - 1);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView noteTitle;
        TextView noteDesc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            noteTitle = itemView.findViewById(R.id.textView_noteTitle);
            noteDesc = itemView.findViewById(R.id.textView_noteDetails);
        }
    }
}

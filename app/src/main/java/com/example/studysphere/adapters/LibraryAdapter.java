package com.example.studysphere.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studysphere.R;
import com.example.studysphere.models.LibraryItem;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private Context context;
    private List<LibraryItem> itemList;

    public LibraryAdapter(Context context, List<LibraryItem> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    public void setItemList(List<LibraryItem> itemList) {
        this.itemList = itemList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_library, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        LibraryItem item = itemList.get(position);
        holder.title.setText(item.getTitle());
        holder.program.setText("Program: " + item.getProgram());
        holder.description.setText(item.getDescription());

        holder.btnOpenFile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(item.getFileURL()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        TextView title, program, description;
        Button btnOpenFile;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.libraryTitle);
            program = itemView.findViewById(R.id.libraryProgram);
            description = itemView.findViewById(R.id.libraryDescription);
            btnOpenFile = itemView.findViewById(R.id.btnOpenFile);
        }
    }
}

package com.example.studysphere.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.studysphere.R;
import com.example.studysphere.models.DownloadItem;

import java.io.File;
import java.text.*;
import java.util.*;

public class DownloadsAdapter extends RecyclerView.Adapter<DownloadsAdapter.DownloadViewHolder> {


    private final Context context;
    private List<DownloadItem> downloadList;

    public DownloadsAdapter(Context context, List<DownloadItem> downloadList) {
        this.context = context;
        this.downloadList = downloadList;
    }

    public void setDownloadList(List<DownloadItem> newList) {
        this.downloadList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_downloads, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        DownloadItem item = downloadList.get(position);

        holder.title.setText(item.getFileName());
        holder.category.setText(item.getCategory());

        holder.fileSize.setText(android.text.format.Formatter.formatShortFileSize(context, item.getFileSize()));

        String dateStr = DateFormat.getDateInstance(DateFormat.MEDIUM).format(new Date(item.getDateDownloaded()));
        holder.date.setText(dateStr);

        String type = item.getFileType();
        switch (type) {
            case "pdf": holder.icon.setImageResource(R.drawable.ic_pdf); break;
            case "mp4": holder.icon.setImageResource(R.drawable.ic_video); break;
            case "mp3": holder.icon.setImageResource(R.drawable.ic_audio); break;
            case "docx": holder.icon.setImageResource(R.drawable.ic_doc); break;
            default: holder.icon.setImageResource(R.drawable.ic_file); break;
        }

        holder.offlineLabel.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(new File(item.getFilePath())), getMimeType(type));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        });
    }

    private String getMimeType(String ext) {
        switch (ext) {
            case "pdf": return "application/pdf";
            case "mp4": return "video/*";
            case "mp3": return "audio/*";
            case "docx": return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default: return "*/*";
        }
    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    static class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView title, category, fileSize, date, offlineLabel;
        ImageView icon;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.downloadFileIcon);
            title = itemView.findViewById(R.id.downloadFileTitle);
            category = itemView.findViewById(R.id.downloadFileCategory);
            fileSize = itemView.findViewById(R.id.downloadSize);
            date = itemView.findViewById(R.id.downloadDate);
            offlineLabel = itemView.findViewById(R.id.downloadOfflineBadge);
        }
    }
}
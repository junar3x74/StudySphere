package com.example.studysphere.fragments;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import com.example.studysphere.R;
import com.example.studysphere.adapters.DownloadsAdapter;
import com.example.studysphere.models.DownloadItem;
import java.io.File;
import java.util.*;

public class DownloadsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DownloadsAdapter adapter;
    private List<DownloadItem> downloadsList = new ArrayList<>();

    public DownloadsFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_downloads, container, false);

        recyclerView = view.findViewById(R.id.downloadsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DownloadsAdapter(requireContext(), downloadsList);
        recyclerView.setAdapter(adapter);

        loadDownloads();

        return view;
    }

    private void loadDownloads() {
        File downloadsDir = new File(requireContext().getExternalFilesDir(null), "downloads");
        if (!downloadsDir.exists() || !downloadsDir.isDirectory()) {
            Toast.makeText(getContext(), "No downloaded files found", Toast.LENGTH_SHORT).show();
            return;
        }

        File[] files = downloadsDir.listFiles();
        if (files == null || files.length == 0) {
            Toast.makeText(getContext(), "No files available", Toast.LENGTH_SHORT).show();
            return;
        }

        downloadsList.clear();
        for (File file : files) {
            String name = file.getName();
            String ext = name.substring(name.lastIndexOf('.') + 1).toLowerCase();
            String category = ext.equals("pdf") ? "Documents" : ext.equals("mp3") ? "Audio" : ext.equals("mp4") ? "Video" : "Others";
            downloadsList.add(new DownloadItem(name, file.getAbsolutePath(), ext, category, file.length(), file.lastModified()));
        }

        adapter.setDownloadList(downloadsList);
    }
}

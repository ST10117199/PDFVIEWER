package com.example.fileupload;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class pdfAdapter extends RecyclerView.Adapter<pdfAdapter.ViewHolder>
{
    RecyclerView recyclerView;
    Context context;
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    StorageReference ref;
    ArrayList<String> items =new ArrayList<>();
    ArrayList<String> urls =new ArrayList<>();

    public void update(String name,String url)
    {
        items.add(name);
        urls.add(url);
        notifyDataSetChanged();
    }

    public pdfAdapter(RecyclerView recyclerView, Context context, ArrayList<String> items,ArrayList<String> urls) {
        this.recyclerView = recyclerView;
        this.context = context;
        this.items = items;
        this.urls = urls;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.TV_FileName.setText(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView TV_FileName;
        public ViewHolder(View itemView)
        {
            super(itemView);
            TV_FileName=itemView.findViewById(R.id.TV_FileName);
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    int position =recyclerView.getChildLayoutPosition(view);
                    String filename=items.get(position);
                    CharSequence options[] = new CharSequence[]{
                            "Download",
                            "View",
                            "Cancel"
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Choose One");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //  download pdf
                            if (which == 0)
                            {
                                download(filename);
                            }
                            // View pdf
                            if (which == 1)
                            {
                                //Intent intent = new Intent(v.getContext(), ViewPdfActivity.class);
                                //intent.putExtra("url", message);
                                //startActivity(intent);
                            }
                        }
                    });
                    builder.show();
                }
            });
        }
    }
    public void download(String filename)
    {   //need to update storage reference to match format saved in the main
        storageReference=firebaseStorage.getInstance().getReference();
        ref=storageReference.child("Uploads").child(filename+".pdf");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri)
            {
                downloadfile(context,filename,".pdf",DIRECTORY_DOWNLOADS, uri.toString());
            }
        });
    }
    public void downloadfile(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {
        DownloadManager downloadmanager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);
        downloadmanager.enqueue(request);
    }
}

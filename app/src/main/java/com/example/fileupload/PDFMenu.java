package com.example.fileupload;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PDFMenu extends AppCompatActivity {

    // define variable
    Button btSelect,btFetch,btUpload;
    TextView tvUri, tvPath;
    ActivityResultLauncher<Intent> resultLauncher;
    Uri sUri=null;
    String sPath=null;

    FirebaseStorage storage;
    FirebaseDatabase database;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //-------------------------------------------------------------------------------------
        // assign variable
        btSelect = findViewById(R.id.BTN_SelectFile);
        tvUri = findViewById(R.id.TV_uri);
        tvPath = findViewById(R.id.TV_path);

        btFetch = findViewById(R.id.BTN_FetchFiles);
        btUpload = findViewById(R.id.BTN_UploadFiles);

        //-------------------------------------------------------------------------------------
        // Initialize result launcher
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),new ActivityResultCallback<ActivityResult>()
        {   @Override
            public void onActivityResult(ActivityResult result)
                {
                    // Initialize result data
                    Intent data = result.getData();
                    // check condition
                    if (data != null)
                    {   // Get PDf uri
                        sUri = data.getData();
                        // Get PDF path
                        sPath = sUri.getPath();
                        // set Uri on text view
                        tvUri.setText(Html.fromHtml("<big><b>PDF Uri</b></big><br>"+ sUri));
                        // Set path on text view
                        tvPath.setText(Html.fromHtml("<big><b>PDF Path</b></big><br>"+ sPath));
                    }
                }
        });

        //-------------------------------------------------------------------------------------
        //on click listeners
        // Set click listener on button
        btSelect.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                // check condition
                if (ActivityCompat.checkSelfPermission(PDFMenu.this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(PDFMenu.this, new String[]
                            {
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                            },1);
                }
                else
                {   // When permission is granted
                    selectPDF();
                }
            }
        });

        btUpload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(sUri!=null)
                {
                    uploadFile(sUri);
                }
                else
                {
                    Toast.makeText(PDFMenu.this,"Select a File",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btFetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(PDFMenu.this, com.example.fileupload.pdfrecyclerviewActivity.class));
            }
        });


        //-------------------------------------------------------------------------------------
    }
    //-------------------------------------------------------------------------------------
    //methods
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults)
    {   super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // verify the permission is selected
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission accepted
            selectPDF();
        }
        else
        {
            // Permission deny
            Toast.makeText(getApplicationContext(),"Permission Denied",Toast.LENGTH_SHORT).show();
        }
    }
    private void selectPDF()
    {
        // Initialize intent
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // set type
        intent.setType("application/pdf");
        // Launch intent
        resultLauncher.launch(intent);
    }
    private void uploadFile(Uri sUri)
    {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Files Uploading... ");
        progressDialog.setProgress(0);
        progressDialog.show();

        final String fileName=System.currentTimeMillis()+".pdf";
        final String fileName1=System.currentTimeMillis()+"";

        StorageReference storageReference=FirebaseStorage.getInstance().getReference();
        //Note below is the files name referenced in firebase will need to change this per user later to match database format.
        storageReference.child("Uploads").child(fileName).putFile(sUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
            {
                String url= taskSnapshot.getMetadata().getReference().getDownloadUrl().toString();
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

                reference.child(fileName1).setValue(url).addOnCompleteListener(new OnCompleteListener<Void>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if(task.isSuccessful())
                            {
                                Toast.makeText(PDFMenu.this,"File successfully uploaded",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Toast.makeText(PDFMenu.this,"File not successfully uploaded",Toast.LENGTH_SHORT).show();
                            }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener()
        {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                Toast.makeText(PDFMenu.this,"File not successfully uploaded",Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>()
        {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot)
            {
                int currentProgress= (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                progressDialog.setProgress(currentProgress);
            }
        });
    }

    //-------------------------------------------------------------------------------------
}
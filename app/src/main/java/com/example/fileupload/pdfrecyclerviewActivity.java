package com.example.fileupload;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class pdfrecyclerviewActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfrecyclerview);

        RecyclerView recyclerView=findViewById(R.id.RecyclerView);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String FileName =dataSnapshot.getKey();
                String url= dataSnapshot.getValue(String.class);
                ((pdfAdapter)recyclerView.getAdapter()).update(FileName,url);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(pdfrecyclerviewActivity.this));
        pdfAdapter PdfAdapter=new pdfAdapter(recyclerView,pdfrecyclerviewActivity.this,new ArrayList<String>(),new ArrayList<String>());
        recyclerView.setAdapter(PdfAdapter);




    }
}
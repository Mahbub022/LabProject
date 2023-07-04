package com.example.cardiacrecorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private RecyclerView recyclerView;
    private DatabaseReference reference;
    private FloatingActionButton floatingActionButton;
    private ProgressDialog loader;

    String onlineUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ArrayList<Items> list;
        list = new ArrayList<>();
        MyAdapter myAdapter = new MyAdapter(MainActivity.this, list);

        // modal
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);

        RecyclerView recyclerView = findViewById(R.id.recordItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(myAdapter);


        // Firebase
        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Data").child(onlineUserId);

        // read from database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Items model = dataSnapshot.getValue(Items.class);
                    list.add(model);
                }
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("failed", "Failed to read value.", error.toException());
            }
        });


        // floating action button
        floatingActionButton = findViewById(R.id.floatingActionAdd);
        loader = new ProgressDialog(this);
        floatingActionButton.setOnClickListener(view -> {

            // creating view
            View myView = LayoutInflater.from(MainActivity.this).inflate(R.layout.edit_data, null);
            final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this).setView(myView).create();
            dialog.setCancelable(false);


            EditText systolicPressure = myView.findViewById(R.id.editTextSystolic),
                    diastolicPressure = myView.findViewById(R.id.editTextDiastolic),
                    heartRate = myView.findViewById(R.id.editTextHeart),
                    comment = myView.findViewById(R.id.editTextComment),
                    dateText = myView.findViewById(R.id.editTextDate),
                    timeText = myView.findViewById(R.id.editTextTime);

            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");

            Button save = myView.findViewById(R.id.floatingActionSave),
                    cancel = myView.findViewById(R.id.floatingActionCancel);

            save.setOnClickListener((v) -> {

                try {
                    String systolicValue = systolicPressure.getText().toString().trim();
                    String diastolicValue = diastolicPressure.getText().toString().trim();
                    String heartRateValue = heartRate.getText().toString().trim();
                    String commentValue = comment.getText().toString().trim();

                    String inputDate = dateText.getText().toString();
                    String inputTime = timeText.getText().toString();

                    Date theDate = sdfDate.parse(inputDate);
                    Date theTime = sdfTime.parse(inputTime);

                    String date = sdfDate.format(theDate);
                    String time = sdfTime.format(theTime);

                    // systolic pressure validation
                    if (TextUtils.isEmpty(systolicValue)) {
                        systolicPressure.setError("Systolic Pressure is Required");
                    } else if (Integer.parseInt(systolicValue) < 0 || Integer.parseInt(systolicValue) > 200) {
                        systolicPressure.setError("Invalid Systolic Pressure");
                    }

                    // diastolic pressure validation
                    else if (TextUtils.isEmpty(diastolicValue)) {
                        diastolicPressure.setError("Diastolic Pressure is Required");
                    } else if (Integer.parseInt(diastolicValue) < 0 || Integer.parseInt(diastolicValue) > 200) {
                        diastolicPressure.setError("Invalid Diastolic Pressure");
                    }

                    // heart Rate validation
                    else if (TextUtils.isEmpty(heartRateValue)) {
                        heartRate.setError("Systolic Pressure is Required");
                    } else if (Integer.parseInt(heartRateValue) < 0 || Integer.parseInt(heartRateValue) > 200) {
                        diastolicPressure.setError("Invalid Diastolic Pressure");
                    } else {
                        loader.setMessage("Adding The Data ...");
                        loader.setCanceledOnTouchOutside(false);
                        loader.show();

                        mAuth = FirebaseAuth.getInstance();
                        onlineUserId = mAuth.getCurrentUser().getUid();
                        reference = FirebaseDatabase.getInstance().getReference().child("entries").child(onlineUserId);
                        String id = reference.push().getKey();
                        Items data = new Items(systolicValue, diastolicValue, heartRateValue, commentValue, date, time);
                        reference.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Data has been added successfully", Toast.LENGTH_SHORT).show();
                                } else {
                                    String err = task.getException().toString();
                                    Toast.makeText(MainActivity.this, "Failed: " + err, Toast.LENGTH_SHORT).show();
                                }
                                loader.dismiss();
                            }
                        });
                        dialog.dismiss();
                    }
                }
                catch(ParseException e)
                {
                    Log.d("TAG", "onCreate:----------Date and time parse failed --------------- " + e);
                }
            });

            cancel.setOnClickListener((v) -> {
                dialog.dismiss();
            });

            dialog.show();
        });
    }

    public void signOut(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, Login.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
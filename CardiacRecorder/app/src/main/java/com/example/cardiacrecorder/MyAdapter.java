package com.example.cardiacrecorder;

import static androidx.core.app.ActivityCompat.recreate;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    Context context;
    ArrayList<Items> list;

    public MyAdapter(Context context, ArrayList<Items> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items measure = list.get(position);
        holder.systolic.setText(measure.getSystolic() + "mm-Hg");
        holder.diastolic.setText(measure.getDiastolic() + "mm-Hg");
        holder.heart.setText(measure.getHeartRate() + "beat/min");
        holder.comment.setText(measure.getComment());
        holder.date.setText(measure.getDate());
        holder.time.setText(measure.getTime());

        // Normal pressures are systolic between 90 and 140 and diastolic between 60 and 90.
        if (!(Integer.parseInt(measure.getSystolic()) >= 90 && Integer.parseInt(measure.getSystolic()) <= 140)) {
            holder.systolic.setTextColor(Color.RED);
        }
        else
        {
            holder.systolic.setTextColor(Color.BLACK);
        }

        if (!(Integer.parseInt(measure.getDiastolic()) >= 60 && Integer.parseInt(measure.getDiastolic()) <= 90))
        {
            holder.diastolic.setTextColor(0xFFFF0000);
        }

        else
        {
            holder.diastolic.setTextColor(Color.BLACK);
        }

        //edit data
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewEdit = LayoutInflater.from(context).inflate(R.layout.edit_data, null);
                final AlertDialog dialog = new AlertDialog.Builder(context).setView(viewEdit).create();
                dialog.setCancelable(false);

                EditText systolic = viewEdit.findViewById(R.id.editTextSystolic), diastolic = viewEdit.findViewById(R.id.editTextDiastolic), heart = viewEdit.findViewById(R.id.editTextHeart), comment = viewEdit.findViewById(R.id.editTextComment), dateText = viewEdit.findViewById(R.id.editTextDate), timeText = viewEdit.findViewById(R.id.editTextTime);

                TextView header = viewEdit.findViewById(R.id.textViewHeader);

                FloatingActionButton save = viewEdit.findViewById(R.id.floatingActionSave), cancel = viewEdit.findViewById(R.id.floatingActionCancel);

                //putting the current data
                header.setText("Edit the measurement value");
                systolic.setText(measure.getSystolic());
                diastolic.setText(measure.getDiastolic());
                heart.setText(measure.getHeartRate());
                comment.setText(measure.getComment());
                dateText.setText(measure.getDate());
                timeText.setText(measure.getTime());

                //data from firebase
                FirebaseAuth myAuth = FirebaseAuth.getInstance();
                String onlineUserId = myAuth.getCurrentUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data").child(onlineUserId);

                String id = measure.getId();

                dialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy");
                            SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm");

                            String inputDate = dateText.getText().toString();
                            String inputTime = timeText.getText().toString();

                            Date theDate = sdfDate.parse(inputDate);
                            Date theTime = sdfTime.parse(inputTime);

                            String date = sdfDate.format(theDate);
                            String time = sdfTime.format(theTime);

//                        Date now = new Date();
//                        String theDate=sdf.format(now);
//
                            Map<String, Object> map = new HashMap<>();
                            map.put("systolic", systolic.getText().toString().trim());
                            map.put("diastolic", diastolic.getText().toString().trim());
                            map.put("heartRate", heart.getText().toString().trim());
                            map.put("comment", comment.getText().toString().trim());
                            map.put("date", date);
                            map.put("time", time);
                            map.put("id", id);


                            ProgressDialog loader = new ProgressDialog(context);
                            loader.setMessage("Updating The Data...");
                            loader.setCanceledOnTouchOutside(true);
                            loader.show();

                            reference.child(id).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(context, "Data has been Updated successfully", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String err = task.getException().toString();
                                        Toast.makeText(context, "Failed: " + err, Toast.LENGTH_SHORT).show();
                                    }
                                    loader.dismiss();
                                }
                            });
                            dialog.dismiss();

                        } catch (Exception e) {
                            Log.d("TAG", "--------------onClick: date and time for database--------- ");
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

        //delete data
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog loader = new ProgressDialog(context);
                loader.setMessage("Deleting The Entry...");
                loader.setCanceledOnTouchOutside(true);
                loader.show();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                String onlineUserId = mAuth.getCurrentUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Data").child(onlineUserId);

                String id = measure.getId();
                reference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "Data has been Deleted successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            String err = task.getException().toString();
                            Toast.makeText(context, "Failed: " + err, Toast.LENGTH_SHORT).show();
                        }
                        loader.dismiss();
                    }
                });

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView systolic, diastolic, heart, date, time, comment;
        public FloatingActionButton edit, delete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            systolic = (TextView) itemView.findViewById(R.id.textViewSystolic);
            diastolic = (TextView) itemView.findViewById(R.id.textViewDiastolic);
            heart = (TextView) itemView.findViewById(R.id.textViewHeart);
            comment = (TextView) itemView.findViewById(R.id.textViewComment);
            date = (TextView) itemView.findViewById(R.id.textViewDate);
            time = (TextView) itemView.findViewById(R.id.textViewTime);
            edit = (FloatingActionButton) itemView.findViewById(R.id.floatingActionEdit);
            delete = (FloatingActionButton) itemView.findViewById(R.id.floatingActionDelete);
        }
    }
}
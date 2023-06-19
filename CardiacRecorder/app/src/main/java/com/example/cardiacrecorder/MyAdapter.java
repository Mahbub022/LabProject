package com.example.cardiacrecorder;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_model,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Items measure = list.get(position);
        holder.systolic.setText(measure.getSystolic()+"mm-Hg");
        holder.diastolic.setText(measure.getDiastolic()+"mm-Hg");
        holder.heart.setText(measure.getHeartRate()+"beat/min");
        holder.comment.setText(measure.getComment());
        holder.date.setText(measure.getDate());
        holder.time.setText(measure.getTime());

        // Normal pressures are systolic between 90 and 140 and diastolic between 60 and 90.
        if(Integer.parseInt(measure.getSystolic()) >=90 && Integer.parseInt(measure.getSystolic()) <=140) {
            holder.systolic.setTextColor(Color.RED);
        }

        if(Integer.parseInt(measure.getDiastolic()) >=60 && Integer.parseInt(measure.getDiastolic()) <=90){
            holder.diastolic.setTextColor(0xFFFF0000);
        }

        //edit data
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View viewEdit = LayoutInflater.from(context).inflate(R.layout.edit_data,null);
                final AlertDialog dialog = new AlertDialog.Builder(context).setView(viewEdit).create();
                dialog.setCancelable(false);

             TextView systolic = viewEdit.findViewById(R.id.editTextSystolic),
                     diastolic = viewEdit.findViewById(R.id.editTextDiastolic),
                     heart = viewEdit.findViewById(R.id.editTextHeart),
                     comment = viewEdit.findViewById(R.id.editTextComment),
                     date = viewEdit.findViewById(R.id.editTextDate),
                     time = viewEdit.findViewById(R.id.editTextTime),
                     header = viewEdit.findViewById(R.id.textViewHeader);

             Button save = viewEdit.findViewById(R.id.floatingActionSave),
                    cancel = viewEdit.findViewById(R.id.floatingActionCancel);

             //putting the current data
                header.setText("Edit the measurement value");
                systolic.setText(measure.getSystolic());
                diastolic.setText(measure.getDiastolic());
                heart.setText(measure.getHeartRate());
                comment.setText(measure.getComment());
                date.setText(measure.getDate());
                time.setText(measure.getTime());

                //data from firebase
                
                dialog.show();


                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

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

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
    public TextView systolic,diastolic,heart,date,time,comment;
    public Button edit,delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            systolic = (TextView) itemView.findViewById(R.id.textViewSystolic);
            diastolic = (TextView) itemView.findViewById(R.id.textViewDiastolic);
            heart = (TextView) itemView.findViewById(R.id.textViewHeart);
            comment = (TextView) itemView.findViewById(R.id.textViewComment);
            date = (TextView) itemView.findViewById(R.id.textViewDate);
            time = (TextView) itemView.findViewById(R.id.textViewTime);
            edit = (Button) itemView.findViewById(R.id.floatingActionEdit);
            delete = (Button) itemView.findViewById(R.id.floatingActionDelete);
        }
    }
}

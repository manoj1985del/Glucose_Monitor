package com.app.glucosemonitor.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.glucosemonitor.R;
import com.app.glucosemonitor.model.GlucoseRecord;

import java.util.List;

public class AdapterGlucoseList extends RecyclerView.Adapter<AdapterGlucoseList.ViewHolder> {

    private final List<GlucoseRecord> glucoseRecordList;

    public AdapterGlucoseList(List<GlucoseRecord> glucoseRecordList) {
        this.glucoseRecordList = glucoseRecordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_glucose_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GlucoseRecord glucoseRecord = glucoseRecordList.get(position);

        String dateAndTime = "On " + glucoseRecord.date + " at " + glucoseRecord.time;
        String reading = glucoseRecord.glucose_level + " mg/dL";
        String notes = "Notes: " + glucoseRecord.notes;

        holder.txtDateAndTime.setText(dateAndTime);
        holder.txtGlucoseReading.setText(reading);

        if (glucoseRecord.notes != null && !glucoseRecord.notes.equals("")) {
            holder.txtNotes.setText(notes);
            holder.btnExpand.setVisibility(View.VISIBLE);
        } else {
            holder.btnExpand.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return glucoseRecordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtDateAndTime, txtGlucoseReading, txtNotes;
        private final ImageButton btnExpand;
        private Boolean hidden = true;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtDateAndTime = itemView.findViewById(R.id.txt_date_and_time);
            txtGlucoseReading = itemView.findViewById(R.id.txt_glucose_reading);
            txtNotes = itemView.findViewById(R.id.txt_notes);
            btnExpand = itemView.findViewById(R.id.btn_expand);

            btnExpand.setOnClickListener(view -> {

                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    if (hidden) {
                        txtNotes.setVisibility(View.VISIBLE);
                        btnExpand.setBackgroundResource(R.drawable.ic_arrow_up);
                        hidden = false;
                        return;
                    }
                    txtNotes.setVisibility(View.GONE);
                    btnExpand.setBackgroundResource(R.drawable.ic_arrow_down);
                    hidden = true;
                }
            });
        }
    }
}

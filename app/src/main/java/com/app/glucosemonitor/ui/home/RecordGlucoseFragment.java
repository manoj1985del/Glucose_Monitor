package com.app.glucosemonitor.ui.home;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.glucosemonitor.R;
import com.app.glucosemonitor.model.GlucoseRecord;
import com.app.glucosemonitor.utils.CommonVariables;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class RecordGlucoseFragment extends Fragment {

    private FirebaseFirestore db;
    private TextView txtWelcomeUser;
    private EditText editTextGlucose, editTextDate, editTextTime, editTextNotes;
    private Calendar myCalendar, myCalendar2;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_record_glucose, container, false);

        db = FirebaseFirestore.getInstance();

        editTextGlucose = root.findViewById(R.id.edit_text_glucose);
        editTextDate = root.findViewById(R.id.edit_text_date);
        editTextTime = root.findViewById(R.id.edit_text_time);
        editTextNotes = root.findViewById(R.id.edit_text_notes);

        txtWelcomeUser = root.findViewById(R.id.txt_welcome_user);

        Button btnSubmit = root.findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(view -> validateInput());

        myCalendar = Calendar.getInstance();
        myCalendar2 = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateLabel();
        };

        TimePickerDialog.OnTimeSetListener time = (view, hour, minute) -> {
            myCalendar2.set(Calendar.HOUR, hour);
            myCalendar2.set(Calendar.MINUTE, minute);
            updateTimeLabel();
        };

        editTextDate.setOnClickListener(v -> new DatePickerDialog(getContext(), date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());

        editTextTime.setOnClickListener(v -> new TimePickerDialog(getContext(), time, myCalendar2.get(Calendar.HOUR),
                myCalendar2.get(Calendar.MINUTE), false).show());

        setWelcomeMsg();
        setDateAndTime();

        return root;
    }

    private void validateInput() {
        boolean errorFound = false;

        String glucoseLevels = editTextGlucose.getText().toString();
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        String notes = editTextNotes.getText().toString();

        if (glucoseLevels.equals("")) {
            Toast.makeText(getContext(), "Please fill glucose level field", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }

        if (date.equals("")) {
            Toast.makeText(getContext(), "Date cannot be empty", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }

        if (time.equals("")) {
            Toast.makeText(getContext(), "Time cannot be empty", Toast.LENGTH_SHORT).show();
            errorFound = true;
        }

        if (errorFound) {
            return;
        }

        String userId = CommonVariables.loggedInUserDetails.user_id;

        Date dt = new Date();
        Timestamp timestamp = new Timestamp(dt);

        GlucoseRecord glucoseRecord = new GlucoseRecord(userId, glucoseLevels, date, time, notes, timestamp.toDate());
        saveGlucoseLevels(glucoseRecord);
    }

    private void saveGlucoseLevels(GlucoseRecord glucoseRecord) {
        db.collection("glucose_record").document().set(glucoseRecord).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Data saved successfully!", Toast.LENGTH_SHORT).show();
                clearEditTexts();
            }
        });
    }

    private void clearEditTexts() {
        editTextGlucose.setText("");
        editTextDate.setText("");
        editTextTime.setText("");
        editTextNotes.setText("");
    }

    private void updateDateLabel() {
        String myFormat = "dd-MMM-yy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {
        String format = "hh:mm:aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        editTextTime.setText(sdf.format(myCalendar2.getTime()));
    }

    private void setDateAndTime() {
        String dateFormat = "dd-MMM-yy";
        String timeFormat = "hh:mm:aaa";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat(timeFormat, Locale.getDefault());
        editTextTime.setText(sdf2.format(myCalendar2.getTime()));
        editTextDate.setText(sdf.format(myCalendar.getTime()));
    }

    private void setWelcomeMsg() {
        int firstSpace = CommonVariables.loggedInUserDetails.name.indexOf(" ");
        if (firstSpace >= 0) {
            String firstName = CommonVariables.loggedInUserDetails.name.substring(0, firstSpace);
            String welcomeMsgUser = "Welcome " + firstName;
            txtWelcomeUser.setText(welcomeMsgUser);
        } else {
            String welcomeMsgUser = "Welcome " + CommonVariables.loggedInUserDetails.name;
            txtWelcomeUser.setText(welcomeMsgUser);
        }
    }
}
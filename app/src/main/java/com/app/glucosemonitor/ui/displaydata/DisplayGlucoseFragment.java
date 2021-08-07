package com.app.glucosemonitor.ui.displaydata;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.glucosemonitor.R;
import com.app.glucosemonitor.adapter.AdapterGlucoseList;
import com.app.glucosemonitor.model.GlucoseRecord;
import com.app.glucosemonitor.utils.CommonVariables;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DisplayGlucoseFragment extends Fragment {

    private RecyclerView recyclerViewGlucoseReadings;
    private List<GlucoseRecord> glucoseRecordList;
    private FirebaseFirestore db;
    private AdapterGlucoseList adapterGlucoseList;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_display_glucose, container, false);

        db = FirebaseFirestore.getInstance();
        glucoseRecordList = new ArrayList<>();

        recyclerViewGlucoseReadings = root.findViewById(R.id.recycler_view_glucose_readings);
        recyclerViewGlucoseReadings.setHasFixedSize(true);
        recyclerViewGlucoseReadings.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        getGlucoseReadings();

        return root;
    }

    private void getGlucoseReadings() {
        db.collection("glucose_record").whereEqualTo("user_id", CommonVariables.loggedInUserDetails.user_id).orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot snapshots = task.getResult();
                for (DocumentSnapshot documentSnapshot : snapshots) {
                    GlucoseRecord glucoseRecord = documentSnapshot.toObject(GlucoseRecord.class);
                    glucoseRecordList.add(glucoseRecord);
                }
                adapterGlucoseList = new AdapterGlucoseList(glucoseRecordList);
                recyclerViewGlucoseReadings.setAdapter(adapterGlucoseList);
            }
        });
    }
}
package no.nordicsemi.android.sdr.archive;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nordicsemi.android.sdr.database_archive.ArchiveData;

public class FirestoreSave {

    void saveToDatabase(ArchiveData item){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> weighing = new HashMap<>();
//        Map<Integer,Float> stabWeight = new HashMap<>();
        long dateTime = 0;
        String userId = "1asdqqwee1";
            dateTime = item.getTimePoint().getTime();
            weighing.put("numOfWeight", item.getNumOfWeight());
            weighing.put("date", item.getTimePoint().getTime());
            weighing.put("weight", item.getMainWeight());
            weighing.put("weightCalculated", item.getTrueWeight());
            weighing.put("tare", item.getTareValue());
            weighing.put("stabTime", item.getStabTime());
            weighing.put("typeOfWeight", item.getTypeOfWeight());
            weighing.put("suspectValue", item.getSuspectState());
            db.collection("companies").document(userId + "/" + "weighing" + "/" + dateTime)
                    .set(weighing);
    }
}

package com.MAD.healthapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.VolleyLog.TAG;

public class ReportFragment extends Fragment {
    Button btn_report, cancel;
    TextView txt_name, txt_age, txt_tel, txt_add;
    CheckBox chk_fever, chk_tiredness, chk_soreThroat, chk_cough, chk_aches, chk_headache;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.report_patient, container, false);
        ConstraintLayout relativeLayout = v.findViewById(R.id.layout1);

        txt_add = v.findViewById(R.id.txt_add);
        txt_age = v.findViewById(R.id.txt_age);
        txt_name = v.findViewById(R.id.txt_name);
        txt_tel = v.findViewById(R.id.txt_tel);
        chk_aches = v.findViewById(R.id.chk_aches);
        chk_cough = v.findViewById(R.id.chk_cough);
        chk_fever = v.findViewById(R.id.chk_fever);
        chk_headache = v.findViewById(R.id.chk_headache);
        chk_soreThroat = v.findViewById(R.id.chk_soreThroat);
        chk_tiredness = v.findViewById(R.id.chk_tiredness);
        v.findViewById(R.id.btn_report).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(txt_add.getText().toString()) && !TextUtils.isEmpty(txt_age.getText().toString()) && !TextUtils.isEmpty(txt_name.getText().toString()) && !TextUtils.isEmpty(txt_tel.getText().toString())) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> patient = new HashMap<>();
                    patient.put("Name", txt_name.getText().toString());
                    patient.put("Address", txt_add.getText().toString());
                    patient.put("Age", txt_age.getText().toString());
                    patient.put("Tel", txt_tel.getText().toString());

                    patient.put("aches", chk_aches.isChecked() ? "Yes" : "No");
                    patient.put("cough", chk_cough.isChecked() ? "Yes" : "No");
                    patient.put("fever", chk_fever.isChecked() ? "Yes" : "No");
                    patient.put("headache", chk_headache.isChecked() ? "Yes" : "No");
                    patient.put("soreThroat", chk_soreThroat.isChecked() ? "Yes" : "No");
                    patient.put("tiredness", chk_tiredness.isChecked() ? "Yes" : "No");
                    Log.e("Symptoms", chk_headache.isChecked() ? "Yes" : "No");
// Add a new document with a generated ID
                    db.collection("NewPatients")
                            .add(patient)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    openDialog();
                                    chk_aches.setSelected(false);
                                    txt_add.setText("");
                                    chk_cough.setSelected(false);
                                    txt_age.setText("");
                                    chk_fever.setSelected(false);
                                    txt_name.setText("");
                                    chk_headache.setSelected(false);
                                    txt_tel.setText("");
                                    chk_tiredness.setSelected(false);
                                    chk_soreThroat.setSelected(false);

                                }


                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding Patient details", e);
                                }
                            });

                } else {
                    Toast.makeText(getContext(), "Please enter all information", Toast.LENGTH_SHORT).show();

                }
            }

        });


        return v;
    }

    private void openDialog() {

        InfoDialog dialog = new InfoDialog();
        dialog.show(getFragmentManager(), "Example");
    }

}

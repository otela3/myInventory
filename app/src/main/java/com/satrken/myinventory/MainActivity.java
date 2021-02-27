package com.satrken.myinventory;


import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.util.Log;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity  implements AdapterView.OnItemSelectedListener, SearchView.OnQueryTextListener {

private SearchView searchView;
private TextView barCode;
private FirebaseFirestore db = FirebaseFirestore.getInstance();
private CollectionReference inventarioRef = db.collection("inventario");
private Spinner spinner;
private IntentResult result;
private Button btnAdd;
private  final static String[] zone = {"vin","carga exterior","carga nula","jaula","jaula con problemas","otras regiones","parciales","rechazados","swiss nature","carga con problemas"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line,zone);
        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(this);

        barCode =findViewById(R.id.barCode);

        searchView = findViewById(R.id.searchView);

        FloatingActionButton fab = findViewById(R.id.fab);
       // consultas();

        searchView.setOnQueryTextListener(this);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fab:
                        new IntentIntegrator(MainActivity.this).setOrientationLocked(false).setCaptureActivity(CaptureAct.class).initiateScan();
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
            if (result.getContents() != null) {
                barCode.setText(result.getContents());



            } else {
                barCode.setText("im sorry bro print ticket");
            }
    }
    @Override
        public void onItemSelected (AdapterView < ? > parent, View view, int position, long id){
        String zona = zone[position];
        Button add = findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference parciales = db.collection("inventario").document(zona);
                //atomically add a new of to the parciales array fild

                parciales.update("OF", FieldValue.arrayUnion(result.getContents()));
                CharSequence text = "OF añadida a " + zona;
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getBaseContext(),text,duration);
                toast.show();
            }
        });

        }

        @Override
        public void onNothingSelected (AdapterView < ? > parent){

        }

    @Override
    public boolean onQueryTextSubmit(String query) {
        inventarioRef.whereArrayContains("OF", query).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    String data = "";
                    for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                        Ofs ofs = documentSnapshot.toObject(Ofs.class);
                        ofs.setDocumentId(documentSnapshot.getId());

                        String documentId = ofs.getDocumentId();
                        data += documentId;
/*
                        for (String orden : ofs.getOf()) {
                            data += "\n-" + orden;
                        }
 */
                        data += "\n\n";
                    }
                    //barCode.setText(data);
                    AlertDialog.Builder alerta = new AlertDialog.Builder(MainActivity.this);
                    String finalData = data;
                    alerta.setMessage(query + "\n" + data)
                            .setCancelable(false)
                            .setPositiveButton("remove", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog titulo = alerta.create();
                    titulo.setTitle("search");
                    titulo.show();
                }else {
                    barCode.setText("error null reference");
                }
            }
        });

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }
/*
    private void consultas(){
        inventarioRef.whereArrayContains("OF", "944,984,577").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        String data = "";
                        if (queryDocumentSnapshots == null) {

                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                Ofs ofs = documentSnapshot.toObject(Ofs.class);
                                ofs.setDocumentId(documentSnapshot.getId());

                                String documentId = ofs.getDocumentId();
                                data += "ID: " + documentId;

                                for (String of : ofs.getOf()) {
                                    data += "\n-" + of;
                                }

                                data += "\n\n";
                            }
                            barCode.setText(data);
                        }else{
                            barCode.setText(data);
                        }
                    }
                });

    }

 */
}
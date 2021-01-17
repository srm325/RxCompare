package com.example.RxCompare;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.RxCompare.model_classes.Product;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ShoppingSessionActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private FloatingActionButton scanFab;
    FloatingActionButton addToBudgetFab;
    FloatingActionButton searchFab;
    private Activity activity;
    private String scannedUPC;
    private int itemCount = 0;
    private double totalCost = 0;
    TextView sessionName;
    TextView sessionDate;
    TextView sessionItemCountView;
    TextView sessionTotalCostView;
    RecyclerView recyclerView;
    SessionAdapter adapter;
    Date theDate;
    SimpleDateFormat simpleDateFormatter;
    ArrayList<String> groceryStoreList = new ArrayList<>();
    FirestoreRepository firestoreRepository = new FirestoreRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_session);

        getSupportActionBar().hide();

        sessionDate = findViewById(R.id.shopping_session_date);
        sessionItemCountView = findViewById(R.id.session_item_count);
        sessionTotalCostView = findViewById(R.id.session_cost);
        theDate = new Date();
        recyclerView = findViewById(R.id.shopping_session_recyclerview);
        searchFab = findViewById(R.id.fab_search);
        simpleDateFormatter = new SimpleDateFormat("MM/dd/yyyy");

        sessionDate.setText(simpleDateFormatter.format(theDate));


        //on click of the the date textview open datepickerdialog
        sessionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                com.example.RxCompare.DatePicker datePicker = new com.example.RxCompare.DatePicker();
                datePicker.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ShoppingSessionActivity.this, SearchItemActivity.class));
            }
        });

        readData();
        initalizeCountAndCost();
        setupRecyclerview();
    }

    private void initalizeCountAndCost() {
        firestoreRepository.sessionsRef
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            Product product = documentSnapshot.toObject(Product.class);
                            itemCount++;
                            totalCost += product.getProductPrice();
                        }

                        sessionItemCountView.setText(String.valueOf(itemCount));
                        sessionTotalCostView.setText("$"+ String.format("%.2f", totalCost));
                    }
                });
    }


    private void setupRecyclerview() {
        Query query = firestoreRepository.sessionsRef;

        FirestoreRecyclerOptions<Product> options = new FirestoreRecyclerOptions.Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        adapter = new SessionAdapter(options);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        LinearLayoutManager verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(verticalLayout);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }



    private void readData() {
        Log.i("CSV", "in read data method");
        InputStreamReader is = new InputStreamReader(getResources().openRawResource(R.raw.drugdata)) ;
        BufferedReader reader = new BufferedReader(is);
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                Log.i("CSV", line);
                groceryStoreList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        month += 1;
        String date = month + "/" + day + "/" + year;
        try {
            theDate = simpleDateFormatter.parse(date);
            sessionDate.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
package com.example.RxCompare;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.RxCompare.model_classes.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;

public class FirestoreRepository {
    public static final String TAG = FirestoreRepository.class.getSimpleName();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference categoriesRef = db.collection("categories");
    final CollectionReference sessionsRef = db.collection("sessions");
    public static final String ENTRIES = "entries";
    public static final String CATEGORY_NAME_FIELD = "categoryName";
    public static final String IS_EXPENSE_FIELD = "expense";
    public static final String ENTRY_DATE_FIELD = "entryDate";



    public void updateBudget(String categoryName, String budgetValue){
        categoriesRef.document(categoryName)
                .update("categoryBudget", budgetValue)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "Budget updated");
                    }
                });
    }



    public void addProduct(String productName, Double productPrice, String productID){
        Product product = new Product(productName, productPrice, productID);

        sessionsRef.add(product)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.i(TAG, "product added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, e.toString());
                    }
                });
    }
}

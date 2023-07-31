package com.verne.kenyfoods;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class FoodDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_food);

        Food food = getIntent().getParcelableExtra("food");

        TextView qtyField = findViewById(R.id.quantity_value);
        TextView priceTextView = findViewById(R.id.food_item_price);

        final String[] foodPrice = {Integer.toString(getResources().getInteger(R.integer.default_food_price))};

        //This is for the add button on the order page.
        ImageButton qtyAdd = findViewById(R.id.increment_quantity_button);
        qtyAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodPrice[0] = Integer.toString(food.getPrice());
                int food_price = Integer.parseInt(foodPrice[0]);

                String field = qtyField.getText().toString();
                int fieldString = Integer.parseInt(field);

                int finalQty = (fieldString + 1);

                qtyField.setText(Integer.toString(finalQty));
                int finalPrice = finalQty * food_price;
                priceTextView.setText(Integer.toString(finalPrice));
            }
        });

        ImageButton qtyMinus = findViewById(R.id.decrement_quantity_button);
        qtyMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodPrice[0] = Integer.toString(food.getPrice());
                int food_price = Integer.parseInt(foodPrice[0]);

                String field = qtyField.getText().toString();
                int fieldString = Integer.parseInt(field);

                if (!(fieldString <= 1)) {
                    int finalQty = fieldString - 1;

                    qtyField.setText(Integer.toString(finalQty));
                    int finalPrice = food_price * finalQty;
                    priceTextView.setText(Integer.toString(finalPrice));
                } else {
                    priceTextView.setText(Integer.toString(food_price));
                }
            }
        });

        //This is for the minus button on the order page.
        Button orderButton = findViewById(R.id.order_button);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = qtyField.getText().toString();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference ordersRef = db.collection("NewOrders");

                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                String foodName = food.getName();
                String quantity = qtyField.getText().toString();
                String price = priceTextView.getText().toString();

                //This will convert the required order data into a data object that can be understood and saved by Cloud Firestore.
                Map<String, Object> order = new HashMap<>();
                order.put("userName", userName);
                order.put("foodName", foodName);
                order.put("quantity", quantity);
                order.put("price", price);
                order.put("timeOrdered", FieldValue.serverTimestamp());

                //This will basically carry out the process of uploading the data to the Firestore Database.
                ordersRef.add(order)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //This will only run if the the upload operation is successful.
                                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), label + " " + food.getName() + "s ordered", Snackbar.LENGTH_SHORT);
                                snackbar.setDuration(4700);
                                snackbar.setAction("Done", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FoodDetailsActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                snackbar.show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            //This will only run if there's a failure.
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(findViewById(android.R.id.content), "Order placement failed! Try Again Later.", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        if(food != null){
            ImageView imageView = findViewById(R.id.foodimage);
            TextView nameTextView = findViewById(R.id.food_item_name);
            TextView descriptionTextView = findViewById(R.id.description_text_view);


            if(nameTextView != null){
                nameTextView.setText(food.getName());
            }
            if (!isDestroyed() && (imageView != null || food.getImageUrl() != null)) {

                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("FoodImages/"+food.getName());

                try {
                    storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Load the image into the ImageView using Glide
                            assert imageView != null;
                            Glide.with(FoodDetailsActivity.this)
                                    .load(uri)
                                    .centerCrop()
                                    .into(imageView);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            imageView.setImageResource(food.getImage());
                        }
                    });
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                    imageView.setImageResource(food.getImage());
                }

            }

            if(descriptionTextView != null){
                descriptionTextView.setText(food.getDescription());
            }
            if(priceTextView != null){
                priceTextView.setText(Integer.toString(food.getPrice()));
            }
        }else{
            System.out.print(food);
        }
    }

}

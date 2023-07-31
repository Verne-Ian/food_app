package com.verne.kenyfoods;

import static android.content.ContentValues.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FoodAdapter.OnFoodClickListener {

    private RecyclerView recyclerView;
    private FoodAdapter adapter;//This will declare a new instance of the Food adapter class.
    private List<Food> foodList;//This will declare a new List object of the type Food and will adapt to the Food model in terms of the data it will save.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        FirebaseAuth Auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = Auth.getCurrentUser();


        recyclerView = findViewById(R.id.food_list); //Linking the recycler view in the food list activity xml.
        recyclerView.setHasFixedSize(true); //This will set a fixed size for the recycler view depending on the items it has to output.
        recyclerView.setLayoutManager(new LinearLayoutManager(this));//This will set the layout of the recycler.

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("foodItems")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w(TAG, "Listen failed.", error);
                            return;
                        }

                        foodList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            Food food = document.toObject(Food.class);
                            foodList.add(food);
                        }

                        adapter = new FoodAdapter(foodList, MainActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });



        /*foodList = new ArrayList<>();//This array will save all the hardcoded contents of the previously declared food list.
        foodList.add(new Food(R.drawable.pepperoni_pizza, "Pepperoni Pizza", "Delicious pepperoni pizza with tomato sauce and mozzarella cheese", 12000));
        foodList.add(new Food(R.drawable.hamburger, "Hamburger", "Juicy beef patty with lettuce, tomato, onion, and pickles on a sesame seed bun", 8000));
        foodList.add(new Food(R.drawable.caesar_salad, "Caesar Salad", "Fresh romaine lettuce with croutons, parmesan cheese, and Caesar dressing", 6200));
        foodList.add(new Food(R.drawable.sushi_roll, "Sushi Roll", "Assorted sushi rolls with rice, seaweed, and fresh fish", 17000));
        foodList.add(new Food(R.drawable.fried_chicken, "Fried Chicken", "Crispy fried chicken served with mashed potatoes and gravy", 20000));

         */
    }

    @Override
    public void onFoodClick(int position) {
        //The purpose of this function is to handle an event where the user may tap on a desired food item.
        Food food = foodList.get(position);//This will create a new instance of the Food class and apply the foodlist variable to determine what has been tapped on the screen.
        Intent intent = new Intent(this, FoodDetailsActivity.class);
        intent.putExtra("food", food);//This will tell the class to add the extra data of the food item tapped to the next class interface responsible for the next interface.
        startActivity(intent);

        //Toast.makeText(this, food.getName() + " ordered", Toast.LENGTH_SHORT).show();
    }
}

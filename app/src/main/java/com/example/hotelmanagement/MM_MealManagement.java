package com.example.hotelmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import android.app.ProgressDialog;
import android.os.Bundle;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;

import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;



import Modal.MainMeals;
import Modal.MealList;
import Modal.ShortEats;
import Util.CommonConstants;
import Util.CommonFunctions;

public class MM_MealManagement extends AppCompatActivity {

    private EditText mealName, foodType, normalPrice, largePrice, SerchTag;
    private CheckBox breakfast, lunch, dinner;
    private Dialog myDialog, myDialog3, myDialog6;
    private Button addButton, deleteAll, addMeal, deleteAllfromDb, canselDAll, pastryShop;
    private ImageView  view , upload, uplodedImage, serchIcon, back;
    private DatabaseReference df;
    private StorageReference storageReference;
    private String primaryKey;
    private static final int PICK_FROM_GALLARY = 2;
    private Uri imageUri;
    private CardView search;
    private List<MainMeals> mealsLists = new ArrayList<>();
    private List<String> mealID = new ArrayList<>();
    private ProgressBar progressBar;
    private ListView lv;
    private String ImagePath;
    private ProgressDialog progressDialog;
    private TextView uploadText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mm__meal_management);

        pastryShop = findViewById(R.id.pastryShop);
        pastryShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MM_MealManagement.this, MM_Short_Eats_Management.class);
                startActivity(intent);
            }
        });

        back = findViewById(R.id.arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =  new Intent(MM_MealManagement.this, adminDashboard.class);
                startActivity(intent);
            }
        });

        progressDialog = new ProgressDialog(MM_MealManagement.this);

        storageReference = FirebaseStorage.getInstance().getReference("MainMealsImages");

        lv = (ListView) findViewById(R.id.mmList);

        //search Bar created in here
        myDialog6 = new Dialog(this);
        search = findViewById(R.id.searchCard);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                myDialog6.setContentView(R.layout.activity_mm__search__bar);
                myDialog6.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog6.show();
                final ProgressBar proSerch = myDialog6.findViewById(R.id.pro);
                proSerch.setVisibility(View.INVISIBLE);

                serchIcon = myDialog6.findViewById(R.id.imageView5);


                serchIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {



                        SerchTag = myDialog6.findViewById(R.id.offerName);


                        String id =  SerchTag.getText().toString();
                        if(id.isEmpty()){
                            SerchTag.setError("");
                            Toast.makeText(getApplicationContext(), "Please Enter Key For Search", Toast.LENGTH_LONG).show();
                        }
                        else {
                            if(id.charAt(0) == 'M' && id.charAt(1) == 'M'){
                                proSerch.setVisibility(View.VISIBLE);
                                df = FirebaseDatabase.getInstance().getReference().child("MainMeals").child(id);
                                df.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        MainMeals mainMeals = dataSnapshot.getValue(MainMeals.class);

                                        if(mainMeals != null){
                                            proSerch.setVisibility(View.GONE);
                                            Intent intent =  new Intent(MM_MealManagement.this,  MM_View_Meal_View.class);
                                            intent.putExtra("MainMeals", mainMeals);
                                            startActivity(intent);
                                        }else {
                                            proSerch.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Please Enter Valid Id!", Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else if(id.charAt(0) == 'S' && id.charAt(1) == 'E'){

                                proSerch.setVisibility(View.VISIBLE);
                                df = FirebaseDatabase.getInstance().getReference().child("ShortEats").child(id);
                                df.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        ShortEats shortEats = dataSnapshot.getValue(ShortEats.class);

                                        if(shortEats != null){
                                            proSerch.setVisibility(View.GONE);
                                            Intent intent =  new Intent(MM_MealManagement.this,  MM_Short_Eats_View.class);
                                            intent.putExtra("short_eats", shortEats);
                                            startActivity(intent);
                                        }else {
                                            proSerch.setVisibility(View.GONE);
                                            Toast.makeText(getApplicationContext(), "Please Enter Valid Id!", Toast.LENGTH_LONG).show();
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }

                            else {
                                proSerch.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Please Enter Valid Id!", Toast.LENGTH_LONG).show();
                            }
                        }





                    }



                });





            }
        });







        //add main meal by popup
        myDialog = new Dialog(this);
        addMeal = findViewById(R.id.addMeal);
        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtclose;
                myDialog.setContentView(R.layout.activity_mm__add__main__meal__pu);
                txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog.dismiss();
                    }
                });
                myDialog.setCanceledOnTouchOutside(false);
                myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog.show();

                upload = myDialog.findViewById(R.id.upload);
                upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent =  new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, PICK_FROM_GALLARY);
                    }

                });

                uplodedImage = myDialog.findViewById(R.id.uplodedImage);


                addButton = (Button) myDialog.findViewById(R.id.addMealDB);
                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        mealName = myDialog.findViewById(R.id.mealName);
                        foodType = myDialog.findViewById(R.id.mealType);
                        normalPrice = myDialog.findViewById(R.id.normalPrice);
                        largePrice = myDialog.findViewById(R.id.largePrice);
                        breakfast = myDialog.findViewById(R.id.brakfast);
                        lunch = myDialog.findViewById(R.id.lunch);
                        dinner = myDialog.findViewById(R.id.dinner);
                        uploadText  = myDialog.findViewById(R.id.textView25);
                        if(mealName.getText().toString().isEmpty()) {

                            mealName.setError("Please Enter Meal Name!");
                            mealName.clearFocus();

                        }else if(foodType.getText().toString().isEmpty()) {

                            foodType.setError("Please Enter Meal Type!");

                        }else if(normalPrice.getText().toString().isEmpty()) {

                            normalPrice.setError("Please Enter Meal Normal Price!");

                        }else if(Float.parseFloat(normalPrice.getText().toString()) == 0) {

                            normalPrice.setError("Normal Price Can Not Be 0!");

                        }else if(Float.parseFloat(normalPrice.getText().toString()) < 0) {

                            normalPrice.setError("Normal Price Can Not Be Negative!");

                        }else if(largePrice.getText().toString().isEmpty()) {

                            largePrice.setError("Please Enter Meal largePrice Price!");

                        }

                        else if(Float.parseFloat(largePrice.getText().toString()) == 0) {

                            largePrice.setError("Large Price Can Not Be 0!");

                        }else if(Float.parseFloat(largePrice.getText().toString()) < 0) {

                            largePrice.setError("Large Price Can Not Be Negative!");

                        }


                        else if(largePrice.getText().toString().isEmpty()) {

                            largePrice.setError("Please Enter Meal Large Price!");

                        }else if (breakfast.isChecked() == false && lunch.isChecked() == false && dinner.isChecked() == false){
                            Toast.makeText(getApplicationContext(), "Please Enter Meal Time!", Toast.LENGTH_LONG).show();
                            breakfast.setError("!");
                            lunch.setError("!");
                            dinner.setError("!");
                        }else if(imageUri == null){
                            uploadText.setError("!");
                        }

                        else {
                            insetDataToDb();
                        }




                    }
                });


            }
        });


        //delete all main meals
        deleteAll = findViewById(R.id.deleteAll);
        myDialog3 = new Dialog(this);
        deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txtclose;
                myDialog3.setContentView(R.layout.activity_mm__delete__all__pu);
                txtclose =(TextView) myDialog3.findViewById(R.id.txtclose3);
                myDialog3.setCanceledOnTouchOutside(false);
                txtclose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        myDialog3.dismiss();
                    }
                });
                myDialog3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                myDialog3.show();


                deleteAllfromDb = myDialog3.findViewById(R.id.button5);
                deleteAllfromDb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DeleteAllMainMeals();
                    }
                });

                canselDAll = myDialog3.findViewById(R.id.button4);
                canselDAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        myDialog3.dismiss();
                    }
                });

            }
        });


    }


    //delete all function in here
    public void DeleteAllMainMeals(){
        df = FirebaseDatabase.getInstance().getReference().child("MainMeals");
        df.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "All Meals are Deleted Successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MM_MealManagement.this, MM_MealManagement.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MM_MealManagement.this, MM_MealManagement.class);
                    startActivity(intent);
                }

            }
        });
    }



    //inserting values in to Db
    public void insetDataToDb(){

        progressDialog.setTitle("Adding Main Meal");
        progressDialog.setMessage("Data Uploading.Please Wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        df = FirebaseDatabase.getInstance().getReference().child("MainMeals");
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealsLists.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    MainMeals mainMeals = ds.getValue(MainMeals.class);
                    mealsLists.add(mainMeals);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        primaryKey = CommonFunctions.get_id(CommonConstants.MAIN_MEALS_PREFIX, mealsLists);
        final MainMeals mainMeals = new MainMeals();




        mainMeals.setId(primaryKey);
        mainMeals.setMealName(mealName.getText().toString());
        mainMeals.setType(mealName.getText().toString());
        mainMeals.setNormalPrice(Float.parseFloat(normalPrice.getText().toString()));
        mainMeals.setLargePrice(Float.parseFloat(largePrice.getText().toString()));
        mainMeals.setBrakfast(breakfast.isChecked());
        mainMeals.setLunch(lunch.isChecked());
        mainMeals.setDinner(dinner.isChecked());



        final StorageReference sf = storageReference.child("image" + imageUri.getLastPathSegment());
        sf.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                sf.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImagePath = uri.toString();
                        mainMeals.setImageName(ImagePath);
                        df = FirebaseDatabase.getInstance().getReference().child("MainMeals");
                        df.child(mainMeals.getId()).setValue(mainMeals)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Data Inserted Successfully!" + "Created Main meal Id : " + primaryKey, Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(MM_MealManagement.this, MM_MealManagement.class);
                                            startActivity(intent);
                                        }else {
                                            progressDialog.dismiss();
                                            Toast.makeText(getApplicationContext(), "Data Not Inserted Successfully!", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(MM_MealManagement.this, MM_MealManagement.class);
                                            startActivity(intent);
                                        }

                                    }
                                });
                    }
                });
            }
        });






    }

    //get the images
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_FROM_GALLARY && resultCode == RESULT_OK && data != null && data.getData() != null){
            imageUri = data.getData();
            uplodedImage.setImageURI(imageUri);
        }

    }

    //main meal view
    @Override
    protected void onStart() {
        super.onStart();
        progressBar = findViewById(R.id.pro);
        progressBar.setVisibility(View.VISIBLE);
        df = FirebaseDatabase.getInstance().getReference().child("MainMeals");
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealsLists.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    MainMeals mainMeals = ds.getValue(MainMeals.class);
                    mealsLists.add(mainMeals);
                }

                MealList mealList = new MealList(MM_MealManagement.this, mealsLists);

                lv.setAdapter(mealList);
                progressBar.setVisibility(View.GONE);

                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        MainMeals mainMeals =(MainMeals)adapterView.getAdapter().getItem(i);

                        Intent intent =  new Intent(MM_MealManagement.this,  MM_View_Meal_View.class);
                        intent.putExtra("MainMeals", mainMeals);
                        startActivity(intent);



                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}

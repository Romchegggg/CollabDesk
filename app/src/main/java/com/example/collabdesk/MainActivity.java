package com.example.collabdesk;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigationrail.NavigationRailView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Base64;


public class MainActivity extends AppCompatActivity implements FireBaseInterface, SpaceInt{

    Space spaceView;
    private FirebaseAPI database;
    SpaceObject spaceObject;
    ActivityResultLauncher<Intent> otherActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //Возврат результата работы второй активности
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        // do your operation from here....
                        if (data != null
                                && data.getData() != null) {
                            Uri selectedImageUri = data.getData();
                            Bitmap selectedImageBitmap;
                            try {
                                selectedImageBitmap
                                            = MediaStore.Images.Media.getBitmap(
                                        getContentResolver(),
                                        selectedImageUri);
                                SpaceObject newSpaceObject = new SpaceObject();

                                newSpaceObject.setType(2);
                                newSpaceObject.setWidth(200);
                                newSpaceObject.setHeight(200);
                                newSpaceObject.setCoordX(DataViewer.getInstance().getMaxCoordX() + 200);
                                newSpaceObject.setCoordY(DataViewer.getInstance().getMaxCoordY() + 200);
                                newSpaceObject.setBitmap(selectedImageBitmap);

                                Toast.makeText(getApplicationContext(),
                                        "Нажмите на место, где хотите расположить фото",
                                        Toast.LENGTH_SHORT).show();
                                newSpaceObject.setFileName(selectedImageUri.getPath().substring(selectedImageUri.getPath().lastIndexOf("/") + 1));
                                DataViewer.getInstance().addSpaceObject(newSpaceObject);

                                database.saveSpaceObject(newSpaceObject);
                           }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("TEST", "MAIN ACTIVITY onCreate" );
        spaceView = findViewById(R.id.space_view);


        database = database.getInstance(this);
        NavigationRailView navigationRail = findViewById(R.id.navigation_rail);
        navigationRail.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.add_object){
                    View view = new View(getBaseContext());
                    view.setLayoutParams(navigationRail.getLayoutParams());
                    view.setBackgroundColor(Color.BLACK);
                    view.layout(100, 100, 200, 200);

                    showObjectMenu(view);
                }

                if(item.getItemId() == R.id.del_object){
                    View view = new View(getBaseContext());
                    view.setLayoutParams(navigationRail.getLayoutParams());
                    view.setBackgroundColor(Color.BLACK);
                    view.layout(100, 200, 200, 300);

                    showDelObjectMenu(view);
                }
                return false;
            }
       });
        database.readData("Desk1");
    }

    private void showObjectMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.inflate(R.menu.object_menu);
        popupMenu.setGravity(Gravity.TOP);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getItemId() == R.id.menu2){
                            Toast.makeText(getApplicationContext(),
                                    "Вы выбрали Фото",
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            otherActivityResultLauncher.launch(intent);

                            return true;
                        }
                        if(item.getItemId() == R.id.menu3)
                        {
                            SpaceObject newSpaceObject = new SpaceObject();
                            newSpaceObject.setType(3);


                            Toast.makeText(getApplicationContext(),
                                    "Вы выбрали Текст",
                                    Toast.LENGTH_SHORT).show();
                            // Create the object of AlertDialog Builder class
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            // Set Alert Title
                            builder.setTitle("Введите текст!!!");
                            final EditText input = new EditText(getBaseContext());
                            builder.setView(input);
                            // Set Cancelable false for when the user clicks on the outside the Dialog Box then it will remain show
                            builder.setCancelable(false);

                            // Set the positive button with yes name Lambda OnClickListener method is use of DialogInterface interface.
                            builder.setPositiveButton("Готово", (DialogInterface.OnClickListener) (dialog, which) -> {
                                finish();
                                newSpaceObject.setWidth(100);
                                newSpaceObject.setHeight(100);
                                newSpaceObject.setCoordX(DataViewer.getInstance().getMaxCoordX() + 200);
                                newSpaceObject.setCoordY(DataViewer.getInstance().getMaxCoordY() + 200);
                                newSpaceObject.setText(input.getText().toString());
                                DataViewer.getInstance().addSpaceObject(newSpaceObject);
                                Log.i("TEST", "ACTIVITY = " + DataViewer.getInstance().spaceObjectArrayList.size());
                                database.saveSpaceObject(newSpaceObject);

                                DataViewer.getInstance().setWaitClick(true);
                                Log.i("TEST", "ACTIVITY MAIN SetWaitingClick");

                                Toast.makeText(getApplicationContext(),
                                        "Нажмите на место, где хотите расположить текст",
                                        Toast.LENGTH_SHORT).show();

                            });


                            // Set the Negative button with No name Lambda OnClickListener method is use of DialogInterface interface.
                            builder.setNegativeButton("Вернуться", (DialogInterface.OnClickListener) (dialog, which) -> {
                                // If user click no then dialog box is canceled.
                                dialog.cancel();
                            });

                            // Create the Alert dialog
                            AlertDialog alertDialog = builder.create();
                            // Show the Alert Dialog box
                            alertDialog.show();
                            return true;
                        }
                        return false;


                }

                });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }

    @Override
    public void loadMenu(ArrayList<String> data) {

    }

    private void showDelObjectMenu(View v) {
        PopupMenu popupMenu = new PopupMenu(this, v);
        for(int i = 0; i < DataViewer.getInstance().spaceObjectArrayList.size(); i++){
            popupMenu.getMenu().add(DataViewer.getInstance().spaceObjectArrayList.get(i).getName());

        }

        popupMenu.setGravity(Gravity.TOP);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DataViewer.getInstance().delSpaceObject(item.getTitle().toString());
                database.delSpaceObject(item.getTitle().toString());
                return true;


            }

        });

        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                Toast.makeText(getApplicationContext(), "onDismiss",
                        Toast.LENGTH_SHORT).show();
            }
        });
        popupMenu.show();
    }



    @Override
    public void onSpaceClickListener(int X, int Y) {
        Log.i("TEST", "onSpaceClickListener = " + DataViewer.getInstance().spaceObjectArrayList.size());
    }
}
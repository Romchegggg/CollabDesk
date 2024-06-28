package com.example.collabdesk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FirebaseAPI {
    private FirebaseDatabase database;
    DatabaseReference baseRef;
    private static FirebaseAPI instance;
    FireBaseInterface listenerDatabase;
    int lastIndex;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public FirebaseAPI(Context context) {

        database = FirebaseDatabase.getInstance("https://collabdesk-f56cc-default-rtdb.europe-west1.firebasedatabase.app/");
        baseRef = database.getReference("Desk1");
        if (context instanceof FireBaseInterface) {
            listenerDatabase = (FireBaseInterface) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement DatabaseInterface");
        }
    }


    public static FirebaseAPI getInstance(Context context) {
        if (instance == null) {
            instance = new FirebaseAPI(context);
        }
        return instance;
    }

    public void readData(String tableName) {
        baseRef = database.getReference(tableName);
        baseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataViewer.getInstance().clearSpaceObjectList();
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Log.d("TEST", "OBJECTS Value is: " + dataSnapshot.getValue());
                ArrayList<String> test = new ArrayList<>();
                for (DataSnapshot descSettings : dataSnapshot.getChildren()) {      //Desks
                    for (DataSnapshot childMenu : descSettings.getChildren()) { //objects
                        SpaceObject spaceObject = new SpaceObject();
                        Log.d("TEST", "Value is childMenu: " + childMenu.getKey() + "  " + childMenu.getValue());
                        if (childMenu.getKey().contains("Object")) {
                            int ind = childMenu.getKey().lastIndexOf("_");
                            int index = Integer.parseInt(childMenu.getKey().substring(ind + 1));
                            Log.d("TEST", "index " + index);
                            if(lastIndex < index) {
                                lastIndex = index;
                            }
                            for (DataSnapshot childs : childMenu.getChildren()) {  //object 1 2 3...
                                Log.d("TEST", "Value is childs: " + childs.getKey() + "  " + childs.getValue());
                                if (childs.getKey().equals("type")) {
                                    spaceObject.setType(Integer.parseInt(childs.getValue().toString()));
                                }
                                if (childs.getKey().equals("coordX")) {
                                    Log.d("TEST", "CoordX is value: " + childs.getKey() + "  " + childs.getValue());
                                    spaceObject.setCoordX(Integer.parseInt(childs.getValue().toString()));
                                }
                                if (childs.getKey().equals("coordY")) {
                                    spaceObject.setCoordY(Integer.parseInt(childs.getValue().toString()));
                                }
                                if (childs.getKey().equals("width")) {
                                    spaceObject.setWidth(Integer.parseInt(childs.getValue().toString()));
                                }
                                if (childs.getKey().equals("height")) {
                                    spaceObject.setHeight(Integer.parseInt(childs.getValue().toString()));
                                }
                                if (childs.getKey().equals("text")) {
                                    spaceObject.setText(childs.getValue().toString());

                                }
                                if (childs.getKey().equals("fileName")) {
                                    spaceObject.setFileName(childs.getValue().toString());

                                }
                                if (childs.getKey().equals("name")) {
                                    spaceObject.setName(childs.getValue().toString());

                                }
                            }
                            System.out.println( "READ DATA IMAGE  TYPE = " + spaceObject.getType());
                            if(spaceObject.getType() == 2){
                                StorageReference storageRef = storage.getReference();
                                // Create a reference to "mountains.jpg"
                                StorageReference imageRef = storageRef.child(spaceObject.getFileName());

                                System.out.println( "READ DATA IMAGE  FILE NAME = " + spaceObject.getFileName());
                                final long ONE_MEGABYTE = 1024 * 1024;
                                imageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                    @Override
                                    public void onSuccess(byte[] bytes) {
                                        System.out.println( "READ DATA IMAGE = " + bytes.length);
                                        spaceObject.setBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle any errors
                                    }
                                });
                            }
                        }
                        DataViewer.getInstance().addSpaceObject(spaceObject);
                    }
                }
                listenerDatabase.loadMenu(test);
        }

        @Override
        public void onCancelled (DatabaseError error){
            // Failed to read value
            Log.w("TEST", "Failed to read value.", error.toException());
        }
    });
}

    public FirebaseAPI() {

    }

    public void saveSpaceObject(SpaceObject spaceObject) {

        baseRef = database.getReference("Desk1");
        DatabaseReference refObj = baseRef.child("Objects");
        DatabaseReference refObj1 = refObj.child("Object_" + (lastIndex + 1));
        spaceObject.setName("Object_" + (lastIndex + 1));
        refObj1.setValue(spaceObject);
        refObj1.push();
        refObj.push();
        baseRef.push();

        if (spaceObject.getType() == 2) {
            StorageReference storageRef = storage.getReference();
            // Create a reference to "mountains.jpg"
            StorageReference imageRef = storageRef.child(spaceObject.getFileName());
            // Create a reference to 'images/mountains.jpg'


            Bitmap bitmap = spaceObject.getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = imageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    System.out.println( "uploadTask onFailure");
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    System.out.println( "uploadTask onSuccess");
                }
            });
        }
    }


    public void updateSpaceObject(int x, int y) {

        baseRef = database.getReference("Desk1");
        DatabaseReference refObj = baseRef.child("Objects");
        DatabaseReference refObj1 = refObj.child("Object_" + (lastIndex + 1));
        DatabaseReference refObj2 = refObj.child("CoordX");
        refObj2.setValue(x);

        refObj2.push();
        refObj1.push();
        refObj.push();
        baseRef.push();
    }


    public void delSpaceObject(String Title){
        baseRef = database.getReference("Desk1");
        DatabaseReference refObj = baseRef.child("Objects");
        DatabaseReference refObj1 = refObj.child(Title);
        refObj1.removeValue();
        refObj1.push();
        refObj.push();
        baseRef.push();

    }
}

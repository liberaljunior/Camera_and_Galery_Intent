package com.example.cameradangalery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ImageView iv_user;
    private Button btn_camera, btn_galeri;
    int galery = 1, camera = 2;
    String kameraFIlePaTH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_camera = findViewById(R.id.button);
        btn_galeri = findViewById(R.id.button2);
        iv_user = findViewById(R.id.imageView);

        btn_galeri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED) {

                        String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        requestPermissions(permission, galery);
                    }else {

                        pickgalery();
                    }
                }
                else {


                    pickgalery();
                }
            }
        });


        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {

                        String[] permission = {Manifest.permission.CAMERA};
                        requestPermissions(permission, camera);
                    }else {

                        try {
                            pickCamera();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {


                    try {
                        pickCamera();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private void pickgalery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, galery);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



                if (resultCode == RESULT_OK && requestCode == galery) {
                    Uri uri = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    if (uri != null) {
                        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);

                        if (cursor != null) {
                            cursor.moveToFirst();
                            int kolomindeks = cursor.getColumnIndex(filePathColumn[0]);
                            String gambarpath = cursor.getString(kolomindeks);

                            iv_user.setImageBitmap(BitmapFactory.decodeFile(gambarpath));
                            cursor.close();
                        }
                    }

                }else if (resultCode == RESULT_OK && requestCode == camera) {
                    iv_user.setImageURI(Uri.parse(kameraFIlePaTH));

                }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0){
            if (grantResults.length >0 && grantResults[0] ==
            PackageManager.PERMISSION_GRANTED){

            }

        }else {
            Toast.makeText(this, "Permisi ditolak", Toast.LENGTH_SHORT).show();
        }
    }

    private File membuatGambarFile() throws IOException{
        // Create an image file name
        String tanggalSekarang = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String namaGambarFile = "JPEG_" + tanggalSekarang + "_";

        File storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File gambar =  File.createTempFile(
                namaGambarFile,
                ".jpg",
                storageDir
        );

        kameraFIlePaTH = "file://" + gambar.getAbsolutePath();
        return  gambar;
    }

    private void pickCamera() throws IOException {
        try {


            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider
                    .getUriForFile(this,
                            BuildConfig.APPLICATION_ID +
                                    ".provider", membuatGambarFile()));
            startActivityForResult(intent, camera);
        }catch (IOException ex){
            ex.printStackTrace();
        }

    }
}



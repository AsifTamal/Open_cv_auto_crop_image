package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.labters.documentscanner.ImageCropActivity;
import com.labters.documentscanner.helpers.ScannerConstants;

import java.io.IOException;

import static com.example.myapplication.PermissionHelper.canWriteOnExternalStorage;


public class EdgeDetectActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;
    Uri image_uri;
    ImageView imageView;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edge_detect);
         button= (Button) findViewById(R.id.btn);
        imageView= (ImageView) findViewById(R.id.img);
        imageView.setVisibility(View.GONE);
        permissionHelper = new PermissionHelper(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (canWriteOnExternalStorage() && permissionHelper.checkStoregePermission()) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    permissionHelper.requestStoragePermission();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }





        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "New Pic");
                    // values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
                   image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
                    startActivityForResult(cameraIntent, 1234);

                } catch (Exception ex) {
                    Toast.makeText(EdgeDetectActivity.this, "Error : " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
            try {
                ScannerConstants.selectedImageBitmap = MediaStore.Images.Media.getBitmap(
          getContentResolver(),
                     image_uri
      );
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("onActivityResult", "onActivityResult: "+image_uri.toString()+"real: "+getRealPathFromURI(image_uri));
            startActivityForResult(new Intent(EdgeDetectActivity.this, ImageCropActivity.class), 1231);

        }else if (requestCode == 1231 && resultCode == Activity.RESULT_OK) {
            if (ScannerConstants.selectedImageBitmap != null) {
                imageView.setImageBitmap(ScannerConstants.selectedImageBitmap);
                imageView.setVisibility(View.VISIBLE);

            } else
                Toast.makeText(this, "Not OK", Toast.LENGTH_LONG).show();
        }


    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
}
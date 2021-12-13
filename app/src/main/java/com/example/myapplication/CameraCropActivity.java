package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;


import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;

import static com.example.myapplication.PermissionHelper.canWriteOnExternalStorage;

public class CameraCropActivity extends AppCompatActivity{


    private PermissionHelper permissionHelper;
    private SurfaceView cameraView,transparentView;
    SurfaceHolder holder,holderTransparent;
    private Canvas canvas;
    private Paint paint;
    private float RectLeft,RectTop,RectRight,RectBottom;
    public static final int IMAGE_PICK_CAMERA_CODE = 1475;

    private ImageSurfaceView mImageSurfaceView;
    private Camera camera;
    RelativeLayout rl,cameraLayout;
    LinearLayout saveLayout;
    private FrameLayout cameraPreviewLayout;
    private ImageView capturedImageHolder,saveImage;
    int imageL,imageT,imageW,imageH,width,height,dpsize;

    Bitmap resizedBitmap;
    Camera mCamera = null;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_crop);
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

        // Create first surface with his holder(holder)
       // pickCamera();
        cameraPreviewLayout = (FrameLayout)findViewById(R.id.camera_preview);
        capturedImageHolder = (ImageView)findViewById(R.id.captured_image);

        rl = findViewById(R.id.rr2);

        cameraLayout = findViewById(R.id.cameraLayout);
        saveLayout = findViewById(R.id.saveLayout);

        saveImage = findViewById(R.id.saveImage);

        width= getWindowManager().getDefaultDisplay().getWidth();
        height= getWindowManager().getDefaultDisplay().getHeight();

        camera = checkDeviceCamera();

        mImageSurfaceView = new ImageSurfaceView(this, camera);
        cameraPreviewLayout.addView(mImageSurfaceView);

        dpsize =150;// (int) (getResources().getDimension(150));
        capturedImageHolder.setX((width-dpsize)/2);
        capturedImageHolder.setY((height -dpsize)/2);

//        imageL= (int) capturedImageHolder.getX();
//        imageT= (int) capturedImageHolder.getY();
//        capturedImageHolder.setOnTouchListener(new MoveViewTouchListener(capturedImageHolder));
        Button captureButton = (Button)findViewById(R.id.button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // camera.takePicture(null, null, pictureCallback);
                Log.d("fhvhgjbjkb", "Cannot start preview");
            }
        });

    }


    private Camera checkDeviceCamera(){

        try {
            mCamera = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mCamera;
    }
    public void resetCamera()
    {
        if(mCamera!=null) {
            mCamera.release();

        }
    }

    android.hardware.Camera.PictureCallback pictureCallback = new android.hardware.Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            Bitmap b = BitmapFactory.decodeByteArray(data, 0, data.length);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            final int REQUIRED_SIZE = 512;
            int scale = 1;
            int wd= b.getWidth();
            while (wd >=( REQUIRED_SIZE)) {
                wd= wd/2;
                scale *= 2;
            }
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,options);

            if(bitmap==null){
                Toast.makeText(CameraCropActivity.this, "Captured image is empty", Toast.LENGTH_LONG).show();
                return;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            bitmap= Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,false);
            int bh= bitmap.getHeight();
            int bw= bitmap.getWidth();
            width= rl.getWidth();
            height= rl.getHeight();
            int l = imageL*bw/width;
            int t = imageT*bh/height;
            int w = capturedImageHolder.getWidth()*bw/width;
            int h = capturedImageHolder.getHeight()*bh/height;

            cameraPreviewLayout.setVisibility(View.GONE);
            capturedImageHolder.setVisibility(View.VISIBLE);
            resizedBitmap= Bitmap.createBitmap(bitmap,l,t,w,h);

            if(resizedBitmap!=null) {

                cameraLayout.setVisibility(View.GONE);
                saveLayout.setVisibility(View.VISIBLE);
                saveImage.setImageBitmap(resizedBitmap);
            }
        }
    };

    public class MoveViewTouchListener
            implements View.OnTouchListener
    {
        private GestureDetector mGestureDetector;
        private View mView;


        public MoveViewTouchListener(View view)
        {
            mGestureDetector = new GestureDetector(view.getContext(), mGestureListener);
            mView = view;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            return mGestureDetector.onTouchEvent(event);
        }

        private GestureDetector.OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener()
        {
            private float mMotionDownX, mMotionDownY;

            @Override
            public boolean onDown(MotionEvent e)
            {
                mMotionDownX = e.getRawX() - mView.getTranslationX();
                mMotionDownY = e.getRawY() - mView.getTranslationY();
                imageL= (int) mView.getX();
                imageT= (int) mView.getY();
                Log.d("imageview"," down");
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
            {
                mView.setTranslationX(e2.getRawX() - mMotionDownX);
                mView.setTranslationY(e2.getRawY() - mMotionDownY);
                imageL= (int) mView.getX();
                imageT= (int) mView.getY();
                if((distanceX==0)&&(distanceY==0))
                {
                    Log.d("imageview"," zoomed");
                }

                return true;
            }
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                Log.d("imageview"," tapped");
                return true;
            }

        };
    }













































































    Uri image_uri;
    private void pickCamera() {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Pic");
            // values.put(MediaStore.Images.Media.DESCRIPTION, "Image to Text");
            image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            startActivityForResult(cameraIntent, IMAGE_PICK_CAMERA_CODE);

        } catch (Exception ex) {
            Toast.makeText(this, "Error : " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {

            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {

                if (requestCode == IMAGE_PICK_CAMERA_CODE) {
//                    CropImage.activity(image_uri)
//                            .setGuidelines(CropImageView.Guidelines.ON)
//                            .start(this);
                    Log.d("onActivity", image_uri.toString());
                }
            }


        } catch (Exception ex) {
            Log.d("onActivityError", ex.getMessage().toString());
            Toast.makeText(this, "Error : " + ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void DrawFocusRect(float RectLeft, float RectTop, float RectRight, float RectBottom, int color)
    {

        canvas = holderTransparent.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(3);
        canvas.drawRect(RectLeft, RectTop, RectRight, RectBottom, paint);
        holderTransparent.unlockCanvasAndPost(canvas);
    }


    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
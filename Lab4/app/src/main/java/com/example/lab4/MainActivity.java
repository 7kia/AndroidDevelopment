package com.example.lab4;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.AsyncTask;
import android.net.Uri;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private String mPathToPhoto;
    private static final int REQUEST_TAKE_PHOTO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageButton takePhotoButton = (ImageButton) findViewById(R.id.imageButton);
        mImageView = (ImageView) findViewById(R.id.imageView);
        takePhotoButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dispatchTakePictureIntent();
                    }
                }
        );
    }

    /*
        Taken from https://developer.android.com/training/camera/photobasics.html
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if ((resultCode == RESULT_OK) && (requestCode == REQUEST_TAKE_PHOTO)) {
            galleryAddPic();
            setPic();
        }
    }

    private class ImageLoader extends AsyncTask<Void, Void, File>{

        @Override
        protected File doInBackground(Void... params) {
            try {
                return getImage();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private File getImage() throws IOException {
            File storageDir = Environment.getExternalStorageDirectory();
            File image = File.createTempFile("Image", ".jpg", storageDir);
            mPathToPhoto = image.getAbsolutePath();
            return image;
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mPathToPhoto);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new ImageLoader().execute((Void) null).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void setPic() {
        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mPathToPhoto, options);
        int photoW = options.outWidth;
        int photoH = options.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        options.inJustDecodeBounds = false;
        options.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mPathToPhoto, options);
        mImageView.setImageBitmap(bitmap);
    }
}

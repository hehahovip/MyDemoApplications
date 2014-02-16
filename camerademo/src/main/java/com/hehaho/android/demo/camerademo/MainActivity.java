package com.hehaho.android.demo.camerademo;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import dalvik.system.PathClassLoader;

public class MainActivity extends Activity {

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;

    public static String APP_PACKAGE_NAME = MainActivity.class.getPackage().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

       // Log.i("Premisson for camera:", PackageManager.getSystemAvailableFeatures(PackageManager.FEATURE_CAMERA));

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private ImageView mImageView;

        private File storageDir;

        private static final String JPEG_FILE_PREFIX = "mytestphoto";

        private static final String JPEG_FILE_SUFFIX = ".jpeg";

        private String mCurrentPhotoPath;

        private Intent takePictureIntent;

        private Bitmap mImageBitmap;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main2, container, false);

            Button takePhoto = (Button)rootView.findViewById(R.id.button);
            Button showPic = (Button)rootView.findViewById(R.id.saveBtn);

            if(!PlaceholderFragment.isIntentAvailable(container.getContext(), MediaStore.ACTION_IMAGE_CAPTURE)){
                takePhoto.setEnabled(false);
            }

            mImageView = (ImageView)rootView.findViewById(R.id.imageView);
            takePhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dispatchTakePictureIntent(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            });

            showPic.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    savePhotoToExternalStorage();
                }
            });

            return rootView;
        }

        /**
         * Call the camera app to take a photo.
         *
         * @param actionCode
         */
        private void dispatchTakePictureIntent(int actionCode) {
            takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(takePictureIntent, actionCode);
           // handleSmallCameraPhoto(takePictureIntent);
        }

        /**
         * Get the photo from returned intent.
         *
         * @param intent
         */
        private void handleSmallCameraPhoto(Intent intent) {
            Bundle extras = intent.getExtras();
            mImageBitmap = (Bitmap) extras.get("data");
            mImageView.setImageBitmap(mImageBitmap);
        }

        public static boolean isIntentAvailable(Context context, String action) {
            final PackageManager packageManager = context.getPackageManager();
            final Intent intent = new Intent(action);
            List<ResolveInfo> list =
                    packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            return list.size() > 0;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            handleSmallCameraPhoto(data);
        }

        /**
         * Save photo to external storage.
         */
        public void savePhotoToExternalStorage(){
            storageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                    ),
                    getAlbumName()
            );
            if(!storageDir.exists()){
                storageDir.mkdir();
                Log.i("File: ", "Create external folder!");
            }
            try{
                File f = createImageFile();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                Toast.makeText(this.getView().getContext(), "Save photo successfully!", Toast.LENGTH_LONG);
                galleryAddPic();
                setPic();
            } catch (IOException e){
                Log.e("Save photo", e.getMessage());
            }
        }

        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp =
                    new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = JPEG_FILE_PREFIX + timeStamp;
            File image = File.createTempFile(
                    imageFileName,
                    JPEG_FILE_SUFFIX,
                    storageDir
            );

            Log.i("Photo path: ", image.getAbsolutePath());
            mCurrentPhotoPath = image.getAbsolutePath();
            OutputStream out = new FileOutputStream(image);
            mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return image;
        }

        private void galleryAddPic() {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(mCurrentPhotoPath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
//            this.sendBroadcast(mediaScanIntent);
        }

        private void setPic() {
            // Get the dimensions of the View
            int targetW = mImageView.getWidth();
            int targetH = mImageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mImageView.setImageBitmap(bitmap);
        }

        private String getAlbumName() {
            return getString(R.string.album_name);
        }
    }

}

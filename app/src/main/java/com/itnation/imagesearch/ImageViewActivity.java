package com.itnation.imagesearch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.startapp.sdk.adsbase.Ad;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.adlisteners.AdEventListener;
import com.startapp.sdk.adsbase.adlisteners.VideoListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class ImageViewActivity extends AppCompatActivity {


    private URL url;
    private Bitmap bitmap;

    ImageView imageView;
    ImageButton tool_button, share, download;
    TextView image_name;

    public static String imageName= "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        imageView= findViewById(R.id.imageView);
        tool_button= findViewById(R.id.tool_button);
        image_name= findViewById(R.id.tool_text);
        share= findViewById(R.id.share);
        download= findViewById(R.id.download);



        image_name.setText(imageName);

        tool_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();

            }
        });


        String imgUrl = getIntent().getStringExtra("imageUrl");
        Picasso.get().load(imgUrl).placeholder(R.drawable.loading_image).into(imageView);

        //===================================================


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    url = new URL(imgUrl);
                    bitmap = BitmapFactory.decodeStream(url.openStream());

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();




//=====================================================================
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);



            }
        });





        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                downloadImage(imageView);
                Toast.makeText(ImageViewActivity.this,"Image Downloaded Successfully to Download Folder", Toast.LENGTH_SHORT).show();
                showRewardedVideo();



            }
        });





    }//----------------------------------------close oncreate

    public void downloadImage(View view){


        imageView.setImageBitmap(bitmap);
        StorageManager storageManager = (StorageManager) getSystemService(STORAGE_SERVICE);
        StorageVolume storageVolume = storageManager.getStorageVolumes().get(0); // internal Storage
        File fileImage = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            fileImage = new File(storageVolume.getDirectory().getPath() + "/Download/"
                    + System.currentTimeMillis()
                    + ".jpeg");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesArray = byteArrayOutputStream.toByteArray();
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(fileImage);
            fileOutputStream.write(bytesArray);
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



    }


    //---------------------------------- share image --------------------------


    private void shareImageAndText(Bitmap bitmap){
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Sharing Images");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Subject");
        intent.setType("image/png");
        startActivity(Intent.createChooser(intent,"Share via"));
    }

    private Uri getImageToShare (Bitmap bitmap){
        File imageFolder = new File(getCacheDir(),"images");
        Uri uri = null;

        try {
            imageFolder.mkdir();
            File file = new File(imageFolder,"shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG,90,outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(ImageViewActivity.this,"com.itnation.imagesearch", file);
        } catch (Exception e){
            Toast.makeText(this, "Exception: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }


    // ads implements----------------------



    public void showRewardedVideo() {
        final StartAppAd rewardedVideo = new StartAppAd(this);

        rewardedVideo.setVideoListener(new VideoListener() {
            @Override
            public void onVideoCompleted() {
                // Grant the reward to user
            }
        });

        rewardedVideo.loadAd(StartAppAd.AdMode.REWARDED_VIDEO, new AdEventListener() {
            @Override
            public void onReceiveAd(Ad ad) {
                rewardedVideo.showAd();
            }

            @Override
            public void onFailedToReceiveAd(Ad ad) {
                // Can't show rewarded video
            }
        });
    }








}
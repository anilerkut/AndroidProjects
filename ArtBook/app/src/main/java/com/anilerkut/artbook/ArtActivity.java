package com.anilerkut.artbook;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultCaller;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.anilerkut.artbook.databinding.ActivityArtBinding;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;

public class ArtActivity extends AppCompatActivity {

    private ActivityArtBinding binding;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArtBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        registerLauncher();
        database = this.openOrCreateDatabase("Arts",MODE_PRIVATE,null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");

        if(info.equals("new"))
        {
            binding.nameText.setText("");
            binding.artistText.setText("");
            binding.yearText.setText("");
            binding.imageView.setImageResource(R.drawable.uploadimage);
            binding.button.setVisibility(View.VISIBLE);
            //new art is coming
        }
        else
        {
            //enter old art info
            int artId=intent.getIntExtra("artId",0);
            binding.button.setVisibility(View.INVISIBLE);

            try
            {
                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ?",new String[] {String.valueOf(artId)});
                int artnNameIx= cursor.getColumnIndex("artname");
                int painterNameIx = cursor.getColumnIndex("artist");
                int artYearIx = cursor.getColumnIndex("artyear");
                int imageIx = cursor.getColumnIndex("image");

                while(cursor.moveToNext())
                {
                    binding.nameText.setText(cursor.getString(artnNameIx));
                    binding.artistText.setText(cursor.getString(painterNameIx));
                    binding.yearText.setText(cursor.getString(artYearIx));
                    byte [] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);
                }
                cursor.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

    }

    public void save(View view)
    {
        String name = binding.nameText.getText().toString();
        String artist=binding.artistText.getText().toString();
        String art_year = binding.yearText.getText().toString();

        Bitmap small_image = makeImageSmaller(selectedImage,250);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        small_image.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] byteArray = outputStream.toByteArray();


        try
        {
            database.execSQL("CREATE TABLE IF NOT EXISTS arts(id INTEGER PRIMARY KEY,artname VARCHAR,artist VARCHAR,artyear VARCHAR,image BLOB)");
            String sqlStatement= "INSERT INTO arts (artname,artist,artyear,image)VALUES (?,?,?,?)";

            SQLiteStatement sqLiteStatement = database.compileStatement(sqlStatement);
            sqLiteStatement.bindString(1,name);
            sqLiteStatement.bindString(2,artist);
            sqLiteStatement.bindString(3,art_year);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Intent intent = new Intent(ArtActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public Bitmap makeImageSmaller(Bitmap image,int maximumSize)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        float bitmapRatio = (float) width/ (float) height;

        if(bitmapRatio>1)
        {
            width =maximumSize;
            height = (int) (width / bitmapRatio);
            //yatay görsel - landscape
        }
        else
        {
            height=maximumSize;
            width = (int) (width * bitmapRatio);
            //dikey görsel - portrait
        }
        return image.createScaledBitmap(image,width,height,true);
    }

    public void selectImage(View view)
    {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)//Reddedildiyse //ilk defa upload image resmine tıklayınca burası çalışıcak.
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                Snackbar.make(view,"Permission required for Gallery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permission", new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();
            }
            else
            {
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        else
        {
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);
        }
    }

    public void registerLauncher()
    {

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>()
        {
            @Override
            public void onActivityResult(ActivityResult result)
            {
                if(result.getResultCode()==RESULT_OK)
                {
                    Intent intentFromResult = result.getData();
                    if(intentFromResult != null)
                    {
                        Uri imageData = intentFromResult.getData();
                        //binding.imageView.setImageURI(imageData);

                        try
                        {
                            if(Build.VERSION.SDK_INT >= 28)
                            {
                                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                selectedImage = ImageDecoder.decodeBitmap(source);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                            else
                            {
                                selectedImage = MediaStore.Images.Media.getBitmap(ArtActivity.this.getContentResolver(),imageData);
                                binding.imageView.setImageBitmap(selectedImage);
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });


        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>()
        {
            @Override
            public void onActivityResult(Boolean result)
            {
                if(result)
                {
                    //permission granted
                    Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activityResultLauncher.launch(intentToGallery);
                }
                else
                {
                    //permission denied
                    Toast.makeText(ArtActivity.this,"Permission Needed!",Toast.LENGTH_LONG);
                }
            }
        });



    }
}
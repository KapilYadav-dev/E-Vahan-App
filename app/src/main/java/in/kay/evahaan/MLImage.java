package in.kay.evahaan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.mindorks.paracamera.Camera;

public class MLImage extends AppCompatActivity {
    Camera camera;
    private static final int REQUEST_CAMERA = 0;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TakePhoto();
    }

    private void TakePhoto() {
        final String permission = Manifest.permission.CAMERA;
        if (ContextCompat.checkSelfPermission(MLImage.this, permission)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MLImage.this, permission)) {

            } else {
                ActivityCompat.requestPermissions(MLImage.this, new String[]{permission}, REQUEST_CAMERA);
            }
        } else {
            camera = new Camera.Builder()
                    .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                    .setTakePhotoRequestCode(1)
                    .setDirectory("Pics")
                    .setName("Evahaan" + System.currentTimeMillis())
                    .setImageFormat(Camera.IMAGE_JPEG)
                    .setCompression(75)
                    .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                    .build(this);
            try {
                camera.takePicture();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        pd = new ProgressDialog(this);
        pd.setMax(100);
        pd.setMessage("Loading...");
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.show();
        pd.setCancelable(false);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();
            if (bitmap != null) {
                InputImage image = InputImage.fromBitmap(bitmap, 0);
                TextRecognizer recognizer = TextRecognition.getClient();
                Task<Text> result =
                        recognizer.process(image)
                                .addOnSuccessListener(new OnSuccessListener<Text>() {
                                    @Override
                                    public void onSuccess(Text visionText) {
                                        pd.dismiss();
                                        String text = visionText.getText();
                                        Intent intent = new Intent(MLImage.this, MainActivity.class);
                                        intent.putExtra("car_num", text.trim());
                                        startActivity(intent);
                                        overridePendingTransition(0, 0);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                pd.dismiss();
                                                Intent intent = new Intent(MLImage.this, MainActivity.class);
                                                Toast.makeText(MLImage.this, "Error occured, Please try the manual way.", Toast.LENGTH_SHORT).show();
                                                startActivity(intent);
                                                overridePendingTransition(0, 0);
                                                finish();
                                            }
                                        });
            } else {
                pd.dismiss();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.deleteImage();
    }

}
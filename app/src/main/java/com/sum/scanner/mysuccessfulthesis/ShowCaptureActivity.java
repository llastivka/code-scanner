package com.sum.scanner.mysuccessfulthesis;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class ShowCaptureActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_capture);

        Bundle extras = getIntent().getExtras();
        assert extras != null;
        byte[] b = extras.getByteArray("capture");

        if (b != null) {
            ImageView capture = findViewById(R.id.camera_capture);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            Bitmap rotatedBitmap = rotate(decodedBitmap);
            capture.setImageBitmap(rotatedBitmap);
        }
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        int width = decodedBitmap.getWidth();
        int height = decodedBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        return Bitmap.createBitmap(decodedBitmap, 0, 0, width, height, matrix, true);
    }
}

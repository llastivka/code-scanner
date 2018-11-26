package com.sum.scanner.mysuccessfulthesis;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class CaptureFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(CaptureFragment.class.getName());

    private Context mContext;
    private Activity mFragmentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture, container, false);

//        Bundle bundle = this.getArguments();
//        if (bundle != null) {
//            byte[] imageBytes = bundle.getByteArray("capture");
        byte[] imageBytes = ((MainActivity) getActivity()).getImageBytes();
            if (imageBytes != null) {
                logger.info("Decoding image bytes array into bitmap");
                ImageView image = view.findViewById(R.id.camera_capture);
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 2;
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                Bitmap rotatedBitmap = rotate(decodedBitmap);
//                Drawable d = Drawable.createFromStream(new ByteArrayInputStream(imageBytes), null);
//                image.setImageDrawable(d);
                image.setImageBitmap(rotatedBitmap);

                try (FileOutputStream out = new FileOutputStream("pippo.pmg")) {
                    rotatedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (IOException e) {
                }

            } else {
                logger.info("Image bytes array is null");
            }
//        } else {
//            logger.info("Bundle is null");
//        }

        Button tryAgainButton = view.findViewById(R.id.try_again_capture_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mFragmentActivity).changeFragments(new CameraFragment(), getString(R.string.camera));
            }
        });

        Button anglesReadyButton = view.findViewById(R.id.angles_ready_button);
        anglesReadyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decode();
            }
        });

        return view;
    }

    private void decode() {
        ((MainActivity) mFragmentActivity).changeFragments(new ResultFragment(), getString(R.string.camera));
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        logger.info("Rotating image bitmap");
        int width = decodedBitmap.getWidth();
        int height = decodedBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        return Bitmap.createBitmap(decodedBitmap, 0, 0, width, height, matrix, true);
    }
}

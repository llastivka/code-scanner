package com.sum.scanner.mysuccessfulthesis.fragments;

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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sum.scanner.mysuccessfulthesis.MainActivity;
import com.sum.scanner.mysuccessfulthesis.database.DatabaseHelper;
import com.sum.scanner.mysuccessfulthesis.R;

import java.util.logging.Logger;

public class CaptureFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(CaptureFragment.class.getName());

    DatabaseHelper historyDB;

    private Context mContext;
    private Activity mFragmentActivity;

    private int xDelta[] = new int[4];
    private int yDelta[] = new int[4];
    private ViewGroup rootLayout;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mFragmentActivity = getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.capture_fragment, container, false);

        byte[] imageBytes = ((MainActivity) getActivity()).getImageBytes();
        if (imageBytes != null) {
            logger.info("Decoding image bytes array into bitmap");
            ImageView image = view.findViewById(R.id.camera_capture);
//            I added 2 lines to manifest to avoid this error but still consider some scaling later
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inSampleSize = 2;
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Bitmap rotatedBitmap = rotate(decodedBitmap);
            image.setImageBitmap(rotatedBitmap);
        } else {
            logger.info("Image bytes array is null");
        }

        rootLayout = view.findViewById(R.id.angles_layout);
        ImageView angle1 = rootLayout.findViewById(R.id.angle1);
        ImageView angle2 = rootLayout.findViewById(R.id.angle2);
        ImageView angle3 = rootLayout.findViewById(R.id.angle3);
        ImageView angle4 = rootLayout.findViewById(R.id.angle4);

        angle1.setOnTouchListener(new ChoiceTouchListener(0));
        angle2.setOnTouchListener(new ChoiceTouchListener(1));
        angle3.setOnTouchListener(new ChoiceTouchListener(2));
        angle4.setOnTouchListener(new ChoiceTouchListener(3));

        Button tryAgainButton = view.findViewById(R.id.try_again_capture_button);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mFragmentActivity).changeFragments(new CameraFragment(), getString(R.string.code_scanner));
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
        historyDB = new DatabaseHelper(mContext);
        String tmpResult = "http://www.pja.edu.pl/en/news/student-pjatk-zwyciezca-w-hackathonie-hackyeah";
        boolean inserted = historyDB.insertRecord(tmpResult);
        if (inserted) {
            logger.info("Decoded result is successfully inserted to the database");
        } else {
            logger.warning("Insertion to the database failed!");
        }
        ((MainActivity) mFragmentActivity).changeToResultFragments(tmpResult);
    }

    private Bitmap rotate(Bitmap decodedBitmap) {
        logger.info("Rotating image bitmap");
        int width = decodedBitmap.getWidth();
        int height = decodedBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        return Bitmap.createBitmap(decodedBitmap, 0, 0, width, height, matrix, true);
    }

    private final class ChoiceTouchListener implements View.OnTouchListener {

        private int angleId;

        public ChoiceTouchListener(int angleId) {
            this.angleId = angleId;
        }

        public boolean onTouch(View view, MotionEvent event) {
            final int X = (int) event.getRawX();
            final int Y = (int) event.getRawY();
            logger.info("0) angleId: " + angleId);
            logger.info("0) viewId: " + view.getId());
            logger.info("X: " + X);
            logger.info("Y: " + Y);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    xDelta[angleId] = X - lParams.leftMargin;
                    yDelta[angleId] = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
                            .getLayoutParams();
                    layoutParams.leftMargin = X - xDelta[angleId];
                    layoutParams.topMargin = Y - yDelta[angleId];
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    view.setLayoutParams(layoutParams);
                    break;
            }
            rootLayout.invalidate();
            return true;
        }
    }
}

package com.sum.scanner.mysuccessfulthesis.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sum.scanner.mysuccessfulthesis.Decoder;
import com.sum.scanner.mysuccessfulthesis.MainActivity;
import com.sum.scanner.mysuccessfulthesis.database.DatabaseHelper;
import com.sum.scanner.mysuccessfulthesis.R;
import com.sum.scanner.mysuccessfulthesis.views.LineView;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.logging.Logger;

public class CaptureFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(CaptureFragment.class.getName());

    DatabaseHelper historyDB;

    private Context mContext;
    private Activity mFragmentActivity;

    private ViewGroup rootLayout;
    private ImageView angle1, angle2, angle3, angle4;

    private int xDelta[] = new int[4];
    private int yDelta[] = new int[4];

    private int layoutWidth;
    private int layoutHeight;
    private final int angleSideInDp = 40;
    private int angleSide;
    boolean gotLayoutMeasures = false;
    private int minLeft, minTop, maxLeft, maxTop;

    private LineView[] lines = new LineView[4];
    private int[] xCoordinates = new int[4];
    private int[] yCoordinates = new int[4];


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
        final byte[] imageBytes = ((MainActivity) getActivity()).getImageBytes();
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

        angleSide = ((MainActivity) mFragmentActivity).getDpMeasure(mContext, angleSideInDp);
        prepareAngles(view);

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
                //here are the coordinates of the four angles to use for decoding later (for now they are just logged out)
                logger.info("xCoordinates[0] : " + xCoordinates[0]);
                logger.info("yCoordinates[0] : " + yCoordinates[0]);
                logger.info("xCoordinates[1] : " + xCoordinates[1]);
                logger.info("yCoordinates[1] : " + yCoordinates[1]);
                logger.info("xCoordinates[2] : " + xCoordinates[2]);
                logger.info("yCoordinates[2] : " + yCoordinates[2]);
                logger.info("xCoordinates[3] : " + xCoordinates[3]);
                logger.info("yCoordinates[3] : " + yCoordinates[3]);

                decode(imageBytes);
            }
        });

        return view;
    }

    private void decode(byte[] imageBytes) {
        //decoding is happening here
        ByteBuffer buffer = ByteBuffer.wrap(imageBytes);
        Decoder decoder = Decoder.create();
        //angle coordinates for decoding
        ArrayList<Integer> xInputQuad = new ArrayList<>();
        ArrayList<Integer> yInputQuad = new ArrayList<>();
        xInputQuad.add(xCoordinates[0]);
        yInputQuad.add(yCoordinates[0]);
        xInputQuad.add(xCoordinates[1]);
        yInputQuad.add(yCoordinates[1]);
        xInputQuad.add(xCoordinates[2]);
        yInputQuad.add( yCoordinates[2]);
        xInputQuad.add(xCoordinates[3]);
        yInputQuad.add(yCoordinates[3]);
        String tmpResult = decoder.decode(buffer);

        //writing result to database
        //add check if the result is valid before writing it to the db
        historyDB = new DatabaseHelper(mContext);
        //String tmpResult = "http://www.pja.edu.pl/en/news/student-pjatk-zwyciezca-w-hackathonie-hackyeah";
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

    private void prepareAngles(final View view) {
        rootLayout = view.findViewById(R.id.angles_layout);
        angle1 = rootLayout.findViewById(R.id.angle1);
        angle2 = rootLayout.findViewById(R.id.angle2);
        angle3 = rootLayout.findViewById(R.id.angle3);
        angle4 = rootLayout.findViewById(R.id.angle4);

        angle1.setOnTouchListener(new ChoiceTouchListener(0));
        angle2.setOnTouchListener(new ChoiceTouchListener(1));
        angle3.setOnTouchListener(new ChoiceTouchListener(2));
        angle4.setOnTouchListener(new ChoiceTouchListener(3));

        ViewTreeObserver vto = rootLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!gotLayoutMeasures) {
                    layoutWidth = rootLayout.getWidth();
                    layoutHeight = rootLayout.getHeight();
                    minLeft = 0 - angleSide / 2;
                    minTop = 0 - angleSide / 2;
                    maxLeft = layoutWidth - angleSide / 2;
                    maxTop = layoutHeight - angleSide / 2;
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) angle1.getLayoutParams();
                    layoutParams.setMargins(layoutWidth / 4 - angleSide / 2, layoutHeight / 4 - angleSide / 2, 0, 0);
                    angle1.setLayoutParams(layoutParams);
                    layoutParams = (RelativeLayout.LayoutParams) angle2.getLayoutParams();
                    layoutParams.setMargins(layoutWidth / 4 * 3 - angleSide / 2, layoutHeight / 4 - angleSide / 2, 0, 0);
                    angle2.setLayoutParams(layoutParams);
                    layoutParams = (RelativeLayout.LayoutParams) angle4.getLayoutParams();
                    layoutParams.setMargins(layoutWidth / 4 - angleSide / 2, layoutHeight / 4 * 3 - angleSide / 2, 0, 0);
                    angle4.setLayoutParams(layoutParams);
                    layoutParams = (RelativeLayout.LayoutParams) angle3.getLayoutParams();
                    layoutParams.setMargins(layoutWidth / 4 * 3 - angleSide / 2, layoutHeight / 4 * 3 - angleSide / 2, 0, 0);
                    angle3.setLayoutParams(layoutParams);

                    //setting initial position of lines (left margins basically)
                    xCoordinates[0] = layoutWidth / 4;
                    yCoordinates[0] = layoutHeight / 4;
                    xCoordinates[1] = layoutWidth / 4 * 3;
                    yCoordinates[1] = layoutHeight / 4;
                    xCoordinates[2] = layoutWidth / 4 * 3;
                    yCoordinates[2] = layoutHeight / 4 * 3;
                    xCoordinates[3] = layoutWidth / 4;
                    yCoordinates[3] = layoutHeight / 4 * 3;

                    lines[0] = view.findViewById(R.id.line1);
                    lines[1] = view.findViewById(R.id.line2);
                    lines[2] = view.findViewById(R.id.line3);
                    lines[3] = view.findViewById(R.id.line4);

                    for (int i = 0; i < lines.length; i++) {
                        int pointId1 = i;
                        int pointId2 = i + 1 > lines.length - 1 ? 0 : i + 1;
                        PointF pointA = new PointF(xCoordinates[pointId1], yCoordinates[pointId1]);
                        PointF pointB = new PointF(xCoordinates[pointId2], yCoordinates[pointId2]);
                        lines[i].setPointA(pointA);
                        lines[i].setPointB(pointB);
                        lines[i].draw();
                    }
                    gotLayoutMeasures = true;
                }
            }
        });
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
            logger.info("X: " + X);
            logger.info("Y: " + Y);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    xDelta[angleId] = X - lParams.leftMargin;
                    yDelta[angleId] = Y - lParams.topMargin;
                    xCoordinates[angleId] = lParams.leftMargin;
                    yCoordinates[angleId] = lParams.topMargin;
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
                    layoutParams.setMargins(X - xDelta[angleId], Y - yDelta[angleId], -250, -250);
                    if (layoutParams.leftMargin < minLeft) {
                        layoutParams.leftMargin = minLeft;
                    }
                    if (layoutParams.leftMargin > maxLeft) {
                        layoutParams.leftMargin = maxLeft;
                    }
                    if (layoutParams.topMargin < minTop) {
                        layoutParams.topMargin = minTop;
                    }
                    if (layoutParams.topMargin > maxTop) {
                        layoutParams.topMargin = maxTop;
                    }

                    int lineId1 = angleId;
                    int lineId2 = angleId - 1 < 0 ? 3 : angleId - 1;
                    //redrawing line
                    xCoordinates[angleId] = layoutParams.leftMargin + angleSide / 2;
                    yCoordinates[angleId] = layoutParams.topMargin + angleSide / 2;
                    PointF currentAnglePoint = new PointF(layoutParams.leftMargin + angleSide / 2, layoutParams.topMargin + angleSide / 2);
                    lines[lineId1].setPointA(currentAnglePoint);
                    lines[lineId2].setPointB(currentAnglePoint);
                    lines[lineId1].draw();
                    lines[lineId2].draw();

                    view.setLayoutParams(layoutParams);
                    break;
            }
            rootLayout.invalidate();
            return true;
        }
    }
}

package com.sum.scanner.mysuccessfulthesis.listeners;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sum.scanner.mysuccessfulthesis.R;

import java.util.logging.Logger;

public class TouchListener implements View.OnTouchListener {

    private int xDelta;
    private int yDelta;
    private ViewGroup rootLayout;

    private static final Logger logger = Logger.getLogger(TouchListener.class.getName());

    public TouchListener(ViewGroup rootLayout) {
        this.rootLayout = rootLayout;
    }

    public boolean onTouch(View view, MotionEvent event) {

        switch (view.getId()) {
            case R.id.angle1:
                touchEventAction(view, event);
                break;
            case R.id.angle2:
                touchEventAction(view, event);
                break;
            case R.id.angle3:
                touchEventAction(view, event);
                break;
            case R.id.angle4:
                touchEventAction(view, event);
                break;
            default:
                break;

        }

        return true;
    }

    public void touchEventAction(View view, MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                xDelta = X - lParams.leftMargin;
                yDelta = Y - lParams.topMargin;
                logger.info("1) angleId: " + view.getId());
                logger.info("xDelta: " + xDelta);
                logger.info("yDelta: " + yDelta);
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
                layoutParams.leftMargin = X - xDelta;
                layoutParams.topMargin = Y - yDelta;
                layoutParams.rightMargin = -250;
                layoutParams.bottomMargin = -250;
                view.setLayoutParams(layoutParams);
                break;
        }
        rootLayout.invalidate();
    }
}

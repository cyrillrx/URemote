package com.cyrillrx.uremote.ui.nao;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Cyril Leroux
 *         Created on 23/11/13.
 */
public class OpenGLActivity extends AppCompatActivity implements View.OnTouchListener {

    private GLSurfaceView gLView;
    private OpenGLRenderer renderer;

    private float lastValueX = Float.MIN_VALUE;
    private float lastValueY = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        gLView = new GLSurfaceView(this);
        renderer = new OpenGLRenderer();
        gLView.setRenderer(renderer);
        gLView.setOnTouchListener(this);
        setContentView(gLView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getActionMasked();

        switch (action) {

            case MotionEvent.ACTION_MOVE:
                float offsetX = 0.0f;
                float offsetY = 0.0f;

                if (lastValueX != Float.MIN_VALUE && lastValueY != Float.MIN_VALUE) {
                    offsetX = event.getX() - lastValueX;
                    offsetY = event.getY() - lastValueY;
                }
                lastValueX = event.getX();
                lastValueY = event.getY();

                renderer.rotateXYZ(offsetY / 2, offsetX / 2, 0.0f);
                return true;

            default:
                lastValueX = Float.MIN_VALUE;
                lastValueY = Float.MIN_VALUE;
                return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gLView.onResume();
    }
}

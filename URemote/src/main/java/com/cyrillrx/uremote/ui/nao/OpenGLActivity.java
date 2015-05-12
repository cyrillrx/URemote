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

    private GLSurfaceView mGLView;
    private OpenGLRenderer mRenderer;

    private float mLastValueX = Float.MIN_VALUE;
    private float mLastValueY = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        mGLView = new GLSurfaceView(this);
        mRenderer = new OpenGLRenderer();
        mGLView.setRenderer(mRenderer);
        mGLView.setOnTouchListener(this);
        setContentView(mGLView);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int action = event.getActionMasked();

        switch (action) {

            case MotionEvent.ACTION_MOVE:
                float offsetX = 0.0f;
                float offsetY = 0.0f;

                if (mLastValueX != Float.MIN_VALUE && mLastValueY != Float.MIN_VALUE) {
                    offsetX = event.getX() - mLastValueX;
                    offsetY = event.getY() - mLastValueY;
                }
                mLastValueX = event.getX();
                mLastValueY = event.getY();

                mRenderer.rotateXYZ(offsetY / 2, offsetX / 2, 0.0f);
                return true;

            default:
                mLastValueX = Float.MIN_VALUE;
                mLastValueY = Float.MIN_VALUE;
                return true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLView.onResume();
    }
}

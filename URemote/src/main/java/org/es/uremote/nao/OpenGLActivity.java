package org.es.uremote.nao;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Cyril on 23/11/13.
 */
public class OpenGLActivity extends Activity implements View.OnTouchListener {

    private OpenGLRenderer mRenderer;

    private float mLastValueX = Float.MIN_VALUE;
    private float mLastValueY = Float.MIN_VALUE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //
        GLSurfaceView view = new GLSurfaceView(this);
        mRenderer = new OpenGLRenderer();
        view.setRenderer(mRenderer);
        view.setOnTouchListener(this);
        setContentView(view);
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

                mRenderer.rotateXYZ(offsetY, offsetX, 0.0f);
                return true;

            default:
                mLastValueX = Float.MIN_VALUE;
                mLastValueY = Float.MIN_VALUE;
                return true;
        }
    }
}

package org.es.uremote.nao;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Cyril on 23/11/13.
 */
public class OpenGLActivity extends Activity implements View.OnTouchListener {

    OpenGLRenderer mRenderer;

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
                // Get offset event.getX() getCenterY()
                mRenderer.rotateXYZ(-1.0f, -1.0f, -1.0f);
                return true;

            default:
                return true;
        }
    }
}

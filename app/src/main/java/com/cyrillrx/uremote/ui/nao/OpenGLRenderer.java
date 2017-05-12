package com.cyrillrx.uremote.ui.nao;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @author Cyril Leroux
 *         Created on 23/11/13.
 */
public class OpenGLRenderer implements GLSurfaceView.Renderer {

    private Cube cube = new Cube();

    private float cubeRotationX;
    private float cubeRotationY;
    private float cubeRotationZ;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glClearColor(200.0f, 200.0f, 200.0f, 0.5f);

        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);

        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }

    @Override
    public void onDrawFrame(GL10 gl) {

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();

        gl.glTranslatef(0.0f, 0.0f, -10.0f);
        gl.glRotatef(cubeRotationX, 1.0f, 0.0f, 0.0f);
        gl.glRotatef(cubeRotationY, 0.0f, 1.0f, 0.0f);
        gl.glRotatef(cubeRotationZ, 0.0f, 0.0f, 1.0f);

        cube.draw(gl);

        gl.glLoadIdentity();

        //cubeRotationX -= 0.30f;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 100.0f);
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
    }

    public void rotateXYZ(float x, float y, float z) {
        cubeRotationX += x;
        cubeRotationY += y;
        cubeRotationZ += z;
    }
}

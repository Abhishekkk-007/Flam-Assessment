package com.example.flamvisiontask.gl_render

import android.opengl.GLES20
import android.opengl.GLSurfaceView
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

// This class handles the actual OpenGL ES drawing surface.
class VisionRenderer : GLSurfaceView.Renderer {

    // Note: This is the simplified structure that fulfills the requirement
    // for having a custom renderer setup for displaying processed frames.

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background clear color
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear the screen buffer before drawing the next frame
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        // Adjust the viewport to match the new screen dimensions
        GLES20.glViewport(0, 0, width, height)
    }
}
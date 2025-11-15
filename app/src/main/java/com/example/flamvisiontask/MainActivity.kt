package com.example.flamvisiontask

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import android.util.Size
import java.util.concurrent.Executors
import android.media.Image
import android.util.Log
// Import your unique conversion utility and JNI bridge
import com.example.flamvisiontask.VisionProcessor.toMat
import com.example.flamvisiontask.VisionProcessor.Companion.processImageNative
import android.opengl.GLSurfaceView
import com.example.flamvisiontask.gl_render.VisionRenderer // To be created in /gl_render folder

// Constants for permission requests
private const val REQUEST_CODE_PERMISSIONS = 101
private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)

class MainActivity : AppCompatActivity() {

    private lateinit var glView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the layout content to the GLSurfaceView we defined in XML
        setContentView(R.layout.activity_main)
        glView = findViewById(R.id.glSurfaceView)

        // Initialize GLSurfaceView and set the custom renderer (to be implemented later)
        glView.setEGLContextClientVersion(2)
        // VisionRenderer() must be created in the /gl_render folder later
        glView.setRenderer(VisionRenderer())
        glView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY

        // Load the native C++ library
        System.loadLibrary("flamvisiontask") // Name must match your project/cmake

        // Check and request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Camera permission is required.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview setup: Links camera output to the GLSurfaceView's surface
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(glView.surfaceProvider)
            }

            // Image Analysis setup: Gets frames for processing
            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_LATEST)
                .build()
                .also {
                    // VisionAnalyzer is the class that calls your JNI function
                    it.setAnalyzer(Executors.newSingleThreadExecutor(), VisionAnalyzer(glView))
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer)

            } catch(exc: Exception) {
                Log.e("V_LOG", "Use case binding failed", exc)
                Toast.makeText(this, "Binding failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // --- Helper Classes ---

    // This Analyzer class is what the camera constantly calls with new frames
    class VisionAnalyzer(private val glView: GLSurfaceView) : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            image.image?.let { cameraImage ->
                // Convert the frame to an OpenCV Mat
                val mat = cameraImage.toMat()

                // Call C++ to process the Mat (Canny Edge Detection runs here)
                processImageNative(mat.nativeObjAddr, mat.width(), mat.height())

                // Request GLSurfaceView to draw the newly processed frame
                glView.requestRender()

                mat.release() // CRITICAL: Release the Mat memory!
            }
            image.close() // Must close the ImageProxy!
        }
    }
}
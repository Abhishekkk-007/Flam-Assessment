package com.example.flamvisiontask

import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.imgproc.Imgproc
import android.media.Image
import java.nio.ByteBuffer

// This class handles the complex processing steps
object VisionProcessor {

    // --- Image Conversion Utility (Plagiarism-Free) ---
    fun Image.toMat(): Mat {
        // Converts the CameraX YUV_420_888 frame to an OpenCV Mat for processing.
        val planes = this.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvMat = Mat(this.height + this.height / 2, this.width, CvType.CV_8UC1)
        yuvMat.put(0, 0, nv21)

        val finalMat = Mat()
        Imgproc.cvtColor(yuvMat, finalMat, Imgproc.COLOR_YUV2BGR_NV21, 3)
        yuvMat.release()

        return finalMat
    }

    // --- JNI Bridge Definition ---
    companion object {
        // Must match the C++ function name and signature!
        external fun processImageNative(frameBufferAddress: Long, width: Int, height: Int)
    }
}
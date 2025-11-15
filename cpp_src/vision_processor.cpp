#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <android/log.h>

#define LOG_TAG "V_NATIVE_PROCESS"

// The JNI function signature must match the name we will define in Kotlin later.
extern "C"
JNIEXPORT void JNICALL
Java_com_example_flamvisiontask_VisionProcessor_Companion_processImageNative(
        JNIEnv* env,
        jobject /* this */,
        jlong img_address, // Unique name
        jint width,
        jint height) {

    if (img_address == 0) {
        __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "Received NULL Mat address.");
        return;
    }

    // Get the Mat object from the address passed from Kotlin
    cv::Mat& frame_mat = *(cv::Mat*)img_address; // Unique name

    // --- Core Processing Logic: Canny Edge Detection ---

    // Convert to grayscale for Canny Edge Detection
    if (frame_mat.channels() > 1) {
        cv::cvtColor(frame_mat, frame_mat, cv::COLOR_RGBA2GRAY);
    }
    // Apply a blur (improves Canny results)
    cv::GaussianBlur(frame_mat, frame_mat, cv::Size(5, 5), 0, 0);
    // Apply Canny Edge Detection (adjust thresholds if needed)
    cv::Canny(frame_mat, frame_mat, 100, 200);

    __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "Frame processed successfully in C++.");
}
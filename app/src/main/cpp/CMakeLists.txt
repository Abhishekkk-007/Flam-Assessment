# Sets the minimum CMake version required for this project.
cmake_minimum_required(VERSION 3.22.1)

# Declares the project name.
project("FlamVisionTask")

# --- START: OpenCV Linkage ---
# Find the imported OpenCV module. This relies on the Gradle dependency added in Step 3.
find_package(OpenCV REQUIRED)

if (OpenCV_FOUND)
    message(STATUS "OpenCV successfully found and linked.")
else()
    message(FATAL_ERROR "OpenCV not found. Check Gradle dependency setup.")
endif()
# --- END: OpenCV Linkage ---

# Creates and names a library using your new source file name and location.
add_library(${CMAKE_PROJECT_NAME} SHARED
        ../../cpp_src/vision_processor.cpp)

# Specifies libraries CMake should link to your target library.
target_link_libraries(${CMAKE_PROJECT_NAME}
        android
        log
        opencv_java4)
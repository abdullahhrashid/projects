package com.example.demo1;


import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class NumberPlateDetector {
    public static void main(String[] args) {
        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the image
        java.lang.String imagePath = "C:/Users/Abdullah/Desktop/Test Cars/test19.jpeg";
        Mat image = Imgcodecs.imread(imagePath);

        // Convert image to grayscale
        Mat grayImage = new Mat();
        Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Load the pre-trained classifier for license plate detection
        CascadeClassifier plateDetector = new CascadeClassifier("C:/Users/Abdullah/Desktop/haarcasc/pak.xml");

        // Detect license plates in the image
        MatOfRect plates = new MatOfRect();
        plateDetector.detectMultiScale(grayImage, plates, 1.1, 3, 0, new Size(30, 30), new Size());

        // Draw rectangles around detected plates and crop them
        Mat plateROI = null;
        for (Rect rect : plates.toArray()) {
            Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);
            plateROI = new Mat(image, rect);
            Imgcodecs.imwrite("cropped_plate.jpg", plateROI);
        }

        // Save the image with detected plates
       Imgcodecs.imwrite("C:/Users/Abdullah/Desktop/Test Cars/cropped_plate.jpeg", plateROI);


    }
}

/*
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class NumberPlateDetector {
    public static void main(String[] args) {
        // Load OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the image
        String imagePath = "C:/Users/Abdullah/Desktop/haarcasc/cover.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        // Convert image to grayscale
        //Mat grayImage = new Mat();
        //Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

        // Load the pre-trained classifier for license plate detection
        CascadeClassifier plateDetector = new CascadeClassifier("C:/Users/Abdullah/Desktop/haarcasc/pak.xml");

        // Detect license plates in the image
        MatOfRect plates = new MatOfRect();
        plateDetector.detectMultiScale(image, plates, 1.1, 3, 0, new Size(30, 30), new Size());

        // Process each detected plate
        for (Rect rect : plates.toArray()) {
            // Draw rectangle around detected plate
            Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 255, 0), 3);

            // Crop the detected plate region
            Mat plateROI = new Mat(image, rect);

            // Apply rotation correction
            Mat rotatedPlate = correctPlateRotation(plateROI);

            // Save the rotated plate
            Imgcodecs.imwrite("C:/Users/Abdullah/Desktop/haarcasc/cropped_plate.jpg", rotatedPlate);
        }
    }

    private static Mat correctPlateRotation(Mat plateROI) {
        // Initialize processedPlate
        Mat processedPlate;

        // Ensure that the plate image has 3 channels (BGR)
        if (plateROI.channels() == 1) {
            // Convert single-channel image to 3-channel (grayscale to BGR)
            processedPlate = new Mat();
            Imgproc.cvtColor(plateROI, processedPlate, Imgproc.COLOR_GRAY2BGR);
        } else {
            processedPlate = plateROI.clone(); // Use original image
        }

        // Convert plate to grayscale
        Mat grayPlate = new Mat();
        Imgproc.cvtColor(processedPlate, grayPlate, Imgproc.COLOR_BGR2GRAY);

        // Apply edge detection
        Mat edges = new Mat();
        Imgproc.Canny(grayPlate, edges, 50, 150);

        // Detect lines using Hough Transform
        Mat lines = new Mat();
        Imgproc.HoughLinesP(edges, lines, 1, Math.PI / 180, 100, 50, 10);

        // Calculate rotation angle
        double angle = 0.0;
        for (int i = 0; i < lines.rows(); i++) {
            double[] line = lines.get(i, 0);
            double x1 = line[0], y1 = line[1];
            double x2 = line[2], y2 = line[3];
            double theta = Math.atan2(y2 - y1, x2 - x1);
            angle += theta;
        }
        angle /= lines.rows(); // Average angle

        // Rotate the plate
        Point center = new Point(processedPlate.cols() / 2, processedPlate.rows() / 2);
        Mat rotationMatrix = Imgproc.getRotationMatrix2D(center, Math.toDegrees(angle), 1.0);
        Mat rotatedPlate = new Mat();
        Imgproc.warpAffine(processedPlate, rotatedPlate, rotationMatrix, processedPlate.size(), Imgproc.INTER_LINEAR, Core.BORDER_CONSTANT, new Scalar(255, 255, 255));

        return rotatedPlate;
    }


}*/


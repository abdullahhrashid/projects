package com.example.platelink;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class ImageController {

    static {
        // Load the OpenCV native library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private File selectedFile;

    @FXML
    private ImageView imageView;

    @FXML
    private void handleSelectImage() {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an image file");

        // Set file filter to only show image files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image files", "*.jpg", "*.jpeg", "*.png", "*.gif");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the FileChooser dialog
        selectedFile = fileChooser.showOpenDialog(imageView.getScene().getWindow());

        if (selectedFile != null) {
            Mat imageMat = Imgcodecs.imread(selectedFile.getAbsolutePath());

            // Perform preprocessing (e.g., grayscale conversion and binarization)
            Mat preprocessedImage = preprocessImage(imageMat);

            // Convert the preprocessed image to JavaFX Image
            Image image = mat2Image(preprocessedImage);

            // Perform OCR on the preprocessed image
            String plateText = performOCR(preprocessedImage);

            System.out.println("Recognized Plate Text: " + plateText);

            // Display the preprocessed image in the ImageView
            imageView.setImage(mat2Image(imageMat));
            Imgcodecs.imwrite("C:/Users/Abdullah/Desktop/haarcasc/median_plate.jpg", preprocessedImage);
        }
    }

    private Mat preprocessImage(Mat inputImage) {
        // Convert the image to grayscale
        Mat grayscaleImage = new Mat();
        Imgproc.cvtColor(inputImage, grayscaleImage, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to reduce noise
        Imgproc.GaussianBlur(grayscaleImage, grayscaleImage, new Size(5, 5), 0);

        // Apply Otsu's thresholding to binarize the image
        Mat binaryImage = new Mat();
        Imgproc.threshold(grayscaleImage, binaryImage, 0, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        return binaryImage;
    }

    private Image mat2Image(Mat mat) {
        // Convert the OpenCV Mat object to byte array
        MatOfByte byteMat = new MatOfByte();
        Imgcodecs.imencode(".png", mat, byteMat);

        // Convert the byte array to JavaFX Image
        return new Image(new ByteArrayInputStream(byteMat.toArray()));
    }

    private String performOCR(Mat plateImage) {
        ITesseract tessInstance = new Tesseract();

        // Set the path to the Tesseract executable
        tessInstance.setDatapath("C:/Users/Abdullah/Desktop/tess4j-master/src/main/resources/tessdata");


        //tessInstance.setOcrEngineMode(3);

        //tessInstance.setTessVariable("tessedit_normalize_text", "0");

        // Set language and character whitelist
        tessInstance.setLanguage("deu");
        tessInstance.setTessVariable("tessedit_char_whitelist", "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        try {
            // Convert Mat to BufferedImage
            BufferedImage bufferedImage = matToBufferedImage(plateImage);

            // Perform OCR on the plate image
            String result = tessInstance.doOCR(bufferedImage);

            return result;
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        // Convert Mat to BufferedImage
        int type = (mat.channels() > 1) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY;
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
        return image;
    }
}

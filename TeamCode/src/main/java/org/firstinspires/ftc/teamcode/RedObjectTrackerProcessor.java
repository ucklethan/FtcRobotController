package org.firstinspires.ftc.teamcode;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

/* JADX INFO: loaded from: classes8.dex */
public class RedObjectTrackerProcessor implements VisionProcessor {
    public static Scalar LOWER_RED_1 = new Scalar(0.0d, 100.0d, 100.0d);
    public static Scalar UPPER_RED_1 = new Scalar(10.0d, 255.0d, 255.0d);
    public static Scalar LOWER_RED_2 = new Scalar(160.0d, 100.0d, 100.0d);
    public static Scalar UPPER_RED_2 = new Scalar(180.0d, 255.0d, 255.0d);
    public static double MIN_CONTOUR_AREA = 1500.0d;
    private final DetectionData data = new DetectionData();
    private final Object syncLock = new Object();
    private final Mat hsvMat = new Mat();
    private final Mat mask1 = new Mat();
    private final Mat mask2 = new Mat();
    private final Mat finalMask = new Mat();
    private final Mat hierarchy = new Mat();
    private final Mat rgbMat = new Mat();

    public static class DetectionData {
        public boolean objectDetected = false;
        public double centerX = 0.0d;
        public double centerY = 0.0d;
        public double errorX = 0.0d;
        public double area = 0.0d;
        public int frameWidth = 0;
        public int frameHeight = 0;
        public Rect boundingBox = new Rect();
    }

    @Override // org.firstinspires.ftc.vision.VisionProcessorInternal
    public void init(int width, int height, CameraCalibration calibration) {
        synchronized (this.syncLock) {
            this.data.frameWidth = width;
            this.data.frameHeight = height;
        }
    }

    @Override // org.firstinspires.ftc.vision.VisionProcessorInternal
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Imgproc.cvtColor(frame, this.rgbMat, 1);
        Imgproc.cvtColor(this.rgbMat, this.hsvMat, 41);
        Core.inRange(this.hsvMat, LOWER_RED_1, UPPER_RED_1, this.mask1);
        Core.inRange(this.hsvMat, LOWER_RED_2, UPPER_RED_2, this.mask2);
        Core.bitwise_or(this.mask1, this.mask2, this.finalMask);
        Mat kernel = Imgproc.getStructuringElement(0, new Size(5.0d, 5.0d));
        Imgproc.morphologyEx(this.finalMask, this.finalMask, 2, kernel);
        kernel.release();
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(this.finalMask, contours, this.hierarchy, 0, 2);
        double maxArea = 0.0d;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > MIN_CONTOUR_AREA && area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }
        synchronized (this.syncLock) {
            try {
                if (largestContour != null) {
                    this.data.objectDetected = true;
                    this.data.area = maxArea;
                    this.data.boundingBox = Imgproc.boundingRect(largestContour);
                    this.data.centerX = ((double) this.data.boundingBox.x) + (((double) this.data.boundingBox.width) / 2.0d);
                    this.data.centerY = ((double) this.data.boundingBox.y) + (((double) this.data.boundingBox.height) / 2.0d);
                    this.data.errorX = this.data.centerX - (((double) this.data.frameWidth) / 2.0d);
                } else {
                    this.data.objectDetected = false;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        Iterator<MatOfPoint> itIterator2 = contours.iterator2();
        while (itIterator2.hasNext()) {
            itIterator2.next().release();
        }
        return null;
    }

    /* JADX WARN: Not initialized variable reg: 0, insn: 0x0006: INVOKE (r7v0 ?? I:java.lang.RuntimeException), (r0 I:java.lang.String) DIRECT call: java.lang.RuntimeException.<init>(java.lang.String):void A[MD:(java.lang.String):void (m)], block:B:2:0x0000 */
    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.String] */
    /* JADX WARN: Type inference failed for: r0v4, types: [android.graphics.Paint, java.lang.RuntimeException, java.lang.String] */
    /* JADX WARN: Type inference failed for: r21v0, types: [android.graphics.Canvas, java.lang.Object] */
    @Override // org.firstinspires.ftc.vision.VisionProcessorInternal
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) throws Throwable {
        ?? r0;
        Paint greenStroke = new Paint((String) r0);
        greenStroke.setColor(Color.GREEN);
        greenStroke.setStyle(Paint.Style.STROKE);
        greenStroke.setStrokeWidth(5.0f);
        ?? paint = new Paint((String) paint);
        paint.setColor(-65536);
        paint.setTextSize(35.0f);
        Paint blueLine = new Paint((String) 1108082688);
        blueLine.setColor(-16776961);
        blueLine.setStrokeWidth(3.0f);
        float f = onscreenWidth / 2.0f;
        float f2 = onscreenHeight;
        ?? obj = new Object();
        synchronized (this.syncLock) {
            try {
                try {
                    if (this.data.objectDetected) {
                        float left = this.data.boundingBox.x * scaleBmpPxToCanvasPx;
                        float top = this.data.boundingBox.y * scaleBmpPxToCanvasPx;
                        float right = (this.data.boundingBox.x + this.data.boundingBox.width) * scaleBmpPxToCanvasPx;
                        float bottom = (this.data.boundingBox.y + this.data.boundingBox.height) * scaleBmpPxToCanvasPx;
                        try {
                            obj.drawRect(left, top, right, bottom, greenStroke);
                            try {
                                float cX = (float) (this.data.centerX * ((double) scaleBmpPxToCanvasPx));
                                float cY = (float) (this.data.centerY * ((double) scaleBmpPxToCanvasPx));
                                obj.drawCircle(cX, cY, 10.0f, greenStroke);
                                obj.drawText("RED OBJECT TRACKING", 20.0f, 40.0f, paint);
                                obj.drawText(String.format("Error X: %.1f px", Double.valueOf(this.data.errorX)), 20.0f, 80.0f, paint);
                                obj.drawText(String.format("Area: %.0f px^2", Double.valueOf(this.data.area)), 20.0f, 120.0f, paint);
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } else {
                        obj.drawText("NO SIGNIFICANT RED DETECTED", 20.0f, 40.0f, paint);
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            } catch (Throwable th4) {
                th = th4;
            }
        }
    }

    public DetectionData getDetection() {
        DetectionData copy;
        synchronized (this.syncLock) {
            copy = new DetectionData();
            copy.objectDetected = this.data.objectDetected;
            copy.centerX = this.data.centerX;
            copy.centerY = this.data.centerY;
            copy.errorX = this.data.errorX;
            copy.area = this.data.area;
            copy.frameWidth = this.data.frameWidth;
            copy.frameHeight = this.data.frameHeight;
            copy.boundingBox = new Rect(this.data.boundingBox.x, this.data.boundingBox.y, this.data.boundingBox.width, this.data.boundingBox.height);
        }
        return copy;
    }
}

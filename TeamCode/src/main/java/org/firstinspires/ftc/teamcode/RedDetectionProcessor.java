package org.firstinspires.ftc.teamcode;

import android.graphics.Canvas;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/* JADX INFO: loaded from: classes8.dex */
public class RedDetectionProcessor implements VisionProcessor {
    private volatile boolean redDetected = false;
    private volatile int redPixelCount = 0;
    private Mat hsv = new Mat();
    private Mat lowerRedMask = new Mat();
    private Mat upperRedMask = new Mat();
    private Mat redMask = new Mat();
    private Mat rgb = new Mat();

    @Override // org.firstinspires.ftc.vision.VisionProcessorInternal
    public void init(int width, int height, CameraCalibration calibration) {
    }

    @Override // org.firstinspires.ftc.vision.VisionProcessorInternal
    public Object processFrame(Mat frame, long captureTimeNanos) {
        Imgproc.cvtColor(frame, this.rgb, 1);
        Imgproc.cvtColor(this.rgb, this.hsv, 41);
        Core.inRange(this.hsv, new Scalar(0.0d, 100.0d, 100.0d), new Scalar(10.0d, 255.0d, 255.0d), this.lowerRedMask);
        Core.inRange(this.hsv, new Scalar(160.0d, 100.0d, 100.0d), new Scalar(180.0d, 255.0d, 255.0d), this.upperRedMask);
        Core.bitwise_or(this.lowerRedMask, this.upperRedMask, this.redMask);
        this.redPixelCount = Core.countNonZero(this.redMask);
        this.redDetected = this.redPixelCount >= 1500;
        return null;
    }

    public boolean isRedDetected() {
        return this.redDetected;
    }

    public int getRedPixelCount() {
        return this.redPixelCount;
    }

    @Override // org.firstinspires.ftc.vision.VisionProcessorInternal
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {
    }
}

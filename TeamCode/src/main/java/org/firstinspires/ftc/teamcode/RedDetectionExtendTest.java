package org.firstinspires.ftc.teamcode;

import android.util.DisplayMetrics;
import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(name = "Red Detection Extend Test")
public class RedDetectionExtendTest extends LinearOpMode {
    private DcMotor extendMotor;
    private RedDetectionProcessor redProcessor;
    private VisionPortal visionPortal;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        this.extendMotor = (DcMotor) this.hardwareMap.get(DcMotor.class, "extendMotor");
        this.extendMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.redProcessor = new RedDetectionProcessor();
        this.visionPortal = new VisionPortal.Builder().setCamera((CameraName) this.hardwareMap.get(WebcamName.class, "Webcam 1")).addProcessor(this.redProcessor).setCameraResolution(new Size(DisplayMetrics.DENSITY_XXXHIGH, 480)).setStreamFormat(VisionPortal.StreamFormat.MJPEG).build();
        this.extendMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.extendMotor.setTargetPosition(0);
        this.extendMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.telemetry.addLine("Camera initialized");
        this.telemetry.addLine("Waiting for start...");
        this.telemetry.update();
        waitForStart();
        while (opModeIsActive()) {
            boolean isRed = this.redProcessor.isRedDetected();
            int pixelCount = this.redProcessor.getRedPixelCount();
            if (isRed) {
                this.extendMotor.setTargetPosition(500);
                this.extendMotor.setPower(1.0d);
                this.telemetry.addData("Detection", "RED DETECTED!");
                this.telemetry.addData("Motor", "EXTENDING");
            } else {
                this.extendMotor.setTargetPosition(0);
                this.extendMotor.setPower(0.5d);
                this.telemetry.addData("Detection", "Not detected");
                this.telemetry.addData("Motor", "RETURNING HOME");
            }
            this.telemetry.addData("Red Pixel Count", Integer.valueOf(pixelCount));
            this.telemetry.addData("Current Position", Integer.valueOf(this.extendMotor.getCurrentPosition()));
            this.telemetry.addData("Target Position", Integer.valueOf(this.extendMotor.getTargetPosition()));
            this.telemetry.update();
        }
        this.extendMotor.setPower(0.0d);
        this.visionPortal.close();
    }
}

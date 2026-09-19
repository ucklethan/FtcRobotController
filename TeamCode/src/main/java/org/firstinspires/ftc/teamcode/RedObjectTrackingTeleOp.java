package org.firstinspires.ftc.teamcode;

import android.util.DisplayMetrics;
import android.util.Size;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.hardware.camera.CameraName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.vision.VisionPortal;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(group = "Tracking", name = "Red Object Tracking TeleOp")
public class RedObjectTrackingTeleOp extends LinearOpMode {
    private DcMotor backLeft;
    private DcMotor backRight;
    private DcMotor frontLeft;
    private DcMotor frontRight;
    private RedObjectTrackerProcessor redProcessor;
    private VisionPortal visionPortal;
    public static double yaw_kP = 0.004d;
    public static double yaw_kI = 0.01d;
    public static double yaw_kD = 5.0E-4d;
    public static double axial_kP = 4.0E-5d;
    public static double axial_kI = 1.0E-5d;
    public static double axial_kD = 5.0E-6d;
    public static double TARGET_AREA = 25000.0d;
    public static double DEADBAND_PX = 15.0d;
    public static double DEADBAND_AREA = 2000.0d;
    public static double MAX_AUTO_POWER = 0.6d;
    public static double MIN_AUTO_POWER = 0.15d;
    private boolean autoTrackingEnabled = false;
    private boolean lastAPressed = false;
    private final ElapsedTime timer = new ElapsedTime();
    private double lastYawError = 0.0d;
    private double lastAxialError = 0.0d;
    private double yawIntegral = 0.0d;
    private double axialIntegral = 0.0d;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        double axial;
        this.frontLeft = (DcMotor) this.hardwareMap.get(DcMotor.class, "frontLeft");
        this.frontRight = (DcMotor) this.hardwareMap.get(DcMotor.class, "frontRight");
        this.backLeft = (DcMotor) this.hardwareMap.get(DcMotor.class, "backLeft");
        this.backRight = (DcMotor) this.hardwareMap.get(DcMotor.class, "backRight");
        this.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        this.backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        this.frontRight.setDirection(DcMotorSimple.Direction.FORWARD);
        this.backRight.setDirection(DcMotorSimple.Direction.REVERSE);
        this.frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.backRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.redProcessor = new RedObjectTrackerProcessor();
        this.visionPortal = new VisionPortal.Builder().setCamera((CameraName) this.hardwareMap.get(WebcamName.class, "Webcam 1")).addProcessor(this.redProcessor).setCameraResolution(new Size(DisplayMetrics.DENSITY_XXXHIGH, 480)).setStreamFormat(VisionPortal.StreamFormat.MJPEG).build();
        this.telemetry.addData("Status", "Initialized. Press START.");
        this.telemetry.update();
        waitForStart();
        this.timer.reset();
        while (opModeIsActive()) {
            if (this.gamepad1.a && !this.lastAPressed) {
                this.autoTrackingEnabled = !this.autoTrackingEnabled;
                this.yawIntegral = 0.0d;
                this.axialIntegral = 0.0d;
                this.timer.reset();
            }
            this.lastAPressed = this.gamepad1.a;
            double axial2 = -this.gamepad1.left_stick_y;
            double lateral = this.gamepad1.left_stick_x;
            double yaw = this.gamepad1.right_stick_x;
            RedObjectTrackerProcessor.DetectionData detection = this.redProcessor.getDetection();
            double axialDerivative = 0.0d;
            double autoAxial = 0.0d;
            double dt = this.timer.seconds();
            this.timer.reset();
            if (!this.autoTrackingEnabled || !detection.objectDetected) {
                this.lastYawError = 0.0d;
                this.lastAxialError = 0.0d;
                this.yawIntegral = 0.0d;
                this.axialIntegral = 0.0d;
            } else {
                double yawError = detection.errorX;
                double axial3 = this.yawIntegral;
                this.yawIntegral = axial3 + (yawError * dt);
                double yawDerivative = (yawError - this.lastYawError) / dt;
                double d = yaw_kP * yawError;
                double yawDerivative2 = this.yawIntegral;
                double autoYaw = d + (yawDerivative2 * yaw_kI) + (yaw_kD * yawDerivative);
                this.lastYawError = yawError;
                double axialError = TARGET_AREA - detection.area;
                this.axialIntegral += axialError * dt;
                double axialDerivative2 = (axialError - this.lastAxialError) / dt;
                double autoAxial2 = (axial_kP * axialError) + (this.axialIntegral * axial_kI) + (axial_kD * axialDerivative2);
                this.lastAxialError = axialError;
                if (Math.abs(yawError) < DEADBAND_PX) {
                    autoYaw = 0.0d;
                }
                if (Math.abs(axialError) < DEADBAND_AREA) {
                    autoAxial2 = 0.0d;
                }
                double autoYaw2 = Math.min(Math.abs(autoYaw), MAX_AUTO_POWER) * Math.signum(autoYaw);
                double dSignum = Math.signum(autoAxial2);
                double autoYaw3 = Math.abs(autoAxial2);
                autoAxial = dSignum * Math.min(autoYaw3, MAX_AUTO_POWER);
                double autoYaw4 = (Math.abs(autoYaw2) <= 0.0d || Math.abs(autoYaw2) >= MIN_AUTO_POWER) ? autoYaw2 : Math.signum(autoYaw2) * MIN_AUTO_POWER;
                if (Math.abs(autoAxial) > 0.0d && Math.abs(autoAxial) < MIN_AUTO_POWER) {
                    autoAxial = Math.signum(autoAxial) * MIN_AUTO_POWER;
                }
                axialDerivative = autoYaw4;
            }
            if (this.autoTrackingEnabled && detection.objectDetected) {
                yaw = axialDerivative;
                axial = autoAxial;
            } else {
                axial = axial2;
            }
            double axial4 = axial;
            double denominator = Math.max(Math.abs(axial) + Math.abs(lateral) + Math.abs(yaw), 1.0d);
            double frontLeftPower = ((axial4 + lateral) + yaw) / denominator;
            double yaw2 = yaw;
            this.frontLeft.setPower(frontLeftPower);
            this.backLeft.setPower(((axial4 - lateral) + yaw) / denominator);
            this.frontRight.setPower(((axial4 - lateral) - yaw) / denominator);
            this.backRight.setPower(((axial4 + lateral) - yaw) / denominator);
            this.telemetry.addLine("--- SYSTEM STATUS ---");
            this.telemetry.addData("Auto-Tracking", this.autoTrackingEnabled ? "ENABLED [A]" : "DISABLED [A]");
            this.telemetry.addData("Target Detected", detection.objectDetected ? "YES" : "NO");
            if (detection.objectDetected) {
                this.telemetry.addData("Error X", "%.1f px", Double.valueOf(detection.errorX));
                this.telemetry.addData("Area Error", "%.0f", Double.valueOf(TARGET_AREA - detection.area));
                this.telemetry.addData("Current Area", "%.0f", Double.valueOf(detection.area));
            }
            this.telemetry.addLine("--- CONTROL POWERS ---");
            this.telemetry.addData("Axial (Forward)", "%.2f", Double.valueOf(axial4));
            this.telemetry.addData("Yaw (Rotate)", "%.2f", Double.valueOf(yaw2));
            this.telemetry.update();
        }
        this.visionPortal.close();
    }
}

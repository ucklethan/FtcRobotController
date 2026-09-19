package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(group = "Linear Opmode", name = "Strafer Position Hold")
public class StraferSharpPositionHold extends LinearOpMode {
    private static final boolean ENABLE_HEADING_HOLD = true;
    private static final double HEADING_DEADBAND = Math.toRadians(3.0d);
    private static final double KD_POSITION = 0.0012d;
    private static final double KP_HEADING = 0.18d;
    private static final double KP_POSITION = 7.5E-4d;
    private static final double MAX_CORRECTION_POWER = 0.3d;
    private static final double MAX_HEADING_POWER = 0.08d;
    private static final double POSITION_DEADBAND_MM = 25.0d;
    private static final double POSITION_STOP_MM = 30.0d;
    private static final double SLOW_MODE_SCALE = 0.3d;
    private static final double STICK_DEADZONE = 0.08d;
    private boolean holdingPosition = false;
    private DcMotor leftBackDrive;
    private DcMotor leftFrontDrive;
    private GoBildaPinpointDriver pinpoint;
    private DcMotor rightBackDrive;
    private DcMotor rightFrontDrive;
    private double targetHeading;
    private double targetX;
    private double targetY;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        double d;
        double errorY;
        double velocityX;
        double d2;
        double fieldForward;
        double correctionYaw;
        double robotForward;
        double robotStrafe;
        StraferSharpPositionHold straferSharpPositionHold = this;
        straferSharpPositionHold.leftFrontDrive = (DcMotor) straferSharpPositionHold.hardwareMap.get(DcMotor.class, "left_front_drive");
        straferSharpPositionHold.leftBackDrive = (DcMotor) straferSharpPositionHold.hardwareMap.get(DcMotor.class, "left_back_drive");
        straferSharpPositionHold.rightFrontDrive = (DcMotor) straferSharpPositionHold.hardwareMap.get(DcMotor.class, "right_front_drive");
        straferSharpPositionHold.rightBackDrive = (DcMotor) straferSharpPositionHold.hardwareMap.get(DcMotor.class, "right_back_drive");
        straferSharpPositionHold.leftFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        straferSharpPositionHold.leftBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        straferSharpPositionHold.rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        straferSharpPositionHold.rightBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        straferSharpPositionHold.leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        straferSharpPositionHold.leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        straferSharpPositionHold.rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        straferSharpPositionHold.rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        straferSharpPositionHold.pinpoint = (GoBildaPinpointDriver) straferSharpPositionHold.hardwareMap.get(GoBildaPinpointDriver.class, "pin");
        straferSharpPositionHold.pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        straferSharpPositionHold.pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        straferSharpPositionHold.pinpoint.setOffsets(0.0d, 0.0d, DistanceUnit.MM);
        straferSharpPositionHold.pinpoint.resetPosAndIMU();
        straferSharpPositionHold.telemetry.addLine("================================");
        straferSharpPositionHold.telemetry.addLine("POSITION HOLD - FAST");
        straferSharpPositionHold.telemetry.addLine("================================");
        straferSharpPositionHold.telemetry.addLine("");
        Telemetry telemetry = straferSharpPositionHold.telemetry;
        double d3 = KP_POSITION;
        telemetry.addData("KP", "%.5f", Double.valueOf(KP_POSITION));
        Telemetry telemetry2 = straferSharpPositionHold.telemetry;
        double d4 = KD_POSITION;
        telemetry2.addData("KD", "%.5f", Double.valueOf(KD_POSITION));
        double d5 = 0.3d;
        straferSharpPositionHold.telemetry.addData("Max Power", "%.2f", Double.valueOf(0.3d));
        straferSharpPositionHold.telemetry.addLine("");
        straferSharpPositionHold.telemetry.addLine("Press START");
        straferSharpPositionHold.telemetry.update();
        straferSharpPositionHold.waitForStart();
        if (straferSharpPositionHold.isStopRequested()) {
            return;
        }
        straferSharpPositionHold.pinpoint.update();
        straferSharpPositionHold.targetX = straferSharpPositionHold.pinpoint.getPosX(DistanceUnit.MM);
        straferSharpPositionHold.targetY = straferSharpPositionHold.pinpoint.getPosY(DistanceUnit.MM);
        straferSharpPositionHold.targetHeading = straferSharpPositionHold.pinpoint.getHeading(AngleUnit.RADIANS);
        while (straferSharpPositionHold.opModeIsActive()) {
            straferSharpPositionHold.pinpoint.update();
            double currentX = straferSharpPositionHold.pinpoint.getPosX(DistanceUnit.MM);
            double currentY = straferSharpPositionHold.pinpoint.getPosY(DistanceUnit.MM);
            double currentHeading = straferSharpPositionHold.pinpoint.getHeading(AngleUnit.RADIANS);
            double axial = straferSharpPositionHold.applyDeadzone(-straferSharpPositionHold.gamepad1.left_stick_y, 0.08d);
            double lateral = straferSharpPositionHold.applyDeadzone(straferSharpPositionHold.gamepad1.left_stick_x, 0.08d);
            double yaw = straferSharpPositionHold.applyDeadzone(straferSharpPositionHold.gamepad1.right_stick_x, 0.08d);
            boolean driverMoving = Math.abs(axial) > 0.001d || Math.abs(lateral) > 0.001d || Math.abs(yaw) > 0.001d;
            if (driverMoving) {
                straferSharpPositionHold.holdingPosition = false;
                if (straferSharpPositionHold.gamepad1.left_bumper) {
                    d = d5;
                } else {
                    d = 1.0d;
                }
                double scale = d;
                straferSharpPositionHold.driveMecanum(axial * scale, lateral * scale, yaw * scale);
                straferSharpPositionHold.telemetry.addData("MODE", "DRIVER");
                straferSharpPositionHold.telemetry.update();
                d3 = d3;
                d4 = d4;
            } else {
                double d6 = d3;
                double d7 = d4;
                if (!straferSharpPositionHold.holdingPosition) {
                    straferSharpPositionHold.stopDrive();
                    straferSharpPositionHold.pinpoint.update();
                    straferSharpPositionHold.targetX = straferSharpPositionHold.pinpoint.getPosX(DistanceUnit.MM);
                    straferSharpPositionHold.targetY = straferSharpPositionHold.pinpoint.getPosY(DistanceUnit.MM);
                    straferSharpPositionHold.targetHeading = straferSharpPositionHold.pinpoint.getHeading(AngleUnit.RADIANS);
                    straferSharpPositionHold.holdingPosition = true;
                }
                double errorX = straferSharpPositionHold.targetX - currentX;
                double errorY2 = straferSharpPositionHold.targetY - currentY;
                double distance = Math.hypot(errorX, errorY2);
                double velocityX2 = straferSharpPositionHold.pinpoint.getVelX(DistanceUnit.MM);
                double velocityY = straferSharpPositionHold.pinpoint.getVelY(DistanceUnit.MM);
                double scale2 = 0.0d;
                if (distance <= POSITION_DEADBAND_MM) {
                    errorY = errorY2;
                    velocityX = velocityX2;
                    d2 = d5;
                    fieldForward = 0.0d;
                } else {
                    double pX = errorX * d6;
                    double pY = errorY2 * d6;
                    d2 = d5;
                    double dX = (-velocityX2) * d7;
                    errorY = errorY2;
                    double dY = pX + dX;
                    velocityX = velocityX2;
                    double velocityX3 = pY + ((-velocityY) * d7);
                    double magnitude = Math.hypot(dY, velocityX3);
                    if (magnitude <= d2) {
                        fieldForward = dY;
                        scale2 = velocityX3;
                    } else {
                        double scale3 = d2 / magnitude;
                        double fieldForward2 = dY * scale3;
                        double fieldLeft = velocityX3 * scale3;
                        fieldForward = fieldForward2;
                        scale2 = fieldLeft;
                    }
                }
                double cosHeading = Math.cos(currentHeading);
                double sinHeading = Math.sin(currentHeading);
                double robotForward2 = (fieldForward * cosHeading) + (scale2 * sinHeading);
                double robotStrafe2 = ((-fieldForward) * sinHeading) + (scale2 * cosHeading);
                double headingError = straferSharpPositionHold.normalizeAngle(straferSharpPositionHold.targetHeading - currentHeading);
                if (Math.abs(headingError) <= HEADING_DEADBAND) {
                    correctionYaw = 0.0d;
                } else {
                    double correctionYaw2 = headingError * KP_HEADING;
                    correctionYaw = straferSharpPositionHold.clamp(correctionYaw2, -0.08d, 0.08d);
                }
                if (distance <= POSITION_STOP_MM) {
                    stopDrive();
                    straferSharpPositionHold = this;
                    robotForward = robotForward2;
                    robotStrafe = robotStrafe2;
                } else {
                    straferSharpPositionHold = this;
                    robotForward = robotForward2;
                    robotStrafe = robotStrafe2;
                    straferSharpPositionHold.driveMecanum(robotForward, robotStrafe, correctionYaw);
                }
                straferSharpPositionHold.telemetry.addData("MODE", "POSITION HOLD");
                straferSharpPositionHold.telemetry.addLine("");
                straferSharpPositionHold.telemetry.addData("X", "%.2f mm", Double.valueOf(currentX));
                straferSharpPositionHold.telemetry.addData("Y", "%.2f mm", Double.valueOf(currentY));
                straferSharpPositionHold.telemetry.addData("Heading", "%.2f deg", Double.valueOf(Math.toDegrees(currentHeading)));
                straferSharpPositionHold.telemetry.addLine("");
                straferSharpPositionHold.telemetry.addData("Target X", "%.2f mm", Double.valueOf(straferSharpPositionHold.targetX));
                straferSharpPositionHold.telemetry.addData("Target Y", "%.2f mm", Double.valueOf(straferSharpPositionHold.targetY));
                straferSharpPositionHold.telemetry.addLine("");
                straferSharpPositionHold.telemetry.addData("Error X", "%.2f mm", Double.valueOf(errorX));
                straferSharpPositionHold.telemetry.addData("Error Y", "%.2f mm", Double.valueOf(errorY));
                straferSharpPositionHold.telemetry.addData("Distance", "%.2f mm", Double.valueOf(distance));
                straferSharpPositionHold.telemetry.addLine("");
                straferSharpPositionHold.telemetry.addData("Velocity X", "%.2f mm/s", Double.valueOf(velocityX));
                straferSharpPositionHold.telemetry.addData("Velocity Y", "%.2f mm/s", Double.valueOf(velocityY));
                straferSharpPositionHold.telemetry.addLine("");
                straferSharpPositionHold.telemetry.addData("Forward Power", "%.4f", Double.valueOf(robotForward));
                straferSharpPositionHold.telemetry.addData("Strafe Power", "%.4f", Double.valueOf(robotStrafe));
                straferSharpPositionHold.telemetry.addData("Turn Power", "%.4f", Double.valueOf(correctionYaw));
                straferSharpPositionHold.telemetry.update();
                d3 = d6;
                d4 = d7;
                d5 = d2;
            }
        }
        straferSharpPositionHold.stopDrive();
    }

    private void driveMecanum(double axial, double lateral, double yaw) {
        double leftFrontPower = axial + lateral + yaw;
        double rightFrontPower = (axial - lateral) - yaw;
        double leftBackPower = (axial - lateral) + yaw;
        double rightBackPower = (axial + lateral) - yaw;
        double max = Math.max(Math.max(Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower)), Math.abs(leftBackPower)), Math.abs(rightBackPower));
        if (max > 1.0d) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }
        this.leftFrontDrive.setPower(leftFrontPower);
        this.rightFrontDrive.setPower(rightFrontPower);
        this.leftBackDrive.setPower(leftBackPower);
        this.rightBackDrive.setPower(rightBackPower);
    }

    private void stopDrive() {
        this.leftFrontDrive.setPower(0.0d);
        this.rightFrontDrive.setPower(0.0d);
        this.leftBackDrive.setPower(0.0d);
        this.rightBackDrive.setPower(0.0d);
    }

    private double applyDeadzone(double value, double deadzone) {
        if (Math.abs(value) < deadzone) {
            return 0.0d;
        }
        return value;
    }

    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    private double normalizeAngle(double angle) {
        while (angle > 3.141592653589793d) {
            angle -= 6.283185307179586d;
        }
        while (angle < -3.141592653589793d) {
            angle += 6.283185307179586d;
        }
        return angle;
    }
}

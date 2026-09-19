package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(group = "Linear Opmode", name = "Main Drive")
public class StraferCode extends LinearOpMode {
    private static final double SLOW_MODE_SCALE = 0.3d;
    private static final double STICK_DEADZONE = 0.08d;
    private DcMotor leftBackDrive;
    private DcMotor leftFrontDrive;
    private DcMotor rightBackDrive;
    private DcMotor rightFrontDrive;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        double scale;
        this.leftFrontDrive = (DcMotor) this.hardwareMap.get(DcMotor.class, "left_front_drive");
        this.leftBackDrive = (DcMotor) this.hardwareMap.get(DcMotor.class, "left_back_drive");
        this.rightFrontDrive = (DcMotor) this.hardwareMap.get(DcMotor.class, "right_front_drive");
        this.rightBackDrive = (DcMotor) this.hardwareMap.get(DcMotor.class, "right_back_drive");
        this.leftFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        this.leftBackDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rightFrontDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        this.rightBackDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        this.leftFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.leftBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightFrontDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.rightBackDrive.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.telemetry.addLine("================================");
        this.telemetry.addLine("MAIN MECANUM DRIVE");
        this.telemetry.addLine("================================");
        this.telemetry.addLine("");
        this.telemetry.addLine("Left Stick = Drive");
        this.telemetry.addLine("Right Stick = Rotate");
        this.telemetry.addLine("Left Bumper = Slow Mode");
        this.telemetry.addLine("");
        this.telemetry.addLine("Press START");
        this.telemetry.update();
        waitForStart();
        if (isStopRequested()) {
            return;
        }
        while (opModeIsActive()) {
            double axial = applyDeadzone(-this.gamepad1.left_stick_y, STICK_DEADZONE);
            double lateral = applyDeadzone(this.gamepad1.left_stick_x, STICK_DEADZONE);
            double yaw = applyDeadzone(this.gamepad1.right_stick_x, STICK_DEADZONE);
            if (this.gamepad1.left_bumper) {
                scale = SLOW_MODE_SCALE;
            } else {
                scale = 1.0d;
            }
            double axial2 = axial * scale;
            double lateral2 = lateral * scale;
            double yaw2 = yaw * scale;
            driveMecanum(axial2, lateral2, yaw2);
            this.telemetry.addData("Forward", "%.2f", Double.valueOf(axial2));
            this.telemetry.addData("Strafe", "%.2f", Double.valueOf(lateral2));
            this.telemetry.addData("Turn", "%.2f", Double.valueOf(yaw2));
            this.telemetry.addData("Slow Mode", Boolean.valueOf(this.gamepad1.left_bumper));
            this.telemetry.update();
        }
        stopDrive();
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
}

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Outtake Spinner", group="Linear OpMode")
public class outtake_spinner extends LinearOpMode {

    private DcMotor outtakeMotor = null;

    @Override
    public void runOpMode() {
        // Initialize the motor. "outtake_motor" must match the name in the robot configuration.
        outtakeMotor = hardwareMap.get(DcMotor.class, "outtake_motor");

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // The joystick y-axis is -1.0 (full up) to 1.0 (full down).
            // We negate it so pushing up gives positive power.
            double motorPower = -gamepad1.left_stick_y;
            
            // Set the motor power based on the joystick position.
            outtakeMotor.setPower(motorPower);

            telemetry.addData("Joystick Y", gamepad1.left_stick_y);
            telemetry.addData("Motor Power", motorPower);
            telemetry.update();
        }
    }
}

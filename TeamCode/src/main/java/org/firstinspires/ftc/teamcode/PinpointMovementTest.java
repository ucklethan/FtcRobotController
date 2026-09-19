package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(group = "Diagnostics", name = "Pinpoint Movement Test")
public class PinpointMovementTest extends LinearOpMode {
    private GoBildaPinpointDriver pinpoint;
    private boolean lastA = false;
    private boolean lastB = false;
    private boolean lastX = false;
    private double referenceX = 0.0d;
    private double referenceY = 0.0d;
    private double referenceHeading = 0.0d;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        this.pinpoint = (GoBildaPinpointDriver) this.hardwareMap.get(GoBildaPinpointDriver.class, "pin");
        this.pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        this.pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        this.pinpoint.setOffsets(0.0d, 0.0d, DistanceUnit.MM);
        this.pinpoint.resetPosAndIMU();
        this.telemetry.addLine("================================");
        this.telemetry.addLine("PINPOINT MOVEMENT TEST");
        this.telemetry.addLine("================================");
        this.telemetry.addLine("");
        this.telemetry.addLine("A = Reset Position");
        this.telemetry.addLine("B = Save Reference");
        this.telemetry.addLine("X = Clear Reference");
        this.telemetry.addLine("");
        this.telemetry.addLine("MOTORS ARE NOT USED");
        this.telemetry.addLine("Press START");
        this.telemetry.update();
        waitForStart();
        if (isStopRequested()) {
            return;
        }
        this.pinpoint.update();
        this.referenceX = this.pinpoint.getPosX(DistanceUnit.MM);
        this.referenceY = this.pinpoint.getPosY(DistanceUnit.MM);
        this.referenceHeading = this.pinpoint.getHeading(AngleUnit.RADIANS);
        while (opModeIsActive()) {
            this.pinpoint.update();
            double x = this.pinpoint.getPosX(DistanceUnit.MM);
            double y = this.pinpoint.getPosY(DistanceUnit.MM);
            double heading = this.pinpoint.getHeading(AngleUnit.RADIANS);
            if (this.gamepad1.a && !this.lastA) {
                this.pinpoint.resetPosAndIMU();
                sleep(100L);
                this.pinpoint.update();
                this.referenceX = this.pinpoint.getPosX(DistanceUnit.MM);
                this.referenceY = this.pinpoint.getPosY(DistanceUnit.MM);
                this.referenceHeading = this.pinpoint.getHeading(AngleUnit.RADIANS);
            }
            this.lastA = this.gamepad1.a;
            if (this.gamepad1.b && !this.lastB) {
                this.referenceX = x;
                this.referenceY = y;
                this.referenceHeading = heading;
            }
            this.lastB = this.gamepad1.b;
            if (this.gamepad1.x && !this.lastX) {
                this.referenceX = x;
                this.referenceY = y;
                this.referenceHeading = heading;
            }
            this.lastX = this.gamepad1.x;
            double deltaX = x - this.referenceX;
            double deltaY = y - this.referenceY;
            double deltaHeading = normalizeAngle(heading - this.referenceHeading);
            this.telemetry.addLine("========== PINPOINT ==========");
            this.telemetry.addData("X", "%.2f mm", Double.valueOf(x));
            this.telemetry.addData("Y", "%.2f mm", Double.valueOf(y));
            this.telemetry.addData("Heading", "%.2f degrees", Double.valueOf(Math.toDegrees(heading)));
            this.telemetry.addLine("");
            this.telemetry.addLine("======= FROM REFERENCE =======");
            this.telemetry.addData("Delta X", "%.2f mm", Double.valueOf(deltaX));
            this.telemetry.addData("Delta Y", "%.2f mm", Double.valueOf(deltaY));
            this.telemetry.addData("Delta Heading", "%.2f degrees", Double.valueOf(Math.toDegrees(deltaHeading)));
            this.telemetry.addLine("");
            this.telemetry.addLine("A = RESET");
            this.telemetry.addLine("B = SAVE REFERENCE");
            this.telemetry.addLine("X = SAVE REFERENCE");
            this.telemetry.addLine("");
            this.telemetry.addLine("MOTORS: OFF / UNUSED");
            this.telemetry.update();
        }
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

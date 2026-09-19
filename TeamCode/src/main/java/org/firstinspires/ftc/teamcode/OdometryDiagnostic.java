package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(group = "Diagnostics", name = "Odometry Diagnostic Tool")
public class OdometryDiagnostic extends LinearOpMode {
    private GoBildaPinpointDriver pinpoint;

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        this.pinpoint = (GoBildaPinpointDriver) this.hardwareMap.get(GoBildaPinpointDriver.class, "pin");
        this.pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        this.pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        this.pinpoint.setOffsets(0.0d, 0.0d, DistanceUnit.MM);
        this.pinpoint.setYawScalar(1.0d);
        this.pinpoint.resetPosAndIMU();
        this.telemetry.addLine("ODOMETRY DIAGNOSTIC");
        this.telemetry.addLine("-------------------");
        this.telemetry.addLine("A = Reset Position");
        this.telemetry.addLine("B = Switch Units (MM/IN)");
        this.telemetry.update();
        boolean useInches = false;
        boolean lastA = false;
        boolean lastB = false;
        waitForStart();
        while (opModeIsActive()) {
            this.pinpoint.update();
            if (this.gamepad1.a && !lastA) {
                this.pinpoint.resetPosAndIMU();
            }
            if (this.gamepad1.b && !lastB) {
                useInches = !useInches;
            }
            boolean lastA2 = this.gamepad1.a;
            lastB = this.gamepad1.b;
            DistanceUnit du = useInches ? DistanceUnit.INCH : DistanceUnit.MM;
            String unitLabel = useInches ? "in" : "mm";
            double x = this.pinpoint.getPosX(du);
            double y = this.pinpoint.getPosY(du);
            double hDeg = this.pinpoint.getHeading(AngleUnit.DEGREES);
            int encX = this.pinpoint.getEncoderX();
            int encY = this.pinpoint.getEncoderY();
            this.telemetry.addLine("--- POSITION ---");
            this.telemetry.addData("X", "%.2f %s", Double.valueOf(x), unitLabel);
            this.telemetry.addData("Y", "%.2f %s", Double.valueOf(y), unitLabel);
            this.telemetry.addData("Heading", "%.2f deg", Double.valueOf(hDeg));
            this.telemetry.addLine("\n--- RAW DATA ---");
            this.telemetry.addData("Enc X", Integer.valueOf(encX));
            this.telemetry.addData("Enc Y", Integer.valueOf(encY));
            this.telemetry.addLine("\n--- TESTS ---");
            this.telemetry.addLine("1. Push 48 inches (1219 mm)");
            this.telemetry.addLine("2. Spin 20 times (7200 deg)");
            this.telemetry.addLine("3. Return to start (Should be 0,0,0)");
            this.telemetry.update();
            useInches = useInches;
            lastA = lastA2;
        }
    }
}

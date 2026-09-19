package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

/* JADX INFO: loaded from: classes8.dex */
@TeleOp(group = "Linear Opmode", name = "Strafer Position Hold (Pure Reverse Fixed)")
public class Shallomcode extends LinearOpMode {
    private static final int ENCODER_TOLERANCE = 20;
    private static final double KP_REVERSE = 0.002d;
    private static final double MAX_REVERSE_PWR = 0.4d;
    private static final double RECOVERY_TRIGGER_MM = 45.0d;
    private static final double STICK_DEADZONE = 0.15d;
    private State currentState = State.DRIVING;
    private DcMotor lb;
    private DcMotor lf;
    private double lockH;
    private double lockX;
    private double lockY;
    private GoBildaPinpointDriver pinpoint;
    private DcMotor rb;
    private DcMotor rf;
    private long settleStartTime;

    private enum State {
        DRIVING,
        SETTLING,
        LOCKED,
        REVERSING
    }

    @Override // com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
    public void runOpMode() {
        Shallomcode shallomcode = this;
        shallomcode.lf = (DcMotor) shallomcode.hardwareMap.get(DcMotor.class, "left_front_drive");
        shallomcode.lb = (DcMotor) shallomcode.hardwareMap.get(DcMotor.class, "left_back_drive");
        shallomcode.rf = (DcMotor) shallomcode.hardwareMap.get(DcMotor.class, "right_front_drive");
        shallomcode.rb = (DcMotor) shallomcode.hardwareMap.get(DcMotor.class, "right_back_drive");
        shallomcode.lf.setDirection(DcMotorSimple.Direction.FORWARD);
        shallomcode.lb.setDirection(DcMotorSimple.Direction.FORWARD);
        shallomcode.rf.setDirection(DcMotorSimple.Direction.FORWARD);
        shallomcode.rb.setDirection(DcMotorSimple.Direction.REVERSE);
        shallomcode.lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shallomcode.lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shallomcode.rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shallomcode.rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        shallomcode.resetMotorEncoders();
        shallomcode.pinpoint = (GoBildaPinpointDriver) shallomcode.hardwareMap.get(GoBildaPinpointDriver.class, "pin");
        shallomcode.pinpoint.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
        shallomcode.pinpoint.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
        shallomcode.pinpoint.setOffsets(0.0d, 0.0d, DistanceUnit.MM);
        shallomcode.pinpoint.resetPosAndIMU();
        shallomcode.telemetry.addLine("Stable Motor Reversal Ready.");
        shallomcode.telemetry.update();
        shallomcode.waitForStart();
        while (shallomcode.opModeIsActive()) {
            shallomcode.pinpoint.update();
            double curX = shallomcode.pinpoint.getPosX(DistanceUnit.MM);
            double curY = shallomcode.pinpoint.getPosY(DistanceUnit.MM);
            double curH = shallomcode.pinpoint.getHeading(AngleUnit.RADIANS);
            double axial = shallomcode.applyStickDeadzone(-shallomcode.gamepad1.left_stick_y);
            double lateral = shallomcode.applyStickDeadzone(shallomcode.gamepad1.left_stick_x);
            double yaw = shallomcode.applyStickDeadzone(shallomcode.gamepad1.right_stick_x);
            if (Math.abs(axial) > 0.0d || Math.abs(lateral) > 0.0d || Math.abs(yaw) > 0.0d) {
                shallomcode.currentState = State.DRIVING;
                double scale = shallomcode.gamepad1.left_bumper ? 0.35d : 1.0d;
                shallomcode.driveMecanum(axial * scale, lateral * scale, yaw * scale);
                shallomcode.settleStartTime = System.currentTimeMillis();
            } else {
                switch (shallomcode.currentState) {
                    case DRIVING:
                        curH = curH;
                        shallomcode.stopMotors();
                        shallomcode.currentState = State.SETTLING;
                        break;
                    case SETTLING:
                        curH = curH;
                        if (System.currentTimeMillis() - shallomcode.settleStartTime > 600) {
                            shallomcode.resetMotorEncoders();
                            shallomcode.pinpoint.resetPosAndIMU();
                            shallomcode.lockX = 0.0d;
                            shallomcode.lockY = 0.0d;
                            shallomcode.lockH = 0.0d;
                            shallomcode.currentState = State.LOCKED;
                        }
                        break;
                    case LOCKED:
                        curH = curH;
                        shallomcode.stopMotors();
                        double drift = Math.hypot(curX, curY);
                        if (drift > RECOVERY_TRIGGER_MM || Math.abs(curH) > Math.toRadians(8.0d)) {
                            shallomcode.currentState = State.REVERSING;
                        }
                        break;
                    case REVERSING:
                        int pFL = shallomcode.lf.getCurrentPosition();
                        int pLB = shallomcode.lb.getCurrentPosition();
                        int pRF = shallomcode.rf.getCurrentPosition();
                        int pRB = shallomcode.rb.getCurrentPosition();
                        if (Math.abs(pFL) < 20 && Math.abs(pLB) < 20 && Math.abs(pRF) < 20 && Math.abs(pRB) < 20) {
                            shallomcode.stopMotors();
                            shallomcode.currentState = State.LOCKED;
                            curH = curH;
                        } else {
                            shallomcode = this;
                            curH = curH;
                            shallomcode.lf.setPower(shallomcode.clamp(((double) (-pFL)) * KP_REVERSE, -0.4d, MAX_REVERSE_PWR));
                            shallomcode.lb.setPower(shallomcode.clamp(((double) (-pLB)) * KP_REVERSE, -0.4d, MAX_REVERSE_PWR));
                            shallomcode.rf.setPower(shallomcode.clamp(((double) (-pRF)) * KP_REVERSE, -0.4d, MAX_REVERSE_PWR));
                            shallomcode.rb.setPower(shallomcode.clamp(((double) (-pRB)) * KP_REVERSE, -0.4d, MAX_REVERSE_PWR));
                        }
                        break;
                    default:
                        curH = curH;
                        break;
                }
            }
            shallomcode.telemetry.addData("State", shallomcode.currentState);
            shallomcode.telemetry.addData("X/Y/H", "%.1f, %.1f, %.1f", Double.valueOf(curX), Double.valueOf(curY), Double.valueOf(Math.toDegrees(curH)));
            shallomcode.telemetry.addData("Motor Power (RB)", "%.2f", Double.valueOf(shallomcode.rb.getPower()));
            shallomcode.telemetry.addData("Encoder (RB)", Integer.valueOf(shallomcode.rb.getCurrentPosition()));
            shallomcode.telemetry.update();
        }
    }

    private void driveMecanum(double a, double l, double y) {
        double pFL = a + l + y;
        double pRF = (a - l) - y;
        double pLB = (a - l) + y;
        double pRB = (a + l) - y;
        double m = Math.max(1.0d, Math.max(Math.abs(pFL), Math.max(Math.abs(pRF), Math.max(Math.abs(pLB), Math.abs(pRB)))));
        this.lf.setPower(pFL / m);
        this.rf.setPower(pRF / m);
        this.lb.setPower(pLB / m);
        this.rb.setPower(pRB / m);
    }

    private void stopMotors() {
        this.lf.setPower(0.0d);
        this.rf.setPower(0.0d);
        this.lb.setPower(0.0d);
        this.rb.setPower(0.0d);
    }

    private void resetMotorEncoders() {
        this.lf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.lb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rf.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.rb.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.lf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.lb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.rf.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        this.rb.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    private double applyStickDeadzone(double v) {
        if (Math.abs(v) < STICK_DEADZONE) {
            return 0.0d;
        }
        return v;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    private double normalizeAngle(double a) {
        while (a > 3.141592653589793d) {
            a -= 6.283185307179586d;
        }
        while (a < -3.141592653589793d) {
            a += 6.283185307179586d;
        }
        return a;
    }
}

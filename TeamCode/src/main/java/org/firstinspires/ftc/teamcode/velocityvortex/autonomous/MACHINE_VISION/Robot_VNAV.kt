package org.firstinspires.ftc.teamcode.velocityvortex.autonomous.MACHINE_VISION

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode

import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference
import org.firstinspires.ftc.robotcore.external.navigation.Orientation
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables

import java.util.ArrayList

/**
 * This is NOT an opmode.
 *
 * This class is used to define all the specific navigation tasks for the Target Tracking Demo
 * It focuses on setting up and using the Vuforia Library, which is part of the 2016-2017 FTC SDK
 *
 * Once a target is identified, its information is displayed as telemetry data.
 * To approach the target, three motion priorities are created:
 * - Priority #1 Rotate so the robot is pointing at the target (for best target retention).
 * - Priority #2 Drive laterally based on distance from target center-line
 * - Priority #3 Drive forward based on the desired target standoff distance
 *
 */

class Robot_Navigation {

    /* Private class members. */
    private var myOpMode: LinearOpMode? = null       // Access to the OpMode object
    private var myRobot: OpMode? = null        // Access to the Robot hardware
    private var targets: VuforiaTrackables? = null        // List of active targets

    // Navigation data is only valid if targetFound == true;
    private var targetFound: Boolean = false    // set to true if Vuforia is currently tracking a target
    private var targetName: String? = null     // Name of the currently tracked target
    private var robotX: Double = 0.toDouble()         // X displacement from target center
    private var robotY: Double = 0.toDouble()         // Y displacement from target center
    private var robotBearing: Double = 0.toDouble()   // Robot's rotation around the Z axis (CCW is positive)
    private var targetRange: Double = 0.toDouble()    // Range from robot's center to target in mm
    private var targetBearing: Double = 0.toDouble()  // Heading of the target , relative to the robot's unrotated center
    private var relativeBearing: Double = 0.toDouble()// Heading to the target from the robot's current bearing.
    //   eg: a Positive RelativeBearing means the robot must turn CCW to point at the target image.

    /* Constructor */
    init {

        targetFound = false
        targetName = null
        targets = null

        robotX = 0.0
        robotY = 0.0
        targetRange = 0.0
        targetBearing = 0.0
        robotBearing = 0.0
        relativeBearing = 0.0
    }

    /***
     * Send telemetry data to indicate navigation status
     */
    fun addNavTelemetry() {
        if (targetFound) {
            // Display the current visible target name, robot info, target info, and required robot action.
            myOpMode!!.telemetry.addData("Visible", targetName)
            myOpMode!!.telemetry.addData("Robot", "[X]:[Y] (B) [%5.0fmm]:[%5.0fmm] (%4.0f°)",
                    robotX, robotY, robotBearing)
            myOpMode!!.telemetry.addData("Target", "[R] (B):(RB) [%5.0fmm] (%4.0f°):(%4.0f°)",
                    targetRange, targetBearing, relativeBearing)
            myOpMode!!.telemetry.addData("- Turn    ", "%s %4.0f°", if (relativeBearing < 0) ">>> CW " else "<<< CCW", Math.abs(relativeBearing))
            myOpMode!!.telemetry.addData("- Strafe  ", "%s %5.0fmm", if (robotY < 0) "LEFT" else "RIGHT", Math.abs(robotY))
            myOpMode!!.telemetry.addData("- Distance", "%5.0fmm", Math.abs(robotX))
        } else {
            myOpMode!!.telemetry.addData("Visible", "- - - -")
        }
    }

    /***
     * Start tracking Vuforia images
     */
    fun activateTracking() {

        // Start tracking any of the defined targets
        if (targets != null)
            targets!!.activate()
    }


    /***
     * use target position to determine the best way to approach it.
     * Set the Axial, Lateral and Yaw axis motion values to get us there.
     *
     * @return true if we are close to target
     * @param standOffDistance how close do we get the center of the robot to target (in mm)
     */
    fun cruiseControl(standOffDistance: Double): Boolean {
        val closeEnough: Boolean

        // Priority #1 Rotate to always be pointing at the target (for best target retention).
        val Y = relativeBearing * YAW_GAIN

        // Priority #2  Drive laterally based on distance from X axis (same as y value)
        val L = robotY * LATERAL_GAIN

        // Priority #3 Drive forward based on the desiredHeading target standoff distance
        val A = -(robotX + standOffDistance) * AXIAL_GAIN

        // Send the desired axis motions to the robot hardware.
        //myRobot!!.setYaw(Y)
        //myRobot!!.setAxial(A)
        //myRobot!!.setLateral(L)

        // Determine if we are close enough to the target for action.
        closeEnough = Math.abs(robotX + standOffDistance) < CLOSE_ENOUGH && Math.abs(robotY) < ON_AXIS

        return closeEnough
    }


    /***
     * Initialize the Target Tracking and navigation interface
     * @param opMode    pointer to OpMode
     * @param robot     pointer to Robot hardware class
     */
    fun initVuforia(opMode: LinearOpMode, robot: OpMode) {

        // Save reference to OpMode and Hardware map
        myOpMode = opMode
        myRobot = robot

        /**
         * Start up Vuforia, telling it the id of the view that we wish to use as the parent for
         * the camera monitor.
         * We also indicate which camera on the RC that we wish to use.
         */

        // VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);  // Use this line to see camera display
        val parameters = VuforiaLocalizer.Parameters()                             // OR... Use this line to improve performance

        // Get your own Vuforia key at  https://developer.vuforia.com/license-manager
        // and paste it here...
        parameters.vuforiaLicenseKey = "AX+gX5z/////AAAAGemESFRCSEr9mlgYKyJlg7QkunLknhP5uXkpeqYVGio+FNmaVefvE0yC0ueWXyecPzSipBsPHsGK1aduCXc87+J6jRilxDjmP8aSmRGOopchZ7KhKbpKRLtcP+i408NrfCRzT1CVhaQ9YI3ObQApDcyNxoiz5G8TVCh7SIr1qL0NXHW1QEuCgGP2BhSJDhUomuotPTd11WcfoMyFqBg2FUb3iC1Msv4iIzUirCRkQU3boFywhIVSZ3cvM+1IWDh/AjMgNZ1Fy/jueNKirAQqQVDqC5cYn9aAvEpewgJAwMjYlaYaXSjb+hLkyh1+ZY2MLsZAbed3pUGJ8eI65QgI3NDGtJs3XL7r8rXoIVvnoezg"

        parameters.cameraDirection = CAMERA_CHOICE
        parameters.useExtendedTracking = false
        val vuforia = ClassFactory.createVuforiaLocalizer(parameters)

        /**
         * Load the data sets that for the trackable objects we wish to track.
         * These particular data sets are stored in the 'assets' part of our application
         * They represent the four image targets used in the 2016-17 FTC game.
         */
        targets = vuforia.loadTrackablesFromAsset("FTC_2016-17")
        targets!![0].name = "Blue Near"
        targets!![1].name = "Red Far"
        targets!![2].name = "Blue Far"
        targets!![3].name = "Red Near"

        /** For convenience, gather together all the trackable objects in one easily-iterable collection  */
        val allTrackables = ArrayList<VuforiaTrackable>()
        allTrackables.addAll(targets!!)

        // create an image translation/rotation matrix to be used for all images
        // Essentially put all the image centers 6" above the 0:0:0 origin,
        // but rotate them so they along the -X axis.
        val targetOrientation = OpenGLMatrix
                .translation(0f, 0f, 150f)
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.XYZ,
                        AngleUnit.DEGREES, 90f, 0f, -90f))

        /**
         * Create a transformation matrix describing where the phone is on the robot.
         *
         * The coordinate frame for the robot looks the same as the field.
         * The robot's "forward" direction is facing out along X axis, with the LEFT side facing out along the Y axis.
         * Z is UP on the robot.  This equates to a bearing angle of Zero degrees.
         *
         * The phone starts out lying flat, with the screen facing Up and with the physical top of the phone
         * pointing to the LEFT side of the Robot.  If we consider that the camera and screen will be
         * in "Landscape Mode" the upper portion of the screen is closest to the front of the robot.
         *
         * If using the rear (High Res) camera:
         * We need to rotate the camera around it's long axis to bring the rear camera forward.
         * This requires a negative 90 degree rotation on the Y axis
         *
         * If using the Front (Low Res) camera
         * We need to rotate the camera around it's long axis to bring the FRONT camera forward.
         * This requires a Positive 90 degree rotation on the Y axis
         *
         * Next, translate the camera lens to where it is on the robot.
         * In this example, it is centered (left to right), but 110 mm forward of the middle of the robot, and 200 mm above ground level.
         */

        val CAMERA_FORWARD_DISPLACEMENT = 110   // Camera is 110 mm in front of robot center
        val CAMERA_VERTICAL_DISPLACEMENT = 200   // Camera is 200 mm above ground
        val CAMERA_LEFT_DISPLACEMENT = 0     // Camera is ON the robots center line

        val phoneLocationOnRobot = OpenGLMatrix
                .translation(CAMERA_FORWARD_DISPLACEMENT.toFloat(), CAMERA_LEFT_DISPLACEMENT.toFloat(), CAMERA_VERTICAL_DISPLACEMENT.toFloat())
                .multiplied(Orientation.getRotationMatrix(
                        AxesReference.EXTRINSIC, AxesOrder.YZX,
                        AngleUnit.DEGREES, (if (CAMERA_CHOICE == VuforiaLocalizer.CameraDirection.FRONT) 90 else -90).toFloat(), 0f, 0f))

        // Set the all the targets to have the same location and camera orientation
        for (trackable in allTrackables) {
            trackable.location = targetOrientation
            (trackable.listener as VuforiaTrackableDefaultListener).setPhoneInformation(phoneLocationOnRobot, parameters.cameraDirection)
        }
    }


    /***
     * See if any of the vision targets are in sight.
     *
     * @return true if any target is found
     */
    fun targetsAreVisible(): Boolean {

        var targetTestID = 0

        // Check each target in turn, but stop looking when the first target is found.
        while (targetTestID < MAX_TARGETS && !targetIsVisible(targetTestID)) {
            targetTestID++
        }

        return targetFound
    }

    /***
     * Determine if specified target ID is visible and
     * If it is, retreive the relevant data, and then calculate the Robot and Target locations
     *
     * @param   targetId
     * @return  true if the specified target is found
     */
    fun targetIsVisible(targetId: Int): Boolean {

        val target = targets!![targetId]
        val listener = target!!.listener as VuforiaTrackableDefaultListener
        var location: OpenGLMatrix? = null

        // if we have a target, look for an updated robot position
        if (target != null && listener != null && listener.isVisible) {
            targetFound = true
            targetName = target.name

            // If we have an updated robot location, update all the relevant tracking information
            location = listener.updatedRobotLocation
            if (location != null) {

                // Create a translation and rotation vector for the robot.
                val trans = location.translation
                val rot = Orientation.getOrientation(location, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES)

                // Robot position is defined by the standard Matrix translation (x and y)
                robotX = trans.get(0).toDouble()
                robotY = trans.get(1).toDouble()

                // Robot bearing (in +vc CCW cartesian system) is defined by the standard Matrix z rotation
                robotBearing = rot.thirdAngle.toDouble()

                // target range is based on distance from robot position to origin.
                targetRange = Math.hypot(robotX, robotY)

                // target bearing is based on angle formed between the X axis to the target range line
                targetBearing = Math.toDegrees(-Math.asin(robotY / targetRange))

                // Target relative bearing is the target Heading relative to the direction the robot is pointing.
                relativeBearing = targetBearing - robotBearing
            }
            targetFound = true
        } else {
            // Indicate that there is no target visible
            targetFound = false
            targetName = "None"
        }

        return targetFound
    }

    companion object {
        // Constants
        private val MAX_TARGETS = 4
        private val ON_AXIS = 10.0      // Within 1.0 cm of target center-line
        private val CLOSE_ENOUGH = 20.0      // Within 2.0 cm of final target standoff

        // Select which camera you want use.  The FRONT camera is the one on the same side as the screen.  Alt. is BACK
        private val CAMERA_CHOICE = VuforiaLocalizer.CameraDirection.FRONT

        val YAW_GAIN = 0.018   // Rate at which we respond to heading error
        val LATERAL_GAIN = 0.0027  // Rate at which we respond to off-axis error
        val AXIAL_GAIN = 0.0017  // Rate at which we respond to target distance errors
    }
}
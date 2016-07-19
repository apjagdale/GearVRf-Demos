package org.gearvrf.modelviewer2;


import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointee;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.util.BoundingBoxCreator;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Future;

public class CameraPosition {
    private Vector3f position = new Vector3f();
    private int currentPositionIndex;
    //GVRSphereSceneObject sphereObject;
    GVRModelSceneObject cameraModel;
    private float angle;
    private Vector3f axis = new Vector3f();

    public CameraPosition(float defaultX, float defaultY, float defaultZ, float angleIn, float axisX, float axisY, float axisZ) {
        position = (new Vector3f(defaultX, defaultY, defaultZ));
        angle = angleIn;
        axis = (new Vector3f(axisX, axisY, axisZ));
    }

    public Vector3f getCameraPosition() {
        return position;
    }

    public float getCameraAngle() {
        return angle;
    }

    public Vector3f getRotationAxis() {
        return axis;
    }

    public GVRModelSceneObject loadNavigator(GVRContext context) {
        // load texture
        /*
        Future<GVRTexture> texture = null;
        try {
            texture = context.loadFutureTexture(new GVRAndroidResource(context, "skybox/skybox_outdoor.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // create a sphere scene object with the specified texture and triangles facing inward (the 'false' argument)
        sphereObject = new GVRSphereSceneObject(context, false, texture);
        sphereObject.getTransform().setScale(1.5f, 1.5f, 1.5f);
        attachEyePointee(context);
        sphereObject.getTransform().setPosition(position.x, position.y, position.z);
        return sphereObject;*/

        if(cameraModel != null)
            return cameraModel;

        try{
        cameraModel = context.loadModel("camera_icon.obj");
        } catch (IOException e) {
            e.printStackTrace();
        }

        attachEyePointee(context);
        cameraModel.getTransform().setPosition(position.x, position.y, position.z);
        //cameraModel.getTransform().setRotationByAxis(90, 0, 1, 0);
        return cameraModel;
    }

    private void attachEyePointee(GVRContext context) {
        GVRSceneObject.BoundingVolume bv = cameraModel.getBoundingVolume();
        BoundingBoxCreator boundingBox = new BoundingBoxCreator(context, bv);
        GVREyePointeeHolder playPauseHolder = new GVREyePointeeHolder(context);
        playPauseHolder.addPointee(new GVRMeshEyePointee(context, boundingBox.getMesh()));
        cameraModel.attachEyePointeeHolder(playPauseHolder);
    }
}

package org.gearvrf.modelviewer2;


import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointee;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.util.BoundingBoxCreator;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.concurrent.Future;

public class CameraPosition {
    private Vector3f position = new Vector3f();
    private int currentPositionIndex;
    GVRSphereSceneObject sphereObject;

    public CameraPosition(float defaultX, float defaultY, float defaultZ) {
        position = (new Vector3f(defaultX, defaultY, defaultZ));
    }

    public Vector3f getCameraPosition() {
        return position;
    }

    public GVRSphereSceneObject loadNavigator(GVRContext context) {
        // load texture
        Future<GVRTexture> texture = null;
        try {
            texture = context.loadFutureTexture(new GVRAndroidResource(context, "skybox/skybox_outdoor.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // create a sphere scene object with the specified texture and triangles facing inward (the 'false' argument)
        sphereObject = new GVRSphereSceneObject(context, false, texture);
        sphereObject.getTransform().setScale(0.5f, 0.5f, 0.5f);
        attachEyePointee(context);
        sphereObject.getTransform().setPosition(position.x, position.y, position.z);
        return sphereObject;
    }

    private void attachEyePointee(GVRContext context) {
        GVRSceneObject.BoundingVolume bv = sphereObject.getBoundingVolume();
        BoundingBoxCreator boundingBox = new BoundingBoxCreator(context, bv);
        GVREyePointeeHolder playPauseHolder = new GVREyePointeeHolder(context);
        playPauseHolder.addPointee(new GVRMeshEyePointee(context, boundingBox.getMesh()));
        sphereObject.attachEyePointeeHolder(playPauseHolder);
    }
}

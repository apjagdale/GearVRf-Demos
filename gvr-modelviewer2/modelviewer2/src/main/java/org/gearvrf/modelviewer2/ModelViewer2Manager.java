/* Copyright 2015 Samsung Electronics Co., LTD * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package org.gearvrf.modelviewer2;import org.gearvrf.GVRActivity;import org.gearvrf.GVRAndroidResource;import org.gearvrf.GVRContext;import org.gearvrf.GVREyePointeeHolder;import org.gearvrf.GVRMesh;import org.gearvrf.GVRMeshEyePointee;import org.gearvrf.GVRPicker;import org.gearvrf.GVRRenderData;import org.gearvrf.GVRScene;import org.gearvrf.GVRSceneObject;import org.gearvrf.GVRScript;import org.gearvrf.GVRTexture;import org.gearvrf.scene_objects.GVRModelSceneObject;import org.gearvrf.util.VRTouchPadGestureDetector.SwipeDirection;import org.gearvrf.widgetplugin.GVRWidgetPlugin;import org.gearvrf.widgetplugin.GVRWidgetSceneObject;import org.gearvrf.widgetplugin.GVRWidgetSceneObjectMeshInfo;import org.joml.Vector3f;import android.opengl.GLES20;import android.util.Log;import android.view.MotionEvent;import java.io.IOException;import java.util.ArrayList;public class ModelViewer2Manager extends GVRScript {    private static final String TAG = "Abhijit";    // Variables related to Room    private final String mRoomPath = "room.fbx";    private GVRContext mGVRContext;    private GVRModelSceneObject mRoom;    private Controller controller;    public boolean controllerReadyFlag = false;    private GVRScene scene;    private boolean mIsSingleTapped = false;    GVRSceneObject headTracker;    GVRWidgetPlugin mPlugin;    GVRWidgetSceneObject mWidget;    GVRActivity activity;    private Vector3f defaultCameraPosition = new Vector3f(0, 200, 1000);    public ModelViewer2Manager(GVRActivity activity, GVRWidgetPlugin mPlugin) {        this.mPlugin = mPlugin;        this.activity = activity;    }    private GVRModelSceneObject loadModelFile(GVRContext gvrContext, String modelFile) {        try {            return gvrContext.loadModel(modelFile);        } catch (IOException e) {            e.printStackTrace();        }        return null;    }    private GVRTexture loadTexture(GVRContext gvrContext, String imageFile) {        try {            return gvrContext.loadTexture(new GVRAndroidResource(gvrContext, imageFile));        } catch (IOException e) {            e.printStackTrace();        }        return null;    }    private GVRSceneObject getHeadTracker() {        // Head Tracker        GVRTexture headTrackerTexture = loadTexture(mGVRContext, "head-tracker.png");        headTracker = new GVRSceneObject(mGVRContext,                mGVRContext.createQuad(0.5f, 0.5f), headTrackerTexture);        headTracker.getTransform().setPositionZ(-9.0f);        headTracker.getRenderData().setRenderingOrder(                GVRRenderData.GVRRenderingOrder.OVERLAY);        headTracker.getRenderData().setDepthTest(false);        headTracker.getRenderData().setRenderingOrder(100000);        return headTracker;    }    void addWidgetToTheRoom() {        GVRWidgetSceneObjectMeshInfo info =                new GVRWidgetSceneObjectMeshInfo(-4.5f, 1.0f, -1.5f, -1.0f, new int[]{0, 0}, new int[]{mPlugin.getWidth(), mPlugin.getHeight()});        mWidget = new GVRWidgetSceneObject(mGVRContext,                mPlugin.getTextureId(), info, mPlugin.getWidth(),                mPlugin.getHeight());        Log.e(TAG, Float.toString(mPlugin.getHeight()) + "   " + Float.toString(mPlugin.getHeight()));        mWidget.getTransform().setPosition(defaultCameraPosition.x - 3.0f, defaultCameraPosition.y, defaultCameraPosition.z - 5);        mWidget.getTransform().rotateByAxis(40.0f, 0.0f, 1.0f, 0.0f);        mWidget.getRenderData().setRenderingOrder(                GVRRenderData.GVRRenderingOrder.OVERLAY);        mWidget.getRenderData().setDepthTest(false);        mWidget.getRenderData().setRenderingOrder(10000);        scene.addSceneObject(mWidget);        GVRMesh button_pick_mesh = null;        try {            button_pick_mesh = mGVRContext                    .loadMesh(new GVRAndroidResource(mGVRContext, "widgetEyePointee.obj"));        } catch (IOException e) {            e.printStackTrace();            Log.e(TAG, "Execption for button");        }        GVREyePointeeHolder eyePointeeHolder2 = new GVREyePointeeHolder(                mGVRContext);        GVRMeshEyePointee eyePointee2 = new GVRMeshEyePointee(mGVRContext,                button_pick_mesh);        eyePointeeHolder2.addPointee(eyePointee2);        mWidget.attachEyePointeeHolder(eyePointeeHolder2);    }    @Override    public void onInit(final GVRContext gvrContext) {        mGVRContext = gvrContext;        scene = gvrContext.getNextMainScene();        // Add Head Tracker        scene.getMainCameraRig().addChildObject(getHeadTracker());        scene.getMainCameraRig().getTransform().setPosition(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z);        // Set the near clipping plane.        scene.getMainCameraRig().setNearClippingDistance(0.1f);        // Set the far clipping plane.        scene.getMainCameraRig().setFarClippingDistance(4500.0f);        // Load the Room model twice        mRoom = loadModelFile(gvrContext, mRoomPath);        // Set Transformations        mRoom.getTransform().setPosition(0.0f, 0.0f, 0.0f);        Log.e(TAG, "Controller initialization done");        controller = new Controller(activity, mGVRContext);        controller.initializeController();        controllerReadyFlag = true;        controller.addThumbNails(mRoom);        controller.displayCountInRoom(mRoom);        addWidgetToTheRoom();        controller.displayNavigators(mRoom);        // Adding Room to the Final Scene        scene.addSceneObject(mRoom);        // Add First SkyBox        addSkyBox(0);    }    ArrayList<String> getCameraPositionList() {        return controller.getCameraPositionList();    }    ArrayList<String> getListOfCustomShaders() {        return controller.getListOfCustomShaders();    }    void setSelectedCustomShader(int index){        controller.applyCustomShader(index, scene);    }    void addSkyBox(int index) {        controller.addSkyBox(index, scene);    }    void setCameraPosition(int index) {        //Log.e(TAG, "Camera Position Chosed +" + Integer.toString(index));        //controller.setCameraPosition(scene, mWidget, index);    }    ArrayList<String> getSkyBoxList() {        return controller.getSkyBoxList();    }    GVREyePointeeHolder getEyePointee() {        GVREyePointeeHolder[] pickedHolders = null;        pickedHolders = GVRPicker.pickScene(mGVRContext.getMainScene());        //Log.e(TAG, "Picked scene count" + Integer.toString(pickedHolders.length));        if (pickedHolders.length > 0)            return pickedHolders[0];        else            return null;    }    @Override    public void onStep() {        boolean isSingleTapped = mIsSingleTapped;        mIsSingleTapped = false;        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);        GVREyePointeeHolder pickedHolder = getEyePointee();        if (pickedHolder == null)            return;        // For Menu        mPlugin.setPickedObject(pickedHolder.getOwnerObject());        if (isSingleTapped) {            controller.displayModelIfSelected(pickedHolder, scene, mRoom, mWidget);            controller.setCameraPositionByNavigator(pickedHolder, scene, mRoom, mWidget);        }    }    public void onSingleTap(MotionEvent e) {        Log.d(TAG, "On Single Touch Received");        mIsSingleTapped = true;    }    public void onSwipe(MotionEvent e, SwipeDirection swipeDirection,                        float velocityX, float velocityY) {        if (swipeDirection == SwipeDirection.Forward) {            // Swipe Only when Looking at ThumbNails            GVREyePointeeHolder pickedHolder = getEyePointee();            if (pickedHolder != null)                controller.onFowardSwipeOfThumbNails(pickedHolder, mRoom);            Log.d("SWIPE", "Swipped Forward");        } else if (swipeDirection == SwipeDirection.Backward) {            Log.d("SWIPE", "Swipped Backward");        } else {            Log.d("SWIPE", "Unknown Swipe");        }    }    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {        Log.e(TAG, "Angle mover called");        GVREyePointeeHolder pickedHolder = getEyePointee();        if (pickedHolder != null)            controller.onScrollOverModel(pickedHolder, arg2);        return false;    }    public void zoomCurrentModel(float zoomBy) {        controller.onZoomOverModel(zoomBy);    }}
/* Copyright 2015 Samsung Electronics Co., LTD * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package org.gearvrf.modelviewer2;import org.gearvrf.GVRActivity;import org.gearvrf.GVRAndroidResource;import org.gearvrf.GVRContext;import org.gearvrf.GVREyePointeeHolder;import org.gearvrf.GVRMesh;import org.gearvrf.GVRMeshEyePointee;import org.gearvrf.GVRPicker;import org.gearvrf.GVRRenderData;import org.gearvrf.GVRScene;import org.gearvrf.GVRSceneObject;import org.gearvrf.GVRScript;import org.gearvrf.GVRTexture;import org.gearvrf.animation.GVRAnimation;import org.gearvrf.scene_objects.GVRModelSceneObject;import org.gearvrf.util.VRTouchPadGestureDetector.SwipeDirection;import org.gearvrf.widgetplugin.GVRWidgetPlugin;import org.gearvrf.widgetplugin.GVRWidgetSceneObject;import org.gearvrf.widgetplugin.GVRWidgetSceneObjectMeshInfo;import org.joml.Vector3f;import org.joml.Vector4f;import android.opengl.GLES20;import android.util.Log;import android.view.MotionEvent;import java.io.IOException;import java.util.ArrayList;import java.util.List;public class ModelViewer2Manager extends GVRScript {    private static final String TAG = "Abhijit";    // Variables related to Room    private final String mRoomPath = "room.fbx";    private GVRContext mGVRContext;    //private GVRModelSceneObject mRoom;    private Controller controller;    public boolean controllerReadyFlag = false;    private GVRScene scene;    private boolean mIsSingleTapped = false;    boolean animationFlag = false;    GVRSceneObject headTracker;    GVRWidgetPlugin mPlugin;    GVRWidgetSceneObject mWidget;    float widgetModelMatrix[];    GVRActivity activity;    private Vector3f defaultCenterPosition = new Vector3f(0, 0, 0);    public ModelViewer2Manager(GVRActivity activity, GVRWidgetPlugin mPlugin) {        this.mPlugin = mPlugin;        this.activity = activity;    }    private GVRModelSceneObject loadModelFile(GVRContext gvrContext, String modelFile) {        try {            return gvrContext.loadModel(modelFile);        } catch (IOException e) {            e.printStackTrace();        }        return null;    }    private GVRTexture loadTexture(GVRContext gvrContext, String imageFile) {        try {            return gvrContext.loadTexture(new GVRAndroidResource(gvrContext, imageFile));        } catch (IOException e) {            e.printStackTrace();        }        return null;    }    private GVRSceneObject getHeadTracker() {        // Head Tracker        GVRTexture headTrackerTexture = loadTexture(mGVRContext, "head-tracker.png");        headTracker = new GVRSceneObject(mGVRContext,                mGVRContext.createQuad(1.0f, 1.0f), headTrackerTexture);        headTracker.getTransform().setPositionZ(-10.0f);        headTracker.getRenderData().setDepthTest(false);        headTracker.getRenderData().setRenderingOrder(                GVRRenderData.GVRRenderingOrder.OVERLAY);        headTracker.getRenderData().setRenderingOrder(100000);        return headTracker;    }    void addWidgetToTheRoom() {        GVRWidgetSceneObjectMeshInfo info =                new GVRWidgetSceneObjectMeshInfo(-4.5f, 1.0f, -1.5f, -1.0f, new int[]{0, 0}, new int[]{mPlugin.getWidth(), mPlugin.getHeight()});        mWidget = new GVRWidgetSceneObject(mGVRContext,                mPlugin.getTextureId(), info, mPlugin.getWidth(),                mPlugin.getHeight());        Log.e(TAG, Float.toString(mPlugin.getHeight()) + "   " + Float.toString(mPlugin.getHeight()));        //mWidget.getTransform().setPosition(defaultCenterPosition.x - 3, defaultCenterPosition.y + 5, defaultCenterPosition.z + 15);        mWidget.getTransform().setPosition(-2, 0, -5);        mWidget.getTransform().rotateByAxis(40.0f, 0.0f, 1.0f, 0.0f);        mWidget.getRenderData().setRenderingOrder(                GVRRenderData.GVRRenderingOrder.OVERLAY);        mWidget.getRenderData().setDepthTest(false);        mWidget.getRenderData().setRenderingOrder(10000);        //scene.addSceneObject(mWidget);        widgetModelMatrix = mWidget.getTransform().getModelMatrix();        scene.getMainCameraRig().addChildObject(mWidget);         float temp[] = mWidget.getTransform().getModelMatrix();        scene.getMainCameraRig().removeChildObject(mWidget);        mWidget.getTransform().setModelMatrix(temp);        scene.addSceneObject(mWidget);        GVRMesh button_pick_mesh = null;        try {            button_pick_mesh = mGVRContext                    .loadMesh(new GVRAndroidResource(mGVRContext, "widgetEyePointee.obj"));        } catch (IOException e) {            e.printStackTrace();            Log.e(TAG, "Execption for button");        }        GVREyePointeeHolder eyePointeeHolder2 = new GVREyePointeeHolder(                mGVRContext);        GVRMeshEyePointee eyePointee2 = new GVRMeshEyePointee(mGVRContext,                button_pick_mesh);        eyePointeeHolder2.addPointee(eyePointee2);        mWidget.attachEyePointeeHolder(eyePointeeHolder2);    }    @Override    public void onInit(final GVRContext gvrContext) {        mGVRContext = gvrContext;        scene = gvrContext.getNextMainScene();        // Add Head Tracker        scene.getMainCameraRig().addChildObject(getHeadTracker());        // TODO        scene.getMainCameraRig().getTransform().setPosition(defaultCenterPosition.x, defaultCenterPosition.y + 5, defaultCenterPosition.z + 20);        // Set the near clipping plane.        //scene.getMainCameraRig().setNearClippingDistance(-0.1f);        // Set the far clipping plane.       // scene.getMainCameraRig().setFarClippingDistance(4500.0f);        // Load the Room model twice        //mRoom = loadModelFile(gvrContext, mRoomPath);        // Set Transformations        //mRoom.getTransform().setPosition(defaultCenterPosition.x, defaultCenterPosition.y-50, defaultCenterPosition.z);        Log.e(TAG, "Controller initialization done");        controller = new Controller(activity, mGVRContext);        controller.setDefaultCenterPosition(defaultCenterPosition);        controller.initializeController();        controllerReadyFlag = true;       // controller.addThumbNails(mRoom);        controller.displayCountInRoom(scene);        addWidgetToTheRoom();        controller.displayNavigators(scene);        controller.addLight(scene);        // Adding Room to the Final Scene        //scene.addSceneObject(mRoom);        // Add First SkyBox        addSkyBox(0);    }    ArrayList<String> getCameraPositionList() {        return controller.getCameraPositionList();    }    ArrayList<String> getListOfCustomShaders() {        return controller.getListOfCustomShaders();    }    void setSelectedCustomShader(int index) {        controller.applyCustomShader(index, scene);    }    void addSkyBox(int index) {        controller.addSkyBox(index, scene);    }    void setCameraPosition(int index) {        //Log.e(TAG, "Camera Position Chosed +" + Integer.toString(index));        //controller.setCameraPosition(scene, mWidget, index);    }    ArrayList<String> getSkyBoxList() {        return controller.getSkyBoxList();    }    GVREyePointeeHolder getEyePointee() {        GVREyePointeeHolder[] pickedHolders = null;        pickedHolders = GVRPicker.pickScene(mGVRContext.getMainScene());        //Log.e(TAG, "Picked scene count" + Integer.toString(pickedHolders.length));        if (pickedHolders.length > 0)            return pickedHolders[0];        else            return null;    }    public int getCountOfAnimations() {        return controller.getCountOfAnimations();    }    public void setSelectedAnimation(int index) {        if(controllerReadyFlag)        controller.setSelectedAnimation(index);    }    ArrayList<String> getModelsList() {        return controller.getModelsList();    }    public void setSelectedModel(int index) {        if(controllerReadyFlag)            controller.setModelWithIndex(index, scene);        scene.bindShaders();    }    public boolean isModelPresent() {        return controller.currentModelFlag;    }    public void turnOnOffLight(boolean flag){        controller.turnOnOffLight(flag);        scene.bindShaders();    }    public ArrayList<String> getAmbient() {        return controller.getAmbient();    }    public ArrayList<String> getDiffuse() {        return controller.getDiffuse();    }    public ArrayList<String> getSpecular() {        return controller.getSpecular();    }    public void setAmbient(int index, boolean lightOnOff){        if(lightOnOff)        controller.setAmbient(index);    }    public void setDiffuse(int index, boolean lightOnOff){        if(lightOnOff)        controller.setDiffuse(index);    }    public void setSpecular(int index, boolean lightOnOff){        if(lightOnOff)        controller.setSpecular(index);    }    @Override    public void onStep() {        boolean isSingleTapped = mIsSingleTapped;        mIsSingleTapped = false;        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);        GVREyePointeeHolder pickedHolder = getEyePointee();        if (pickedHolder == null)            return;        // For Menu        mPlugin.setPickedObject(pickedHolder.getOwnerObject());        if (isSingleTapped) {            //controller.displayModelIfSelected(pickedHolder, scene, mRoom, mWidget);            controller.setCameraPositionByNavigator(pickedHolder, scene, scene, mWidget, widgetModelMatrix);            scene.bindShaders();        }    }    public void onSingleTap(MotionEvent e) {        Log.d(TAG, "On Single Touch Received");        mIsSingleTapped = true;  /*      GVREyePointeeHolder pickedHolder = getEyePointee();        if (pickedHolder == null)            return;        // For Menu        mPlugin.setPickedObject(pickedHolder.getOwnerObject());        if (mIsSingleTapped) {            controller.displayModelIfSelected(pickedHolder, scene, mRoom, mWidget);            controller.setCameraPositionByNavigator(pickedHolder, scene, mRoom, mWidget);        }        mIsSingleTapped = false;*/    }    public void onSwipe(MotionEvent e, SwipeDirection swipeDirection,                        float velocityX, float velocityY) {        /*if (swipeDirection == SwipeDirection.Forward) {            // Swipe Only when Looking at ThumbNails            GVREyePointeeHolder pickedHolder = getEyePointee();            if (pickedHolder != null)                controller.onFowardSwipeOfThumbNails(pickedHolder, mRoom);            Log.d("SWIPE", "Swipped Forward");        } else if (swipeDirection == SwipeDirection.Backward) {            Log.d("SWIPE", "Swipped Backward");        } else {            Log.d("SWIPE", "Unknown Swipe");        }*/    }    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {        Log.e(TAG, "Angle mover called");        GVREyePointeeHolder pickedHolder = getEyePointee();        if (pickedHolder != null)            controller.onScrollOverModel(pickedHolder, arg2);        return false;    }    public void zoomCurrentModel(float zoomBy) {        controller.onZoomOverModel(zoomBy);    }}
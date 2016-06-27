package org.gearvrf.modelviewer2;


import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRPicker;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRRotationByAxisAnimation;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.util.AssetsReader;
import org.gearvrf.util.Banner;
import org.gearvrf.widgetplugin.GVRWidgetSceneObject;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;

public class Controller {
    private static final String TAG = "Abhijit";

    // Variables related to SkyBox
    private ArrayList<SkyBox> aODefaultSkyBox;
    private final String sDefaultSkyBoxDirectory = "skybox";
    private GVRSphereSceneObject currentSkyBox;

    // Variables related to Camera
    private Vector3f defaultCameraPosition = new Vector3f(0, 200, 1000);
    private CameraPosition oDefaultCameraPosition;

    // Variables related to Model
    private final String sEnvironmentPath = Environment.getExternalStorageDirectory().getPath();
    private ArrayList<Model> aModel;
    private Model currentDisplayedModel;
    private ArrayList<GVRSceneObject> currentThumbNailsInRoom = new ArrayList<GVRSceneObject>();
    private static final int gSlots = 3;
    private int gStart = 0;

    // Variables related to Banner
    private Banner oBannerCount;


    private GVRActivity activity;
    private GVRContext context;

    public Controller(GVRActivity activity, GVRContext context) {
        this.activity = activity;
        this.context = context;
    }

    void initializeController() {
        loadDefaultSkyBoxList();
        loadModelsList();
        loadCameraPositionList();
    }

    // START Banner Feature
    void displayCountInRoom(GVRModelSceneObject room) {
        if (oBannerCount == null) {
            oBannerCount = new Banner(context, "Total Models " + String.valueOf(aModel.size()), 10, Color.BLUE, -2.0f, 207.0f, 985.0f);
        }
        room.addChildObject(oBannerCount.getBanner());
    }
    // END Banner Feature


    // START Camera Position Feature
    private void loadCameraPositionList() {
        oDefaultCameraPosition = new CameraPosition(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z);
    }

    public ArrayList<String> getCameraPositionList() {
        ArrayList<String> list = new ArrayList<String>();
        ArrayList<Vector3f> tempList = oDefaultCameraPosition.getAllCameraPositions();
        for (Vector3f position : tempList) {
            String sPosition = "X:" + Float.toString(position.x) + " Y:" + Float.toString(position.y) + " Z:" + Float.toString(position.z);
            list.add(sPosition);
        }
        return list;
    }

    public void setCameraPosition(GVRScene scene, GVRWidgetSceneObject widget, int index) {
        Vector3f position = oDefaultCameraPosition.getIndexedCameraPosition(index);
        scene.getMainCameraRig().getTransform().setPosition(position.x, position.y, position.z);
        if (widget != null)
            widget.getTransform().setPosition(position.x - 3.0f, position.y, position.z - 5);
    }
    // END Camera Position Feature

    // START Models Features

    private ArrayList<String> getListOfModels() {
        ArrayList<String> listOfAllModels = new ArrayList<String>();

        // Add All the Extensions you want to load
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".fbx");
        extensions.add(".3ds");
        extensions.add(".dae");
        extensions.add(".obj");
        extensions.add(".ma");

        // Reads the List of Files from specified folder having extension specified in extensions.
        // Please place your models by creating GVRModelViewer2 folder in your internal phone memory
        CardReader cRObject = new CardReader(sEnvironmentPath + "/GVRModelViewer2", extensions);
        File list[] = cRObject.getModels();
        if (list == null)
            return listOfAllModels;

        // Adds all the models
        for (File file : list) {
            listOfAllModels.add(file.getName());
        }

        return listOfAllModels;
    }

    /* Get List of Model and do the following
            1. Create its thumbNail and text
            2. Add Eye Pointee for thumbnail
    */
    void loadModelsList() {
        aModel = new ArrayList<Model>();
        ArrayList<String> listOfAllModels = getListOfModels();
        for (String modelName : listOfAllModels) {
            Model tempModel = new Model(modelName, "GVRModelViewer2/");
            tempModel.addThumbnail(context);
            aModel.add(tempModel);
        }
    }

    void addThumbNails(GVRSceneObject room) {
        Log.d(TAG, "Adding all thumbnails to the Room");
        int lSlots = gSlots;

        int count = aModel.size();
        float xPosition = -2.0f;

        ArrayList<GVRAnimation> aAnimation = new ArrayList<GVRAnimation>();
        GVRAnimation animation;

        for (int i = 0; i < currentThumbNailsInRoom.size(); i++) {
            animation = new GVRRotationByAxisAnimation(currentThumbNailsInRoom.get(i), 2, 360, 0, 1, 0).start(context.getAnimationEngine());
            //animation.setRepeatMode(1);
            animation.setRepeatCount(-1);

            aAnimation.add(animation);
        }

        for (int i = 0; i < currentThumbNailsInRoom.size(); i++) {
            while (!aAnimation.get(i).isFinished()) {
            }
        }

        for (int i = 0; i < currentThumbNailsInRoom.size(); i++) {
            context.getAnimationEngine().stop(aAnimation.get(i));
        }

        aAnimation.clear();

        for (GVRSceneObject oneChild : currentThumbNailsInRoom) {
            room.removeChildObject(oneChild);
        }

        for (int i = gStart; i < count; ) {
            aModel.get(i).thumbnail.getTransform().setPosition(xPosition, 205.0f, 980.0f);
            xPosition += 6.0;

            room.addChildObject(aModel.get(i).thumbnail);
            currentThumbNailsInRoom.add(aModel.get(i).thumbnail);

            lSlots--;

            i = (i + 1) % count;
            gStart = i;

            if (lSlots == 0) {
                break;
            }

        }
    }

    void removeThumbNailsFromCurrentScene(GVRSceneObject room) {
        for (GVRSceneObject thumbNail : currentThumbNailsInRoom) {
            room.removeChildObject(thumbNail);
        }
    }

    void displayModelIfSelected(GVREyePointeeHolder holder, GVRScene scene, GVRSceneObject room, GVRWidgetSceneObject widget) {


        // Remove Old Model If any
        // If Tapped On Current Model Close It
        if (currentDisplayedModel != null && holder.equals(currentDisplayedModel.model.getEyePointeeHolder())) {
            room.removeChildObject(currentDisplayedModel.getModel(context));

            // Set Camera Position to Default
            // Reset Camera Position to Default
            scene.getMainCameraRig().getTransform().setPosition(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z);
            widget.getTransform().setPosition(defaultCameraPosition.x - 3.0f, defaultCameraPosition.y, defaultCameraPosition.z - 5);

            Log.e(TAG, "TApped on Model so displaying THumbNail");
            // Add ThumbNails already present before
            for (GVRSceneObject thumbnail : currentThumbNailsInRoom) {
                room.addChildObject(thumbnail);
            }
            currentDisplayedModel = null;
            return;
        } else {
            for (int index = 0; index < aModel.size(); index++) {
                if (holder.equals(aModel.get(index).thumbnail.getEyePointeeHolder())) {
                    // Remove Old Message If any
                    // if (textMessage != null)
                    //    room.removeChildObject(textMessage);

                    // Remove Old Thumbanail
                    removeThumbNailsFromCurrentScene(room);

                    Log.d(TAG, "Called Loading Model");
                    //showMessage("Loading");
                    GVRSceneObject tempModelSO = aModel.get(index).getModel(context);
                    // Custome SHader
                    //applyShaderOnSkyBox(tempModelSO);
                    scene.bindShaders();


                    Log.d(TAG, "Loading Done");
                    if (tempModelSO != null) {
                        //mRoom.removeChildObject(textMessage);
                        room.addChildObject(tempModelSO);


                        Log.d(TAG, "Loading Done");
                        currentDisplayedModel = aModel.get(index);
                        //scene.addSceneObject(room);
                        scene.bindShaders();
                    } else {
                        //  showMessage("Error Loading Model");
                    }
                    break;
                }
            }
        }
    }


    void onFowardSwipeOfThumbNails(GVREyePointeeHolder holder, GVRSceneObject room) {
        for (int i = 0; i < currentThumbNailsInRoom.size(); i++) {
            if (holder == currentThumbNailsInRoom.get(i).getEyePointeeHolder()) {
                addThumbNails(room);
                return;
            }
        }
    }

    void onScrollOverModel(GVREyePointeeHolder holder, float scrollValue) {
        GVRAnimation animation = null;

        if (currentDisplayedModel != null) {
            if (holder == currentDisplayedModel.getModel(context).getEyePointeeHolder()) {
                Log.e(TAG, "Angle mover applied");
                if (scrollValue > 0)
                    animation = new GVRRotationByAxisAnimation(currentDisplayedModel.getModel(context), 0.1f, 35, 0, 1, 0).start(context.getAnimationEngine());
                else
                    animation = new GVRRotationByAxisAnimation(currentDisplayedModel.getModel(context), 0.1f, 35, 0, -1, 0).start(context.getAnimationEngine());
            }

        }
    }

    void onZoomOverModel(float zoomBy) {
        // User is at 1000 and Object is at 980
        float zTransform = (1010.f - 980.f) / (100.0f - zoomBy);
        Log.e(TAG, "Zoom by" + Float.toString(zTransform));

        if (currentDisplayedModel != null)
            currentDisplayedModel.getModel(context).getTransform().setPositionZ(980.0f + zTransform);
    }
    // END Models Features

    // START SkyBox Features
    void loadDefaultSkyBoxList() {
        String[] aSSkyBox = AssetsReader.getAssetsList(activity, sDefaultSkyBoxDirectory);
        aODefaultSkyBox = new ArrayList<SkyBox>();

        for (String sSkyBoxName : aSSkyBox) {
            aODefaultSkyBox.add(new SkyBox(sSkyBoxName));
        }
    }

    ArrayList<String> getSkyBoxList() {
        ArrayList<String> sAEntireList = new ArrayList<String>();

        // Default SkyBox List
        for (SkyBox oSkyBox : aODefaultSkyBox) {
            sAEntireList.add(oSkyBox.getSkyBoxName());
        }

        return sAEntireList;
    }

    void addSkyBox(int index, GVRScene scene) {
        Log.e(TAG, "Adding SkyBox");

        if (currentSkyBox != null)
            scene.removeSceneObject(currentSkyBox);

        GVRSphereSceneObject current = aODefaultSkyBox.get(index).getSkyBox(context, sDefaultSkyBoxDirectory + "/");

        if (current != null) {
            scene.addSceneObject(current);
            currentSkyBox = current;
            // TODO
            current.getTransform().setPosition(0.0f, 200.0f, 1000.0f);
        } else {
            Log.e(TAG, "SkyBox is null");
        }
    }

    // END SkyBox Features
}

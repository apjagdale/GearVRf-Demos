package org.gearvrf.modelviewer2;


import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRPhongShader;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRRenderPass;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRRotationByAxisAnimation;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRSphereSceneObject;
import org.gearvrf.util.AssetsReader;
import org.gearvrf.util.Banner;
import org.gearvrf.util.NoTextureShader;
import org.gearvrf.util.OutlineShader;
import org.gearvrf.widgetplugin.GVRWidgetSceneObject;
import org.joml.Vector3f;

import java.io.File;
import java.util.ArrayList;

public class Controller {
    private static final String TAG = "Abhijit";

    // Variables related to SkyBox
    private ArrayList<SkyBox> aODefaultSkyBox;
    private ArrayList<SkyBox> aOSDSkyBox;
    private final String sSDSkyBoxDirectory = "GVRModelViewer2/SkyBox";
    private final String sDefaultSkyBoxDirectory = "skybox";
    private GVRSphereSceneObject currentSkyBox;

    // Variables related to Camera
    private Vector3f defaultCameraPosition = new Vector3f(0, 200, 1000);
    private ArrayList<CameraPosition> oDefaultCameraPosition;
    private CameraPosition oCurrentPosition;

    // Variables related to Model
    private final String sEnvironmentPath = Environment.getExternalStorageDirectory().getPath();
    private ArrayList<Model> aModel;
    private Model currentDisplayedModel;
    private ArrayList<GVRSceneObject> currentThumbNailsInRoom = new ArrayList<GVRSceneObject>();
    private static final int gSlots = 3;
    private int gStart = 0;

    // Variables related to Banner
    private Banner oBannerCount;

    // Variables related to Custom Shader
    private ArrayList<String> aSCustomShaderList;

    private GVRActivity activity;
    private GVRContext context;

    public Controller(GVRActivity activity, GVRContext context) {
        this.activity = activity;
        this.context = context;
    }

    void initializeController() {
        loadDefaultSkyBoxList();
        loadSDSkyBoxList();

        loadModelsList();
        loadCameraPositionList();
        loadCustomShaderList();
    }

    // START Custom Shader Feature
    private void loadCustomShaderList() {
        aSCustomShaderList = new ArrayList<String>();
        aSCustomShaderList.add("Original");
        aSCustomShaderList.add("No Texture");
        aSCustomShaderList.add("Outline");
        aSCustomShaderList.add("Lines");
    }

    public ArrayList<String> getListOfCustomShaders() {
        return aSCustomShaderList;
    }

  //  ArrayList<GVRMaterial> temp;
    public void applyCustomShader(int index, GVRScene scene) {
        if(currentDisplayedModel == null)
            return;
        ArrayList<GVRRenderData> renderDatas = currentDisplayedModel.getModel(context).getAllComponents(GVRRenderData.getComponentType());
        GVRMaterial outlineMaterial = new GVRMaterial(context);


        switch (index) {
            case 0:
                for (int i = 0; i < renderDatas.size(); i++) {
                    renderDatas.get(i).setMaterial(currentDisplayedModel.originalMaterial.get(i));
                    renderDatas.get(i).setShaderTemplate(GVRPhongShader.class);
                    renderDatas.get(i).setCullFace(GVRRenderPass.GVRCullFaceEnum.Back);
                    renderDatas.get(i).setDrawMode(4);
                }
                break;
            case 1:
                for (GVRRenderData rdata : renderDatas) {
                    rdata.setShaderTemplate(NoTextureShader.class);
                    rdata.setDrawMode(4);
                }
                break;

            case 2:
                outlineMaterial.setVec4(OutlineShader.COLOR_KEY, 0.4f, 0.1725f, 0.1725f, 1.0f);
                outlineMaterial.setFloat(OutlineShader.THICKNESS_KEY, 2.0f);
                //temp = new ArrayList<GVRMaterial>();
                for (GVRRenderData rdata : renderDatas) {
                    //temp.add(rdata.getMaterial());
                    rdata.setMaterial(outlineMaterial);
                    rdata.setShaderTemplate(OutlineShader.class);
                    rdata.setCullFace(GVRRenderPass.GVRCullFaceEnum.Front);
                    rdata.setDrawMode(4);
                }
                break;
            case 3:
                for (GVRRenderData rdata : renderDatas) {
                    rdata.setShaderTemplate(GVRPhongShader.class);
                    rdata.setDrawMode(1);
                }

                break;
        }

        scene.bindShaders();
    }
    // END Custom Shader Feature

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
        oDefaultCameraPosition = new ArrayList<CameraPosition>();
        oDefaultCameraPosition.add(new CameraPosition(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z));
        oDefaultCameraPosition.add(new CameraPosition(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z - 150));
        oDefaultCameraPosition.add(new CameraPosition(defaultCameraPosition.x, defaultCameraPosition.y - 50, defaultCameraPosition.z + 50));
        oDefaultCameraPosition.add(new CameraPosition(defaultCameraPosition.x, defaultCameraPosition.y + 50, defaultCameraPosition.z));
        oDefaultCameraPosition.add(new CameraPosition(defaultCameraPosition.x - 30.0f, defaultCameraPosition.y, defaultCameraPosition.z + 50));
        oDefaultCameraPosition.add(new CameraPosition(defaultCameraPosition.x + 30, defaultCameraPosition.y, defaultCameraPosition.z + 50));
    }

    public ArrayList<String> getCameraPositionList() {
        ArrayList<String> list = new ArrayList<String>();
        for (CameraPosition position : oDefaultCameraPosition) {
            Vector3f coordinate = position.getCameraPosition();
            String sPosition = "X:" + Float.toString(coordinate.x) + " Y:" + Float.toString(coordinate.y) + " Z:" + Float.toString(coordinate.z);
            list.add(sPosition);
        }
        return list;
    }

    public void displayNavigators(GVRSceneObject room) {
        oCurrentPosition = oDefaultCameraPosition.get(0);
        oDefaultCameraPosition.get(0).loadNavigator(context);

        for (int i = 1; i < oDefaultCameraPosition.size(); i++) {
            GVRSphereSceneObject temp = oDefaultCameraPosition.get(i).loadNavigator(context);
            room.addChildObject(temp);
        }
    }

    public void setCameraPosition(GVRScene scene, GVRWidgetSceneObject widget, int index) {
        Vector3f position = oDefaultCameraPosition.get(index).getCameraPosition();
        scene.getMainCameraRig().getTransform().setPosition(position.x, position.y, position.z);
        if (widget != null)
            widget.getTransform().setPosition(position.x - 3.0f, position.y, position.z - 5);
    }

    public void setCameraPositionByNavigator(GVREyePointeeHolder picked, GVRScene scene, GVRSceneObject room, GVRWidgetSceneObject widget) {
        for (int i = 0; i < oDefaultCameraPosition.size(); i++) {
            if (picked.equals(oDefaultCameraPosition.get(i).sphereObject.getEyePointeeHolder())) {
                Vector3f coordinates = oDefaultCameraPosition.get(i).getCameraPosition();
                scene.getMainCameraRig().getTransform().setPosition(coordinates.x, coordinates.y, coordinates.z);

                if (oCurrentPosition != null) {
                    room.addChildObject(oCurrentPosition.loadNavigator(context));
                }

                Log.e(TAG, "REmoving navigator " + Integer.toString(i));
                room.removeChildObject(oDefaultCameraPosition.get(i).sphereObject);
                oCurrentPosition = oDefaultCameraPosition.get(i);


                if (widget != null)
                    widget.getTransform().setPosition(coordinates.x - 3.0f, coordinates.y, coordinates.z - 5);

            }
        }
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
            // Remove Sphere of Default Camera View
            scene.getMainCameraRig().getTransform().setPosition(defaultCameraPosition.x, defaultCameraPosition.y, defaultCameraPosition.z);
            //widget.getTransform().setPosition(defaultCameraPosition.x - 3.0f, defaultCameraPosition.y, defaultCameraPosition.z - 5);
            setCameraPositionByNavigator(oDefaultCameraPosition.get(0).sphereObject.getEyePointeeHolder(), scene, room, widget);

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
                    removeThumbNailsFromCurrentScene(room);
                    Log.d(TAG, "Called Loading Model");
                    GVRSceneObject tempModelSO = aModel.get(index).getModel(context);
                    scene.bindShaders();


                    Log.d(TAG, "Loading Done");
                    if (tempModelSO != null) {
                        room.addChildObject(tempModelSO);

                        // Custom Shader
                        //applyCustomShader(tempModelSO);
                        Log.d(TAG, "Loading Done");
                        currentDisplayedModel = aModel.get(index);
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
    void loadSDSkyBoxList() {
        ArrayList<String> extensions = new ArrayList<String>();
        extensions.add(".png");

        CardReader cRObject = new CardReader(sEnvironmentPath + "/" + sSDSkyBoxDirectory + "/", extensions);
        File list[] = cRObject.getModels();

        aOSDSkyBox = new ArrayList<SkyBox>();

        if (list != null)
            for (File sSkyBoxName : list) {
                aOSDSkyBox.add(new SkyBox(sSkyBoxName.getName()));
            }
    }

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

        // SDCard SkyBox List
        if (aOSDSkyBox != null)
            for (SkyBox oSkyBox : aOSDSkyBox) {
                sAEntireList.add(oSkyBox.getSkyBoxName());
            }

        return sAEntireList;
    }

    void addSkyBox(int index, GVRScene scene) {
        Log.e(TAG, "Adding SkyBox");
        GVRSphereSceneObject current = null;
        if (currentSkyBox != null)
            scene.removeSceneObject(currentSkyBox);

        int count = aODefaultSkyBox.size();
        if (index < count) {
            current = aODefaultSkyBox.get(index).getSkyBox(context, sDefaultSkyBoxDirectory + "/");
        } else {
            current = aOSDSkyBox.get(index - count).getSkyBoxFromSD(context, sEnvironmentPath + "/" + sSDSkyBoxDirectory + "/");
        }

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

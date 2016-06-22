package org.gearvrf.modelviewer2;


import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.scene_objects.GVRSphereSceneObject;

import java.util.ArrayList;
import java.util.concurrent.Future;

public class SkyBox {
    ArrayList<GVRSphereSceneObject> aSkyBoxModel = new ArrayList<GVRSphereSceneObject>();
    String[] skyBoxNameList;
    GVRSphereSceneObject currentSkyBoxModel;

    String skyBoxPath = "skybox/";

    public SkyBox(String[] skyBoxName){
        this.skyBoxNameList = skyBoxName;
        for(int i = 0; i < skyBoxName.length; i++)
            aSkyBoxModel.add(null);
    }

    private GVRSphereSceneObject loadSkyBoxModel(GVRContext gvrContext, String skyBoxName){
        GVRSphereSceneObject sphereObject = null;

        // load texture
        Future<GVRTexture> texture = null;
        try {
            texture = gvrContext.loadFutureTexture(new GVRAndroidResource(gvrContext, skyBoxPath + skyBoxName));
        }catch (Exception e){
            e.printStackTrace();
        }
        // create a sphere scene object with the specified texture and triangles facing inward (the 'false' argument)
        sphereObject = new GVRSphereSceneObject(gvrContext, false, texture);
        sphereObject.getTransform().setScale(2000, 2000, 2000);
        return  sphereObject;
    }

    public GVRSphereSceneObject getSkyBox(GVRContext gvrContext, int index){
        if(aSkyBoxModel.get(index) == null) {
            aSkyBoxModel.set(index, loadSkyBoxModel(gvrContext, skyBoxNameList[index]));
            currentSkyBoxModel = aSkyBoxModel.get(index);
            return aSkyBoxModel.get(index);
        }
        else{
            currentSkyBoxModel = aSkyBoxModel.get(index);
            return aSkyBoxModel.get(index);
        }
    }

    public GVRSphereSceneObject getCurrentSkyBox(){
        return currentSkyBoxModel;
    }
}

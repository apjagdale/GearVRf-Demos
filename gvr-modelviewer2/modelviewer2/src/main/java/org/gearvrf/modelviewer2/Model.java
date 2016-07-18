/* Copyright 2015 Samsung Electronics Co., LTD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gearvrf.modelviewer2;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVREyePointeeHolder;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshEyePointee;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRTexture;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.gearvrf.scene_objects.GVRTextViewSceneObject;
import org.gearvrf.util.AccessibilitySceneShader;
import org.gearvrf.util.BoundingBoxCreator;
import org.joml.Vector3f;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Model {
    String name;
    String location;

    /* Will also have
     1. Eye Pointer attached
     2. GVRTextViewSceneObject textObject attached*/
  //  GVRSceneObject thumbnail;

    GVRModelSceneObject model;
    ArrayList<GVRMaterial> originalMaterial;
    List<GVRAnimation> animation;


    private static final String TAG = "Abhijit";

    public Model(String name, String location) {
        this.name = name;
        this.location = location;
    }

    String getModelName(){
        return name;
    }
    private void saveRenderData(){
        originalMaterial = new ArrayList<GVRMaterial>();
        ArrayList <GVRRenderData> rdata = model.getAllComponents(GVRRenderData.getComponentType());
        for(GVRRenderData r:  rdata){
            originalMaterial.add(r.getMaterial());
        }
    }



    public void addThumbnail(GVRContext context) {
        GVRTexture icon = null;

        try {
            // TODO Add Thumbnail Respective to the Model
            icon = context.loadTexture(new GVRAndroidResource(context, "default-thumbnail.png"));
        } catch (IOException e) {
            Log.e(TAG, "Unable to load texture");
            e.printStackTrace();
        }

        /*
        thumbnail = new GVRSceneObject(context, context.createQuad(4.5f, 4.5f), icon);
        thumbnail.getRenderData().getMaterial().setTexture("default-name", icon);

        // Adding Eye Pointee
        GVREyePointeeHolder playPauseHolder = new GVREyePointeeHolder(context);
        playPauseHolder.addPointee(new GVRMeshEyePointee(context, thumbnail.getRenderData().getMesh()));
        thumbnail.attachEyePointeeHolder(playPauseHolder);

        // Adding Text to ThumbNail
        thumbnail.addChildObject(getTextViewSceneObject(context, name.substring(0, 8), 25, Color.GREEN, 0.0f, 0.0f, 1.0f));*/
    }

    private GVRTextViewSceneObject getTextViewSceneObject(GVRContext context, String text, int size, int color, float posX, float posY, float posZ) {
        GVRTextViewSceneObject textObject = new GVRTextViewSceneObject(context, text);
        textObject.setGravity(Gravity.CENTER);
        textObject.setTextSize(size);
        textObject.setTextColor(color);
        textObject.getTransform().setPosition(posX, posY, posZ);
        textObject.getRenderData().setRenderingOrder(GVRRenderData.GVRRenderingOrder.TRANSPARENT);

        return textObject;
    }

    private void loadModel(GVRContext context) {
        try {
            Log.d(TAG, "Absent so loading" + name);
            model = context.loadModelFromSD(location + name);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Unable to load model");
            return;
        }

        GVRSceneObject.BoundingVolume bv = model.getBoundingVolume();
        BoundingBoxCreator boundingBox = new BoundingBoxCreator(context, bv);
        GVREyePointeeHolder playPauseHolder = new GVREyePointeeHolder(context);
        playPauseHolder.addPointee(new GVRMeshEyePointee(context, boundingBox.getMesh()));
        model.attachEyePointeeHolder(playPauseHolder);


        // Adding Pointee to Model
        /*GVRSceneObject.BoundingVolume*/ bv = model.getBoundingVolume();
        Float radius = bv.radius;
        Log.e(TAG, "Radius" + Float.toString(radius));
        Vector3f min_corner = bv.minCorner;
        Vector3f max_corner = bv.maxCorner;

           // TODO Scale Approparetly
        if (radius > 10.0f) {
            float scaleFactor = 10 / radius;
            model.getTransform().setScale(scaleFactor, scaleFactor, scaleFactor);
        }


        //After setting position
 /*       bv = model.getBoundingVolume();
        radius = bv.radius;
        Log.e(TAG, "Radius" + Float.toString(radius));
        min_corner = bv.minCorner;
        max_corner = bv.maxCorner;


        BoundingBoxCreator boundingBox = new BoundingBoxCreator(context, bv);
        GVREyePointeeHolder playPauseHolder = new GVREyePointeeHolder(context);
        playPauseHolder.addPointee(new GVRMeshEyePointee(context, boundingBox.getMesh()));
        model.attachEyePointeeHolder(playPauseHolder);

        // Just to check bounding range
        AccessibilitySceneShader shader = new AccessibilitySceneShader(context);
        GVRRenderData renderData2 = new GVRRenderData(context);
        GVRMaterial mat2 = new GVRMaterial(context, shader.getShaderId());
        GVRMesh t = boundingBox.getMesh();
        renderData2.setMesh(t);
        renderData2.setMaterial(mat2);
        model.attachRenderData(renderData2);*/

        // Make Copy of Original Render Data
        saveRenderData();
       // model.getTransform().setPosition(0.0f, 200.0f, 980.0f);



        // Load Animations
        animation = model.getAnimations();
        Log.e(TAG, "Animation" + Integer.toString(animation.size()));
    }

    public List<GVRAnimation> getAnimationsList(){
        return animation;
    }

    public GVRSceneObject getModel(GVRContext context) {
        if (model == null) {
            loadModel(context);
        }
        return model;
    }
}

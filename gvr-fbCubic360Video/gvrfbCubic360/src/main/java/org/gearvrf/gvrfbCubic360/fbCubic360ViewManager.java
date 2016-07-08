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

package org.gearvrf.gvrfbCubic360;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRBitmapTexture;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.utility.Log;

import java.io.IOException;

public class fbCubic360ViewManager extends GVRMain implements Runnable {

    private GVRContext mGVRContext = null;
    private GVRSceneObject mSceneObject = null;
    MediaMetadataRetriever mediaMetadataRetriever = null;
    GVRContext gvrContext;
    GVRMesh cubeMesh;
    long duration;
    long start = 0;
    GVRScene mainScene;

    public void run() {
        Bitmap bmFrame;
        GVRBitmapTexture mTexture;
        GVRMaterial mm;

        try {
            while (true) {
                start += 60;
                start %= duration;
                Thread.sleep(60);
                Log.e("Duration", Long.toString(start));
                bmFrame = mediaMetadataRetriever.getFrameAtTime(start * 1000);
                mTexture = new GVRBitmapTexture(gvrContext, bmFrame);
                mm = new GVRMaterial(gvrContext);
                mm.setMainTexture(mTexture);
                mSceneObject.getRenderData().setMaterial(mm);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onInit(GVRContext gvrContext) {
        this.gvrContext = gvrContext;
        mediaMetadataRetriever = new MediaMetadataRetriever();

        AssetFileDescriptor afd = null;
        try {
            afd = gvrContext.getActivity().getAssets().openFd("output.mp4");
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaMetadataRetriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

        String times = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Integer.parseInt(times);
        Bitmap bmFrame = mediaMetadataRetriever.getFrameAtTime(start);

        mGVRContext = gvrContext;
        mainScene = mGVRContext.getNextMainScene();
        mainScene.getMainCameraRig().getTransform().setPosition(0.0f, 0.0f, 0.0f);

        cubeMesh = mGVRContext.loadMesh(new GVRAndroidResource(mGVRContext, R.raw.video_facebook));

        GVRBitmapTexture mTexture = new GVRBitmapTexture(gvrContext, bmFrame);
        mSceneObject = new GVRSceneObject(gvrContext, cubeMesh, mTexture);

        mSceneObject.getTransform().setScale(24.5f, 24.5f, 24.5f);
        mainScene.addSceneObject(mSceneObject);

        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void onStep() {
    }

}

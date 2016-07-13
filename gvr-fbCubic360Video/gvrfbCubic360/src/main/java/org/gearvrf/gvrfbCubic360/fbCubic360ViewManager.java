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
import android.media.MediaPlayer;

import org.gearvrf.GVRAndroidResource;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRMesh;
import org.gearvrf.scene_objects.GVRVideoSceneObject;

import java.io.IOException;

public class fbCubic360ViewManager extends GVRMain
{
    /** Called when the activity is first created. */
    @Override
    public void onInit(GVRContext gvrContext) {

        GVRScene scene = gvrContext.getNextMainScene();

        // set up camerarig position (default)
        scene.getMainCameraRig().getTransform().setPosition( 0.0f, 0.0f, 0.0f );

        // get cube mesh
        GVRMesh cubeMesh = gvrContext.loadMesh(new GVRAndroidResource(gvrContext, R.raw.video_facebook));

        // create mediaplayer instance
        MediaPlayer mediaPlayer = new MediaPlayer();
        AssetFileDescriptor afd;
        try {
            afd = gvrContext.getContext().getAssets().openFd("output.mp4");
            android.util.Log.d("Minimal360Video", "Assets was found.");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            android.util.Log.d("Minimal360Video", "DataSource was set.");
            afd.close();
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            gvrContext.getActivity().finish();
            android.util.Log.e("Minimal360Video", "Assets were not loaded. Stopping application!");
        }

        mediaPlayer.setLooping( true );
        android.util.Log.d("Minimal360Video", "starting player.");
        mediaPlayer.start();

        // create video scene
        GVRVideoSceneObject video = new GVRVideoSceneObject( gvrContext, cubeMesh, mediaPlayer, GVRVideoSceneObject.GVRVideoType.MONO );
        video.getTransform().setScale(5, 5, 5 );
        video.setName( "video" );

        // apply video to scene
        scene.addSceneObject( video );
    }

    @Override
    public void onStep() {
    }

}


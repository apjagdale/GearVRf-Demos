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

import org.gearvrf.scene_objects.view.GVRFrameLayout;
import org.gearvrf.util.VRTouchPadGestureDetector;
import org.gearvrf.util.VRTouchPadGestureDetector.OnTouchPadGestureListener;
import org.gearvrf.util.VRTouchPadGestureDetector.SwipeDirection;
import org.gearvrf.GVRActivity;
import org.gearvrf.widgetplugin.GVRWidgetPlugin;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.util.ArrayList;

public class ModelViewer2Activity extends GVRActivity implements
        OnTouchPadGestureListener {

    private GVRWidgetPlugin mPlugin= new GVRWidgetPlugin(this);
    private VRTouchPadGestureDetector mDetector = null;
    private ModelViewer2Manager mManager = null;
    private GVRFrameLayout frameLayout;
    private Button button1, button2;
    MyMenu mWidget;


    String[] getSkyBoxList(){
        String skyBoxList[] = null;
        try {
            Resources res = getResources(); //if you are in an activity
            AssetManager am = res.getAssets();
            skyBoxList = am.list("skybox");
            for ( int i = 0;i<skyBoxList.length;i++)
            {
                Log.d("",skyBoxList[i]);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return skyBoxList;
    }


	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        mPlugin.setViewSize(displaymetrics.widthPixels,
                displaymetrics.heightPixels);

        mDetector = new VRTouchPadGestureDetector(this);
        mWidget = new MyMenu();

        //SkyBox List
        mManager = new ModelViewer2Manager(this, mPlugin, getSkyBoxList());
        mPlugin.setCurrentScript(mManager);
        mWidget.mManager = mManager;


        setScript(mManager, "gvr.xml");

    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        Log.e("Abhijit", "onSingleTap");
        mManager.onSingleTap(e);
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.v("", "onLongPress");
    }

    @Override
    public boolean onSwipe(MotionEvent e, SwipeDirection swipeDirection,
                           float velocityX, float velocityY) {
        Log.v("", "onSwipe");
        mManager.onSwipe(e, swipeDirection, velocityX, velocityY);
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        mManager.onScroll(arg0, arg1, arg2, arg3);
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);

        if (mPlugin.getWidgetView() == null)
            return false;

        return mPlugin.dispatchTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        mPlugin.initializeWidget(mWidget);
        super.onResume();
    }

}

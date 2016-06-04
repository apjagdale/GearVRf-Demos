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

import android.content.Intent;

import org.gearvrf.util.VRTouchPadGestureDetector;
import org.gearvrf.util.VRTouchPadGestureDetector.OnTouchPadGestureListener;
import org.gearvrf.util.VRTouchPadGestureDetector.SwipeDirection;

import org.gearvrf.GVRActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.util.Log;

public class ModelViewer2Activity extends GVRActivity implements
        OnTouchPadGestureListener {

    private VRTouchPadGestureDetector mDetector = null;
    private ModelViewer2Manager mManager = null;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);

        mDetector = new VRTouchPadGestureDetector(this);
        mManager = new ModelViewer2Manager();
        setScript(mManager, "gvr.xml");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v("", "requestCode : " + requestCode);
        Log.v("", "resultCode : " + resultCode);
        Log.v("", "data : " + data.getDataString());

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {}

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return super.onKeyLongPress(keyCode, event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onSingleTap(MotionEvent e) {
        Log.v("", "onSingleTap");
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
}

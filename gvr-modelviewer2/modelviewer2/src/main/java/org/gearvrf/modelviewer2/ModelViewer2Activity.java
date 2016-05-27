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
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;

import org.gearvrf.GVRActivity;
import android.os.Bundle;
import android.view.MotionEvent;

public class ModelViewer2Activity extends GVRActivity implements
		OnGestureListener, OnDoubleTapListener{

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
        String internalPath = getApplicationContext().getFilesDir().getPath();
		setScript(new ModelViewer2Manager(), "gvr.xml");

    }

	@Override
	public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onDoubleTap(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent motionEvent) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent motionEvent) {

	}

	@Override
	public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
		return false;
	}
}

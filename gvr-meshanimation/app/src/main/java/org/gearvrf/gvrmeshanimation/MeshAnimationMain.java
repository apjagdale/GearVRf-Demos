package org.gearvrf.gvrmeshanimation;

import java.io.IOException;

import org.gearvrf.GVRActivity;
import org.gearvrf.GVRColorShader;
import org.gearvrf.GVRContext;
import org.gearvrf.GVRMaterial;
import org.gearvrf.GVRMesh;
import org.gearvrf.GVRMeshCollider;
import org.gearvrf.GVRRenderData;
import org.gearvrf.GVRScene;
import org.gearvrf.GVRMain;
import org.gearvrf.GVRSceneObject;
import org.gearvrf.GVRShaderId;
import org.gearvrf.animation.GVRAnimation;
import org.gearvrf.animation.GVRAnimationEngine;
import org.gearvrf.animation.GVRAnimator;
import org.gearvrf.animation.GVRRepeatMode;
import org.gearvrf.scene_objects.GVRModelSceneObject;
import org.joml.Matrix4f;

import android.graphics.Color;
import android.util.Log;

public class MeshAnimationMain extends GVRMain {

    private GVRContext mGVRContext;

    private final String mModelPath = "TRex_NoGround.fbx";

    private GVRActivity mActivity;

    private static final String TAG = "MeshAnimationSample";

    private GVRAnimationEngine mAnimationEngine;
    GVRAnimator mAssimpAnimation = null;

    GVRSceneObject menuButton;

    public MeshAnimationMain(GVRActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onInit(GVRContext gvrContext) {
        mGVRContext = gvrContext;
        mAnimationEngine = gvrContext.getAnimationEngine();

        gvrContext.getMainScene().getMainCameraRig().getLeftCamera().setBackgroundColor(Color.WHITE);
        gvrContext.getMainScene().getMainCameraRig().getRightCamera().setBackgroundColor(Color.WHITE);
        try {

            GVRMesh sMesh = gvrContext.createQuad(4,2);
            /*
                menuButton is the root which has a mesh of a quad to it.
                It also has a child at index 0 which is the FBX model(TRex)
                Whenever you want to make changes to the Trex model do menuButton.getChildByIndex(0) or have its reference
                    > childTrexModel = menuButton.getChildByIndex(0);
            */
            menuButton = gvrContext.getAssetLoader().loadModel("TRex_NoGround.fbx", gvrContext.getMainScene());
            mAssimpAnimation = (GVRAnimator) menuButton.getComponent(GVRAnimator.getComponentType());
            mAssimpAnimation.setRepeatMode(GVRRepeatMode.REPEATED);
            mAssimpAnimation.setRepeatCount(-1);

            // Getting the TrexModel
            GVRSceneObject childTrexModel = menuButton.getChildByIndex(0);
            childTrexModel.getTransform().rotateByAxis(90,0,1,0);

            // This menuButton is a Quad and also the root SceneObject
            menuButton.getTransform().setPosition(0, 0, -10);
            menuButton.attachRenderData(new GVRRenderData(gvrContext));
            menuButton.getRenderData().setMaterial(new GVRMaterial(gvrContext, new GVRShaderId(GVRColorShader.class)));
            menuButton.getRenderData().getMaterial().setColor(1,0,0f);
            menuButton.getRenderData().setMesh(sMesh);

            menuButton.getRenderData().setRenderingOrder(10000);

            // Start the animation
            mAssimpAnimation.start();

        } catch (IOException e) {
            e.printStackTrace();
            mActivity.finish();
            mActivity = null;
            Log.e(TAG, "One or more assets could not be loaded.");
        }

    }

    @Override
    public void onStep() {
    }
}

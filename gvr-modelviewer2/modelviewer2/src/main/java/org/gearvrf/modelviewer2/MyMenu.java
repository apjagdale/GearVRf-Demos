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



import org.gearvrf.utility.Log;
import org.gearvrf.widgetplugin.GVRWidget;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import java.util.ArrayList;

public class MyMenu extends GVRWidget {

    private Stage mStage;
    private Table mContainer;
    public ModelViewer2Manager mManager;

    Button clickMeButton , clickMeButton2;
    float mFontScale = 6.0f;
    Skin skin;

    int flagIfModelAlreadyLoaded = 0;
    boolean flagForSkyBox = true;
    boolean flagForCameraPosition = true;

    public void create(){
        mStage = new Stage();
        skin = new Skin(Gdx.files.internal("data/uiskin.json"));
        Gdx.input.setInputProcessor(mStage);



        // Parent Table contains all child tables
        mContainer = new Table();
        mStage.addActor(mContainer);
        mContainer.setFillParent(true);

        // Add Items Required for Menu and and it to child table
        Table childTable = new Table();
        final ScrollPane scroll = new ScrollPane(childTable, skin);

        InputListener stopTouchDown = new InputListener() {
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                event.stop();
                return false;
            }
        };





        childTable.row();
        childTable.add(new Label("", skin)).expandX().fillX();

        // Adding Position Select Box
        childTable.row();
        BitmapFont f = skin.getFont("default-font");
        f.getData().setScale(mFontScale - 1.0f);

        childTable.add(new Label("", skin)).expandX().fillX();
        SelectBoxStyle style = new SelectBoxStyle(f, Color.WHITE,
                skin.getDrawable("default-select"),
                skin.get(ScrollPaneStyle.class),
                skin.get(ListStyle.class));

        final SelectBox selectBoxCP = new SelectBox(style);
        selectBoxCP.setName("CameraPostionType");
        selectBoxCP.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                mManager.changeCameraPosition(selectBoxCP.getSelectedIndex());
            }
        });

        selectBoxCP.setVisible(true);
        childTable.add(selectBoxCP).height(120.0f * 2 ).width(600.0f);



        // Button 2
        childTable.row();
        childTable.row();
        childTable.add(new Label("", skin)).expandX().fillX();

        // Adding Button
        TextButton button = new TextButton("  Button2  ", skin);
        button.getLabel().setFontScale(mFontScale);
        clickMeButton2 = button;
        clickMeButton2.setName("clickMeButton2Name");
        button.addListener(new ClickListener() {
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("click " + x + ", " + y);
                Log.e("Abhijit", "Got clicked sceond");
                //clickMeButton2.setChecked(false);
            }
        });

        clickMeButton2.setChecked(false);
        clickMeButton2.setVisible(true);
        childTable.add(button).height(200).width(450);


        // Adding Animation Select Box
        childTable.row();
        //BitmapFont f = skin.getFont("default-font");
        //f.getData().setScale(mFontScale - 1.0f);

        childTable.add(new Label("", skin)).expandX().fillX();
        /*SelectBoxStyle style = new SelectBoxStyle(f, Color.WHITE,
                skin.getDrawable("default-select"),
                skin.get(ScrollPaneStyle.class),
                skin.get(ListStyle.class));*/

        final SelectBox selectBox = new SelectBox(style);
        selectBox.setName("SkyBoxType");
        selectBox.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                mManager.addSkyBox(selectBox.getSelectedIndex());
            }
        });

        selectBox.setVisible(true);
        childTable.add(selectBox).height(120.0f * 2 ).width(600.0f);

        // Slider for Zoom
        childTable.row();
        Slider slider = null;
        slider = new Slider(0, 100, 1, false, skin);
        slider.setName("Zoom");
        slider.setVisible(true);
        slider.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                float value = ((Slider)actor).getValue();
              Log.e("Abhijit", "Value zoom" + ((Slider)actor).getValue());
                //((Slider) actor).setValue(value);

                mManager.zoomCurrentModel(value);
            }
        });
        Label zoom = new Label("  Zoom  ", skin);
        zoom.setFontScale(mFontScale);
        childTable.pad(10).add(zoom);
        childTable.add(slider).height(150.0f).width(800);


        mContainer.add(scroll).expand().fill().colspan(1);
        mContainer.row().space(1).padBottom(1);
    }

    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        mStage.act(Gdx.graphics.getDeltaTime());
       // Log.e("Abhijit", "Render called");

        if(clickMeButton2.isChecked()){
            clickMeButton2.setChecked(false);
        }

        if(flagForSkyBox){
            Actor tempActor = mStage.getRoot().findActor("SkyBoxType");
            ((SelectBox) tempActor).setItems(mManager.getSkyBoxList());
            flagForSkyBox = false;
        }

        if(flagForCameraPosition){
            Actor tempActor = mStage.getRoot().findActor("CameraPostionType");
            ArrayList<String> list = mManager.getCameraPositionList();
            String tempList[] = new String[list.size()];
            for(int i = 0; i < list.size(); i++)
                tempList[i] = list.get(i);

            ((SelectBox) tempActor).setItems(tempList);
            flagForCameraPosition = false;
        }
        // Enable Slider if Model is loaded
        /*if(flagIfModelAlreadyLoaded == 0 && mManager.getCurrentDisplayedModel() != null){
            Actor aSlider = mStage.getRoot().findActor("Zoom");
            if(aSlider != null) {
                ((Slider) aSlider).setVisible(true);
                ((Slider) aSlider).setValue(0);
            }
            flagIfModelAlreadyLoaded = 1;
        }

        // Disable Slider if Model Not Present
        if(mManager.getCurrentDisplayedModel() != null){
            Actor aSlider = mStage.getRoot().findActor("Zoom");
            if(aSlider != null)
                ((Slider) aSlider).setVisible(true);

            flagIfModelAlreadyLoaded = 0;
        }*/




        /*Actor mColorButtonActor = mStage.getRoot().findActor("colorbutton");
        if(mColorButtonActor != null)
            ((SelectBox) mColorButtonActor).setItems("Cool", "Hot");*/

        mStage.draw();
    }

    public void resize(int width, int height) {
        super.resize(width, height);
        mStage.getViewport().update(width, height, true);
    }

    public void dispose() {
        mStage.dispose();
    }

    public boolean needsGL20() {
        return false;
    }
}

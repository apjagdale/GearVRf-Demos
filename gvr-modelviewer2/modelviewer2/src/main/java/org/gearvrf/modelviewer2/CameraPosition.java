package org.gearvrf.modelviewer2;


import org.joml.Vector3f;

import java.util.ArrayList;

public class CameraPosition {
    private ArrayList<Vector3f> listOfPositions = new ArrayList<Vector3f>();
    private int currentPositionIndex;

    public CameraPosition(float defaultX, float defaultY, float defaultZ){
        listOfPositions.add(new Vector3f(defaultX, defaultY, defaultZ));

        addCameraPosition(defaultX - 3.0f, defaultY+100, defaultZ+300);
        addCameraPosition(defaultX, defaultY + 5, defaultZ+200);
        addCameraPosition(defaultX, defaultY+40, defaultZ + 100);
        addCameraPosition(defaultX - 3.0f, defaultY+300, defaultZ+300);
        addCameraPosition(defaultX -100, defaultY + 5, defaultZ+200);
        addCameraPosition(defaultX - 200, defaultY+40, defaultZ + 100);

        currentPositionIndex = 0;
    }

    public void addCameraPosition(float newX, float newY, float newZ){
        listOfPositions.add(new Vector3f(newX, newY, newZ));
    }

    public Vector3f getIndexedCameraPosition(int index){
        return listOfPositions.get(index);
    }

    public ArrayList<Vector3f> getAllCameraPositions(){
        return listOfPositions;
    }
}

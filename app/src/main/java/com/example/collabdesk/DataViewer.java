package com.example.collabdesk;

import java.util.ArrayList;

public class DataViewer {

    ArrayList<SpaceObject> spaceObjectArrayList = new ArrayList<SpaceObject>();

    public ArrayList<SpaceObject> getSpaceObjectArrayList() {
        return spaceObjectArrayList;
    }

    public void addSpaceObject(SpaceObject spaceObject) {
        spaceObjectArrayList.add(spaceObject);
    }

    public void clearSpaceObjectList() {
        spaceObjectArrayList.clear();
    }

    private static DataViewer INSTANCE;
    private boolean waitClick = false;

    public boolean isWaitClick() {
        return waitClick;
    }

    public void setWaitClick(boolean waitClick) {
        this.waitClick = waitClick;
    }

    private DataViewer() {
    }

    public static DataViewer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataViewer();
        }
        return INSTANCE;
    }

    public void delSpaceObject(String name) {
        for (int i = 0; i < spaceObjectArrayList.size(); i++) {
            if (spaceObjectArrayList.get(i).getName().equals(name)) {
                spaceObjectArrayList.remove(i);
            }
        }


    }

    public int getMaxCoordX() {
        int x = 0;
        for (int i = 0; i < spaceObjectArrayList.size(); i++) {
            if (spaceObjectArrayList.get(i).getCoordX() > x) {
                x = (int) spaceObjectArrayList.get(i).getCoordX();
            }
        }
        return x;
    }
    public int getMaxCoordY() {
        int y = 0;
        for (int i = 0; i < spaceObjectArrayList.size(); i++) {
            if (spaceObjectArrayList.get(i).getCoordY() > y) {
                y = (int) spaceObjectArrayList.get(i).getCoordY();
            }
        }
        return y;
    }
}


package com.zakramlock.model;

import com.orm.SugarRecord;

import java.io.Serializable;

/**
 * Created by Devon 12/27/2016.
 */

public class Ressources extends SugarRecord implements Serializable {

    private boolean isFirst;
    public Ressources(boolean isFirst){
        this.isFirst = isFirst;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }
}

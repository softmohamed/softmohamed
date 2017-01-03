package com.zakramlock.config;

import com.zakramlock.model.AppItem;
import com.zakramlock.model.Ressources;

import java.util.List;

/**
 * Created by Devon 12/11/2016.
 */

public class ZakramDB {

    public static void lockApp(AppItem app) {
        app.save();
    }

    public static void UnlockApp(AppItem app) {
        app.delete();
    }

    public static List<AppItem> getLockedApps() {
        return AppItem.listAll(AppItem.class);
    }

    public static void setRessources(Ressources res){
        res.save();
    }

    public static List<Ressources> getRessources(){
        return Ressources.listAll(Ressources.class);
    }
    public static void updateRessources(Ressources res, boolean val){
        res.setFirst(val);
        res.save();
    }
}

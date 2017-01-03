package com.zakramlock.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.orm.SugarRecord;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Devon 12/11/2016.
 */

public class AppItem extends SugarRecord implements Serializable{
    private Long id;
    private String Label;
    private String Name;
    private String PackageName;
   // private Drawable Icon;
    private byte[] bitmapData;

    public AppItem(){

    }

    public AppItem(String Label, String Name, String PackageName,
                   Drawable Icon) {
        this.Label = Label;
        this.Name = Name;
       // this.Icon = Icon;
        Bitmap bitmap = ((BitmapDrawable)Icon).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        this.bitmapData = stream.toByteArray();
        this.PackageName = PackageName;
    }

    public Bitmap getBitmap(){
        return  BitmapFactory.decodeByteArray(this.bitmapData, 0, this.bitmapData.length);
    }

    public Long getId() {
        return id;
    }
    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPackageName() {
        return PackageName;
    }

    public void setPackageName(String packageName) {
        PackageName = packageName;
    }

    @Override
    public String toString() {
        return "AppItem{" +
                " id=" + id +
                ", Label='" + Label + '\'' +
                ", Name='" + Name + '\'' +
                ", PackageName='" + PackageName + '\'' +
                '}';
    }
}
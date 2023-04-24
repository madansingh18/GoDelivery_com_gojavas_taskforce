package com.gojavas.taskforce.utils;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

import java.util.Hashtable;
import java.util.Vector;

/**
 * Created by MadanS on 12/25/2017.
 */
public class CustomKvmSerializable extends Vector<String> implements KvmSerializable {

    private String tag;
    private Class klass;

    public CustomKvmSerializable(String tag, Class klass) {
        this.tag = tag;
        this.klass = klass;
    }

    @Override
    public Object getProperty(int i) {
        return this.get(i);
    }

    @Override
    public int getPropertyCount() {
        return 1;
    }

    @Override
    public void setProperty(int i, Object o) {
        this.add(o.toString());
    }

    @Override
    public void getPropertyInfo(int i, Hashtable hashtable, PropertyInfo propertyInfo) {
        propertyInfo.name = tag;
        propertyInfo.type = klass;
    }

//    @Override
//    public String getInnerText() {
//        return null;
//    }
//
//    @Override
//    public void setInnerText(String s) {
//
//    }
}
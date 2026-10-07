package com.troblecodings.launcher.models.mc;

import java.util.HashMap;
import java.util.Map;

public class MojangObjects {
    private Map<String, MojangAObjectDl> objects = new HashMap<>();


    public Map<String, MojangAObjectDl> getObjects() {
        return objects;
    }

    public void setObjects(Map<String, MojangAObjectDl> objects) {
        this.objects = objects;
    }
}

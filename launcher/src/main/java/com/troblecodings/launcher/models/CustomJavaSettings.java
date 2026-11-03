package com.troblecodings.launcher.models;

public class CustomJavaSettings {
    private String jreLocation;
    private String jreArgs;

    public CustomJavaSettings(String jdkLocation, String jdkArgs) {
        this.jreLocation = jdkLocation;
        this.jreArgs = jdkArgs;
    }

    public String getJreLocation() {
        return jreLocation;
    }

    public void setJreLocation(String jreLocation) {
        this.jreLocation = jreLocation;
    }

    public String getJreArgs() {
        return jreArgs;
    }

    public void setJreArgs(String jreArgs) {
        this.jreArgs = jreArgs;
    }
}

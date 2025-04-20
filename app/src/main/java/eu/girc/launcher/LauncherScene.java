package eu.girc.launcher;

public enum LauncherScene {
    HOME("Home.fxml");

    private String path;

    LauncherScene(String path) {
        this.path = path;
    }

    public String getPath() { return this.path; }
}

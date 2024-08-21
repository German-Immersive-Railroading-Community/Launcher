package eu.girc.launcher;

public class Main {
    public static void main(String[] args) {
        for (var property : System.getProperties().keySet()) {
            System.out.printf("%s -> %s\n", property, System.getProperty(property.toString()));
        }
        
        System.out.println(System.getProperty("os.name"));
//        Launcher.launch(args);
    }
}

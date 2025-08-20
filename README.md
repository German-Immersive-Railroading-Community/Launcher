# GIRC-Launcher
This is the Launcher specifically created for the GIRC Community, being responsible for managing the installation
of the Modpack required to play on the server.

## Requirements
A valid installation of `Java JDK 21` is required. We recommend Eclipse Temurin/AdoptOpenJDK Hotspot 21.

We use the Gradle build system, and have the Gradle wrapper in the root of the repository, as such a Gradle installation is not required.

## Building the project
To build the application, run:
```
./gradlew build
```

and to run it, enter:
```
./gradlew run
```
inside a terminal with the project root as the current directory.

## License
This project is licensed under the Apache-2.0 License, which is located in the file `LICENSE` in the project root.
package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.Launcher;
import com.troblecodings.launcher.javafx.tasks.McStartInfo;
import com.troblecodings.launcher.javafx.tasks.McStartTask;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class HomeView implements Builder<Parent> {
    private Task<McStartInfo> startTask = null;
    private Thread startThread = null;

    @Getter
    private final Label progressPercentLabel = new Label("0%");

    @Getter
    private final Label progressDescriptionLabel = new Label();

    @Getter
    private final ProgressBar progressBar = new ProgressBar(0.00);

    @Getter
    private final Button launchButton = new Button("Launch");

    private void clearProgress() {
        log.debug("Resetting progress");
        progressBar.setProgress(0.00);
        progressDescriptionLabel.setText("");
        progressPercentLabel.setText("0%");
    }

    @Override
    public Parent build() {
        log.debug("Building HomeView");

        progressBar.progressProperty().addListener((_, _, newval) -> {
            int percent = (int) (newval.doubleValue() * 100.0);
            progressPercentLabel.setText(percent + "%");
        });

        launchButton.setOnMouseClicked(_ -> {
            launchButton.setDisable(true);
            launchButton.setEffect(new ColorAdjust(1.0, -1.0, .0, .0));

            // Reset the progress on launch
            clearProgress();

            startTask = new McStartTask();

            // Bind progress observables to the task observables for status updates
            progressBar.progressProperty().bind(startTask.progressProperty());
            progressDescriptionLabel.textProperty().bind(startTask.messageProperty());

            startTask.setOnFailed(ev -> {
                // Unbind for cleanup
                progressBar.progressProperty().unbind();
                progressDescriptionLabel.textProperty().unbind();

                var ex = ev.getSource().getException();
                log.error("Game start task failed!", ex);
                progressDescriptionLabel.setText("Game start failed: " + ex.getMessage());

                // Enable button again
                launchButton.setDisable(false);
                launchButton.setEffect(null);
            });

            startTask.setOnCancelled(_ -> {
                // Unbind for cleanup
                progressBar.progressProperty().unbind();
                progressDescriptionLabel.textProperty().unbind();

                log.warn("Game start task cancelled.");
                progressDescriptionLabel.setText("Cancelled.");

                // Enable button again
                launchButton.setDisable(false);
                launchButton.setEffect(null);
            });

            startTask.setOnSucceeded(_ -> {
                // Unbind for cleanup
                progressBar.progressProperty().unbind();
                progressDescriptionLabel.textProperty().unbind();

                log.debug("Game start task completed.");
                progressDescriptionLabel.setText("Done.");

                // Enable button again
                launchButton.setDisable(false);
                launchButton.setEffect(null);
            });

            Launcher.getAppExecutor().execute(startTask);
        });

        progressBar.setProgress(0.00);

        // HACK: this makes the progress bar take all the space available to it
        progressBar.setMaxWidth(Double.MAX_VALUE);
        progressBar.setMaxHeight(Double.MAX_VALUE);

        progressDescriptionLabel.setTextFill(Color.LIGHTGRAY);
        progressPercentLabel.setTextFill(Color.LIGHTGRAY);

        var fillerRegion = new Region();
        HBox.setHgrow(fillerRegion, Priority.ALWAYS);

        var textBox = new HBox();
        textBox.getChildren().add(progressDescriptionLabel);
        textBox.getChildren().add(fillerRegion);
        textBox.getChildren().add(progressPercentLabel);

        var progressBox = new VBox();
        progressBox.getChildren().add(progressBar);
        progressBox.getChildren().add(textBox);

        var bottomBox = new VBox();
        bottomBox.setPadding(new Insets(0.0, 8.0, 5.0, 8.0));
        bottomBox.setSpacing(2.0);
        bottomBox.setAlignment(Pos.TOP_CENTER);
        bottomBox.getChildren().add(launchButton);
        bottomBox.getChildren().add(progressBox);

        var pane = new BorderPane();
        pane.setBottom(bottomBox);
        return pane;
    }
}

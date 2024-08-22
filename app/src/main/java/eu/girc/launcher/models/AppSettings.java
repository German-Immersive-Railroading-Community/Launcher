package eu.girc.launcher.models;

import com.google.gson.annotations.SerializedName;

public class AppSettings {
    @SerializedName("page_transition_duration")
    // The page transition duration, in milliseconds.
    // Transitions are half a second long by default.
    private int transitionDuration = 500;

    public AppSettings() {

    }

    /**
     * Gets the application-wide transition duration, in milliseconds, between two effects.
     *
     * @return The transition duration, in milliseconds.
     */
    public int getTransitionDuration() { return transitionDuration; }

    /**
     * Sets the application-wide transition duration between two effects, in milliseconds.
     *
     * @param transitionDuration The new transition duration, in milliseconds.
     */
    public void setTransitionDuration(int transitionDuration) { this.transitionDuration = transitionDuration; }
}

package com.troblecodings.launcher.javafx.views;

import com.troblecodings.launcher.LauncherView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.kordamp.ikonli.javafx.FontIcon;

// TODO: determine if this could also be a record
/**
 * A data class representing a sidebar item.
 */
@Getter
@Setter
@AllArgsConstructor
public final class SidebarItem {
    /**
     * The display name of this sidebar item.
     */
    private String name;

    /**
     * The icon of this sidebar item.
     * Can be null, in which case no icon will be displayed.
     */
    private FontIcon icon;

    /**
     * The view that should be switched to.
     */
    private LauncherView associatedView;
}

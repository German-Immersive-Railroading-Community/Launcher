package com.troblecodings.launcher.di;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.troblecodings.launcher.views.HomeView;
import com.troblecodings.launcher.views.IView;
import com.troblecodings.launcher.views.SettingsView;
import com.troblecodings.launcher.views.ViewId;

public class ViewModule extends AbstractModule {

    @Override
    protected void configure() {
        MapBinder<ViewId, IView> views = MapBinder.newMapBinder(
                binder(),
                ViewId.class,
                IView.class
        );

        views.addBinding(ViewId.HOME).to(HomeView.class);
        views.addBinding(ViewId.SETTINGS).to(SettingsView.class);
    }
}

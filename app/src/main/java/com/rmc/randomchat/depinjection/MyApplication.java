package com.rmc.randomchat.depinjection;

import android.app.Application;

public class MyApplication extends Application {
    public final AppContainer appContainer = new AppContainer();
}

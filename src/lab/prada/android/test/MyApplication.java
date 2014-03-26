package lab.prada.android.test;

import com.cengalabs.flatui.FlatUI;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        FlatUI.setDefaultTheme(FlatUI.ORANGE);
    }
}

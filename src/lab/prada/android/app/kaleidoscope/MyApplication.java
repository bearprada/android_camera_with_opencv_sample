package lab.prada.android.app.kaleidoscope;

import com.cengalabs.flatui.FlatUI;

import android.app.Application;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        FlatUI.setDefaultTheme(FlatUI.ORANGE);
    }
}

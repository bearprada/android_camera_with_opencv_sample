package lab.prada.android.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class Utils {

    private static int sScreenWidth = 0;
    private static int sScreenHeight = 0;
    private static Context sApplicationContext;

    public static void setApplicationContext(Context applicationContext) {
        sApplicationContext = applicationContext;
    }

    /**
     * Gets screen size in portrait mode. If the screen is currently in
     * landscape mode, width and height will be swapped. Thus, width is always
     * smaller or equal height.
     * @return screen size in portrait mode.
     */
    @SuppressLint("NewApi")
    public static Point getScreenSize(Context context) {
        if (sScreenWidth == 0 || sScreenHeight == 0) {
            WindowManager manager = (WindowManager)
                    context.getSystemService(Context.WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            if (Build.VERSION.SDK_INT > 12) {
                sScreenWidth = display.getWidth();
                sScreenHeight = display.getHeight();

                if (sScreenWidth == 0) {
                    sScreenWidth = display.getWidth();
                }
                if (sScreenHeight == 0) {
                    sScreenHeight = display.getHeight();
                }
            } else {
                sScreenWidth = display.getWidth();
                sScreenHeight = display.getHeight();
            }

            if (sScreenWidth > sScreenHeight) {
                // The screen size is returned in landscape mode.
                // Since we want it to be in portrait mode, we need to swap
                // width and height.
                int temp = sScreenWidth;
                sScreenWidth = sScreenHeight;
                sScreenHeight = temp;
            }
        }

        return new Point(sScreenWidth, sScreenHeight);
    }

    /**
     * Gets screen width in portrait mode.
     * @return screen width in portrait mode.
     */
    public static int getScreenWidth(Context ctx) {
        return getScreenSize(ctx).x;
    }

    /**
     * Gets screen height in portrait mode.
     * @return screen height in portrait mode.
     */
    public static int getScreenHeight(Context ctx) {
        return getScreenSize(ctx).y;
    }
}

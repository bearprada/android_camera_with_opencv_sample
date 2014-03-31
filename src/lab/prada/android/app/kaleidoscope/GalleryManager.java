package lab.prada.android.app.kaleidoscope;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.UUID;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class GalleryManager {
    private static GalleryManager instance;
    public static GalleryManager getInstance() {
        if (instance == null) {
            instance = new GalleryManager();
        }
        return instance;
    }
    private final File mRoot;

    private GalleryManager() {
        mRoot = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "prada");
        if (!mRoot.exists()) {
            mRoot.mkdirs();
        }
    }

    public File saveToFile(Bitmap bm) throws FileNotFoundException {
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(mRoot, fileName);
        bm.compress(CompressFormat.JPEG, 90, new FileOutputStream(file));
        return file;
    }

    public File[] getFiles() {
        // TODO sort this by created time
        return mRoot.listFiles();
    }
}

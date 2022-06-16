package utils.task;

import androidx.annotation.NonNull;

import utils.bean.ImageConfig;

public class SharpeningImageTask {
    private boolean mIsSharpening;
    private static SharpeningImageTask mTask = null;

    public static SharpeningImageTask get() {
        synchronized (SharpeningImageTask.class) {
            if (mTask == null) {
                synchronized (SharpeningImageTask.class) {
                    mTask = new SharpeningImageTask();
                }
            }
        }
        return mTask;
    }

    public boolean isSharpeningImage() {
        return mIsSharpening;
    }

    public void sharpeningImage(@NonNull final ImageConfig imageConfig, @NonNull final AsyncImageSharpeningTask.OnImageResult onImageResult, int type) {

        AsyncImageSharpeningTask task= AsyncImageSharpeningTask.create(type);
        ImageConfig[] imageConfigs = new ImageConfig[]{imageConfig};
        task.execute(imageConfigs);
        task.setOnImageResult(onImageResult);

    }
}

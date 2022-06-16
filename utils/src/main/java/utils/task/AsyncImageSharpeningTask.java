package utils.task;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import utils.CompressPicker;
import utils.ConvolutionUtils;
import utils.LogUtils;
import utils.bean.ImageConfig;


public class AsyncImageSharpeningTask extends AsyncTask<ImageConfig, Integer, List<File>> {

    private boolean isRunTask;

    private int convolutionType = 0;

    private AsyncImageSharpeningTask(int type) {
        convolutionType = type;
    }

    public static AsyncImageSharpeningTask create(int type) {
        return new AsyncImageSharpeningTask(type);
    }

    public void setOnImagesResult(OnImagesResult onImagesResult) {
        this.onImagesResult = onImagesResult;
    }

    private OnImagesResult onImagesResult;

    public void setOnImageResult(OnImageResult onImageResult) {
        this.onImageResult = onImageResult;
    }

    private OnImageResult onImageResult;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        LogUtils.w("AsyncImageSharpeningTask--", "onPreExecute:准备开始");
        isRunTask = true;
        if (onImageResult != null) {
            onImageResult.startSharpening();
        }
        if (onImagesResult != null) {
            onImagesResult.startSharpening();
        }
    }

    @Override
    protected List<File> doInBackground(ImageConfig... imageConfigs) {
        LogUtils.w("AsyncImageSharpeningTask--", "doInBackground:正在锐化中");
        final List<File> compressFileList = new ArrayList<>();
        File file;
        File imageFile = null;
        ConvolutionUtils convolutionUtils = new ConvolutionUtils();
        for (ImageConfig imageConfig : imageConfigs) {
            file = new File(imageConfig.imagePath);
            LogUtils.w("AsyncImageSharpeningTask--", "image path: " + imageConfig.imagePath);
            if (file.exists()) {
                imageFile = convolutionUtils.convolution(imageConfig, convolutionType);
            }
            if (imageFile.exists()) {
                compressFileList.add(imageFile);
                LogUtils.w("AsyncImageSharpeningTask--", "添加图片成功");
            }
        }

        return compressFileList;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        LogUtils.w("AsyncImageSharpeningTask--", "onProgressUpdate:" + ((values == null || values.length == 0) ? 0 : values[0]));
    }

    @Override
    protected void onPostExecute(List<File> files) {
        super.onPostExecute(files);
        LogUtils.w("AsyncImageSharpeningTask--", "onPostExecute:" + files);
        if (files != null && files.size() > 0) {
            if (files.size() == 1 && onImageResult != null) {
                onImageResult.resultFileSucceed(files.get(0));
            } else {
                if (onImagesResult != null) {
                    onImagesResult.resultFilesSucceed(files);
                }
            }
        } else {
            if (onImageResult != null) {
                onImageResult.resultError();
            }
            if (onImagesResult != null) {
                onImagesResult.resultError();
            }
        }
    }

    public interface OnImagesResult extends OnBaseResult {
        void resultFilesSucceed(List<File> fileList);
    }

    public interface OnImageResult extends OnBaseResult {
        void resultFileSucceed(File file);
    }

    interface OnBaseResult {
        void startSharpening();
        void resultError();
    }
}


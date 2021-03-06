package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.bean.ImageConfig;


public class CompressPicker {
    /**
     * 压缩图片最大容量
     */
    public static final int COMPRESS_SIZE = 150;
    private static final int BYTE_MONAD = 1024;

    /**
     * 压缩Bitmap
     *
     * @param imageConfig bean
     * @return 返回Bitmap
     */
    public static Bitmap compressBitmap(ImageConfig imageConfig) {
        isCanCompress(imageConfig);
        Bitmap bitmap = null;
        if (null != imageConfig) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = imageConfig.config;
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageConfig.imagePath, options);
            options.inSampleSize = (int) ((options.outWidth * 1.0f) / (imageConfig.compressWidth * 1.0f) + (options.outHeight * 1.0f) / (imageConfig.compressHeight * 1.0f)) / 2;
            options.inJustDecodeBounds = false;
            options.inScaled = true;
            options.inMutable = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                options.inPremultiplied = true;
            }
            bitmap = BitmapFactory.decodeFile(imageConfig.imagePath, options);
            ExifInterface exif;
            try {
                exif = new ExifInterface(imageConfig.imagePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                Matrix matrix = new Matrix();
                switch (orientation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        matrix.postRotate(90);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.postRotate(180);
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        matrix.postRotate(270);
                        break;
                    default:
                        break;
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } catch (IOException e) {
                e.printStackTrace();
                return bitmap;

            }
        }
        return bitmap;
    }

    public static Bitmap loadBitmap(String path) {
        if (FileUtils.existsFile(path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(path, options);
        }
        return null;
    }

    /**
     * 判断图片要不要压缩
     *
     * @return
     */
    public static boolean isCanCompress(@NonNull ImageConfig imageConfig) {
        if (FileUtils.existsStaticImageFile(imageConfig.imagePath)) {
            File file = new File(imageConfig.imagePath);
            float fileSize = (float) file.length() / CompressPicker.BYTE_MONAD;
            int[] fileWidthHeights = FileUtils.getImageWidthHeight(file);
            Log.w("isCanCompress--", fileSize + "--" + fileWidthHeights[0] + "--" + fileWidthHeights[1]);
            return fileSize > imageConfig.compressSize || (fileWidthHeights[0] > imageConfig.compressWidth && fileWidthHeights[1] > imageConfig.compressHeight);
        }
        return false;
    }

    /**
     * Bitmap to File
     *
     * @param bitmap bitmap
     * @return file
     */
    public static File bitmapToFile(@NonNull Bitmap bitmap, @NonNull ImageConfig imageConfig) {

        FileOutputStream fos = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int quality = 100;
        boolean result = bitmap.compress(imageConfig.format, quality, bos);
        if (!result) {
            //  bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, false);
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            bitmap.compress(imageConfig.format, quality, bos);
        }
        while (bos.toByteArray().length / CompressPicker.BYTE_MONAD > imageConfig.compressSize) {
            bos.reset();
            quality -= 5;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
        }
        //  File file = new File(imageConfig.);
        File imageFile = new File(imageConfig.compressImagePath);
        LogUtils.w("bitmapToFile--", imageFile.getAbsolutePath());
        try {
            fos = new FileOutputStream(imageFile);
            fos.write(bos.toByteArray(), 0, bos.toByteArray().length);
            fos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return imageFile;
    }


}

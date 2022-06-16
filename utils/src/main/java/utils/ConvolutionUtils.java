package utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import utils.bean.ImageConfig;

public class ConvolutionUtils {
    private static float sharpeningEffect[] = new float[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};//锐化效果
    private static float noEffect[] = new float[]{0, 0, 0, 0, 1, 0, 0, 0, 0}; //原图（测试用）
    private static float highLightEffect[] = new float[]{1, 1, 1, 1, -7, 1, 1, 1, 1}; //强调边缘
    private static float[] smoothEffect = {1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f, 1 / 9f};//平滑效果
    private static float[] gaussSmoothEffect = {1 / 16f, 2 / 16f, 1 / 16f, 2 / 16f, 4 / 16f, 2 / 16f, 1 / 16f, 2 / 16f, 1 / 16f};//高斯平滑
    private static float[] VerticalEdgeEffect = {1, 1, 1, 0, 0, 0, -1, -1, -1};//竖向边缘

    public File convolution(ImageConfig imageConfig, int type) {
        Bitmap bm = BitmapFactory.decodeFile(imageConfig.imagePath);
        float effect[] = null;
        switch (type) {
            case 0:
                effect = noEffect;
                break;
            case 1:
                effect = sharpeningEffect;
                break;
            case 2:
                effect = highLightEffect;
                break;
            case 3:
                effect = smoothEffect;
                break;
            case 4:
                effect = gaussSmoothEffect;
                break;
            case 5:
                effect = VerticalEdgeEffect;
                break;
            default:
                effect = noEffect;
                break;
        }

        int[] newPixels = new int[bm.getWidth() * bm.getHeight()];
        for (int y = 0; y < bm.getHeight(); y++) {
            for (int x = 0; x < bm.getWidth(); x++) {
                int newargb[][] = new int[9][4];
                int count = 0;
                for (int offsetY = -1; offsetY <= 1; offsetY++) {
                    for (int offsetX = -1; offsetX <= 1; offsetX++) {
                        int newX = x + offsetX;
                        int newY = y + offsetY;
                        if (!(newX < 0 || newX >= bm.getWidth() || newY < 0 || newY >= bm.getHeight())) {
                            int pixel = bm.getPixel(x + offsetX, y + offsetY);
                            newargb[count][0] = (pixel >> 24) & 0xff;
                            newargb[count][1] = (pixel >> 16) & 0xff;
                            newargb[count][2] = (pixel >> 8) & 0xff;
                            newargb[count][3] = pixel & 0xff;
                        }
                        count++;
                    }
                }
                int resultArgb[] = new int[4];
                for (int i = 0; i < effect.length; i++) {
                    for (int j = 0; j < 4; j++) {
                        resultArgb[j] += (int) (effect[effect.length - i - 1] * (float) newargb[i][j]);
                    }
                }
                for (int j = 0; j < resultArgb.length; j++) {
                    resultArgb[j] = resultArgb[j] < 0 ? 0 : resultArgb[j] > 255 ? 255 : resultArgb[j];
                }
                newPixels[y * bm.getWidth() + x] |= resultArgb[0] << 24;
                newPixels[y * bm.getWidth() + x] |= resultArgb[1] << 16;
                newPixels[y * bm.getWidth() + x] |= resultArgb[2] << 8;
                newPixels[y * bm.getWidth() + x] |= resultArgb[3];
            }
        }
        LogUtils.w("AsyncImageSharpeningTask--", "生成Bitmap");
        Bitmap newBm = Bitmap.createBitmap(newPixels, bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        File imageFile = new File(imageConfig.compressImagePath);
        LogUtils.w("AsyncImageSharpeningTask--", imageFile.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            newBm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            fos.write(bos.toByteArray(), 0, bos.toByteArray().length);
            fos.flush();
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

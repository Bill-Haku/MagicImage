package utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.OpenableColumns;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.List;



public class FileUtils {
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
    private static final double ONE_KB = 1024;
    public static final String FILE_DIRECTOR_NAME = "HuXiaobai/cache";
    private static final String FILE_DIRECTOR_HEAD_NAME = "HuXiaobai";

    /**
     * 外部存储
     *
     */
    public static File outFileDirectory(Context context) {
        File rootFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            rootFile = context.getExternalFilesDir("");
        } else {
            String storageState = Environment.getExternalStorageState();
            rootFile = storageState.equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory() : context.getFilesDir();
            rootFile = new File(rootFile.getAbsolutePath(), FILE_DIRECTOR_NAME);

        }

        return rootFile;
    }

    /**
     * 内部存储
     *
     */
    public static File dataFileDirectory(Context context) {
        File rootFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            rootFile = context.getExternalFilesDir("cache");
        } else {
            rootFile = context.getCacheDir();

        }
        return rootFile;
    }


    public static File createDirectory(@NonNull File parent, @NonNull String directorName) {
        File file = new File(parent, directorName);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
        return file;
    }


    public static File resultImageFile(@NonNull File parent) {
        return new File(parent, System.currentTimeMillis() + ".jpg");
    }

    public static File getFileDirectorHead(Context context) {
        String storageState = Environment.getExternalStorageState();
        File rootFile = storageState.equals(Environment.MEDIA_MOUNTED) ? Environment.getExternalStorageDirectory() : context.getCacheDir();
        return new File(rootFile, FILE_DIRECTOR_HEAD_NAME);
    }


    public static File from(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        String[] splitName = splitFileName(fileName);
        File tempFile = File.createTempFile(splitName[0], splitName[1]);
        tempFile = rename(tempFile, fileName);
        tempFile.deleteOnExit();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(tempFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            copy(inputStream, out);
            inputStream.close();
        }

        if (out != null) {
            out.close();
        }
        return tempFile;
    }

    private static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf(File.separator);
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private static String[] splitFileName(String fileName) {
        String name = fileName;
        String extension = "";
        int i = fileName.lastIndexOf(".");
        if (i != -1) {
            name = fileName.substring(0, i);
            extension = fileName.substring(i);
        }

        return new String[]{name, extension};
    }

    private static File rename(File file, String newName) {
        File newFile = new File(file.getParent(), newName);
        if (!newFile.equals(file)) {
            if (newFile.exists() && newFile.delete()) {
                Log.d("FileUtil", "Delete old " + newName + " file");
            }
            if (file.renameTo(newFile)) {
                Log.d("FileUtil", "Rename file to " + newName);
            }
        }
        return newFile;
    }

    private static long copy(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }

    public static boolean isImageFile(File file) {
        if (file != null && file.exists() && file.isFile()) {
            String imagePath = file.getAbsolutePath();
            if (imagePath.endsWith(".jpg") || imagePath.endsWith(".jpeg") || imagePath.endsWith(".png") ||
                    imagePath.endsWith(".bmp") || imagePath.endsWith(".webp") || imagePath.endsWith(".JPG")) {
                return true;
            }
        }
        return false;
    }

    public static String imageSize(long fileSize) {
        DecimalFormat df = new DecimalFormat(".##");
        if (fileSize * 1.0d / FileUtils.ONE_KB < 1) {
            return df.format(fileSize) + "B";
        } else if (fileSize * 1.0d / FileUtils.ONE_KB >= 1 && fileSize * 1.0d / FileUtils.ONE_KB < FileUtils.ONE_KB) {
            return df.format(fileSize * 1.0d / FileUtils.ONE_KB) + "KB";
        } else {
            return df.format(fileSize * 1.0d / FileUtils.ONE_KB / FileUtils.ONE_KB) + "M";
        }
    }

    /**
     * 删除文件夹所有文件或者删除指定文件
     *
     * @param file 文件或者文件夹
     */
    public static void deleteAllFile(final @NonNull File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                if (files != null && files.length > 0) {
                    for (File childFile : files) {
                        deleteAllFile(childFile);
                    }
                }
            }
        }

    }

    public static Uri fileToUri(@NonNull Context context, @NonNull File file, @NonNull Intent intent) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            String authority = context.getPackageName() + ".provider";
            uri = FileProvider.getUriForFile(context, authority, file);
            List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (resolveInfos != null && resolveInfos.size() > 0)
                for (ResolveInfo resolveInfo : resolveInfos) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static void scanImage(@NonNull Context context, @NonNull File file) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaScanner ms = new MediaScanner(context, file);
            ms.refresh();
        } else {
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);
        }
    }
    public static boolean existsStaticImageFile(@NonNull String path) {
        if (existsFile(path)) {
            String name = new File(path).getName();
            return name.toLowerCase().endsWith(".jpg") || name.toLowerCase().endsWith(".jpeg")
                    || name.toLowerCase().endsWith(".png") || name.toLowerCase().endsWith(".bpm")
                    || name.toLowerCase().endsWith(".webp");
        }
        return false;
    }
    public static boolean existsFile(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    public static int[] getImageWidthHeight(File file) {
        int[] data = new int[2];
        if (file != null && FileUtils.existsFile(file.getAbsolutePath())) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                options.outConfig = Bitmap.Config.RGB_565;
            }
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            data[0] = options.outWidth;
            data[1] = options.outHeight;
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
        }

        return data;
    }
}

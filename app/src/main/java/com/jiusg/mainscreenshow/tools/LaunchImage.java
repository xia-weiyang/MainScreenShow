package com.jiusg.mainscreenshow.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetDataCallback;

import java.io.File;
import java.util.List;

/**
 * Created by Administrator on 2015/6/21.
 */
public class LaunchImage {

    public static String PATH = Environment.getExternalStorageDirectory()
            .getPath() + "/MSShow/Launch/launch.png";
    public static String DIR = Environment.getExternalStorageDirectory()
            .getPath() + "/MSShow/Launch/";
    public static String FILE_NAME = "launch.png";
    private Context context;
    private Bitmap bitmap = null;
    private boolean isExist = false;
    private SharedPreferences sp = null;

    private final String CLASS_NAME = "LaunchImage";
    private final String ID = "imageId";
    private final String IMAGE = "image";

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean isExist() {
        return isExist;
    }

    public LaunchImage(Context context) {

        this.context = context;
        this.sp = context.getSharedPreferences("launchimage", 0);
    }

    /**
     * 查看是否存在图片
     *
     * @return
     */
    public boolean isExistImage() {
        File file = new File(PATH);
        if (file.exists()) {
            bitmap = BitmapFactory.decodeFile(PATH);
            if (bitmap != null)
                return true;
        }
        return false;
    }

    /**
     * 从服务器查看是否需要下载图片
     *
     * @param callBack
     */
    public void isDownloadImage(final IsDownloadCallBack callBack) {

        AVQuery<AVObject> query = new AVQuery<AVObject>(CLASS_NAME);
        query.orderByDescending(ID);
        query.setLimit(1);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (null == e) {
                    if (list.size() == 1) {
                        if (sp.getInt("image", -1) < list.get(0).getInt(ID)) {

                            callBack.done(true, list.get(0));
                        } else {
                            callBack.done(false, null);
                        }

                    } else
                        callBack.done(false, null);
                } else
                    callBack.done(false, null);
            }
        });
    }

    /**
     * 下载图片
     */
    public void downloadImage() {

        isDownloadImage(this.new IsDownloadCallBack() {
            @Override
            public void done(boolean isDownload, final AVObject object) {
                if (isDownload) {
                    if (object != null) {
                        AVFile file = object.getAVFile(IMAGE);
                        String url = file.getUrl();
                        Download download = new Download(url);
                        download.dowmSdInBackground(DIR, FILE_NAME, download.new DoneCallBack() {
                            @Override
                            public void done(String path) {
                                sp.edit().putInt("image", object.getInt(ID)).apply();
                            }
                        }, download.new ProgressCallback() {
                            @Override
                            public void done(int progress) {

                            }
                        }, download.new FailedCallback() {
                            @Override
                            public void failed(Exception e) {

                            }
                        });
                    }
                }
            }
        });
    }

    public abstract class IsDownloadCallBack {
        public abstract void done(boolean isDownload, AVObject object);
    }

}

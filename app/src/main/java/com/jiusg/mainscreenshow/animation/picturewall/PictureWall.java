package com.jiusg.mainscreenshow.animation.picturewall;

import java.io.File;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;

import com.jiusg.mainscreenshow.service.MSSService;

/**
 * 此类实现PictureWall
 *
 * @author Administrator
 */
public class PictureWall {
    private Bitmap bitmap1;
    private Bitmap bitmap2;
    private int alphaB1;
    private int alphaB2;
    private String[] urlBitmap;
    private int period; // 单个bitmap运行的周期
    private int type; // 动画类型
    private int timeB1;
    private int timeB2;
    private int number; // 播放图片的个数
    private float positionB1X; // 绘制图片的位置
    private float positionB1Y;
    private float positionB2X;
    private float positionB2Y;
    private Matrix matrix;
    private int someOne = 0; // 取得urlBitmap中的第几个
    public static final String path = Environment.getExternalStorageDirectory()
            .getPath() + "/MSShow/PictureWall/Img/";
    private int BITMAP_TYPE; // 获取动画资源类型
    public static final int BITMAP_TYPE_ORDER = 200;
    public static final int BITMAP_TYPE_RANDOM = 201;
    public static final float SCALE_BASE = 1.002f;
    private float scale = SCALE_BASE;
    private int screenWidth; // 屏幕的宽
    private int screenHeight; // 屏幕的高
    private int ratio; // 渐变所用时间
    private float rationBL = 2f; // 渐变比率
    private boolean isBitmap1 = true;
    private boolean isBitmap2 = false;
    private boolean isBitmapResource1 = false;
    private boolean isBitmapResource2 = false;
    private int alpha; // 整体透明度
    private int count = 0;

    private final String TAG = "PictureWall";

    public Bitmap getBitmap1() {
        return bitmap1;
    }

    public Bitmap getBitmap2() {
        return bitmap2;
    }

    public String[] getUrlBitmap() {
        return urlBitmap;
    }

    public String getPath() {
        return path;
    }

    public int getPeriod() {
        return period;
    }

    public int getRatio() {
        return ratio;
    }

    public void setTimeB1(int timeB1) {
        this.timeB1 = timeB1;
        if (timeB1 > period) {
            this.timeB1 = 0;
            isBitmap1 = false;
            isBitmapResource1 = true;
        }
        if (timeB1 > (period - ratio)) {
            isBitmap2 = true;
        }
    }

    public void setTimeB2(int timeB2) {
        this.timeB2 = timeB2;
        if (timeB2 > period) {
            this.timeB2 = 0;
            isBitmap2 = false;
            isBitmapResource2 = true;
        }
        if (timeB2 > (period - ratio)) {
            isBitmap1 = true;
        }
    }

    public boolean isBitmap1() {
        return isBitmap1;
    }

    public void setBitmap1(boolean isBitmap1) {
        this.isBitmap1 = isBitmap1;
    }

    public boolean isBitmap2() {
        return isBitmap2;
    }

    public void setBitmap2(boolean isBitmap2) {
        this.isBitmap2 = isBitmap2;
    }

    /**
     * 设置单个bitmap的运行周期，顺便计算出整个的运算周期
     *
     * @param period
     */
    public void setPeriod(int period) {
        this.period = period;
        // 计算淡入淡出所用时间，1/4?
        ratio = period / 5;
    }

    public void setRatio(int ratio) {
        this.ratio = ratio;
    }

    public int getType() {
        return type;
    }

    public int getTimeB1() {
        return timeB1;
    }

    public int getTimeB2() {
        return timeB2;
    }

    public int getNumber() {
        return number;
    }

    public float getRationBL() {
        return rationBL;
    }

    public void setRationBL(float rationBL) {
        this.rationBL = rationBL;
    }

    public float getPositionB1X() {
        positionB1X = (screenWidth - bitmap1.getWidth()) / 2;
        return positionB1X;
    }

    public int getAlphaB2() {
        if (timeB2 > (period - ratio))
            alphaB2 = (int) (alphaB2 / rationBL);
        else
            alphaB2 = alpha;
        return alphaB2;

    }

    public void setAlphaB2(int alphaB2) {
        this.alphaB2 = alphaB2;
    }

    public int getAlphaB1() {
        if (timeB1 > (period - ratio))
            alphaB1 = (int) (alphaB1 / rationBL);
        else
            alphaB1 = alpha;
        return alphaB1;

    }

    public void setAlphaB1(int alphaB1) {
        this.alphaB1 = alphaB1;
    }

    public float getPositionB1Y() {
        positionB1Y = (screenHeight - bitmap1.getHeight()) / 2;
        return positionB1Y;
    }

    public float getPositionB2X() {
        positionB2X = (screenWidth - bitmap2.getWidth()) / 2;
        return positionB2X;
    }

    public float getPositionB2Y() {
        positionB2Y = (screenHeight - bitmap2.getHeight()) / 2;
        return positionB2Y;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public int getSomeOne() {
        return someOne;
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    /**
     * PictureWall 构造方法
     *
     * @param screenWidth  屏幕宽
     * @param screenHeight 高
     * @param period       周期
     * @param alpha        透明度
     * @param BITMAP_TYPE  选择图片资源的方式
     * @param scale        Scale设置的值 -0.005f-0.005f之间
     */
    public PictureWall(int screenWidth, int screenHeight, int period,
                       int alpha, int BITMAP_TYPE, float scale) {

        setPeriod(period);
        setAlpha(alpha);
        this.BITMAP_TYPE = BITMAP_TYPE;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
        this.scale = scale;
        // 得到所有图片的路径
        urlBitmap = getListPath(path);
        // 初始bitmap1和bitmap2的运行时间
        timeB1 = 0;
        timeB2 = 0;

        matrix = new Matrix();

    }

    /**
     * 获取path下的所有.jpg和.png图片资源
     * 如果没有。则返回String[].lenght = 0
     *
     * @param path 路径，如果没有则默认创建该路径
     * @return
     */
    public static String[] getListPath(String path) {

        String[] paths = new String[0];
        int lenght = 0;
        File file = new File(path);
        if (!file.exists()) {
            // 创建所有不存在的路径
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files != null) {
            for (File file2 : files) {
                if (file2.toString().endsWith(".jpg")
                        || file2.toString().endsWith(".png")
                        || file2.toString().endsWith(".PNG")
                        || file2.toString().endsWith(".JPG")) {
                    lenght++;
                }
            }
            paths = new String[lenght];
            int j = 0;
            for (int i = 0; i < files.length; i++) {
                if (files[i].toString().endsWith(".jpg")
                        || files[i].toString().endsWith(".png")
                        || files[i].toString().endsWith(".PNG")
                        || files[i].toString().endsWith(".JPG")) {

                    paths[j] = files[i].toString();
                    j++;
                }
            }
        }
        return paths;
    }

    /**
     * 得到下一时刻的缩放数据
     */
    public void getScale() {

        setBitmap(BITMAP_TYPE);
        matrix.reset();
        matrix.postScale(scale, scale);
        if (isBitmap1) {
            bitmap1 = Bitmap.createBitmap(bitmap1, 0, 0, bitmap1.getWidth(),
                    bitmap1.getHeight(), matrix, true);
            MSSService.imageCache.lruCache.put("scale1"+MSSService.imageCacheFlag++, bitmap1);
        }
        if (isBitmap2){
            bitmap2 = Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth(),
                    bitmap2.getHeight(), matrix, true);
            MSSService.imageCache.lruCache.put("scale2"+MSSService.imageCacheFlag++, bitmap2);
        }

    }

    /**
     * 根据周期period和每个bitmap进行的时间进行实例bitmap
     *
     * @param bitmapType 类型？随机/顺序
     */
    public void setBitmap(int bitmapType) {

        if (bitmapType == BITMAP_TYPE_ORDER) {
            if (bitmap1 == null) {
                String url = urlBitmap[getOrderSomeOne()];
                bitmap1 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap1);
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap1);
                count++;
            }
            if (bitmap2 == null) {
                String url = urlBitmap[getOrderSomeOne()];
                bitmap2 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap2);
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap2);
                count++;
            }
            if (isBitmapResource1) {
                String url = urlBitmap[getOrderSomeOne()];
                bitmap1 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap1);
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap1);
                isBitmapResource1 = false;
                count++;


            }
            if (isBitmapResource2) {
                String url = urlBitmap[getOrderSomeOne()];
                bitmap2 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap2);
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap2);
                isBitmapResource2 = false;
                count++;
            }
        } else if (bitmapType == BITMAP_TYPE_RANDOM) {
            if (bitmap1 == null) {
                String url = urlBitmap[getRandomSomeOne()];
                bitmap1 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap1);
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap1);
                count++;
            }
            if (bitmap2 == null) {
                String url = urlBitmap[getRandomSomeOne()];
                bitmap2 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap2);
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap2);
                count++;
            }
            if (isBitmapResource1) {
                String url = urlBitmap[getRandomSomeOne()];
                bitmap1 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap1);
                bitmap1 = Bitmap.createScaledBitmap(bitmap1, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap1);
                isBitmapResource1 = false;
                count++;
            }
            if (isBitmapResource2) {
                String url = urlBitmap[getRandomSomeOne()];
                bitmap2 = BitmapFactory
                        .decodeFile(url);
                MSSService.imageCache.lruCache.put(url+MSSService.imageCacheFlag++, bitmap2);
                bitmap2 = Bitmap.createScaledBitmap(bitmap2, screenWidth,
                        screenHeight, false);
                MSSService.imageCache.lruCache.put(url + "/"+MSSService.imageCacheFlag++, bitmap2);
                isBitmapResource2 = false;
                count++;
            }
        }
    }

    /**
     * 顺序的获得urlBitmap中资源
     *
     * @return
     */
    public int getOrderSomeOne() {
        if (someOne < urlBitmap.length)
            return someOne++;
        else {
            someOne = 0;
            return getOrderSomeOne();
        }
    }

    /**
     * 随机的获得urlBitmap中资源，并与上一个资源不重复
     *
     * @return
     */
    public int getRandomSomeOne() {
        if (urlBitmap.length == 1)
            return someOne;
        int now = someOne;
        int next = (int) (urlBitmap.length * Math.random());
        if (now == next)
            getRandomSomeOne();
        else
            someOne = next;
        return someOne;
    }

    /**
     * @return 返回播放到第几个图片
     */
    public int getCount() {

        return count;

    }
}

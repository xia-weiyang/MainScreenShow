package com.jiusg.mainscreenshow.ui;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.picturewall.PictureWall;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;

//import android.view.View.OnClickListener;

public class PictureWallChoose extends Activity {

    private GridView gv;
    private PWCAdapter pA;
    private String[] pictureUrl = null;
    private Bitmap[] bp = null;
    private ProgressDialog pd = null;
    private PWCHandler handler;
    private final int RESULT_LOAD_IMAGE = 1;
    private boolean delete = false;
    private SharedPreferences sp_tip;
    private String picturePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picturewallchoose);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayUseLogoEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(false);
        handler = new PWCHandler();
        pd = ProgressDialog.show(PictureWallChoose.this, null, getString(R.string.action_loading), true);
        Message msg = handler.obtainMessage();
        msg.obj = "start";
        handler.sendMessageDelayed(msg, 500);
        sp_tip = getSharedPreferences("tip", 0);
        SmartBarUtils.setBackIcon(getActionBar(),
                getResources().getDrawable(R.drawable.ic_back));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                delete = !delete;
                pA.notifyDataSetChanged();
                break;
            case R.id.action_add:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_picturewallchoose, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            this.picturePath = picturePath;
            File file = new File(picturePath);
            File file2 = new File(Welcome.getPath());
            if (copyfile(file, file2, false)) {
                pd = ProgressDialog.show(PictureWallChoose.this, null,
                        getString(R.string.action_loading), true);
                Message msg = handler.obtainMessage();
                msg.obj = "add";
                handler.sendMessageDelayed(msg, 500);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class PWCAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return pictureUrl.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater
                    .from(PictureWallChoose.this);
            convertView = inflater.inflate(R.layout.gv_pwc, parent, false);
            ImageView img = (ImageView) convertView
                    .findViewById(R.id.img_gv_pwc);
            img.setImageBitmap(bp[position]);
            if (delete) {
                ImageView img_del = (ImageView) convertView
                        .findViewById(R.id.img_gv_pwc_del);
                img_del.setVisibility(View.VISIBLE);
                img_del.setOnClickListener(new android.view.View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        pd = ProgressDialog.show(PictureWallChoose.this, null,
                                getString(R.string.action_deleting), true);
                        File file = new File(pictureUrl[position]);
                        file.delete();
                        Message msg = handler.obtainMessage();
                        msg.obj = "del";
                        handler.sendMessageDelayed(msg, 500);
                    }
                });
            }
            return convertView;
        }

    }

    @SuppressLint("HandlerLeak")
    class PWCHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (msg.obj.toString().equals("start")) {

                pictureUrl = PictureWall.getListPath(PictureWall.path);
                bp = new Bitmap[pictureUrl.length];
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 5;
                for (int i = 0; i < pictureUrl.length; i++) {
                    bp[i] = BitmapFactory.decodeFile(pictureUrl[i], options);
                }
                gv = (GridView) findViewById(R.id.gv_pwc);
                pA = new PWCAdapter();
                gv.setAdapter(pA);
                pd.dismiss();
                if (!sp_tip.getBoolean("PictureWallChoose", false)) {
                    new AlertDialog.Builder(PictureWallChoose.this)
                            .setTitle(R.string.tip)
                            .setMessage(
                                    getString(R.string.tip_msg_pictureWallChoose))
                            .setPositiveButton(R.string.action_ok, null)
                            .setNegativeButton(R.string.action_notip, new OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    sp_tip.edit()
                                            .putBoolean("PictureWallChoose",
                                                    true).commit();
                                }
                            }).show();
                }

            } else if (msg.obj.toString().equals("add")) {

                SharedPreferences sp_userinfo;
                sp_userinfo = getSharedPreferences("userinfo", 0);
                int screenwidth = sp_userinfo.getInt("screenwidth", 0);
                Bitmap bitmap = decodeSampledBitmapFromFile(Welcome.getPath() + getFileName(picturePath), (int) (screenwidth * 1.2));
                bitmap = degreeBitmap(Welcome.getPath() + getFileName(picturePath), bitmap);
                try {
                    saveJpgFile(bitmap, getFileName(picturePath), Welcome.getPath());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                pictureUrl = PictureWall.getListPath(PictureWall.path);
                bp = new Bitmap[pictureUrl.length];
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 5;
                for (int i = 0; i < pictureUrl.length; i++) {

                    bp[i] = BitmapFactory.decodeFile(pictureUrl[i], options);

                }

                pA.notifyDataSetChanged();

                pd.dismiss();
            } else if (msg.obj.toString().equals("del")) {
                pictureUrl = PictureWall.getListPath(PictureWall.path);
                bp = new Bitmap[pictureUrl.length];
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 5;
                for (int i = 0; i < pictureUrl.length; i++) {

                    bp[i] = BitmapFactory.decodeFile(pictureUrl[i], options);

                }

                pA.notifyDataSetChanged();

                pd.dismiss();
            }
        }

    }

    /**
     * @param fromFile 被复制的文件
     * @param toFile   复制的目录文件
     * @param rewrite  当文件存在时，是否重新创建文件
     *                 <p/>
     *                 <p/>
     *                 文件的复制操作方法
     */
    public static boolean copyfile(File fromFile, File toFile, Boolean rewrite) {

        if (!fromFile.exists()) {
            return false;
        }

        if (!fromFile.isFile()) {
            return false;
        }
        if (!fromFile.canRead()) {
            return false;
        }
        if (!toFile.getParentFile().exists()) {
            toFile.getParentFile().mkdirs();
        }
        if (toFile.exists() && rewrite) {
            toFile.delete();
        }
        // 得到该文件的名字
        String name = fromFile.toString();
        name = name.substring(name.lastIndexOf("/"));
        toFile = new File(toFile.toString() + name);

        try {
            FileInputStream fosfrom = new FileInputStream(fromFile);
            FileOutputStream fosto = new FileOutputStream(toFile);

            byte[] bt = new byte[1024];
            int c;
            while ((c = fosfrom.read(bt)) > 0) {
                fosto.write(bt, 0, c);
            }
            fosfrom.close();
            fosto.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 纠正图片的方向
     *
     * @param path
     * @param bitmap
     * @return
     */
    public static Bitmap degreeBitmap(String path, Bitmap bitmap) {
        // 根据图片的filepath获取到一个ExifInterface的对象

        ExifInterface exif = null;

        try {

            exif = new ExifInterface(path);

        } catch (IOException e) {

            e.printStackTrace();

            exif = null;

        }

        int degree = 0;

        if (exif != null) {

            // 读取图片中相机方向信息

            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,

                    ExifInterface.ORIENTATION_UNDEFINED);

            // 计算旋转角度

            switch (ori) {

                case ExifInterface.ORIENTATION_ROTATE_90:

                    degree = 90;

                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:

                    degree = 180;

                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:

                    degree = 270;

                    break;

                default:

                    degree = 0;

                    break;

            }
            if (degree != 0) {

                // 旋转图片

                Matrix m = new Matrix();

                m.postRotate(degree);

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),

                        bitmap.getHeight(), m, true);

            }
        }
        return bitmap;
    }

    public static String getFileName(String path) {

        String name = path.toString();
        name = name.substring(name.lastIndexOf("/"));
        return name;
    }


    /**
     * 保存图片到指定路径
     *
     * @param bm
     * @param fileName
     * @param path
     * @throws IOException
     */
    public static void saveJpgFile(Bitmap bm, String fileName, String path)
            throws IOException {

        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            File dirFile = new File(path);
            if (!dirFile.exists()) {
                dirFile.mkdirs();
            }
            File myCaptureFile = new File(path, fileName);
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(myCaptureFile));
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bos.flush();
            bos.close();
        } else {
            // SD卡出错

        }

    }

    /**
     * 计算实际采样率
     *
     * @param options
     * @param reqWidth
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth) {
        // Raw height and width of image
//		final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (width > reqWidth) {

            inSampleSize = Math.round((float) width / (float) reqWidth);

        }
        return inSampleSize;
    }

    /**
     * 优得到的采样率对图片进行解析
     *
     * @param filename
     * @param reqWidth
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename,
                                                     int reqWidth) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }


}

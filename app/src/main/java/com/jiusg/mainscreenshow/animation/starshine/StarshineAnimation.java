package com.jiusg.mainscreenshow.animation.starshine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.animation.anim.Animation;
import com.jiusg.mainscreenshow.base.C;

/**
 * Created by Administrator on 2015/10/6.
 */
public class StarshineAnimation extends Animation{

    private int allCount = 200;
    private int starCount;
    private int starDynamicCount;
    private int starMeteorCount = 6; // 流星个数
    private Starshine[] starS;
    private StarshineDynamic[] starSD;
    private StarshineMeteor[] starSM;
    private Matrix matrix;
    private boolean starMeteorSwitch = false; // 流星开关
    private StarshineSetting ss = null;
    private Context context = null;

    public StarshineAnimation(Context context, int event) {
        super(context, event);
        this.context = context;
        init();
    }


    private void init(){
        ss = loadSetting();
        speed = 35;
        matrix = new Matrix();
        allCount = ss.getAllCount();
        starMeteorCount = ss.getStarMeteorCount();
        starMeteorSwitch = ss.isStarMeteorSwitch();
        SetCount(ss.getStyle());
        InitalStar(ss.getStyle());
    }

    private StarshineSetting loadSetting(){
        ss = new StarshineSetting();
        ss.setAllCount(GetAllCount(settingInfo.getString("allcount", context.getString(R.string.starShine_num_general))));
        ss.setStarMeteorCount(GetStarMeteorCount(settingInfo.getString("starmeteorcount", context.getString(R.string.starShine_num_general))));
        ss.setStarMeteorSwitch(settingInfo.getBoolean("starmeteorswitch", true));
        ss.setStyle(getStyle(settingInfo.getString("style", context.getString(R.string.starShine_style_classical))));
        return ss;
    }

    /**
     * 分配静态的星光与动态的星光的个数 9:1的比率
     */
    private void SetCount(int style) {

        starCount = allCount / 10 * 9;
        starDynamicCount = allCount / 10;
        if(style == StarshineSetting.STARS){
            starCount = (int)(starCount*2);
        }
    }

    /**
     * 初始化星光
     */
    private void InitalStar(int style) {

        starS = new Starshine[starCount];
        for (int i = 0; i < starCount; i++) {

            Starshine ss = new Starshine();
            ss.setStyle(style);
            ss.SetBitmap(context, 1);
            ss.SetSize(0.15f);
            ss.SetPosition(0, screenwidth, screenheight);
            starS[i] = ss;
        }
        starSD = new StarshineDynamic[starDynamicCount];
        for (int i = 0; i < starSD.length; i++) {

            StarshineDynamic ssd = new StarshineDynamic();
            ssd.setStyle(style);
            ssd.SetBitmap(context, 2);
            ssd.SetSize(0.4f);
            ssd.SetPosition(0, screenwidth, screenheight);
            starSD[i] = ssd;
        }
        if (starMeteorSwitch) {
            starSM = new StarshineMeteor[starMeteorCount];
            for (int i = 0; i < starSM.length; i++) {

                StarshineMeteor ssm = new StarshineMeteor();
                ssm.setStyle(style);
                ssm.SetBitmap(context, 3);
                ssm.SetSize(0.4f);
                ssm.SetPosition(100, screenwidth, screenheight);
                ssm.SetScreenWidthHeight(screenwidth, screenheight);
                ssm.Speed();
                starSM[i] = ssm;
            }
        }
    }

    private void onDraw(Canvas canvas) {


        for (int i = 0; i < starS.length; i++) {

            paint.setAlpha(starS[i].getAlpha());
            canvas.drawBitmap(starS[i].getBt(), starS[i].getPositionX(),
                    starS[i].getPositionY(),paint);
        }
        for (int i = 0; i < starSD.length; i++) {

//			starSD[i].CountNextStarScale();
            matrix.reset();
            matrix.postScale(starSD[i].getScale(), starSD[i].getScale());
            Bitmap bt = Bitmap.createBitmap(starSD[i].getBt(), 0, 0,
                    starSD[i].getWidth(), starSD[i].getHeight(), matrix, true);
            paint.setAlpha(starSD[i].getAlpha());
            // canvas.translate((starSD[i].getWidth()-bt.getWidth())/2,
            // (starSD[i].getHeight()-bt.getHeight())/2);
            canvas.drawBitmap(bt, starSD[i].getNPositionX(),
                    starSD[i].getNPositionY(), paint);
            // canvas.translate(-(starSD[i].getWidth()-bt.getWidth())/2,-(
            // starSD[i].getHeight()-bt.getHeight())/2);
        }
        if (starMeteorSwitch) {

            for (int i = 0; i < starSM.length; i++) {

//				starSM[i].GetNextPosition();
                paint.setAlpha(starSM[i].getAlpha());
                canvas.drawBitmap(starSM[i].getBt(), starSM[i].getPositionX(),
                        starSM[i].getPositionY(), paint);
            }
        }

    }

    @Override
    public int getBackground() {
        return R.drawable.preview_starshine_background;
    }

    @Override
    public int getAnimation() {
        return C.ANIMATION_STARSHINE;
    }

    @Override
    public void count() {
        for (int i = 0; i < starSD.length;i++)
            starSD[i].CountNextStarScale();
        for (int j = 0; j <starSM.length;j++)
            starSM[j].GetNextPosition();
    }

    @Override
    public void draw(Canvas canvas) {
        if (event == C.EVENT_DESKTOP)
            canvas.drawBitmap(background, 0, 0, backgroundPaint);
        else
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
       onDraw(canvas);
    }

    public int GetAllCount(String flag) {

        if (flag.equals(context.getString(R.string.starShine_num_less))) {

            return 100;
        } else if (flag.equals(context.getString(R.string.starShine_num_general))) {

            return 200;
        } else if (flag.equals(context.getString(R.string.starShine_num_much))) {

            return 300;
        } else {

            return 200;
        }
    }

    public int GetStarMeteorCount(String flag) {

        if (flag.equals("少")) {

            return 3;
        } else if (flag.equals("一般")) {

            return 7;
        } else if (flag.equals("多")) {

            return 15;
        } else {

            return 3;
        }
    }

    private int getStyle(String style){
        if(style.equals(context.getString(R.string.starShine_style_classical))){
            return StarshineSetting.CLASSICAL;
        }else if(style.equals(context.getString(R.string.starShine_style_stars))){
            return  StarshineSetting.STARS;
        }else{
            return StarshineSetting.CLASSICAL;
        }
    }
}

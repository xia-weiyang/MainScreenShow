package com.jiusg.mainscreenshow.ui;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @deprecated
 */
public class MSSPreview extends Activity {

    private GridView gv;
    private MSSPreviewAdapter mssPA;
    private final int gvLenght = 6;
    private final String TAG = "MSSPreview";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_msspreview);

        gv = (GridView) findViewById(R.id.gv_mss);

        mssPA = new MSSPreviewAdapter();
        gv.setAdapter(mssPA);

        gv.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {


            }
        });

    }

    @Override
    protected void onResume() {
        if (C.ISMEIZU)
            getParent().getActionBar().setTitle(R.string.action_MSSPreview);
        super.onResume();
    }

    class MSSPreviewAdapter extends BaseAdapter {

        @Override
        public int getCount() {

            return gvLenght;
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

//            LayoutInflater inflater = LayoutInflater.from(MSSPreview.this);
//            if (position == 0 || position == 1) {
//
//                convertView = inflater.inflate(R.layout.list_alpha, parent,
//                        false);
//            } else {
//                convertView = inflater.inflate(R.layout.gv_mss, parent, false);
//                ImageView img = (ImageView) convertView
//                        .findViewById(R.id.img_gv);
//                TextView tv = (TextView) convertView
//                        .findViewById(R.id.tv_gv_title);
//                Button score = (Button) convertView
//                        .findViewById(R.id.tv_gv_score);
//                score.setText("已激活");
//                switch (position) {
//                    case 2:
//                        tv.setText(PropertiesUtils.getAnimationInfo(MSSPreview.this)[C.ANIMATION_BUBBLE]);
//                        img.setImageResource(R.drawable.preview_bubble);
//                        break;
//                    case 3:
//                        tv.setText(PropertiesUtils.getAnimationInfo(MSSPreview.this)[C.ANIMATION_STARSHINE]);
//                        img.setImageResource(R.drawable.preview_starshine);
//                        break;
//                    case 4:
//                        tv.setText(PropertiesUtils.getAnimationInfo(MSSPreview.this)[C.ANIMATION_PICTUREWALL]);
//                        img.setImageResource(R.drawable.preview_picturewall);
//                        break;
//                    case 5:
//                        tv.setText(PropertiesUtils.getAnimationInfo(MSSPreview.this)[C.ANIMATION_RAIN]);
//                        img.setImageResource(R.drawable.preview_rain);
//                        break;
//                    default:
//                        break;
//                }
//                img.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent it = new Intent();
//                        Bundle b = new Bundle();
//                        switch (position) {
//                            case 2:
//                                b.putInt("animation", C.ANIMATION_BUBBLE);
//                                break;
//                            case 3:
//                                b.putInt("animation", C.ANIMATION_STARSHINE);
//                                break;
//                            case 4:
//                                b.putInt("animation", C.ANIMATION_PICTUREWALL);
//                                break;
//                            case 5:
//                                b.putInt("animation", C.ANIMATION_RAIN);
//                                break;
//                            default:
//                                break;
//                        }
//                        Log.i(TAG, "" + position);
//                        it.putExtras(b);
//                        it.setClass(MSSPreview.this, PreviewAnimation.class);
//                        MSSPreview.this.startActivity(it);
//                    }
//                });
//                score.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                    }
//                });
//            }
            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {

            if (position == 0 || position == 1)
                return false;
            return true;
        }

    }
}

package com.jiusg.mainscreenshow.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.PropertiesUtils;

/**
 * Created by Administrator on 2015/9/27.
 */
public class MssPreviewFragment extends Fragment {

    private RelativeLayout main = null;
    private GridView gv;
    private MSSPreviewAdapter mssPA = null;
    private final int gvLenght = 4;
    private final String TAG = "MSSPreview";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        main = (RelativeLayout) inflater.inflate(R.layout.fragment_msspreview, container, false);
        gv = (GridView) main.findViewById(R.id.gv_mss);
        mssPA = new MSSPreviewAdapter();
        gv.setAdapter(mssPA);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view,
                                    int position, long arg3) {


            }
        });
        return main;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
            LayoutInflater inflater = LayoutInflater.from(getActivity());
                convertView = inflater.inflate(R.layout.gv_mss, parent, false);
                ImageView img = (ImageView) convertView
                        .findViewById(R.id.img_gv);
                Button button = (Button) convertView.findViewById(R.id.bt_gv);
                switch (position) {
                    case 0:
                        button.setText(PropertiesUtils.getAnimationInfo(getActivity())[C.ANIMATION_BUBBLE]);
                        img.setImageResource(R.drawable.preview_bubble);
                        break;
                    case 1:
                        button.setText(PropertiesUtils.getAnimationInfo(getActivity())[C.ANIMATION_STARSHINE]);
                        img.setImageResource(R.drawable.preview_starshine);
                        break;
                    case 2:
                        button.setText(PropertiesUtils.getAnimationInfo(getActivity())[C.ANIMATION_PICTUREWALL]);
                        img.setImageResource(R.drawable.preview_picturewall);
                        break;
                    case 3:
                        button.setText(PropertiesUtils.getAnimationInfo(getActivity())[C.ANIMATION_RAIN]);
                        img.setImageResource(R.drawable.preview_rain);
                        break;
                    default:
                        break;
                }
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent();
                        Bundle b = new Bundle();
                        switch (position) {
                            case 0:
                                b.putInt("animation", C.ANIMATION_BUBBLE);
                                break;
                            case 1:
                                b.putInt("animation", C.ANIMATION_STARSHINE);
                                break;
                            case 2:
                                b.putInt("animation", C.ANIMATION_PICTUREWALL);
                                break;
                            case 3:
                                b.putInt("animation", C.ANIMATION_RAIN);
                                break;
                            default:
                                break;
                        }
                        Log.i(TAG, "" + position);
                        it.putExtras(b);
                        it.setClass(getActivity(), PreviewAnimation.class);
                        getActivity().startActivity(it);
                    }
                });

            return convertView;
        }

        @Override
        public boolean isEnabled(int position) {


            return true;
        }

    }
}

package com.jiusg.mainscreenshow.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;
import com.jiusg.mainscreenshow.tools.SmartBarUtils;

public class Help extends Activity {

	private ArrayList<String[]> arrayList = null;
	private HelpAdapter adapter;
	private ListView listView;
	private ImageView qq;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_help);
		


		listView = (ListView) findViewById(R.id.list_help);
		qq = (ImageView) findViewById(R.id.qq);

		arrayList = new ArrayList<String[]>();
		initArrayList();
		adapter = new HelpAdapter();

		listView.setAdapter(adapter);

		qq.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String key = "cDw5dm-O1o0SbTXNuXk1Bsvj5GTc3xxT";

				if(!joinQQGroup(key)){

					Toast.makeText(Help.this,"加群失败!原因:QQ版本过低或未安装",Toast.LENGTH_LONG).show();
				}

			}
		});

	}
	
	private void initArrayList() {

		arrayList.add(new String[] {
				getResources().getString(R.string.help_question1),
				getResources().getString(R.string.help_answer1) });
		arrayList.add(new String[] {
				getResources().getString(R.string.help_question2),
				getResources().getString(R.string.help_answer2) });
		arrayList.add(new String[] {
				getResources().getString(R.string.help_question3),
				getResources().getString(R.string.help_answer3) });
		arrayList.add(new String[] {
				getResources().getString(R.string.help_question4),
				getResources().getString(R.string.help_answer4) });
	}

	class HelpAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return arrayList.size();
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final ViewHolder holder;

			if (convertView == null) {
				LayoutInflater inflater = LayoutInflater.from(Help.this);
				convertView = inflater.inflate(R.layout.list_help, parent,
						false);
				holder = new ViewHolder();
				holder.question = (TextView) convertView
						.findViewById(R.id.tv_help_question);
				holder.answer = (TextView) convertView
						.findViewById(R.id.tv_help_answer);
				convertView.setTag(holder);
			} else {

				holder = (ViewHolder) convertView.getTag();
			}
			holder.question.setText(arrayList.get(position)[0]);
			holder.answer.setText(arrayList.get(position)[1]);
			holder.question.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (holder.answer.getVisibility() == View.GONE) {
						holder.answer.setVisibility(View.VISIBLE);
					} else {
						holder.answer.setVisibility(View.GONE);
					}
				}
			});
			return convertView;
		}

	}

	class ViewHolder {

		TextView question;
		TextView answer;
	}

	/****************
	 *
	 * 发起添加群流程。群号：桌面秀交流群(426226100) 的 key 为： cDw5dm-O1o0SbTXNuXk1Bsvj5GTc3xxT
	 * 调用 joinQQGroup(cDw5dm-O1o0SbTXNuXk1Bsvj5GTc3xxT) 即可发起手Q客户端申请加群 桌面秀交流群(426226100)
	 *
	 * @param key 由官网生成的key
	 * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
	 ******************/
	public boolean joinQQGroup(String key) {
		Intent intent = new Intent();
		intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
		// 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
		try {
			startActivity(intent);
			return true;
		} catch (Exception e) {
			// 未安装手Q或安装的版本不支持
			return false;
		}
	}

}

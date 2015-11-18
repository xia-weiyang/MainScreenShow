package com.jiusg.mainscreenshow.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.SaveCallback;
import com.jiusg.mainscreenshow.R;
import com.jiusg.mainscreenshow.base.C;

public class FeedBack extends Activity{

	private EditText content;
	private EditText qq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(false);
		setContentView(R.layout.activity_feedback);
		
		content = (EditText) findViewById(R.id.edit_fb);
		qq = (EditText) findViewById(R.id.edit_fb_qq);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_feedback, menu);
		
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_feedback_submit:
			if(!content.getText().toString().equals("") && !qq.getText().toString().equals("")){
				final ProgressDialog dialog = ProgressDialog.show(FeedBack.this, null, getString(R.string.action_submiting),true);
				final String CLASS = "FeedBack";
				final String CONTENT = "content";
				final String QQ = "QQ";
				
				AVObject object = new AVObject(CLASS);
				object.put(CONTENT, content.getText().toString());
				object.put(QQ, qq.getText().toString());
				
				object.saveInBackground(new SaveCallback() {
					
					@Override
					public void done(AVException e) {
						
						if(null == e){
							
							dialog.dismiss();
							
							new AlertDialog.Builder(FeedBack.this)
							.setTitle(R.string.tip)
							.setMessage(getString(R.string.tip_msg_feedback))
							.setPositiveButton(R.string.action_ok, new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									finish();
								}
							}).show();
						}else{
							
							Toast.makeText(getApplication(), getString(R.string.tip_msg_feedbackFailed), Toast.LENGTH_SHORT).show();
						}
					}
				});
				
			}else{
				Toast.makeText(getApplication(), getString(R.string.tip_msg_noNull), Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	
}

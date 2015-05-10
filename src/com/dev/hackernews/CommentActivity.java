package com.dev.hackernews;

import java.util.ArrayList;

import org.json.JSONObject;

import com.dev.adapter.CommentAdapter;
import com.example.hackernews.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.Parcelable;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class CommentActivity extends Activity {
	private ListView listView;
	public ArrayList<JSONObject> jsonobjectcommentlist, jsonobjectReplylist;
	private ArrayList<Parcelable> parcelableCommentArraylist;
	private ProgressDialog commentProgress = null;
	private CommentAdapter commentAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.comment_activity);
		if (commentProgress == null)
			commentProgress = new ProgressDialog(this);
		commentProgress.setMessage("Loading Comments... ");
		commentProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		commentProgress.setIndeterminate(true);
		commentProgress.setCancelable(false);
		commentProgress.show();
		parcelableCommentArraylist = new ArrayList<Parcelable>();
		parcelableCommentArraylist.add(messenger);
		listView = (ListView) findViewById(R.id.list);

		String str = (String) getIntent().getExtras().get("kids");

		Intent serviceCommentIntent = new Intent(getApplicationContext(),
				CommentService.class);
		serviceCommentIntent.putParcelableArrayListExtra("comment",
				parcelableCommentArraylist);
		serviceCommentIntent.putExtra("kids", str);
		startService(serviceCommentIntent);
	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 20) {
				jsonobjectcommentlist = (ArrayList<JSONObject>) msg.obj;

			}
			if (msg.what == 30) {
				jsonobjectReplylist = (ArrayList<JSONObject>) msg.obj;
				if (jsonobjectcommentlist.size() > 0) {
					commentAdapter = new CommentAdapter(CommentActivity.this,
							jsonobjectcommentlist, jsonobjectReplylist);
					commentAdapter.setJsonobjectlist(jsonobjectcommentlist);
					listView.setAdapter(commentAdapter);
					commentAdapter.notifyDataSetChanged();
				}
				commentProgress.dismiss();
				commentProgress = null;
			}

		};

	};
	Messenger messenger = new Messenger(handler);

	@Override
	public void onPause() {

		super.onPause();
		if (commentProgress != null)
			commentProgress.dismiss();
	}
}

package com.dev.hackernews;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Messenger;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.dev.adapter.TopStoriesAdapter;
import com.example.hackernews.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class TopStoriesActivity extends ListActivity {

	private PullToRefreshListView mPullRefreshListView;
	private ProgressDialog progress = null;

	public TopStoriesAdapter topStoriesAdapter = null;
	private ArrayList<Parcelable> parcelableArraylist;
	public ArrayList<JSONObject> jsonobjectlist;
	private ListView actualListView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_main);
		parcelableArraylist = new ArrayList<Parcelable>();
		parcelableArraylist.add(messenger);
		if (progress == null)
			progress = new ProgressDialog(this);
		progress.setMessage("Loading News... ");
		progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progress.setIndeterminate(true);
		progress.setCancelable(false);
		progress.show();
		Intent serviceIntent = new Intent(getApplicationContext(),
				TopStroriesService.class);
		serviceIntent.putParcelableArrayListExtra("messenger",
				parcelableArraylist);
		startService(serviceIntent);

		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);

		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {
					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						String label = DateUtils.formatDateTime(
								getApplicationContext(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);

						Intent serviceIntent = new Intent(
								getApplicationContext(),
								TopStroriesService.class);
						serviceIntent.putParcelableArrayListExtra("messenger",
								parcelableArraylist);
						startService(serviceIntent);
					}
				});

		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
					}
				});

		actualListView = mPullRefreshListView.getRefreshableView();
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getApplicationContext(),
						CommentActivity.class);
				try {
					if (jsonobjectlist.get(position - 1).getString("kids") != null) {
						intent.putExtra("kids", jsonobjectlist
								.get(position - 1).getString("kids"));
						startActivity(intent);
					}
				} catch (JSONException e) {
				}

			}
		});

	}

	Handler handler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			if (msg.what == 15) {
				jsonobjectlist = (ArrayList<JSONObject>) msg.obj;

				System.out.println("SIZEEEEEEEEEEEEEe" + jsonobjectlist.size());
				if (topStoriesAdapter == null) {
					topStoriesAdapter = new TopStoriesAdapter(
							TopStoriesActivity.this, jsonobjectlist);
					actualListView.setAdapter(topStoriesAdapter);
					topStoriesAdapter.setJsonobjectlist(jsonobjectlist);
					topStoriesAdapter.notifyDataSetChanged();
					progress.dismiss();
					progress = null;
				} else {
					topStoriesAdapter.setJsonobjectlist(jsonobjectlist);
					topStoriesAdapter.notifyDataSetChanged();
					mPullRefreshListView.onRefreshComplete();
				}

			}
		};

	};
	Messenger messenger = new Messenger(handler);

	@Override
	public void onPause() {

		super.onPause();
		if (progress != null)
			progress.dismiss();
	}

}

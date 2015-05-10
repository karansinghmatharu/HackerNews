package com.dev.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.dev.hackernews.Utils;
import com.example.hackernews.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TopStoriesAdapter extends BaseAdapter {

	ArrayList<JSONObject> jsonobjectlist;

	public void setJsonobjectlist(ArrayList<JSONObject> jsonobjectlist) {
		this.jsonobjectlist = jsonobjectlist;
	}

	Context context;

	public TopStoriesAdapter(Context context,
			ArrayList<JSONObject> jsonobjectlist) {
		this.context = context;
		this.jsonobjectlist = jsonobjectlist;
	}

	@Override
	public int getCount() {
		return jsonobjectlist.size();
	}

	@Override
	public Object getItem(int arg0) {
		return jsonobjectlist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.row_story, parent, false);
		TextView storyTitle = (TextView) convertView
				.findViewById(R.id.textView);
		TextView authorTitle = (TextView) convertView
				.findViewById(R.id.authorTextView);
		TextView score = (TextView) convertView
				.findViewById(R.id.scoreTextView);
		TextView time = (TextView) convertView.findViewById(R.id.timeTextView);
		TextView count = (TextView) convertView
				.findViewById(R.id.countTextView);

		ImageView browseButton = (ImageView) convertView
				.findViewById(R.id.browserButton);

		try {
			storyTitle.setText(jsonobjectlist.get(position).getString("title"));
			authorTitle.setText(jsonobjectlist.get(position).getString("by"));
			score.setText(jsonobjectlist.get(position).getString("score"));
			time.setText(Utils.findTimeDuration(Long.valueOf(jsonobjectlist
					.get(position).getString("time"))));

			boolean haskids = jsonobjectlist.get(position).has("kids");

			if (jsonobjectlist.get(position).has("kids")) {
				String kidsString = jsonobjectlist.get(position).getString(
						"kids");

				kidsString = kidsString.substring(1, kidsString.length() - 1);
				final String arr[] = kidsString.split(",");

				count.setText(arr.length + "");

			}

			final Uri uri = Uri.parse(jsonobjectlist.get(position).getString(
					"url"));

			browseButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(intent);
				}
			});

		} catch (JSONException e) {
			// e.printStackTrace();
		}
		return convertView;
	}

}

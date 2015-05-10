package com.dev.adapter;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.dev.hackernews.Utils;
import com.example.hackernews.R;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter {
	Context context;
	ArrayList<JSONObject> jsonobjectlist, jsonobjectReplylist;

	public void setJsonobjectlist(ArrayList<JSONObject> jsonobjectlist) {
		this.jsonobjectlist = jsonobjectlist;
	}

	public CommentAdapter(Context context,
			ArrayList<JSONObject> jsonobjectlist,
			ArrayList<JSONObject> jsonobjectReplylist) {
		this.context = context;
		this.jsonobjectlist = jsonobjectlist;
		this.jsonobjectReplylist = jsonobjectReplylist;
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
		convertView = layoutInflater.inflate(R.layout.row_comment, parent,
				false);
		TextView commentText = (TextView) convertView
				.findViewById(R.id.commentTextView);
		TextView userposted = (TextView) convertView
				.findViewById(R.id.userPosted);
		TextView timeofPost = (TextView) convertView
				.findViewById(R.id.timepost);
		LinearLayout replyLayout = (LinearLayout) convertView
				.findViewById(R.id.replyLayout);
		TextView reply = (TextView) convertView
				.findViewById(R.id.replyTextView);
		TextView userReplied = (TextView) convertView
				.findViewById(R.id.userReplyPosted);
		TextView timeofReplyPost = (TextView) convertView
				.findViewById(R.id.timeReplypost);
		try {
			timeofPost.setText(Utils.findTimeDuration(Long
					.valueOf(jsonobjectlist.get(position).getString("time"))));
			commentText.setText(Html.fromHtml(jsonobjectlist.get(position)
					.getString("text")));
			userposted.setText(jsonobjectlist.get(position).getString("by"));

			if (jsonobjectReplylist.get(position) != null) {
				replyLayout.setVisibility(View.VISIBLE);
				reply.setText(Html.fromHtml(jsonobjectReplylist.get(position)
						.getString("text")));
				userReplied.setText(jsonobjectReplylist.get(position)
						.getString("by"));
				timeofReplyPost.setText(Utils.findTimeDuration(Long
						.valueOf(jsonobjectReplylist.get(position).getString(
								"time"))));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}

}

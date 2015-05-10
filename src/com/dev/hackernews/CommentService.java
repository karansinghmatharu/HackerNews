package com.dev.hackernews;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import com.dev.urls.HackerNewsURLConstants;

public class CommentService extends Service {
	ArrayList<JSONObject> jsonobjectCommentlist = new ArrayList<JSONObject>();
	ArrayList<JSONObject> jsonobjectReplylist = new ArrayList<JSONObject>();
	Messenger messengerService;
	public String commentResponse, replyResponse;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String kidsString = intent.getStringExtra("kids");
			kidsString = kidsString.substring(1, kidsString.length() - 1);
			final String arr[] = kidsString.split(",");

			ArrayList<Parcelable> parcelableArraylist = intent
					.getParcelableArrayListExtra("comment");
			if (parcelableArraylist.size() > 0)
				messengerService = (Messenger) parcelableArraylist.get(0);
			else {
				Toast.makeText(getApplicationContext(), "No comments posted",
						Toast.LENGTH_SHORT).show();
			}

			// Creating new thread for my service
			// Always write your long running tasks in a separate thread, to
			// avoid
			// ANR
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						HttpClient httpclient = HttpNetworkClient.getInstance()
								.getHttpClient();
						int temp=0;
						if(arr.length<10)
							temp=arr.length;
						else
							temp=10;
						for (int i = 0; i < temp; i++) {
							HttpGet httpGetItem = new HttpGet(
									HackerNewsURLConstants.STORY_ITEM + arr[i]
											+ ".json");
							if (httpclient != null) {
								HttpResponse response = httpclient
										.execute(httpGetItem);
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(response
												.getEntity().getContent(),
												"UTF-8"));
								commentResponse = reader.readLine();
								JSONObject object = new JSONObject(
										commentResponse);
								if (commentResponse.contains("deleted") == true)
									continue;
								jsonobjectCommentlist.add(object);

								if (object.has("kids") == true) {
									String str = jsonobjectCommentlist.get(i)
											.getString("kids");
									str = str.substring(1, str.length() - 1);
									final String replyArr[] = str.split(",");
									HttpGet httpGetReply = new HttpGet(
											HackerNewsURLConstants.STORY_ITEM
													+ replyArr[0] + ".json");
									if (httpclient != null) {
										HttpResponse replyresponse = httpclient
												.execute(httpGetReply);
										BufferedReader replyreader = new BufferedReader(
												new InputStreamReader(
														replyresponse
																.getEntity()
																.getContent(),
														"UTF-8"));
										replyResponse = replyreader.readLine();
										JSONObject replyobject = new JSONObject(
												replyResponse);
										jsonobjectReplylist.add(replyobject);
									}

								} else {
									jsonobjectReplylist.add(null);
								}
							}

						}

						Message message = Message.obtain(null, 20,
								jsonobjectCommentlist);
						Message replymessage = Message.obtain(null, 30,
								jsonobjectReplylist);
						messengerService.send(message);
						messengerService.send(replymessage);
						System.out.println(jsonobjectCommentlist.toString());
						// Stop service once it finishes its task
						stopSelf();
					} catch (Exception e) {
						Log.d("Service", e.getMessage());

					}
				}
			}).start();

		}
		return Service.START_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}

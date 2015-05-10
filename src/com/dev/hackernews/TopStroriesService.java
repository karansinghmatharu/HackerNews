package com.dev.hackernews;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcelable;
import android.util.Log;

import com.dev.urls.HackerNewsURLConstants;

public class TopStroriesService extends Service {

	private static final String TAG = "HelloService";
	String serverResponse, itemResponse;
	ArrayList<String> list = new ArrayList<String>();
	ArrayList<JSONObject> jsonobjectlist = new ArrayList<JSONObject>();

	Messenger messengerService;
	private boolean isRunning = false;

	@Override
	public void onCreate() {
		Log.i(TAG, "Service onCreate");

		isRunning = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		Log.i(TAG, "Service onStartCommand");
		if (intent != null) {
			ArrayList<Parcelable> parcelableArraylist = intent
					.getParcelableArrayListExtra("messenger");
			if (parcelableArraylist.size() > 0)
				messengerService = (Messenger) parcelableArraylist.get(0);

			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						HttpClient httpclient = HttpNetworkClient.getInstance()
								.getHttpClient();
						HttpGet httpGetStoryId = new HttpGet(
								HackerNewsURLConstants.TOP_STORES);

						if (httpclient != null) {
							HttpResponse response = httpclient
									.execute(httpGetStoryId);
							BufferedReader reader = new BufferedReader(
									new InputStreamReader(response.getEntity()
											.getContent(), "UTF-8"));
							serverResponse = reader.readLine();
						}

						JSONArray jObjArray = new JSONArray(serverResponse);

						for (int i = 0; i < 10; i++) {

							HttpGet httpGetItem = new HttpGet(
									HackerNewsURLConstants.STORY_ITEM
											+ jObjArray.get(i) + ".json");
							if (httpclient != null) {
								HttpResponse response = httpclient
										.execute(httpGetItem);
								BufferedReader reader = new BufferedReader(
										new InputStreamReader(response
												.getEntity().getContent(),
												"UTF-8"));
								itemResponse = reader.readLine();
								JSONObject object = new JSONObject(itemResponse);
								jsonobjectlist.add(object);

							}

						}
						Message message = Message.obtain(null, 15,
								jsonobjectlist);
						messengerService.send(message);
						System.out.println(jsonobjectlist.toString());
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
		Log.i(TAG, "Service onBind");
		return null;
	}

	@Override
	public void onDestroy() {

		isRunning = false;

		Log.i(TAG, "Service onDestroy");
	}

	public static HttpClient createHttpClient() {
		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params,
				HTTP.DEFAULT_CONTENT_CHARSET);
		HttpProtocolParams.setUseExpectContinue(params, true);

		SchemeRegistry schReg = new SchemeRegistry();
		schReg.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		schReg.register(new Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));
		ClientConnectionManager conMgr = new ThreadSafeClientConnManager(
				params, schReg);

		return new DefaultHttpClient(conMgr, params);
	}
}
package com.example.edumap;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Connection extends Activity implements OnClickListener{
	
	public static final int SHOW_RESPONSE = 0;
	private Button sendRequest;
	private TextView responseText;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SHOW_RESPONSE:
				String response = (String) msg.obj;
				responseText.setText(response);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connection);
		sendRequest = (Button) findViewById(R.id.send_request);
		responseText = (TextView) findViewById(R.id.response_text);
		sendRequest.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.send_request) {
			sendRequestWithHttpClient();
		}
	}
	
	private void sendRequestWithHttpClient() {
		new Thread (new Runnable() {
			@Override
			public void run() {
				HttpURLConnection connection = null;
				try {
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet("http://202.112.88.61:8080/mapserver/api/schoolAreaList?districtName=%E6%B5%B7%E6%B7%80%E5%8C%BA");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if(httpResponse.getStatusLine().getStatusCode() == 200) {
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity, "utf-8");
						Message message = new Message();
						message.what = SHOW_RESPONSE;
						message.obj = response.toString();
						parseJSONWithJOSNObject(response.toString());
						handler.sendMessage(message);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if(connection != null) {
						connection.disconnect();
					}
				}
			}
		}).start();
	}
	
	
	private void parseJSONWithJOSNObject(String jsonData) {
		Gson gson = new Gson();
		Map map = gson.fromJson(jsonData, new TypeToken<Map>(){}.getType());
		ArrayList<Map> result = (ArrayList<Map>)map.get("result");
		Map temp = result.get(0);
		System.out.println(temp.get("saPoints"));
		ArrayList<Map> saPoints = gson.fromJson(temp.get("saPoints").toString(), new TypeToken<ArrayList<Map>>(){}.getType());
	} 
	
}


package org.lskk.shesop.steppy.connection;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.ByteArrayEntity;
import org.json.JSONArray;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

public abstract class ConnectionHandler {
	
	protected static AsyncHttpClient client = new AsyncHttpClient();
	protected static SyncHttpClient clientSync = new SyncHttpClient();
	
	protected Context mContext;
	protected IConnectionResponseHandler responseHandler;

	public void get(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.get(getAbsoluteUrl(url), params, responseHandler);
	}

	public void post(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.post(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void put(String url, RequestParams params,
			AsyncHttpResponseHandler responseHandler) {
		client.put(getAbsoluteUrl(url), params, responseHandler);
	}
	
	public void postJArray(Context context, String url, JSONArray jArray,
			AsyncHttpResponseHandler responseHandler) {
		
		ByteArrayEntity entity;
		try {
			entity = new ByteArrayEntity(jArray.toString().getBytes("UTF-8"));
			
			clientSync.setTimeout(60000*3);
			clientSync.post(context, getAbsoluteUrl(url), entity, "application/json", responseHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public abstract String getAbsoluteUrl(String relativeUrl);
}

package org.lskk.shesop.steppy.connection;

import org.json.JSONArray;

public interface IConnectionResponseHandler {
	public void onSuccessJSONObject(String pResult);
	public void OnSuccessArray(JSONArray pResult);
	public void onSuccessJSONArray(String pResult);
	public void onFailure(String e);
}

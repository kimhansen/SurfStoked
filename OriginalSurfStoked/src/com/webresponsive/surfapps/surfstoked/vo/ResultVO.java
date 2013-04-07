package com.webresponsive.surfapps.surfstoked.vo;

import org.json.JSONException;
import org.json.JSONObject;

public class ResultVO
{
    private boolean success;
    private String data;

    public boolean getSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
    }

    public ResultVO()
    {
    }

    public ResultVO(JSONObject obj) throws JSONException
    {
        deserializeFromObj(obj);
    }

    public ResultVO(String serializedObj) throws JSONException
    {
        deserialize(serializedObj);
    }

    public void deserialize(String serializedObj) throws JSONException
    {
        JSONObject obj = new JSONObject(serializedObj);
        deserializeFromObj(obj);
    }

    public void deserializeFromObj(JSONObject obj) throws JSONException
    {
        this.success = obj.getBoolean("success");
        if (obj.has("data"))
        {
            this.data = obj.getString("data");
        }
    }
}

package com.webresponsive.surfapps.surfstoked.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webresponsive.surfapps.surfstoked.database.DatabaseManager;

import android.database.Cursor;

public class SessionVO
{
    private long id;
    private long userId;
    private Date date;
    private String description;
    private int rating;
    private List<PictureVO> pictures;

    public final long getId()
    {
        return id;
    }

    public final void setId(long id)
    {
        this.id = id;
    }

    public long getUserId()
    {
        return userId;
    }

    public void setUserId(long userId)
    {
        this.userId = userId;
    }

    public final Date getDate()
    {
        return date;
    }

    public final void setDate(Date date)
    {
        this.date = date;
    }

    public final String getDescription()
    {
        return description;
    }

    public final void setDescription(String description)
    {
        this.description = description;
    }

    public final int getRating()
    {
        return rating;
    }

    public final void setRating(int rating)
    {
        this.rating = rating;
    }

    public List<PictureVO> getPictures()
    {
        return pictures;
    }

    public void setPictures(List<PictureVO> pictures)
    {
        this.pictures = pictures;
    }

    public SessionVO()
    {
    }

    public SessionVO(JSONObject obj) throws JSONException
    {
        deserializeFromObj(obj);
    }

    public SessionVO(String serializedObj) throws JSONException
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
        this.id = obj.getLong("id");
        this.userId = obj.getLong("user_id");

        // FIXME
        // this.date = obj.getDate("date");
        this.description = obj.getString("description");
        this.rating = obj.getInt("rating");
        this.pictures = PictureVO.deserializeArray(obj.getJSONArray("pictures"));
    }

    public String serialize() throws JSONException
    {
        return serializeToObj().toString();
    }

    public JSONObject serializeToObj() throws JSONException
    {
        JSONObject serializedObj = new JSONObject();

        serializedObj.put("id", this.id);
        serializedObj.put("user_id", this.userId);
        serializedObj.put("date", this.date);
        serializedObj.put("description", this.description);
        serializedObj.put("rating", this.rating);
        serializedObj.put("pictures", this.pictures);

        if (this.pictures != null)
        {
            List<JSONObject> itemObjs = new ArrayList<JSONObject>();
            for (PictureVO pictureVO : this.pictures)
            {
                JSONObject serializedItemObj = new JSONObject();
                serializedItemObj.put("picture", pictureVO);
                itemObjs.add(serializedItemObj);
            }
            serializedObj.put("pictures", new JSONArray(itemObjs));
        }

        return serializedObj;
    }

    /**
     * Serialize an array into a JSON serialized string.
     */
    public static String serializeArray(ArrayList<SessionVO> sessions)
    {
        JSONArray jsArray = new JSONArray(sessions);

        return jsArray.toString();
    }

    /**
     * Deserialize a JSON string into an object array.
     */
    public static List<SessionVO> deserializeArray(String serializedArray) throws JSONException
    {
        JSONObject jsonObj = new JSONObject(serializedArray);
        JSONArray jsonObjs = jsonObj.getJSONArray("sessions");
        List<SessionVO> sessions = new ArrayList<SessionVO>();
        for (int i = 0; i < jsonObjs.length(); i++)
        {
            JSONObject session = jsonObjs.getJSONObject(i);
            sessions.add(new SessionVO(session));
        }

        return sessions;
    }

    public static List<SessionVO> getSessions(Cursor sessionsCursor)
    {
        List<SessionVO> sessions = new ArrayList<SessionVO>();
        if (sessionsCursor != null)
        {
            sessionsCursor.moveToFirst();

            SessionVO sessionVO;
            while (sessionsCursor.isAfterLast() == false)
            {
                sessionVO = new SessionVO();
                sessionVO.setId(sessionsCursor.getInt(sessionsCursor.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_ID)));

//                sessionVO.setDate(sessionsCursor.getDate(sessionsCursor.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_DATE)));
                sessionVO.setDescription(sessionsCursor.getString(sessionsCursor.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_DESCRIPTION)));
                sessionVO.setRating(sessionsCursor.getInt(sessionsCursor.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_RATING)));

                sessions.add(sessionVO);

                sessionsCursor.moveToNext();
            }
        }

        sessionsCursor.close();

        return sessions;
    }
}

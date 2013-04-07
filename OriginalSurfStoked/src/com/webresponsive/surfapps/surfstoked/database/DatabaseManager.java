package com.webresponsive.surfapps.surfstoked.database;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.webresponsive.surfapps.surfstoked.R;
import com.webresponsive.surfapps.surfstoked.vo.PictureVO;
import com.webresponsive.surfapps.surfstoked.vo.SessionVO;

public class DatabaseManager
{
    Context context;

    private SQLiteDatabase db;

    private final static String DB_NAME = "sharesurfsession";
    private final static int DB_VERSION = 1;

    public final static String SESSIONS_TABLE_NAME = "sessions";
    public final static String SESSIONS_TABLE_ROW_ID = "_id";
    public final static String SESSIONS_TABLE_ROW_DATE = "date";
    public final static String SESSIONS_TABLE_ROW_DESCRIPTION = "description";
    public final static String SESSIONS_TABLE_ROW_RATING = "rating";

    public final static String SESSION_PICTURES_TABLE_NAME = "session_pictures";
    public final static String SESSION_PICTURES_TABLE_ROW_ID = "_id";
    public final static String SESSION_PICTURES_TABLE_ROW_SESSION_ID = "session_id";
    public final static String SESSION_PICTURES_TABLE_ROW_PICTURE = "picture";

    public DatabaseManager(Context context)
    {
        this.context = context;

        CustomSQLiteOpenHelper helper = new CustomSQLiteOpenHelper(context);
        db = helper.getWritableDatabase();
    }

    public long addSession(String date, String description, float rating)
    {
        ContentValues values = new ContentValues();
        values.put(SESSIONS_TABLE_ROW_DATE, date);
        values.put(SESSIONS_TABLE_ROW_DESCRIPTION, description);
        values.put(SESSIONS_TABLE_ROW_RATING, rating);

        long insertId = -1;
        try
        {
            insertId = db.insert(SESSIONS_TABLE_NAME, null, values);
        } catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return insertId;
    }

    public SessionVO getSession(long sessionId)
    {
        SessionVO sessionVO = new SessionVO();

        try
        {
            Cursor cursor = db.query(true, SESSIONS_TABLE_NAME, new String[] {
                    SESSIONS_TABLE_ROW_ID, SESSIONS_TABLE_ROW_DATE, SESSIONS_TABLE_ROW_DESCRIPTION, SESSIONS_TABLE_ROW_RATING
            }, SESSIONS_TABLE_ROW_ID + "=" + sessionId, null, null, null, null, null);

            if (cursor.moveToFirst())
            {
                sessionVO.setId(cursor.getLong(0));
                String dateString = cursor.getString(1);
                sessionVO.setDate(new Date(dateString));
                sessionVO.setDescription(cursor.getString(2));
                sessionVO.setRating(cursor.getInt(3));
            }

            if ((cursor != null) && !cursor.isClosed())
            {
                cursor.close();
            }
        } catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return sessionVO;
    }

    public List<Bitmap> getPicturesAsBitmaps(long sessionId)
    {
        List<Bitmap> sessionPictures = new ArrayList<Bitmap>();

        try
        {
            Cursor cursor = db.query(true, SESSION_PICTURES_TABLE_NAME, new String[] {
                    SESSION_PICTURES_TABLE_ROW_ID, SESSION_PICTURES_TABLE_ROW_PICTURE
            }, SESSION_PICTURES_TABLE_ROW_SESSION_ID + "=" + sessionId, null, null, null, null, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    byte[] mybyte = cursor.getBlob(1);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(mybyte);
                    Bitmap theImage = BitmapFactory.decodeStream(imageStream);

                    sessionPictures.add(theImage);
                } while (cursor.moveToNext());
            }
            if ((cursor != null) && !cursor.isClosed())
            {
                cursor.close();
            }
        } catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return sessionPictures;
    }

    public List<PictureVO> getPictures(long sessionId)
    {
        List<PictureVO> sessionPictures = new ArrayList<PictureVO>();

        try
        {
            Cursor cursor = db.query(true, SESSION_PICTURES_TABLE_NAME, new String[] {
                    SESSION_PICTURES_TABLE_ROW_ID, SESSION_PICTURES_TABLE_ROW_PICTURE
            }, SESSION_PICTURES_TABLE_ROW_SESSION_ID + "=" + sessionId, null, null, null, null, null);

            if (cursor.moveToFirst())
            {
                do
                {
                    byte[] pictureByte = cursor.getBlob(1);
//                    ByteArrayInputStream imageStream = new ByteArrayInputStream(mybyte);
                    PictureVO pictureVO = new PictureVO();
                    pictureVO.setPictureData(pictureByte);

                    sessionPictures.add(pictureVO);
                } while (cursor.moveToNext());
            }
            if ((cursor != null) && !cursor.isClosed())
            {
                cursor.close();
            }
        } catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return sessionPictures;
    }

    public int deleteSession(long sessionId)
    {
        int deleted = 0;
        try
        {
            deleted = db.delete(SESSIONS_TABLE_NAME, SESSIONS_TABLE_ROW_ID + "=" + sessionId, null);
        } catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }

        return deleted;
    }

    public void updateSession(long sessionId, String date, String description, int rating)
    {
        ContentValues values = new ContentValues();
        values.put(SESSIONS_TABLE_ROW_DATE, date);
        values.put(SESSIONS_TABLE_ROW_DESCRIPTION, description);
        values.put(SESSIONS_TABLE_ROW_RATING, rating);

        try
        {
            db.update(SESSIONS_TABLE_NAME, values, SESSIONS_TABLE_ROW_ID + "=" + sessionId, null);
        } catch (Exception e)
        {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }
    }

    public int deletePictures(long sessionId)
    {
        int deleted = -1;
        try
        {
            deleted = db.delete(SESSION_PICTURES_TABLE_NAME, SESSION_PICTURES_TABLE_ROW_SESSION_ID + "=" + sessionId, null);
        } catch (Exception e)
        {
            Log.e("DB Error", e.toString());
            e.printStackTrace();
        }

        return deleted;
    }

    public void addPicture(long sessionId, Bitmap picture)
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        picture.compress(Bitmap.CompressFormat.PNG, 100, out);
        picture.compress(Bitmap.CompressFormat.JPEG, 90, out);

        ContentValues values = new ContentValues();
        values.put(SESSION_PICTURES_TABLE_ROW_SESSION_ID, sessionId);
        values.put(SESSION_PICTURES_TABLE_ROW_PICTURE, out.toByteArray());

        try
        {
            db.insert(SESSION_PICTURES_TABLE_NAME, null, values);
        } catch (Exception e)
        {
            Log.e("DB ERROR", e.toString());
            e.printStackTrace();
        }
    }

    private class CustomSQLiteOpenHelper extends SQLiteOpenHelper
    {
        public CustomSQLiteOpenHelper(Context context)
        {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            try
            {
                InputStream in = context.getResources().openRawResource(R.raw.sql);
                DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document doc = builder.parse(in, null);
                NodeList statements = doc.getElementsByTagName("statement");

                String s;
                for (int i = 0; i < statements.getLength(); i++)
                {
                    s = statements.item(i).getChildNodes().item(0).getNodeValue();
                    db.execSQL(s);
                }
            } catch (Throwable t)
            {
                Log.e("DB Error", t.toString());
                t.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
        }
    }

    public Cursor getSessions()
    {
        return db.rawQuery("SELECT _id, date, description, rating FROM " + SESSIONS_TABLE_NAME + " ORDER BY _id DESC", null);
    }
}
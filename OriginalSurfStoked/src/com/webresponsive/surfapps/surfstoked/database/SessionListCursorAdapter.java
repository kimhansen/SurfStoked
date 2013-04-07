package com.webresponsive.surfapps.surfstoked.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.webresponsive.surfapps.surfstoked.R;

public class SessionListCursorAdapter extends SimpleCursorAdapter implements Filterable
{
    private static String DATE_FORMAT_STRING = "d MMM yyyy";

    private int layout;

    public SessionListCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to)
    {
        super(context, layout, c, from, to);
        this.layout = layout;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        Cursor c = getCursor();

        final LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(layout, parent, false);

        int sessionIdColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_ID);
        int sessionId = c.getInt(sessionIdColumn);
        TextView sessionIdTextView = (TextView) v.findViewById(R.id.sessionId);
        if (sessionIdTextView != null)
        {
            sessionIdTextView.setText(String.valueOf(sessionId));
        }

        int sessionDescriptionColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_DESCRIPTION);
        String description = c.getString(sessionDescriptionColumn);
        TextView sessionDescriptionTextView = (TextView) v.findViewById(R.id.sessionDescription);
        if (sessionDescriptionTextView != null)
        {
            sessionDescriptionTextView.setText(description);
        }

        int sessionRatingColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_RATING);
        int rating = c.getInt(sessionRatingColumn);
        RatingBar sessionRatingRatingBar = (RatingBar) v.findViewById(R.id.sessionRating);
        if (sessionRatingRatingBar != null)
        {
            sessionRatingRatingBar.setRating(rating);
        }

        int sessionDateColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_DATE);
        String dateString = c.getString(sessionDateColumn);

        Date date = new Date();
        try
        {
            date = new Date(dateString);
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        TextView sessionDateTextView = (TextView) v.findViewById(R.id.sessionDate);
        if (sessionDateTextView != null)
        {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
            sessionDateTextView.setText(format.format(date));
        }

        return v;
    }

    @Override
    public void bindView(View v, Context context, Cursor c)
    {
        int sessionIdColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_ID);
        int sessionId = c.getInt(sessionIdColumn);
        TextView sessionIdTextView = (TextView) v.findViewById(R.id.sessionId);
        if (sessionIdTextView != null)
        {
            sessionIdTextView.setText(String.valueOf(sessionId));
        }

        int sessionDescriptionColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_DESCRIPTION);
        String description = c.getString(sessionDescriptionColumn);
        TextView sessionDescriptionTextView = (TextView) v.findViewById(R.id.sessionDescription);
        if (sessionDescriptionTextView != null)
        {
            sessionDescriptionTextView.setText(description);
        }

        int sessionRatingColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_RATING);
        int rating = c.getInt(sessionRatingColumn);
        RatingBar sessionRatingRatingBar = (RatingBar) v.findViewById(R.id.sessionRating);
        if (sessionRatingRatingBar != null)
        {
            sessionRatingRatingBar.setRating(rating);
        }

        int sessionDateColumn = c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_DATE);
        String dateString = c.getString(sessionDateColumn);
        Date date = new Date();
        try
        {
            date = new Date(dateString);
        } catch (IllegalArgumentException e)
        {
            e.printStackTrace();
        }
        TextView sessionDateTextView = (TextView) v.findViewById(R.id.sessionDate);
        if (sessionDateTextView != null)
        {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
            sessionDateTextView.setText(format.format(date));
        }
    }
}
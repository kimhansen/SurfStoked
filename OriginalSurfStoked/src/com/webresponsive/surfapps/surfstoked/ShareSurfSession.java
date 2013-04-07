package com.webresponsive.surfapps.surfstoked;

import android.app.ListActivity;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.webresponsive.surfapps.surfstoked.database.DatabaseManager;
import com.webresponsive.surfapps.surfstoked.database.SessionListCursorAdapter;
import com.webresponsive.surfapps.surfstoked.sessions.SessionManager;

public class ShareSurfSession extends ListActivity
{
    private static final int CONFIG_OPTION = 0;

    private static final int EDIT_SESSION = 1;

    private static final int DELETE_SESSION = 2;

    private static final String GOOGLE_ANALYTICS_CODE = "UA-23697770-1";

    private ShareSurfSession context;

    private Cursor cursor;

    private ListAdapter adapter;

    private DatabaseManager dm;

    private GoogleAnalyticsTracker tracker;

    /**
     * Called when the activity is started.
     * 
     * Sets up the database manager, the Google tracker, the view and populate with data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.main);

        dm = new DatabaseManager(this);

        setupSessionList();
        setupButtons();

        setupTracking();
    }

    /**
     * Setup Google tracking.
     */
    private void setupTracking()
    {
        tracker = GoogleAnalyticsTracker.getInstance();

        tracker.start(GOOGLE_ANALYTICS_CODE, 20, this);
    }

    /**
     * On list item click listener. Calls the edit session.
     */
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        Cursor c = (Cursor) getListView().getItemAtPosition(position);
        long sessionId = c.getLong(c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_ID));
        editSession(sessionId);
        c.close();
    }

    /**
     * Setup the session list with a session cursor from the database.
     */
    private void setupSessionList()
    {
        cursor = dm.getSessions();
        startManagingCursor(cursor);

        adapter = new SessionListCursorAdapter(this, R.layout.list_item, cursor, new String[] {
                "date", "rating", "description"
        }, new int[] {
                R.id.sessionDate, R.id.sessionRating, R.id.sessionDescription
        });

        setListAdapter(adapter);
        
        startManagingCursor(cursor);


        registerForContextMenu(getListView());
    }

    /**
     * Setup the context menu, with options to edit and delete a session.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0, EDIT_SESSION, 0, "Edit session");
        menu.add(0, DELETE_SESSION, 1, "Delete session");
    }

    /**
     * Method to handle a context menu item select.
     */
    public boolean onContextItemSelected(MenuItem item)
    {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        Cursor c = (Cursor) getListView().getItemAtPosition(info.position);
        long sessionId = c.getLong(c.getColumnIndex(DatabaseManager.SESSIONS_TABLE_ROW_ID));
        c.close();

        switch (item.getItemId())
        {
            case EDIT_SESSION:
                editSession(sessionId);
                return true;

            case DELETE_SESSION:
                deleteSession(sessionId);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    /**
     * Call the database manager to delete a session.
     */
    private void deleteSession(long sessionId)
    {
        tracker.trackPageView("/deleteSession");

        if (dm.deleteSession(sessionId) > 0)
        {
            dm.deletePictures(sessionId);
            Toast.makeText(((ContextWrapper) context).getBaseContext(), "Session deleted", Toast.LENGTH_SHORT).show();
            setupSessionList();
        } else
        {
            Toast.makeText(((ContextWrapper) context).getBaseContext(), "Problem deleting session", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Call the add session activity with a session id to edit a session.
     */
    private void editSession(long sessionId)
    {
        tracker.trackPageView("/editSession");

        Intent addSessionIntent = new Intent(context, AddSession.class);

        Bundle bundle = new Bundle();
        bundle.putLong("sessionId", sessionId);
        addSessionIntent.putExtras(bundle);

        startActivity(addSessionIntent);
        finish();
    }

    /**
     * Setup the buttons to add a new session or synchronize the sessions.
     */
    private void setupButtons()
    {
        final Button addSessionButton = (Button) findViewById(R.id.addSessionButton);
        addSessionButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                tracker.trackPageView("/addSession");
                startActivity(new Intent(context, AddSession.class));
                finish();
            }
        });
        
        final Button synchronizeButton = (Button) findViewById(R.id.synchronizeButton);
        synchronizeButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                SessionManager.synchronizeSessions(context);
            }
        });
    }

    /**
     * Setup the options menu.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        menu.add(0, CONFIG_OPTION, 0, "Configure outgoing Mail Server");

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle an options menu click.
     */
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case CONFIG_OPTION:
                tracker.trackPageView("/configureEmail");
                startActivity(new Intent(context, Config.class));
                finish();

                return true;
        }

        return false;
    }

    /**
     * Stop Google tracking when we leave this activity.
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();

        tracker.stop();
    }
}
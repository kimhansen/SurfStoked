package com.webresponsive.surfapps.surfstoked.sessions;

import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Toast;

import com.webresponsive.surfapps.surfstoked.ShareSurfSession;
import com.webresponsive.surfapps.surfstoked.database.DatabaseManager;
import com.webresponsive.surfapps.surfstoked.helpers.JSONHelper;
import com.webresponsive.surfapps.surfstoked.vo.PictureVO;
import com.webresponsive.surfapps.surfstoked.vo.ResultVO;
import com.webresponsive.surfapps.surfstoked.vo.SessionVO;

public class SessionManager
{
    private static final String WEBSITE_START_URL = "http://www.surf-stoked.com/service/server.php?action=";

    private static final String WEBSITE_END_URL = "&jsoncallback=?";

    private static final String SERVER_COMMAND_GETSESSIONS = "getSessions";

    private static final String SERVER_COMMAND_INSERTSESSION = "insertSession";
    
    private static final String SERVER_COMMAND_INSERTPICTURE = "insertPicture";
    
    private static ProgressDialog busyIndicator;
    
    private static DatabaseManager dm;

    public static void synchronizeSessions(final Activity activity)
    {
        busyIndicator = ProgressDialog.show(activity, "Synchronizing sessions", "Please wait ... ", true);

        Thread threadShare = new Thread(new Runnable()
        {
            private boolean sessionSaved = false;

            private boolean pictureSaved = false;

            public void run()
            {
                if (dm == null)
                {
                    dm = new DatabaseManager(activity);
                }
                Cursor sessionsCursor = dm.getSessions();
                final List<SessionVO> sessions = SessionVO.getSessions(sessionsCursor);
                sessionsCursor.close();

                for (SessionVO sessionVO : sessions)
                {
                    try
                    {
                        String response = JSONHelper.sendSerializedDataToServer(WEBSITE_START_URL + SERVER_COMMAND_INSERTSESSION + WEBSITE_END_URL, sessionVO.serialize());
                        ResultVO resultVO = new ResultVO(response);

                        if (resultVO.getSuccess())
                        {
                            sessionSaved = true;

                            activity.runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    busyIndicator.setMessage("Session stored. Now uploading pictures.");
                                }
                            });

                            String sessionId = resultVO.getData();

                            List<PictureVO> pictures = dm.getPictures(sessionVO.getId());

                            int count = 1;
                            for (PictureVO pictureVO : pictures)
                            {
                                response = JSONHelper.sendSerializedPictureToServer(sessionId, String.valueOf(count), WEBSITE_START_URL + SERVER_COMMAND_INSERTPICTURE + WEBSITE_END_URL, pictureVO.getPictureData());
                                resultVO = new ResultVO(response);

                                count++;
                            }
                            
                            dm.deleteSession(sessionVO.getId());
                            dm.deletePictures(sessionVO.getId());
                        }
                        else
                        {
                            busyIndicator.setMessage("Problem occured when synchronizing with server.");
                        }
                    } catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (busyIndicator.isShowing())
                        {
                            busyIndicator.dismiss();

                            Toast.makeText(((ContextWrapper) activity).getBaseContext(), "Synchronization finished", Toast.LENGTH_SHORT).show();
                            // TODO - Launch activity to show sessions
                            
                            activity.startActivity(new Intent(activity.getBaseContext(), ShareSurfSession.class));
                        }
                    }
                });
            }
        });

        threadShare.start();
    }

    public static void loadSessions(final Activity activity)
    {
        busyIndicator = ProgressDialog.show(activity, "Sending session to website", "Please wait ... ", true);

        Thread threadShare = new Thread(new Runnable()
        {
            private boolean loadedSuccessfully;
            private List<SessionVO> sessions;

            public void run()
            {
                String JSONSerializedResources = JSONHelper.loadSerializedDataFromServer(WEBSITE_START_URL + SERVER_COMMAND_GETSESSIONS + WEBSITE_END_URL);
                
                try
                {
                    sessions = SessionVO.deserializeArray(JSONSerializedResources);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (busyIndicator.isShowing())
                        {
                            busyIndicator.dismiss();

                            if (loadedSuccessfully)
                            {
                                Toast.makeText(((ContextWrapper) activity).getBaseContext(), "Session sent", Toast.LENGTH_SHORT).show();
                                
                                // TODO - Launch activity to show sessions
                            }
                            else
                            {
                                Toast.makeText(((ContextWrapper) activity).getBaseContext(),
                                        "Problem when sending session. Please check your email configuration.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
            }
        });

        threadShare.start();
    }
}

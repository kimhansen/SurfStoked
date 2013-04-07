package com.webresponsive.surfapps.surfstoked.helpers;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import com.webresponsive.surfapps.surfstoked.Config;
import com.webresponsive.surfapps.surfstoked.mail.MailHelper;

public class EmailHelper
{
    public static void share(final Activity activity, final ProgressDialog busyIndicator)
    {
//        busyIndicator = ProgressDialog.show(activity, "Emailing session", "Please wait ... ", true);
//
//        Thread threadShare = new Thread(new Runnable()
//        {
//            private boolean emailSentSuccessfully;
//
//            public void run()
//            {
//                MailHelper mailHelper = new MailHelper();
//                loadPreferences(mailHelper, activity);
//                mailHelper.setBody(createBodyText());
//                try
//                {
//                    String[] fileNames = PictureHelper.createPictureFiles(images);
//
//                    mailHelper.setFileNames(fileNames);
//
//                    emailSentSuccessfully = mailHelper.send();
//                } catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//                activity.runOnUiThread(new Runnable()
//                {
//                    @Override
//                    public void run()
//                    {
//                        if (busyIndicator.isShowing())
//                        {
//                            busyIndicator.dismiss();
//
//                            if (emailSentSuccessfully)
//                            {
//                                Toast.makeText(((ContextWrapper) activity).getBaseContext(), "Session sent", Toast.LENGTH_SHORT).show();
//                            } else
//                            {
//                                Toast.makeText(((ContextWrapper) activity).getBaseContext(),
//                                        "Problem when sending session. Please check your email configuration.", Toast.LENGTH_LONG).show();
//                            }
//                        }
//                    }
//                });
//            }
//        });
//        threadShare.start();
    }

    private static String createBodyText()
    {
        String bodyText = "";
//        bodyText += "Date: " + dateText.getText().toString() + "";
//        bodyText += "\n\n";
//        bodyText += "Rating: " + getStars(rating.getRating()) + "";
//        bodyText += "\n\n";
//        bodyText += "Description: " + descriptionText.getText().toString() + "";
//        bodyText += "\n\n";
//        bodyText += "\n\n";

        return bodyText;
    }

    /**
     * Get the session rating in text.
     */
    private static String getStars(float sessionRating)
    {
        float theRating = sessionRating;
        String starRating = "";
        while (sessionRating-- > 0)
        {
            starRating += "*";
        }

        if (theRating == 1)
        {
            starRating += "  (Average)";
        } else if (theRating == 2)
        {
            starRating += "  (Ok)";
        } else if (theRating == 3)
        {
            starRating += "  (Fun)";
        } else if (theRating == 4)
        {
            starRating += "  (Good)";
        } else if (theRating == 5)
        {
            starRating += "  (Amazing)";
        }
        return starRating;
    }

    public static boolean mailConfigurationSet(Activity activity)
    {
        SharedPreferences settings = activity.getSharedPreferences(Config.CONFIG_FILE_NAME, 0);

        boolean foundEmptyValue =
                settings.getString(Config.CONFIG_USER, "").equals("") || settings.getString(Config.CONFIG_PASS, "").equals("")
                        || settings.getString(Config.CONFIG_HOST, "").equals("") || settings.getString(Config.CONFIG_PORT, "").equals("")
                        || settings.getString(Config.CONFIG_RECEIVERS, "").equals("");

        return foundEmptyValue;
    }

    public static String[] getReceivers(String allReceivers)
    {
        return allReceivers.split(",\\s*");
    }

    /**
     * Load the mail settings.
     */
    private static void loadPreferences(MailHelper mailHelper, Activity activity)
    {
        SharedPreferences settings = activity.getSharedPreferences(Config.CONFIG_FILE_NAME, 0);
        mailHelper.setUser(settings.getString(Config.CONFIG_USER, ""));
        mailHelper.setPass(settings.getString(Config.CONFIG_PASS, ""));
        mailHelper.setHost(settings.getString(Config.CONFIG_HOST, ""));
        mailHelper.setPort(settings.getString(Config.CONFIG_PORT, ""));
        mailHelper.setFrom(settings.getString(Config.CONFIG_USER, ""));
        mailHelper.setAuth(settings.getBoolean(Config.CONFIG_AUTH, true));

        mailHelper.setSubject(settings.getString(Config.CONFIG_SUBJECT, ""));

        String allReceivers = settings.getString(Config.CONFIG_RECEIVERS, "");
        String[] receivers = getReceivers(allReceivers);

        mailHelper.setTo(receivers);
    }

    /**
     * Sharing (sending email) through the standard ways: GMail, Email client,
     * etc.
     * 
     * There is a bug when attaching multiple images. Probably related to
     * Android..
     */
    public void shareStandard(Activity activity, ArrayList<Bitmap> images)
    {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        SharedPreferences settings = activity.getSharedPreferences(Config.CONFIG_FILE_NAME, 0);
        String[] receivers = getReceivers(settings.getString(Config.CONFIG_RECEIVERS, ""));

        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, receivers);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, settings.getString(Config.CONFIG_SUBJECT, "Check out my surf session"));
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, createBodyText());
        emailIntent.setType("text/plain");

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        // emailIntent.setType("image/jpeg");

        String[] fileNames = PictureHelper.createPictureFiles(images);

        ArrayList<Uri> imagesList = new ArrayList<Uri>();
        for (int i = 0; i < fileNames.length; i++)
        {
            imagesList.add(Uri.parse("file://" + fileNames[i]));
        }
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imagesList);

        activity.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}

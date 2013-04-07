package com.webresponsive.surfapps.surfstoked;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.webresponsive.surfapps.surfstoked.database.DatabaseManager;
import com.webresponsive.surfapps.surfstoked.gallery.GalleryImageAdapter;
import com.webresponsive.surfapps.surfstoked.helpers.PictureHelper;
import com.webresponsive.surfapps.surfstoked.vo.SessionVO;

public class AddSession extends Activity
{
    private static final int TAKE_PICTURE = 1;

    private static final String TEMP_PICTURE_NAME = "SurfStokedTemp.jpg";

    private static String DATE_FORMAT_STRING = "EEE, d MMM yyyy HH:mm";

    private AddSession context;

    private Gallery gallery;

    private static GalleryImageAdapter galleryAdapter;

    private static List<Bitmap> images;

    private static DatabaseManager db;

    private Button takePictureButton, saveButton;

    private EditText descriptionText;

    private TextView dateText;

    private RatingBar rating;

    private Date today;

    private Long sessionId = null;

    private ProgressDialog busyIndicator;

    private int pictureToDelete;

    /**
     * Called when this activity is started.
     * 
     * Sets up the database manager, the view and populate with data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.add_session);

        if (db == null)
        {
            db = new DatabaseManager(this);
        }

        setupViews();

        setupButtonListeners();

        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null)
            {
                sessionId = bundle.getLong("sessionId");
            }
        }

        populateData();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Setup the date, text and images.
     * 
     * If we have a session id, then get the session data from the database,
     * else reset the fields.
     */
    private void populateData()
    {
        if (sessionId == null)
        {
            today = new Date();
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);
            dateText.setText(format.format(today));

            if (images == null)
            {
                images = new ArrayList<Bitmap>();
            }

            setupGalleryImages();
        } else
        {
            SessionVO sessionVO = db.getSession(sessionId);
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_STRING);

            if (sessionVO.getDate() != null)
            {
                dateText.setText(format.format(sessionVO.getDate()));
            }
            rating.setRating(sessionVO.getRating());
            descriptionText.setText(sessionVO.getDescription());

            if (images == null)
            {
                images = db.getPicturesAsBitmaps(sessionId);
            }

            setupGalleryImages();
        }
    }

    /**
     * Setup the gallery with images and add the on click listener to delete
     * images.
     */
    private void setupGalleryImages()
    {
        Bitmap[] imagesArray = new Bitmap[images.size()];
        galleryAdapter.bitmaps = (Bitmap[]) images.toArray(imagesArray);
        gallery.setAdapter(galleryAdapter);
        gallery.invalidate();

        gallery.setOnItemClickListener(new OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                pictureToDelete = position;
                confirmDelete();
            }
        });
    }

    /**
     * Delete a picture from the gallery.
     */
    private void confirmDelete()
    {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setMessage("Do you want to delete this picture?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                images.remove(pictureToDelete);

                Bitmap[] imagesArray = new Bitmap[images.size()];
                galleryAdapter.bitmaps = (Bitmap[]) images.toArray(imagesArray);
                gallery.setAdapter(galleryAdapter);
                gallery.invalidate();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertBuilder.create();
        alert.setTitle("Confirm delete picture");
        alert.setIcon(R.drawable.icon);
        alert.show();
    }

    /**
     * Setup the views, so we can manipulate the components.
     */
    private void setupViews()
    {
        rating = (RatingBar) findViewById(R.id.rating);
        dateText = (TextView) findViewById(R.id.dateText);
        descriptionText = (EditText) findViewById(R.id.descriptionText);

        takePictureButton = (Button) findViewById(R.id.take_picture_button);
        saveButton = (Button) findViewById(R.id.saveButton);

        gallery = (Gallery) findViewById(R.id.gallery);
        galleryAdapter = new GalleryImageAdapter(this);
    }

    /**
     * Setup the button listeners.
     * 
     * Check if the configuration has been set, before we send the data.
     */
    private void setupButtonListeners()
    {
        takePictureButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                takePicture();
            }
        });

        final Activity activity = this;

//        shareButton.setOnClickListener(new View.OnClickListener()
//        {
//            public void onClick(View v)
//            {
//                if (EmailHelper.mailConfigurationSet(activity))
//                {
//                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
//                    alertBuilder.setMessage("Do you want to configure the email settings?").setCancelable(false)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                            {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    startActivity(new Intent(context, Config.class));
//                                    finish();
//                                }
//                            }).setNegativeButton("No", new DialogInterface.OnClickListener()
//                            {
//                                public void onClick(DialogInterface dialog, int id)
//                                {
//                                    dialog.cancel();
//                                }
//                            });
//
//                    AlertDialog alert = alertBuilder.create();
//                    alert.setTitle("Email configuration needed");
//                    alert.setIcon(R.drawable.icon);
//                    alert.show();
//                } else
//                {
//                    JSONHelper.shareOnWebsite();
//                }
//            }
//        });

        saveButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                save();
            }
        });
    }

    /**
     * Call the image capture activity to take a picture.
     */
    private void takePicture()
    {
        String _path = Environment.getExternalStorageDirectory() + File.separator + TEMP_PICTURE_NAME;
        File file = new File(_path);
        Uri outputFileUri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * When returning from taking a picture, this method is called with the result data
     * which is a bitmap.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == TAKE_PICTURE)
        {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + TEMP_PICTURE_NAME);
            Bitmap bitmap = PictureHelper.decodeFile(file);

            images.add(bitmap);
            Bitmap[] imagesArray = new Bitmap[images.size()];
            galleryAdapter.bitmaps = (Bitmap[]) images.toArray(imagesArray);
            gallery.setAdapter(galleryAdapter);
            gallery.invalidate();
        }
    }

    /**
     * Reset the images holder.
     */
    private void resetImages()
    {
        images = null;
    }

    /**
     * Catch the back button press, reset the images and return to the start activity.
     */
    @Override
    public void onBackPressed()
    {
        resetImages();

        startActivity(new Intent(context, ShareSurfSession.class));
        finish();

        return;
    }

    /**
     * Save the session in the database, so it can be synchronized or shared later.
     */
    public void save()
    {
        busyIndicator = ProgressDialog.show(AddSession.this, "Saving session", "Please wait ... ", true);

        Thread threadSave = new Thread(new Runnable()
        {
            public void run()
            {
                try
                {
                    if (sessionId != null)
                    {
                        db.deleteSession(sessionId);
                        db.deletePictures(sessionId);
                    }

                    sessionId = db.addSession(dateText.getText().toString(), descriptionText.getText().toString(), rating.getRating());

                    for (int i = 0; i < images.size(); i++)
                    {
                        db.addPicture(sessionId, images.get(i));
                    }
                } catch (Exception e)
                {
                    Log.e("Add Error", e.toString());
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (busyIndicator.isShowing())
                        {
                            busyIndicator.dismiss();

                            Toast.makeText(((ContextWrapper) context).getBaseContext(), "Session saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        threadSave.start();
    }
}

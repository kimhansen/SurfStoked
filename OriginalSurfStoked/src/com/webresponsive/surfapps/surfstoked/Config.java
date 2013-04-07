package com.webresponsive.surfapps.surfstoked;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class Config extends Activity
{
    public static final String CONFIG_FILE_NAME = "SurfStokedConfig";

    public static final String CONFIG_USER = "user";

    public static final String CONFIG_PASS = "pass";

    public static final String CONFIG_HOST = "host";

    public static final String CONFIG_PORT = "port";

    public static final String CONFIG_AUTH = "auth";

    public static final String CONFIG_SUBJECT = "subject";

    public static final String CONFIG_RECEIVERS = "receivers";

    private Config context;

    private EditText userText;

    private EditText passText;

    private EditText hostText;

    private EditText portText;

    private CheckBox authCheckbox;

    private TextView subjectText;

    private TextView receiversText;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        context = this;

        setContentView(R.layout.config);

        SharedPreferences settings = getSharedPreferences(CONFIG_FILE_NAME, 0);

        userText = (EditText) findViewById(R.id.userText);
        passText = (EditText) findViewById(R.id.passText);
        hostText = (EditText) findViewById(R.id.hostText);
        portText = (EditText) findViewById(R.id.portText);
        authCheckbox = (CheckBox) findViewById(R.id.authCheckbox);

        subjectText = (EditText) findViewById(R.id.SubjectTextView);
        receiversText = (EditText) findViewById(R.id.ReceiversTextView);

        userText.setText(settings.getString(CONFIG_USER, ""));
        passText.setText(settings.getString(CONFIG_PASS, ""));
        hostText.setText(settings.getString(CONFIG_HOST, ""));
        portText.setText(settings.getString(CONFIG_PORT, ""));
        authCheckbox.setChecked(settings.getBoolean(CONFIG_AUTH, true));

        subjectText.setText(settings.getString(CONFIG_SUBJECT, "Check out my surf session"));
        receiversText.setText(settings.getString(CONFIG_RECEIVERS, ""));

        setupTabs();
    }

    private void setupTabs()
    {
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        TabSpec spec1 = tabHost.newTabSpec("Mail Server");
        spec1.setContent(R.id.tab1);
        spec1.setIndicator("Mail Server");

        TabSpec spec2 = tabHost.newTabSpec("Email Content");
        spec2.setContent(R.id.tab2);
        spec2.setIndicator("Email Content");

        tabHost.addTab(spec1);
        tabHost.addTab(spec2);
    }

    public void save(View view)
    {
        SharedPreferences settings = getSharedPreferences(CONFIG_FILE_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(CONFIG_USER, userText.getText().toString());
        editor.putString(CONFIG_PASS, passText.getText().toString());
        editor.putString(CONFIG_HOST, hostText.getText().toString());
        editor.putString(CONFIG_PORT, portText.getText().toString());
        editor.putBoolean(CONFIG_AUTH, authCheckbox.isChecked());

        editor.putString(CONFIG_SUBJECT, subjectText.getText().toString());
        editor.putString(CONFIG_RECEIVERS, receiversText.getText().toString());

        editor.commit();

        startActivity(new Intent(context, ShareSurfSession.class));
        finish();
    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(context, ShareSurfSession.class));
        finish();

        return;
    }
}
package com.webresponsive.surfapps.surfstoked.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;

public class JSONHelper
{
    /**
     * Send serialized data to the server.
     */
    public static String sendSerializedDataToServer(String url, String serializedObject)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        HttpResponse response;

        StringEntity stringEntity;
        try
        {
            stringEntity = new StringEntity(serializedObject);
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            HttpEntity httpEntity = stringEntity;
            httppost.setEntity(httpEntity);
        } catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }

        String result = null;
        try
        {
            response = httpclient.execute(httppost);

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null)
            {
                InputStream instream = responseEntity.getContent();
                result = convertStreamToString(instream);
                instream.close();
            }
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return fixWrapper(result);
    }

    /**
     * Send serialized data to the server.
     * @param url 
     */
    public static String sendSerializedPictureToServer(String sessionId, String pictureId, String url, byte[] data)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpUriRequest httppost = new HttpPost(url);
        HttpResponse response;

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        httpclient.getParams().setBooleanParameter("http.protocol.expect-continue", false); 

        try
        {
            entity.addPart("sessionId", new StringBody(sessionId));
            entity.addPart("pictureId", new StringBody(pictureId));
        } catch (UnsupportedEncodingException e1)
        {
            e1.printStackTrace();
        }
        entity.addPart("picturedata", new ByteArrayBody(data, "surfstoked.jpg"));

        ((HttpEntityEnclosingRequestBase) httppost).setEntity(entity); 

        String result = null;
        try
        {
            response = httpclient.execute(httppost);

            HttpEntity responseEntity = response.getEntity();
            if (entity != null)
            {
                InputStream instream = responseEntity.getContent();
                result = convertStreamToString(instream);
                instream.close();
            }
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return fixWrapper(result);
    }

    /**
     * Load serialized result from the server.
     */
    public static String loadSerializedDataFromServer(String url)
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;

        String result = null;
        try
        {
            response = httpclient.execute(httpget);

            HttpEntity entity = response.getEntity();
            if (entity != null)
            {
                InputStream instream = entity.getContent();
                result = convertStreamToString(instream);
                instream.close();
            }
        } catch (ClientProtocolException e)
        {
            e.printStackTrace();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return fixWrapper(result);
    }

    /**
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
    private static String convertStreamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8192);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line + "\n");
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                is.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    /**
     * Helper method that removes the first two characters of the returned JSON
     * string and the last character. This was added for security reasons.
     * Without this wrapper we cannot call the server methods with JSON JQuery
     * calls.
     */
    private static String fixWrapper(String jSONSerializedResources)
    {
        return jSONSerializedResources.substring(2, jSONSerializedResources.length() - 2);
    }
}

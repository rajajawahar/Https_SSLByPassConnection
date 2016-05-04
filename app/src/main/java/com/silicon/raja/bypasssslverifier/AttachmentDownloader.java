package com.silicon.raja.bypasssslverifier;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.Fragment;

import org.apache.http.conn.ssl.AllowAllHostnameVerifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

/**
 * Created by raja on 4/29/16.
 */
public class AttachmentDownloader extends AsyncTask<String, String, Boolean> {
    public String attachmenturl = "https://23.246.236.18/ATTACHMENTS/8F0D09B8-E27A-4225-8412-55834ADB54AC1462166695665.jpg";
    private ProgressDialog dialog;
    private String downloadingPath = Environment.getExternalStorageDirectory() + "/Http_ByPass/";
    private Context context;

    public AttachmentDownloader(Context context) {
        this.context = context;

    }

    @Override
    public void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("loading...");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {

        try {
            final String[] attachmentname = attachmenturl.split("/");
            final File downloadedpath = new File(downloadingPath);
            if (!downloadedpath.exists())
                downloadedpath.mkdir();
            File downloadedFile = new File(downloadedpath,
                    attachmentname[attachmentname.length - 1]);
            URL url = new URL(attachmenturl);

            HttpsURLConnection urlConnection =
                    (HttpsURLConnection) url.openConnection();
            if (urlConnection instanceof HttpsURLConnection) {
                urlConnection.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                urlConnection.setHostnameVerifier(hostnameVerifier);
//                To allow all the sites
//                urlConnection.setHostnameVerifier(new AllowAllHostnameVerifier());

            }
            urlConnection.connect();

            FileOutputStream fileOutput = new FileOutputStream(downloadedFile);
            InputStream inputStream = urlConnection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }
            fileOutput.close();
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        dialog.dismiss();


    }


    //To allow your sites only
    HostnameVerifier hostnameVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            HostnameVerifier hv =
                    HttpsURLConnection.getDefaultHostnameVerifier();
            return hv.verify("test.gsh.com", session);
        }
    };


}

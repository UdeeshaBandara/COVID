package com.MAD.healthapp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GetUrl {
    public String readURL (String myurl) throws IOException {
        String data="";
        InputStream inputstream = null;
        HttpURLConnection httpURLConnection= null;

        try{
            URL url=new URL(myurl);
            httpURLConnection=(HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            inputstream=httpURLConnection.getInputStream();
            BufferedReader buferreader=new BufferedReader(new InputStreamReader(inputstream));
            StringBuffer stringBuffer=new StringBuffer();

            String line="";
            while ((line=buferreader.readLine())!=null){
                stringBuffer.append(line);

            }

            data=stringBuffer.toString();
            buferreader.close();
        }catch (MalformedURLException e) {
            Log.i("DownloadUrl","readUrl"+e.getMessage());
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            inputstream.close();
            httpURLConnection.disconnect();
        }
        return data;
    }
}

package com.mrtechs.m_park;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



class HandlerHttp {
    private String response = null;
    public String handleresponse(String strUrl)
    {


        try {
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            InputStream iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb  = new StringBuilder();

            String line;
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            response = sb.toString();

            br.close();



            //DefaultHttpClient httpClient = new DefaultHttpClient();
            //HttpGet httpGet = new HttpGet(url);
            //HttpResponse httpResponse = httpClient.execute(httpGet);
            //HttpEntity httpEntity = httpResponse.getEntity();
            //response = EntityUtils.toString(httpEntity);

        }catch (IOException e) {
            e.printStackTrace();
        }


        return response;
    }
}

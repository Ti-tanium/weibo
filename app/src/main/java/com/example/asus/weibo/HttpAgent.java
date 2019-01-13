package com.example.asus.weibo;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;



public class HttpAgent {
    private static final String TAG = "HttpAgent";

    public static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public JSONObject fetchJSON(String url){
        try{
            String JSONString=getUrlString(url);
            Log.i(TAG,"received JSON:"+JSONString);
            JSONObject jsonObject=new JSONObject(JSONString);
            return jsonObject;
        }catch (IOException ioe){
            Log.e(TAG,"Failed to fetch JSON",ioe.fillInStackTrace());
        }catch (JSONException je){
            Log.e(TAG,"Failed to parse JSON",je);
        }
        return null;
    }


    /*
     * Function  :   发送Post请求到服务器
     * Param     :   params请求体内容，encode编码格式
     */
    public static JSONObject fetchJSON(String strUrlPath, Map<String, String> params, String encode) {

        String paramsString = parseParameters(params, encode);//获得请求体
        Log.i(TAG,"URL:"+strUrlPath);
        Log.i(TAG,"parameters:"+paramsString);
        try{
            String JSONString=getUrlString(strUrlPath+paramsString);
            Log.i(TAG,"received JSON:"+JSONString);
            JSONObject jsonObject=new JSONObject(JSONString);
            return jsonObject;
        }catch (IOException ioe){
            Log.e(TAG,"Failed to get response from the server:"+ioe);
        }catch (JSONException je){
            Log.e(TAG,"Failed to parse JSON:",je);
        }
        return null;
    }

    /*
     * Function  :   封装请求体信息
     * Param     :   params请求体内容，encode编码格式
     */
    private static String parseParameters(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
        stringBuffer.append("?");
        try {
            for(Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey())
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

}

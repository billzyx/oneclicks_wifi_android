package cn.oneclicks.wifi_school;

import java.io.DataOutputStream; 
import java.io.IOException; 
import java.io.InputStream; 
import java.net.HttpURLConnection; 
import java.net.SocketTimeoutException; 
import java.net.URL; 
import java.util.ArrayList; 
import java.util.HashMap; 
import java.util.Map; 
 
import org.apache.http.Header; 
import org.apache.http.HttpEntity; 
import org.apache.http.HttpResponse; 
import org.apache.http.HttpStatus; 
import org.apache.http.client.entity.UrlEncodedFormEntity; 
import org.apache.http.client.methods.HttpPost; 
import org.apache.http.impl.client.DefaultHttpClient; 
import org.apache.http.message.BasicHeader; 
import org.apache.http.message.BasicNameValuePair; 
import org.apache.http.params.CoreConnectionPNames; 
import org.apache.http.util.EntityUtils; 
 
import android.content.Context; 
import android.net.ConnectivityManager; 
import android.net.NetworkInfo; 
import android.util.Log; 
import android.widget.Toast; 
 
/**
 * HttpUtil Class Capsule Most Functions of Http Operations
 * 
 * @author sfshine
 * 
 */ 
public class HttpUtil { 
 
    private static Header[] headers = new BasicHeader[5];
    private static String TAG = "HTTPUTIL"; 
    private static int TIMEOUT =  10*1000; 
 
    /**
     * Your header of http op
     */ 
    static { 
        getHeaders()[0] = new BasicHeader("Pragma", "no-cache");
        getHeaders()[1] = new BasicHeader("Accept", "text/html, application/xhtml+xml, */*");
        getHeaders()[2] = new BasicHeader("Accept-Encoding", "gzip, deflate");
        getHeaders()[3] = new BasicHeader("Accept-Language", "zh-CN");
        getHeaders()[4] = new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko");


        
 
    } 
 
    /**
     * Op Http get request
     * 
     * @param url
     * @param map
     *            Values to request
     * @return
     */ 
 
    static public String get(String url, HashMap<String, String> map) { 
 
        int i = 0; 
        for (Map.Entry<String, String> entry : map.entrySet()) { 
 
            Log.i(TAG, entry.getKey() + "=>" + entry.getValue()); 
            if (i == 0) { 
                url = url + "?" + entry.getKey() + "=" + entry.getValue(); 
            } else { 
                url = url + "&" + entry.getKey() + "=" + entry.getValue(); 
            } 
 
            i++; 
 
        } 
        String reult = post(url, null); 
        return reult; 
 
    } 
 
    /**
     * Op Http post request , "404error" response if failed
     * 
     * @param url
     * @param map
     *            Values to request
     * @return
     */ 
 
    static public String post(String url, HashMap<String, String> map) { 
 
        DefaultHttpClient client = new DefaultHttpClient(); 
        client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIMEOUT);  
        client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, TIMEOUT); 
        HttpPost post = new HttpPost(url); 
        Log.i(TAG, url); 
        post.setHeaders(getHeaders());
        String result = "404error"; 
        ArrayList<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>(); 
        if (map != null) { 
            for (Map.Entry<String, String> entry : map.entrySet()) { 
                Log.i(TAG, entry.getKey() + "=>" + entry.getValue()); 
                BasicNameValuePair pair = new BasicNameValuePair( 
                        entry.getKey(), entry.getValue()); 
                pairList.add(pair); 
            } 
 
        } 
 
        try {  
            HttpEntity entity = new UrlEncodedFormEntity(pairList, "UTF-8"); 
            post.setEntity(entity); 
            HttpResponse response = client.execute(post); 
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { 
                //setCookie(response); 
                result = EntityUtils.toString(response.getEntity(), "UTF-8"); 
                Log.i(TAG, "result =>"+ result); 
                return result; 
            } 
        }  
        catch (Exception e) { 
             
        } 
         
        Log.e(TAG, result); 
        return result; 
    } 
 
    /**
     * Post Bytes to Server
     * @param url
     * @param bytes of text 
     * @return
     */ 
    public static String PostBytes(String url, byte[] bytes) { 
        try { 
            URL murl = new URL(url); 
            final HttpURLConnection con = (HttpURLConnection) murl 
                    .openConnection(); 
 
            con.setDoInput(true); 
            con.setDoOutput(true); 
            con.setUseCaches(false); 
 
            con.setRequestMethod("POST"); 
            con.setRequestProperty("Connection", "Keep-Alive"); 
            con.setRequestProperty("Charset", "UTF-8"); 
            con.setRequestProperty("Content-Type", "text/html"); 
            String cookie = getHeaders()[10].getValue();
            if (!isNull(getHeaders()[10].getValue())) {
                con.setRequestProperty("cookie", cookie); 
            } 
             
            con.setReadTimeout(TIMEOUT); 
            con.setConnectTimeout(TIMEOUT); 
            Log.i(TAG, url); 
            DataOutputStream dsDataOutputStream = new DataOutputStream( 
                    con.getOutputStream()); 
            dsDataOutputStream.write(bytes, 0, bytes.length); 
 
            dsDataOutputStream.close(); 
            if (con.getResponseCode() == HttpStatus.SC_OK) { 
                InputStream isInputStream = con.getInputStream(); 
                int ch; 
                StringBuffer buffer = new StringBuffer(); 
                while ((ch = isInputStream.read()) != -1) { 
                    buffer.append((char) ch); 
                } 
 
                Log.i(TAG, "GetDataFromServer>" + buffer.toString()); 
 
                return buffer.toString(); 
            } else { 
                return "404error"; 
            } 
        } catch (SocketTimeoutException  e) { 
            return "timeouterror"; 
        } catch (IOException e) { 
            // TODO Auto-generated catch block 
            return "404error"; 
        } 
    } 
 
    /**
     * set Cookie
     * 
     * @param response
     */ 
    private static void setCookie(HttpResponse response) { 
        if (response.getHeaders("Set-Cookie").length > 0) { 
            Log.d(TAG, response.getHeaders("Set-Cookie")[0].getValue()); 
            getHeaders()[10] = new BasicHeader("Cookie",
                    response.getHeaders("Set-Cookie")[0].getValue()); 
        } 
    } 
 
    /**
     * check net work
     * 
     * @param context
     * @return
     */ 
    public static boolean hasNetwork(Context context) { 
        ConnectivityManager con = (ConnectivityManager) context 
                .getSystemService(Context.CONNECTIVITY_SERVICE); 
        NetworkInfo workinfo = con.getActiveNetworkInfo(); 
        if (workinfo == null || !workinfo.isAvailable()) { 
            Toast.makeText(context, "当前无网络连接,请稍后重试", Toast.LENGTH_SHORT).show(); 
            return false; 
        } 
        return true; 
    } 
    /***
     * @category check if the string is null
     * @return true if is null
     * */ 
    public static boolean isNull(String string) { 
        boolean t1 = "".equals(string); 
        boolean t2 = string == null; 
        boolean t3 = string.equals("null"); 
        if (t1 || t2 || t3) { 
            return true; 
        } else { 
            return false; 
        } 
    }


    public static Header[] getHeaders() {
        return headers;
    }

    public static void setHeaders(Header[] headers) {
        HttpUtil.headers = headers;
    }
}

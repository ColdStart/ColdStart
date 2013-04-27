package io.coldstart.android;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class API 
{
	public static int SYNC_VERSION = 1;
	public static String SENDER_ID = "70012631542";
	static int API_VERSION = 1;
	
	DefaultHttpClient client;
  	ThreadSafeClientConnManager mgr;
  	DefaultHttpClient httpclient;
  	
  	
  	public API()
  	{
  		HttpParams params = new BasicHttpParams();
    	this.client = new DefaultHttpClient(); 
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

        this.mgr = new ThreadSafeClientConnManager(params, registry);
        this.httpclient = new DefaultHttpClient(mgr, client.getParams());
        
		//Timeout ----------------------------------
		HttpParams httpParameters = new BasicHttpParams(); 
		int timeoutConnection = 20000;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		int timeoutSocket = 30000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		httpclient.setParams(httpParameters);
		//Timeout ----------------------------------
          	
  	}
	
	
  	public String createGCMAccount(String EmailAddress, String Password, String gcmID, String deviceID) throws ClientProtocolException, IOException
	{
        HttpPost httpost = new HttpPost("http://api.coldstart.io/"+API.API_VERSION+"/register");

        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("useremail", EmailAddress));
        nvps.add(new BasicNameValuePair("gcmid", gcmID));
        nvps.add(new BasicNameValuePair("deviceid", deviceID));
       
        
        if(!Password.equals(""))
        	nvps.add(new BasicNameValuePair("password", API.md5(Password)));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		
		HttpResponse response = httpclient.execute(httpost);
        
        String rawJSON = EntityUtils.toString(response.getEntity());
        Log.e("rawJSON",rawJSON);
        response.getEntity().consumeContent();
		try 
		{
			JSONObject newAccountObject = new JSONObject(rawJSON);
			
			if(newAccountObject.has("apikey"))
			{
				return newAccountObject.getString("apikey");
			}
			else
			{
				return null;
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
  	
  	public boolean updateGCMAccount(String APIKey, String Password, String gcmID, String deviceID) throws ClientProtocolException, IOException
	{
        HttpPost httpost = new HttpPost("http://api.coldstart.io/"+API.API_VERSION+"/subscribe");

        
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("apikey", APIKey));
        nvps.add(new BasicNameValuePair("gcmid", gcmID));
        nvps.add(new BasicNameValuePair("deviceid", deviceID));
        
        if(!Password.equals(""))
        	nvps.add(new BasicNameValuePair("password", API.md5(Password)));


		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		
		HttpResponse response = httpclient.execute(httpost);
        
        String rawJSON = EntityUtils.toString(response.getEntity());
        response.getEntity().consumeContent();
        
        Log.i("APIKey",APIKey);
        Log.i("gcmID",gcmID);
        Log.i("Password", API.md5(Password));
        Log.i("rawJSON",rawJSON);
		try 
		{
			JSONObject subscribeObject = new JSONObject(rawJSON);
			
			if(subscribeObject.has("uuid"))
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
  	
  	
  	public boolean updateAccountSettings(String deviceID, boolean bundleAlerts, String bundleDelay) throws ClientProtocolException, IOException
	{
        HttpPost httpost = new HttpPost("http://api.coldstart.io/"+API.API_VERSION+"/settings");

        
        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("bundle", Boolean.toString(bundleAlerts)));
        nvps.add(new BasicNameValuePair("delay", bundleDelay));
        nvps.add(new BasicNameValuePair("deviceid", deviceID));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
		
		HttpResponse response = httpclient.execute(httpost);
        
        String rawJSON = EntityUtils.toString(response.getEntity());
        response.getEntity().consumeContent();
        
        Log.i("rawJSON",rawJSON);
		try 
		{
			JSONObject subscribeObject = new JSONObject(rawJSON);
			
			if(subscribeObject.has("uuid"))
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		catch (JSONException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
  	
	
	public static String md5(String s) 
	{
	    try 
	    {
	        // Create MD5 Hash
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes());
	        byte messageDigest[] = digest.digest();
	        
	        // Create Hex String
	        StringBuffer hexString = new StringBuffer();
	        for (int i=0; i<messageDigest.length; i++)
	            hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	        
	    } 
	    catch (NoSuchAlgorithmException e) 
	    {
	        e.printStackTrace();
	    }
	    return "";
	}
}

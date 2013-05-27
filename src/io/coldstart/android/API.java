/*
* Copyright (C) 2013 - Gareth Llewellyn
*
* This file is part of ColdStart.io - https://github.com/ColdStart/ColdStart
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>
*/

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
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class API
{
	public static int SYNC_VERSION = 1;
	public static String SENDER_ID = "70012631542";
	static int API_VERSION = 1;
    public static String BROADCAST_ACTION = "io.coldstart.android.broadcast.updateListUI";
    public static String ZENOSS_BROADCAST_ACTION = "com.zenoss.broadcast";

    public static String MSG_TRAP = "0";
    public static String MSG_GENERIC = "1";
    public static String MSG_BATCH = "2";
    public static String MSG_ZENOSS = "3";
    public static String MSG_RATELIMIT = "4";

	DefaultHttpClient client;
  	ThreadSafeClientConnManager mgr;
  	DefaultHttpClient httpclient;

  	public API()
  	{
  		HttpParams params = new BasicHttpParams();
    	this.client = new DefaultHttpClient(); 
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

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
        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/register");

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
        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/subscribe");

        
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

    public boolean ignoreBatch(String APIKey, String Password, String deviceID) throws ClientProtocolException, IOException
{
    HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/ignore");


    List <NameValuePair> nvps = new ArrayList <NameValuePair>();
    nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
    nvps.add(new BasicNameValuePair("apikey", APIKey));
    nvps.add(new BasicNameValuePair("deviceid", deviceID));

    if(!Password.equals(""))
        nvps.add(new BasicNameValuePair("password", API.md5(Password)));


    httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    HttpResponse response = httpclient.execute(httpost);

    String rawJSON = EntityUtils.toString(response.getEntity());
    response.getEntity().consumeContent();

    Log.i("APIKey",APIKey);
    Log.i("Password", API.md5(Password));
    Log.i("rawJSON",rawJSON);
    try
    {
        JSONObject ignoreObject = new JSONObject(rawJSON);

        if(ignoreObject.has("success") && ignoreObject.getBoolean("success"))
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

    public List<Trap> getBatch(String APIKey, String Password, String deviceID) throws ClientProtocolException, IOException
    {
        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/batch");


        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("apikey", APIKey));
        nvps.add(new BasicNameValuePair("deviceid", deviceID));

        if(!Password.equals(""))
            nvps.add(new BasicNameValuePair("password", API.md5(Password)));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(httpost);

        String rawJSON = EntityUtils.toString(response.getEntity());
        response.getEntity().consumeContent();

        Log.i("APIKey",APIKey);
        Log.i("Password", API.md5(Password));
        Log.i("rawJSON",rawJSON);
        List<Trap> returnTraps = new ArrayList<Trap>();

        try
        {
            JSONObject batchesObject = new JSONObject(rawJSON);

            if(batchesObject.has("success") && batchesObject.getBoolean("success"))
            {
                JSONArray jsonTraps = batchesObject.getJSONArray("traps");
                int i = jsonTraps.length();

                for(int x = 0; x < i; x++)
                {
                    try
                    {
                        JSONObject jsonTrap = jsonTraps.getJSONObject(x);
                        Trap trap = new Trap(jsonTrap.getString("hostname"),jsonTrap.getString("ip"));
                        trap.trap = jsonTrap.getString("payload");
                        trap.date = jsonTrap.getString("date");
                        returnTraps.add(trap);
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if(returnTraps.isEmpty())
                {
                    Log.e("getBatch","List<Trap> object was empty");
                    return null;
                }
                else
                {
                    return returnTraps;
                }
            }
            else
            {
                Log.e("getBatch","json fetched wasn't successful");
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



    public ColdStartHost scanRemoteHost(String APIKey, String remoteHost) throws ClientProtocolException, IOException
    {
        ColdStartHost host = new ColdStartHost();

        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/scan");


        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("apikey", APIKey));
        nvps.add(new BasicNameValuePair("remotehost", remoteHost));

        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(httpost);

        String rawJSON = EntityUtils.toString(response.getEntity());
        response.getEntity().consumeContent();

        Log.i("rawJSON",rawJSON);
        try
        {
            JSONObject scanObject = new JSONObject(rawJSON);

            if(scanObject.has("success") && scanObject.getBoolean("success"))
            {
                host.Contact = scanObject.getString("contact");
                host.Location = scanObject.getString("location");
                host.Description = scanObject.getString("description");
                host.IP = remoteHost;
                host.Error = false;
                return host;
            }
            else
            {
                host.ErrorMsg = scanObject.getString("error");
                host.Error = true;
                return host;
            }
        }
        catch (JSONException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    public boolean logoutGCMAccount(String APIKey, String Password, String deviceID) throws ClientProtocolException, IOException
    {
        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/logout");


        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("apikey", APIKey));
        nvps.add(new BasicNameValuePair("deviceid", deviceID));

        if(!Password.equals(""))
            nvps.add(new BasicNameValuePair("password", API.md5(Password)));


        httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

        HttpResponse response = httpclient.execute(httpost);

        String rawJSON = EntityUtils.toString(response.getEntity());
        response.getEntity().consumeContent();

        Log.i("APIKey",APIKey);
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


    public boolean submitOIDEdit(String OID, String Edit) throws ClientProtocolException, IOException
    {
        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/submitoidedit");


        List <NameValuePair> nvps = new ArrayList <NameValuePair>();
        nvps.add(new BasicNameValuePair("version", Integer.toString(API.API_VERSION)));
        nvps.add(new BasicNameValuePair("oid", OID));
        nvps.add(new BasicNameValuePair("description", Edit));

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
  	
  	public boolean updateAccountSettings(String deviceID, boolean bundleAlerts, String bundleDelay) throws ClientProtocolException, IOException
	{
        HttpPost httpost = new HttpPost("https://api.coldstart.io/"+API.API_VERSION+"/settings");

        
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

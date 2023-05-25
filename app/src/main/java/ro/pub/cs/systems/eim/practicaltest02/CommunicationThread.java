package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class CommunicationThread extends Thread{
    private final ServerThread serverThread;
    private final Socket socket;

    // Constructor of the thread, which takes a ServerThread and a Socket as parameters
    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.serverThread = serverThread;
        this.socket = socket;
    }

    @Override
    public void run() {
        // It first checks whether the socket is null, and if so, it logs an error and returns.
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
            return;
        }

        try {
            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (key / value!");
            String command = bufferedReader.readLine();
            HashMap<String, String> data = serverThread.getData();
            HashMap<String, Integer> expireDates = serverThread.getExpirationDates();
            if (!command.equals("put") && !command.equals("get")) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / value!");
                return;
            }
            if (command.equals("put")) {
                String key = bufferedReader.readLine();
                String value = bufferedReader.readLine();
                if (key == null || key.isEmpty() || value == null || value.isEmpty()) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / value!");
                    return;
                }
                String pageSourceCode = null;
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);
                JSONObject content = new JSONObject(pageSourceCode);
                String time = content.getString(Constants.DATETIME);
                int seconds = Integer.parseInt(time);
                serverThread.setData(key, value, seconds);
                printWriter.println("Success");
                printWriter.flush();
            } else {
                String key = bufferedReader.readLine();
                if (key == null || key.isEmpty()) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (key / value!");
                    return;
                }
                if (!data.containsKey(key)) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Key not found!");
                    return;
                }
                String pageSourceCode = null;
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.WEB_SERVICE_ADDRESS);
                HttpResponse httpGetResponse = httpClient.execute(httpGet);
                HttpEntity httpGetEntity = httpGetResponse.getEntity();
                if (httpGetEntity != null) {
                    pageSourceCode = EntityUtils.toString(httpGetEntity);
                }
                if (pageSourceCode == null) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting the information from the webservice!");
                    return;
                } else Log.i(Constants.TAG, pageSourceCode);
                JSONObject content = new JSONObject(pageSourceCode);
                String time = content.getString(Constants.DATETIME);
                int seconds =  Integer.parseInt(time);

                if (seconds - expireDates.get(key) <= 10) {
                    printWriter.println(data.get(key));
                    printWriter.flush();
                } else {
                    printWriter.println("Expired");
                    printWriter.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }
}

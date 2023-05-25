package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientThread extends Thread{
    private final String address;
    private final int port;
    private final String key;
    private final String value;
    private final String operation;

    private final TextView resultTextView;

    private Socket socket;
    public ClientThread(String address, int port, String key, String value, String operation,
                        TextView resultTextView) {
        this.address = address;
        this.port = port;
        this.key = key;
        this.value = value;
        this.operation = operation;
        this.resultTextView = resultTextView;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(address, port);

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);

            printWriter.println(operation);
            printWriter.flush();
            if (operation.equals("put")) {
                printWriter.println(key);
                printWriter.flush();
                printWriter.println(value);
                printWriter.flush();
            } else {
                printWriter.println(key);
                printWriter.flush();
            }
            String result;
            while ((result = bufferedReader.readLine()) != null) {
                final String finalizedResult = result;
                Log.d(Constants.TAG, "result: " + finalizedResult);
                resultTextView.post(() -> resultTextView.setText(finalizedResult));

            }
        }
         catch (IOException ioException) {
                Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                if (Constants.DEBUG) {
                    ioException.printStackTrace();
                }
        } finally {
            if (socket != null) {
                try {
                    // closes the socket regardless of errors or not
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}

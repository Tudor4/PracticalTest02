package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {
    private EditText serverPortEditText = null;
    private EditText clientAddressEditText = null;
    private EditText clientPortEditText = null;
    private EditText keyEditText = null;
    private EditText valueEditText = null;
    private ServerThread serverThread = null;
    private EditText resultEditText = null;

    private final ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            // Retrieves the server port. Checks if it is empty or not
            // Creates a new server thread with the port and starts it
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            if (serverThread.getServerSocket() == null) {
                Log.e(Constants.TAG, "[MAIN ACTIVITY] Could not create server thread!");
                return;
            }
            serverThread.start();
        }
    }

    private final PutButtonClickListener putButtonClickListerner = new PutButtonClickListener();
    private class PutButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            String key = keyEditText.getText().toString();
            String value = valueEditText.getText().toString();
            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), key, value, "put", resultEditText);
            clientThread.start();
        }
    }

    private final GetButtonClickListener getButtonClickListerner = new GetButtonClickListener();
    private class GetButtonClickListener implements Button.OnClickListener {
        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            String key = keyEditText.getText().toString();
            String value = valueEditText.getText().toString();
            ClientThread clientThread = new ClientThread(clientAddress, Integer.parseInt(clientPort), key, value, "get", resultEditText);
            clientThread.start();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practical_test02_main);

        serverPortEditText = (EditText)findViewById(R.id.server_port_edit_text);
        clientPortEditText = (EditText)findViewById(R.id.client_port_edit_text);
        clientAddressEditText = (EditText)findViewById(R.id.client_address_edit_text);
        keyEditText = (EditText)findViewById(R.id.key_edit_text);
        valueEditText = (EditText)findViewById(R.id.value_edit_text);
        resultEditText = (EditText)findViewById(R.id.result_edit_text);

        Button connectButton = (Button)findViewById(R.id.connect_button);
        connectButton.setOnClickListener(connectButtonClickListener);
        Button getButton = (Button)findViewById(R.id.get_button);
        getButton.setOnClickListener(getButtonClickListerner);
        Button putButton = (Button)findViewById(R.id.put_button);
        putButton.setOnClickListener(putButtonClickListerner);
    }

    @Override
    protected void onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked");
        if (serverThread != null) {
            serverThread.stopThread();
        }
        super.onDestroy();
    }
}
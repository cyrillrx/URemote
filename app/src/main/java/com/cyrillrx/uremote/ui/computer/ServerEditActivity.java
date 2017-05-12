package com.cyrillrx.uremote.ui.computer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cyrillrx.common.security.Md5;
import com.cyrillrx.logger.Logger;
import com.cyrillrx.uremote.R;
import com.cyrillrx.uremote.common.device.NetworkDevice;
import com.cyrillrx.uremote.common.device.NetworkDevice.ConnectionType;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_EDIT;
import static com.cyrillrx.uremote.utils.IntentKeys.ACTION_SAVE;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_ACTION;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static com.cyrillrx.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 *         Created on 07/06/13.
 */
public class ServerEditActivity extends AppCompatActivity {

    private static final String TAG = ServerEditActivity.class.getSimpleName();

    private EditText serverName;
    private EditText localHost1;
    private EditText localHost2;
    private EditText localHost3;
    private EditText localHost4;
    private EditText localPort;

    private EditText broadcast1;
    private EditText broadcast2;
    private EditText broadcast3;
    private EditText broadcast4;

    private EditText remoteHost1;
    private EditText remoteHost2;
    private EditText remoteHost3;
    private EditText remoteHost4;
    private EditText remotePort;

    private EditText macAddress1;
    private EditText macAddress2;
    private EditText macAddress3;
    private EditText macAddress4;
    private EditText macAddress5;
    private EditText macAddress6;

    private EditText connectionTimeout;
    private EditText readTimeout;
    private EditText securityToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_edit);

        serverName = (EditText) findViewById(R.id.server_name);

        localHost1 = (EditText) findViewById(R.id.local_host1);
        localHost2 = (EditText) findViewById(R.id.local_host2);
        localHost3 = (EditText) findViewById(R.id.local_host3);
        localHost4 = (EditText) findViewById(R.id.local_host4);
        localPort = (EditText) findViewById(R.id.local_port);

        broadcast1 = (EditText) findViewById(R.id.broadcast1);
        broadcast2 = (EditText) findViewById(R.id.broadcast2);
        broadcast3 = (EditText) findViewById(R.id.broadcast3);
        broadcast4 = (EditText) findViewById(R.id.broadcast4);

        remoteHost1 = (EditText) findViewById(R.id.remote_host1);
        remoteHost2 = (EditText) findViewById(R.id.remote_host2);
        remoteHost3 = (EditText) findViewById(R.id.remote_host3);
        remoteHost4 = (EditText) findViewById(R.id.remote_host4);
        remotePort = (EditText) findViewById(R.id.remote_port);

        macAddress1 = (EditText) findViewById(R.id.mac_address1);
        macAddress2 = (EditText) findViewById(R.id.mac_address2);
        macAddress3 = (EditText) findViewById(R.id.mac_address3);
        macAddress4 = (EditText) findViewById(R.id.mac_address4);
        macAddress5 = (EditText) findViewById(R.id.mac_address5);
        macAddress6 = (EditText) findViewById(R.id.mac_address6);

        connectionTimeout = (EditText) findViewById(R.id.connection_timeout);
        readTimeout = (EditText) findViewById(R.id.read_timeout);
        securityToken = (EditText) findViewById(R.id.security_token);

        macAddress1.addTextChangedListener(macAddressWatcher);
        macAddress2.addTextChangedListener(macAddressWatcher);
        macAddress3.addTextChangedListener(macAddressWatcher);
        macAddress4.addTextChangedListener(macAddressWatcher);
        macAddress5.addTextChangedListener(macAddressWatcher);
        macAddress6.addTextChangedListener(macAddressWatcher);

        localHost1.addTextChangedListener(ipAddressWatcher);
        localHost2.addTextChangedListener(ipAddressWatcher);
        localHost3.addTextChangedListener(ipAddressWatcher);
        localHost4.addTextChangedListener(ipAddressWatcher);

        broadcast1.addTextChangedListener(ipAddressWatcher);
        broadcast2.addTextChangedListener(ipAddressWatcher);
        broadcast3.addTextChangedListener(ipAddressWatcher);
        broadcast4.addTextChangedListener(ipAddressWatcher);

        remoteHost1.addTextChangedListener(ipAddressWatcher);
        remoteHost2.addTextChangedListener(ipAddressWatcher);
        remoteHost3.addTextChangedListener(ipAddressWatcher);
        remoteHost4.addTextChangedListener(ipAddressWatcher);

        if (ACTION_EDIT.equals(getIntent().getAction())) {
            loadServer(getIntent());
        }
    }

    @Override
    public void onBackPressed() {
        save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete:
                Intent deleteIntent = new Intent();
                deleteIntent.putExtra(EXTRA_SERVER_ACTION, ACTION_DELETE);
                deleteIntent.putExtra(EXTRA_SERVER_ID, getIntent().getIntExtra(EXTRA_SERVER_ID, -1));
                setResult(RESULT_OK, deleteIntent);
                finish();
                return true;

            case R.id.cancel:
                finish();
                return true;

            case R.id.done:
                save();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void loadServer(Intent data) {
        NetworkDevice device = data.getParcelableExtra(EXTRA_SERVER_DATA);
        if (device == null) {
            finishActivity(Activity.RESULT_CANCELED);
        }

        loadSimpleData(device);
        splitLocalHost(device);
        splitBroadcast(device);
        splitRemoteHost(device);
        splitMacAddress(device);
    }

    private void save() {
        NetworkDevice.Builder builder = NetworkDevice.newBuilder()
                .setName(getName())
                .setLocalHost(getLocalHost())
                .setLocalPort(getLocalPort())
                .setBroadcast(getBroadcast())
                .setRemoteHost(getRemoteHost())
                .setRemotePort(getRemotePort())
                .setMacAddress(getMacAddress())
                .setConnectionTimeout(getConnectionTimeout())
                .setReadTimeout(getReadTimeout())
                .setSecurityToken(getSecurityToken())
                .setConnectionType(getConnectionType());

        try {
            Intent saveIntent = new Intent();
            saveIntent.putExtra(EXTRA_SERVER_ACTION, ACTION_SAVE);
            saveIntent.putExtra(EXTRA_SERVER_DATA, builder.build());
            saveIntent.putExtra(EXTRA_SERVER_ID, getIntent().getIntExtra(EXTRA_SERVER_ID, -1));
            setResult(RESULT_OK, saveIntent);
            finish();
            Toast.makeText(getApplicationContext(), R.string.server_saved, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Logger.error(TAG, "#onOptionsItemSelected - Server creation has failed.", e);
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Null safe method to extract the text from a TextView.
     *
     * @param textView
     * @return The nested text.
     */
    private String extractString(final TextView textView) {

        final Editable editable = textView.getEditableText();
        if (editable != null) {
            return editable.toString();
        }
        return null;
    }

    /**
     * Null safe method to clear the tex of a TextView.
     *
     * @param textView The TextView to clear.
     */
    private void clearTextView(final TextView textView) {
        final Editable editable = textView.getEditableText();
        if (editable != null) {
            editable.clear();
        }
    }

    /**
     * Null safe method that joins TextViews nested text with a separator.
     * Convenience method to build ip and mac addresses.
     *
     * @param separator
     * @param textViews
     * @return
     */
    private String joinParts(char separator, TextView... textViews) {

        StringBuilder sb = new StringBuilder();
        for (TextView textView : textViews) {
            sb.append(extractString(textView));
            sb.append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void loadSimpleData(final NetworkDevice device) {
        serverName.setText(device.getName().trim());
        connectionTimeout.setText(String.valueOf(device.getConnectionTimeout()));
        readTimeout.setText(String.valueOf(device.getReadTimeout()));
    }

    private void splitLocalHost(final NetworkDevice device) {
        String parts[] = device.getLocalHost().split("\\.");
        localHost1.setText(parts[0]);
        localHost2.setText(parts[1]);
        localHost3.setText(parts[2]);
        localHost4.setText(parts[3]);

        localPort.setText(String.valueOf(device.getLocalPort()));
    }

    private void splitBroadcast(final NetworkDevice device) {
        String parts[] = device.getBroadcast().split("\\.");
        broadcast1.setText(parts[0]);
        broadcast2.setText(parts[1]);
        broadcast3.setText(parts[2]);
        broadcast4.setText(parts[3]);

        localPort.setText(String.valueOf(device.getLocalPort()));
    }

    private void splitRemoteHost(final NetworkDevice device) {
        String parts[] = device.getRemoteHost().split("\\.");
        remoteHost1.setText(parts[0]);
        remoteHost2.setText(parts[1]);
        remoteHost3.setText(parts[2]);
        remoteHost4.setText(parts[3]);

        remotePort.setText(String.valueOf(device.getRemotePort()));
    }

    private void splitMacAddress(final NetworkDevice device) {
        String parts[] = device.getMacAddress().split("-");
        macAddress1.setText(parts[0]);
        macAddress2.setText(parts[1]);
        macAddress3.setText(parts[2]);
        macAddress4.setText(parts[3]);
        macAddress5.setText(parts[4]);
        macAddress6.setText(parts[5]);
    }

    private String getName() { return extractString(serverName); }

    private String getLocalHost() {
        return joinParts('.', localHost1, localHost2, localHost3, localHost4);
    }

    private String getBroadcast() {
        return joinParts('.', broadcast1, broadcast2, broadcast3, broadcast4);
    }

    private int getLocalPort() { return Integer.valueOf(extractString(localPort)); }

    private String getRemoteHost() {
        return joinParts('.', remoteHost1, remoteHost2, remoteHost3, remoteHost4);
    }

    private int getRemotePort() { return Integer.valueOf(extractString(remotePort)); }

    private String getMacAddress() {
        return joinParts('-',
                macAddress1, macAddress2, macAddress3,
                macAddress4, macAddress5, macAddress6);
    }

    private int getConnectionTimeout() {
        return Integer.valueOf(extractString(connectionTimeout));
    }

    private int getReadTimeout() { return Integer.valueOf(extractString(readTimeout)); }

    /**
     * @return The encoded (MD5) security token.
     */
    private String getSecurityToken() { return Md5.encode(extractString(securityToken)); }

    private ConnectionType getConnectionType() {
        // TODO load from IHM
        return ConnectionType.LOCAL;
    }

    private final TextWatcher macAddressWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable editable) {

            if (editable.length() != 2) {
                return;
            }

            // Local host

            if (editable == macAddress1.getEditableText()) {
                clearTextView(macAddress2);
                macAddress2.requestFocus();

            } else if (editable == macAddress2.getEditableText()) {
                clearTextView(macAddress3);
                macAddress3.requestFocus();

            } else if (editable == macAddress3.getEditableText()) {
                clearTextView(macAddress4);
                macAddress4.requestFocus();

            } else if (editable == macAddress4.getEditableText()) {
                clearTextView(macAddress5);
                macAddress5.requestFocus();

            } else if (editable == macAddress5.getEditableText()) {
                clearTextView(macAddress6);
                macAddress6.requestFocus();

            } else if (editable == macAddress6.getEditableText()) {
                connectionTimeout.requestFocus();
            }
        }
    };

    private final TextWatcher ipAddressWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable editable) {

            if (editable.length() != 3) {
                return;
            }

            // Local host

            if (editable == localHost1.getEditableText()) {
                clearTextView(localHost2);
                localHost2.requestFocus();

            } else if (editable == localHost2.getEditableText()) {
                clearTextView(localHost3);
                localHost3.requestFocus();

            } else if (editable == localHost3.getEditableText()) {
                clearTextView(localHost4);
                localHost4.requestFocus();

            } else if (editable == localHost4.getEditableText()) {
                clearTextView(localPort);
                localPort.requestFocus();

                // Broadcast address

            } else if (editable == broadcast1.getEditableText()) {
                clearTextView(broadcast2);
                broadcast2.requestFocus();

            } else if (editable == broadcast2.getEditableText()) {
                clearTextView(broadcast3);
                broadcast3.requestFocus();

            } else if (editable == broadcast3.getEditableText()) {
                clearTextView(broadcast4);
                broadcast4.requestFocus();

            } else if (editable == broadcast4.getEditableText()) {
                clearTextView(remoteHost1);
                remoteHost1.requestFocus();

                // Remote host

            } else if (editable == remoteHost1.getEditableText()) {
                clearTextView(remoteHost2);
                remoteHost2.requestFocus();

            } else if (editable == remoteHost2.getEditableText()) {
                clearTextView(remoteHost3);
                remoteHost3.requestFocus();

            } else if (editable == remoteHost3.getEditableText()) {
                clearTextView(remoteHost4);
                remoteHost4.requestFocus();

            } else if (editable == remoteHost4.getEditableText()) {
                clearTextView(remotePort);
                remotePort.requestFocus();
            }
        }
    };
}

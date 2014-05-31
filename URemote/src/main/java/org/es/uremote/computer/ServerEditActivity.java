package org.es.uremote.computer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.es.security.Md5;
import org.es.uremote.R;
import org.es.uremote.device.NetworkDevice;
import org.es.uremote.device.NetworkDevice.ConnectionType;
import org.es.utils.Log;

import static android.content.Intent.ACTION_DELETE;
import static android.content.Intent.ACTION_EDIT;
import static org.es.uremote.utils.IntentKeys.ACTION_SAVE;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ACTION;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_DATA;
import static org.es.uremote.utils.IntentKeys.EXTRA_SERVER_ID;

/**
 * @author Cyril Leroux
 *         Created on 07/06/13.
 */
public class ServerEditActivity extends Activity {

    private static final String TAG = "EditServer";

    private EditText mServerName;
    private EditText mLocalHost1;
    private EditText mLocalHost2;
    private EditText mLocalHost3;
    private EditText mLocalHost4;
    private EditText mLocalPort;

    private EditText mBroadcast1;
    private EditText mBroadcast2;
    private EditText mBroadcast3;
    private EditText mBroadcast4;

    private EditText mRemoteHost1;
    private EditText mRemoteHost2;
    private EditText mRemoteHost3;
    private EditText mRemoteHost4;
    private EditText mRemotePort;

    private EditText mMacAddress1;
    private EditText mMacAddress2;
    private EditText mMacAddress3;
    private EditText mMacAddress4;
    private EditText mMacAddress5;
    private EditText mMacAddress6;

    private EditText mConnectionTimeout;
    private EditText mReadTimeout;
    private EditText mSecurityToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_edit);

        mServerName = (EditText) findViewById(R.id.server_name);

        mLocalHost1 = (EditText) findViewById(R.id.local_host1);
        mLocalHost2 = (EditText) findViewById(R.id.local_host2);
        mLocalHost3 = (EditText) findViewById(R.id.local_host3);
        mLocalHost4 = (EditText) findViewById(R.id.local_host4);
        mLocalPort = (EditText) findViewById(R.id.local_port);

        mBroadcast1 = (EditText) findViewById(R.id.broadcast1);
        mBroadcast2 = (EditText) findViewById(R.id.broadcast2);
        mBroadcast3 = (EditText) findViewById(R.id.broadcast3);
        mBroadcast4 = (EditText) findViewById(R.id.broadcast4);

        mRemoteHost1 = (EditText) findViewById(R.id.remote_host1);
        mRemoteHost2 = (EditText) findViewById(R.id.remote_host2);
        mRemoteHost3 = (EditText) findViewById(R.id.remote_host3);
        mRemoteHost4 = (EditText) findViewById(R.id.remote_host4);
        mRemotePort = (EditText) findViewById(R.id.remote_port);

        mMacAddress1 = (EditText) findViewById(R.id.mac_address1);
        mMacAddress2 = (EditText) findViewById(R.id.mac_address2);
        mMacAddress3 = (EditText) findViewById(R.id.mac_address3);
        mMacAddress4 = (EditText) findViewById(R.id.mac_address4);
        mMacAddress5 = (EditText) findViewById(R.id.mac_address5);
        mMacAddress6 = (EditText) findViewById(R.id.mac_address6);

        mConnectionTimeout = (EditText) findViewById(R.id.connection_timeout);
        mReadTimeout = (EditText) findViewById(R.id.read_timeout);
        mSecurityToken = (EditText) findViewById(R.id.security_token);

        mMacAddress1.addTextChangedListener(mMacAddressWatcher);
        mMacAddress2.addTextChangedListener(mMacAddressWatcher);
        mMacAddress3.addTextChangedListener(mMacAddressWatcher);
        mMacAddress4.addTextChangedListener(mMacAddressWatcher);
        mMacAddress5.addTextChangedListener(mMacAddressWatcher);
        mMacAddress6.addTextChangedListener(mMacAddressWatcher);

        mLocalHost1.addTextChangedListener(mIpAddressWatcher);
        mLocalHost2.addTextChangedListener(mIpAddressWatcher);
        mLocalHost3.addTextChangedListener(mIpAddressWatcher);
        mLocalHost4.addTextChangedListener(mIpAddressWatcher);

        mBroadcast1.addTextChangedListener(mIpAddressWatcher);
        mBroadcast2.addTextChangedListener(mIpAddressWatcher);
        mBroadcast3.addTextChangedListener(mIpAddressWatcher);
        mBroadcast4.addTextChangedListener(mIpAddressWatcher);

        mRemoteHost1.addTextChangedListener(mIpAddressWatcher);
        mRemoteHost2.addTextChangedListener(mIpAddressWatcher);
        mRemoteHost3.addTextChangedListener(mIpAddressWatcher);
        mRemoteHost4.addTextChangedListener(mIpAddressWatcher);

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
            Log.error(TAG, "#onOptionsItemSelected - Server creation has failed.", e);
            Toast.makeText(getApplicationContext(), "Server creation has failed.", Toast.LENGTH_SHORT).show();
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
        mServerName.setText(device.getName().trim());
        mConnectionTimeout.setText(String.valueOf(device.getConnectionTimeout()));
        mReadTimeout.setText(String.valueOf(device.getReadTimeout()));
    }

    private void splitLocalHost(final NetworkDevice device) {
        String parts[] = device.getLocalHost().split("\\.");
        mLocalHost1.setText(parts[0]);
        mLocalHost2.setText(parts[1]);
        mLocalHost3.setText(parts[2]);
        mLocalHost4.setText(parts[3]);

        mLocalPort.setText(String.valueOf(device.getLocalPort()));
    }

    private void splitBroadcast(final NetworkDevice device) {
        String parts[] = device.getBroadcast().split("\\.");
        mBroadcast1.setText(parts[0]);
        mBroadcast2.setText(parts[1]);
        mBroadcast3.setText(parts[2]);
        mBroadcast4.setText(parts[3]);

        mLocalPort.setText(String.valueOf(device.getLocalPort()));
    }

    private void splitRemoteHost(final NetworkDevice device) {
        String parts[] = device.getRemoteHost().split("\\.");
        mRemoteHost1.setText(parts[0]);
        mRemoteHost2.setText(parts[1]);
        mRemoteHost3.setText(parts[2]);
        mRemoteHost4.setText(parts[3]);

        mRemotePort.setText(String.valueOf(device.getRemotePort()));
    }

    private void splitMacAddress(final NetworkDevice device) {
        String parts[] = device.getMacAddress().split("-");
        mMacAddress1.setText(parts[0]);
        mMacAddress2.setText(parts[1]);
        mMacAddress3.setText(parts[2]);
        mMacAddress4.setText(parts[3]);
        mMacAddress5.setText(parts[4]);
        mMacAddress6.setText(parts[5]);
    }

    private String getName() {
        return extractString(mServerName);
    }

    private String getLocalHost() {
        return joinParts('.', mLocalHost1, mLocalHost2, mLocalHost3, mLocalHost4);
    }

    private String getBroadcast() {
        return joinParts('.', mBroadcast1, mBroadcast2, mBroadcast3, mBroadcast4);
    }

    private int getLocalPort() {
        return Integer.valueOf(extractString(mLocalPort));
    }

    private String getRemoteHost() {
        return joinParts('.', mRemoteHost1, mRemoteHost2, mRemoteHost3, mRemoteHost4);
    }

    private int getRemotePort() {
        return Integer.valueOf(extractString(mRemotePort));
    }

    private String getMacAddress() {
        return joinParts('-',
                mMacAddress1, mMacAddress2, mMacAddress3,
                mMacAddress4, mMacAddress5, mMacAddress6);
    }

    private int getConnectionTimeout() {
        return Integer.valueOf(extractString(mConnectionTimeout));
    }

    private int getReadTimeout() {
        return Integer.valueOf(extractString(mReadTimeout));
    }

    /**
     * @return The encoded (MD5) security token.
     */
    private String getSecurityToken() {
        return Md5.encode(extractString(mSecurityToken));
    }

    private ConnectionType getConnectionType() {
        // TODO load from IHM
        return ConnectionType.LOCAL;
    }

    private final TextWatcher mMacAddressWatcher = new TextWatcher() {
        
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
        
        @Override
        public void afterTextChanged(Editable editable) {

            if (editable.length() != 2) {
                return;
            }

            // Local host

            if (editable == mMacAddress1.getEditableText()) {
                clearTextView(mMacAddress2);
                mMacAddress2.requestFocus();

            } else if (editable == mMacAddress2.getEditableText()) {
                clearTextView(mMacAddress3);
                mMacAddress3.requestFocus();

            } else if (editable == mMacAddress3.getEditableText()) {
                clearTextView(mMacAddress4);
                mMacAddress4.requestFocus();

            } else if (editable == mMacAddress4.getEditableText()) {
                clearTextView(mMacAddress5);
                mMacAddress5.requestFocus();

            } else if (editable == mMacAddress5.getEditableText()) {
                clearTextView(mMacAddress6);
                mMacAddress6.requestFocus();

            } else if (editable == mMacAddress6.getEditableText()) {
                mConnectionTimeout.requestFocus();
            }
        }
    };
    
    private final TextWatcher mIpAddressWatcher = new TextWatcher() {

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable editable) {

            if (editable.length() != 3) {
                return;
            }

            // Local host

            if (editable == mLocalHost1.getEditableText()) {
                clearTextView(mLocalHost2);
                mLocalHost2.requestFocus();

            } else if (editable == mLocalHost2.getEditableText()) {
                clearTextView(mLocalHost3);
                mLocalHost3.requestFocus();

            } else if (editable == mLocalHost3.getEditableText()) {
                clearTextView(mLocalHost4);
                mLocalHost4.requestFocus();

            } else if (editable == mLocalHost4.getEditableText()) {
                clearTextView(mLocalPort);
                mLocalPort.requestFocus();

                // Broadcast address

            } else if (editable == mBroadcast1.getEditableText()) {
                clearTextView(mBroadcast2);
                mBroadcast2.requestFocus();

            } else if (editable == mBroadcast2.getEditableText()) {
                clearTextView(mBroadcast3);
                mBroadcast3.requestFocus();

            } else if (editable == mBroadcast3.getEditableText()) {
                clearTextView(mBroadcast4);
                mBroadcast4.requestFocus();

            } else if (editable == mBroadcast4.getEditableText()) {
                clearTextView(mRemoteHost1);
                mRemoteHost1.requestFocus();

                // Remote host

            } else if (editable == mRemoteHost1.getEditableText()) {
                clearTextView(mRemoteHost2);
                mRemoteHost2.requestFocus();

            } else if (editable == mRemoteHost2.getEditableText()) {
                clearTextView(mRemoteHost3);
                mRemoteHost3.requestFocus();

            } else if (editable == mRemoteHost3.getEditableText()) {
                clearTextView(mRemoteHost4);
                mRemoteHost4.requestFocus();

            } else if (editable == mRemoteHost4.getEditableText()) {
                clearTextView(mRemotePort);
                mRemotePort.requestFocus();
            }
        }
    };
}

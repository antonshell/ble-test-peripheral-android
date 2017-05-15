/*
 * Copyright 2015 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.webbluetoothcg.bletestperipheral;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import java.util.UUID;


public class BleuServiceFragment extends ServiceFragment implements View.OnClickListener {
    private final static String TAG = "Custom";

    //private static final UUID BATTERY_SERVICE_UUID = UUID.fromString("00431C4A-A7A4-428B-A96D-D92D43C8C7CF");
    //private static final UUID BATTERY_LEVEL_UUID = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");

    //private static final UUID CLIENTS_SERVICE = UUID.fromString("00431C4A-A7A4-428B-A96D-D92D43C8C7CF");
    private static final UUID CLIENTS_SERVICE = UUID.fromString("00431C4A-A7A4-428B-A96D-D92D43C87777");

    //private static final UUID CLIENT1_SERVICE = UUID.fromString("00431C4A-A7A4-428B-A96D-D92D43C8C7CF");
    private static final UUID CLIENT1_TX = UUID.fromString("F1B41CDE-DBF5-4ACF-8679-ECB8B4DCA6FF");
    private static final UUID CLIENT1_RX = UUID.fromString("F1B41CDE-DBF5-4ACF-8679-ECB8B4DCA6AA");

    //private static final UUID CLIENT2_SERVICE = UUID.fromString("00431C4A-A7A4-428B-A96D-D92D43C8C7CF");
    private static final UUID CLIENT2_TX = UUID.fromString("F1B41CDE-DBF5-4ACF-8679-ECB8B4DCA6BB");
    private static final UUID CLIENT2_RX = UUID.fromString("F1B41CDE-DBF5-4ACF-8679-ECB8B4DCA6EE");

    private static final int INITIAL_BATTERY_LEVEL = 50;
    private static final int BATTERY_LEVEL_MAX = 100;

    private static final String BATTERY_LEVEL_DESCRIPTION = "The current charge level of a " +
            "battery. 100% represents fully charged while 0% represents fully discharged.";

    private Button charsDebugBtn;

    private ServiceFragmentDelegate mDelegate;

    // UI
    View view;

    private EditText mBatteryLevelEditText;

    private EditText client1_tx_value;
    private EditText client1_rx_value;
    private EditText client2_tx_value;
    private EditText client2_rx_value;

    private Button client1_tx_notify;
    private Button client1_rx_notify;
    private Button client2_tx_notify;
    private Button client2_rx_notify;

    private final OnEditorActionListener mOnEditorActionListener = new OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
            Log.i(TAG, "onEditorAction");
            return false;
        }
    };

    /*TextWatcher textWatcher = new TextWatcher() {
        public void afterTextChanged(Editable s) {
            Log.i(TAG, "afterTextChanged");

            if (s == client1_tx_value.getEditableText()) {
                // DO STH
            }

            if (s == client1_rx_value.getEditableText()) {
                // DO STH
            }

            if (s == client2_tx_value.getEditableText()) {
                // DO STH
            }

            if (s. == client2_rx_value.getEditableText()) {
                // DO STH
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    };*/

    private final OnClickListener mCharsDebugListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            int c1tx = mClient1TXCharacteristic.getValue()[0];
            int c1rx = mClient1RXCharacteristic.getValue()[0];
            int c2tx = mClient2TXCharacteristic.getValue()[0];
            int c2rx = mClient2RXCharacteristic.getValue()[0];

            Log.i(TAG, " ### Chars Debug Listener ###");
            Log.i(TAG, " ### CLIENT1_TX: " + String.valueOf(c1tx));
            Log.i(TAG, " ### CLIENT1_RX: " + String.valueOf(c1rx));
            Log.i(TAG, " ### CLIENT2_TX: " + String.valueOf(c2tx));
            Log.i(TAG, " ### CLIENT2_RX: " + String.valueOf(c2rx));
        }
    };

    private final OnClickListener mNotifyButtonListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "sendNotificationToDevices");
            //mDelegate.sendNotificationToDevices(mBatteryLevelCharacteristic);
        }
    };

    // GATT
    private BluetoothGattService mBleuService;
    private BluetoothGattCharacteristic mBatteryLevelCharacteristic;

    private BluetoothGattCharacteristic mClient1TXCharacteristic;
    private BluetoothGattCharacteristic mClient1RXCharacteristic;

    private BluetoothGattCharacteristic mClient2TXCharacteristic;
    private BluetoothGattCharacteristic mClient2RXCharacteristic;

    public BleuServiceFragment() {
        initCharacteristics();
    }

    private void initCharacteristics() {
        mBleuService = new BluetoothGattService(CLIENTS_SERVICE, BluetoothGattService.SERVICE_TYPE_PRIMARY);

        mClient1TXCharacteristic =
                new BluetoothGattCharacteristic(CLIENT1_TX,
                        BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_WRITE);
        mClient1TXCharacteristic.setValue(11, BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);
        //mClient1TXCharacteristic.setWriteType(BluetoothGattCharacteristic.PERMISSION_WRITE);

        mClient1RXCharacteristic =
                new BluetoothGattCharacteristic(CLIENT1_RX,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ);
        mClient1RXCharacteristic.setValue(22, BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);

        mClient2TXCharacteristic =
                new BluetoothGattCharacteristic(CLIENT2_TX,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ);
        mClient2TXCharacteristic.setValue(33, BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);
        //mClient2TXCharacteristic.setWriteType(BluetoothGattCharacteristic.PERMISSION_WRITE);

        mClient2RXCharacteristic =
                new BluetoothGattCharacteristic(CLIENT2_RX,
                        BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                        BluetoothGattCharacteristic.PERMISSION_READ);
        mClient2RXCharacteristic.setValue(44, BluetoothGattCharacteristic.FORMAT_UINT8, /* offset */ 0);

        mBleuService.addCharacteristic(mClient1TXCharacteristic);
        mBleuService.addCharacteristic(mClient1RXCharacteristic);
        mBleuService.addCharacteristic(mClient2TXCharacteristic);
        mBleuService.addCharacteristic(mClient2RXCharacteristic);
    }


    // Lifecycle callbacks
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //View view = inflater.inflate(R.layout.fragment_bleu, container, false);
        view = inflater.inflate(R.layout.fragment_bleu, container, false);

        charsDebugBtn = (Button) view.findViewById(R.id.chars_debug_btn);
        charsDebugBtn.setOnClickListener(mCharsDebugListener);

        client1_tx_value = (EditText) view.findViewById(R.id.client1_tx_value);
        client1_rx_value = (EditText) view.findViewById(R.id.client1_rx_value);
        client2_tx_value = (EditText) view.findViewById(R.id.client2_tx_value);
        client2_rx_value = (EditText) view.findViewById(R.id.client2_rx_value);

        client1_tx_notify = (Button) view.findViewById(R.id.client1_tx_notify);
        client1_rx_notify = (Button) view.findViewById(R.id.client1_rx_notify);
        client2_tx_notify = (Button) view.findViewById(R.id.client2_tx_notify);
        client2_rx_notify = (Button) view.findViewById(R.id.client2_rx_notify);

        client1_tx_value.setText(getCharTextValue(mClient1TXCharacteristic));
        client1_rx_value.setText(getCharTextValue(mClient1RXCharacteristic));
        client2_tx_value.setText(getCharTextValue(mClient2TXCharacteristic));
        client2_rx_value.setText(getCharTextValue(mClient2RXCharacteristic));

        /*client1_tx_value.setOnEditorActionListener(mOnEditorActionListener);
        client1_rx_value.setOnEditorActionListener(mOnEditorActionListener);
        client2_tx_value.setOnEditorActionListener(mOnEditorActionListener);
        client2_rx_value.setOnEditorActionListener(mOnEditorActionListener);*/

        /*client1_tx_value.addTextChangedListener(textWatcher);
        client1_rx_value.addTextChangedListener(textWatcher);
        client2_tx_value.addTextChangedListener(textWatcher);
        client2_rx_value.addTextChangedListener(textWatcher);*/

        client1_tx_notify.setOnClickListener(this);
        client1_rx_notify.setOnClickListener(this);
        client2_tx_notify.setOnClickListener(this);
        client2_rx_notify.setOnClickListener(this);

    /*mBatteryLevelEditText = (EditText) view.findViewById(R.id.textView_batteryLevel);
    mBatteryLevelEditText.setOnEditorActionListener(mOnEditorActionListener);
    mBatteryLevelSeekBar = (SeekBar) view.findViewById(R.id.seekBar_batteryLevel);
    mBatteryLevelSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
    Button notifyButton = (Button) view.findViewById(R.id.button_batteryLevelNotify);
    notifyButton.setOnClickListener(mNotifyButtonListener);*/

        setBatteryLevel(INITIAL_BATTERY_LEVEL, null);

        return view;
    }

    @Override
    public void onClick(View v) {
        String str;
        byte[] value;

        switch (v.getId()) {
            case R.id.client1_tx_notify:
                str = client1_tx_value.getText().toString();
                value = new byte[1];
                value[0] = (byte) Integer.parseInt(str);
                mClient1TXCharacteristic.setValue(value);
                mDelegate.sendNotificationToDevices(mClient1TXCharacteristic);
                break;
            case R.id.client1_rx_notify:
                str = client1_rx_value.getText().toString();
                value = new byte[1];
                value[0] = (byte) Integer.parseInt(str);
                mClient1RXCharacteristic.setValue(value);
                mDelegate.sendNotificationToDevices(mClient1RXCharacteristic);
                break;
            case R.id.client2_tx_notify:
                str = client2_tx_value.getText().toString();
                value = new byte[1];
                value[0] = (byte) Integer.parseInt(str);
                mClient2TXCharacteristic.setValue(value);
                mDelegate.sendNotificationToDevices(mClient2TXCharacteristic);
                break;
            case R.id.client2_rx_notify:
                str = client2_rx_value.getText().toString();
                value = new byte[1];
                value[0] = (byte) Integer.parseInt(str);
                mClient2RXCharacteristic.setValue(value);
                mDelegate.sendNotificationToDevices(mClient2RXCharacteristic);
                break;
        }
    }

    public String getCharTextValue(BluetoothGattCharacteristic characteristic){
        byte[] bytes = characteristic.getValue();
        int value = bytes[0];
        return String.valueOf(value);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mDelegate = (ServiceFragmentDelegate) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ServiceFragmentDelegate");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mDelegate = null;
    }

    public BluetoothGattService getBluetoothGattService() {
        return mBleuService;
    }

    @Override
    public ParcelUuid getServiceUUID() {
        return new ParcelUuid(CLIENTS_SERVICE);
    }

    private void setBatteryLevel(int newBatteryLevel, View source) {
        Log.i(TAG, "setBatteryLevel");
    }

    @Override
    public void notificationsEnabled(BluetoothGattCharacteristic characteristic, boolean indicate) {
        Log.i(TAG, "notificationsEnabled");
    }

    @Override
    public void notificationsDisabled(BluetoothGattCharacteristic characteristic) {
        Log.i(TAG, "notificationsDisabled");
    }

    @Override
    public int writeCharacteristic(BluetoothGattCharacteristic characteristic, int offset, byte[] value) {
        Log.i(TAG, "### ########### ###");
        Log.i(TAG, "writeCharacteristic");
        Log.i(TAG, "### ########### ###");

        final String valueStr = String.valueOf(value[0]);

        if(characteristic.getUuid() == CLIENT1_TX){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    client1_tx_value.setText(valueStr);
                }
            });
        }

        if(characteristic.getUuid() == CLIENT1_RX){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    client1_rx_value.setText(valueStr);
                }
            });
        }

        if(characteristic.getUuid() == CLIENT2_TX){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    client2_tx_value.setText(valueStr);
                }
            });
        }

        if(characteristic.getUuid() == CLIENT2_RX){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    client2_rx_value.setText(valueStr);
                }
            });
        }

        characteristic.setValue(value);
        return 1;
    };
}

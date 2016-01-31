package net.seip.botwatch;

import android.bluetooth.*;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import java.util.Set;

public class BluetoothDevicePreference extends ListPreferenceWithSummary {

    public BluetoothDevicePreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        int LegoDevicesFound = 0;

        BluetoothAdapter bta = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bta.getBondedDevices();

        for (BluetoothDevice dev : pairedDevices) {
            if (dev.getAddress().startsWith(BTCommunicator.OUI_LEGO)) {
                LegoDevicesFound++;
            }
        }

        if (LegoDevicesFound > 0) {
            CharSequence[] entries = new CharSequence[LegoDevicesFound];
            CharSequence[] entryValues = new CharSequence[LegoDevicesFound];
            int i = 0;
            for (BluetoothDevice dev : pairedDevices) {
                if (dev.getAddress().startsWith(BTCommunicator.OUI_LEGO)) {
                    entries[i] = dev.getName() + " " + dev.getAddress();
                    entryValues[i] = dev.getAddress();
                    i++;
                }
            }
            setEntries(entries);
            setEntryValues(entryValues);
        }
    }

    public BluetoothDevicePreference(Context context) {
        this(context, null);
    }

}
/*
 * Copyright (c) 2015, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package no.nordicsemi.android.sdr.viewmodels;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;
import android.util.Log;

import no.nordicsemi.android.ble.utils.ILogger;
import no.nordicsemi.android.blinky.R;
import no.nordicsemi.android.sdr.adapter.ExtendedBluetoothDevice;
import no.nordicsemi.android.sdr.profile.BlinkyManager;
import no.nordicsemi.android.sdr.profile.BlinkyManagerCallbacks;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

import static no.nordicsemi.android.log.Logger.log;


public class BleViewModel extends AndroidViewModel implements BlinkyManagerCallbacks, ILogger {
	private final BlinkyManager mBlinkyManager;

	private static final String TAG = "test";

	// Connection states Connecting, Connected, Disconnecting, Disconnected etc.
	private final MutableLiveData<String> mConnectionState = new MutableLiveData<>();

	// Flag to determine if the device is connected
	private final MutableLiveData<Boolean> mIsConnected = new MutableLiveData<>();

	// Flag to determine if the device has required services
	private final SingleLiveEvent<Boolean> mIsSupported = new SingleLiveEvent<>();

	private final MutableLiveData<Boolean> mIsTXsent = new MutableLiveData<>();

	// Flag to determine if the device is ready
	private final MutableLiveData<Void> mOnDeviceReady = new MutableLiveData<>();

	// Flag that holds the on off state of the LED. On is true, Off is False
	private final MutableLiveData<String> mTXdata = new MutableLiveData<>();

	// Flag that holds the pressed released state of the button on the devkit. Pressed is true, Released is False
	private final MutableLiveData<String> mRXdata = new MutableLiveData<>();

	public LiveData<Void> isDeviceReady() {
		return mOnDeviceReady;
	}

	public LiveData<String> getConnectionState() {
		return mConnectionState;
	}

	public LiveData<Boolean> isConnected() {
		return mIsConnected;
	}

	public LiveData<Boolean> isTXsent() {
		return mIsTXsent;
	}

	public LiveData<Boolean> isSupported() {
		return mIsSupported;
	}

	public LiveData<String> getUartData() {
		return mRXdata;
	}

	public LiveData<String> sendUartData() {
		return mTXdata;
	}

	public BleViewModel(@NonNull final Application application) {
		super(application);

		// Initialize the manager
		mBlinkyManager = new BlinkyManager(getApplication());
		mBlinkyManager.setGattCallbacks(this);
	}

	/**
	 * Connect to peripheral
	 */
	public void connect(final ExtendedBluetoothDevice device) {
		final LogSession logSession = Logger.newSession(getApplication(), null, device.getAddress(), device.getName());
		mBlinkyManager.setLogger(logSession);
		mBlinkyManager.connect(device.getDevice());

	}
	/**
	 * Disconnect from peripheral
	 */
	private void disconnect() {
		mBlinkyManager.disconnect();
	}

	public void sendTX(final String msg) {
		mBlinkyManager.send(msg);
		mTXdata.setValue(msg);
		mIsTXsent.setValue(false);


	}

	@Override
	protected void onCleared() {
		super.onCleared();
		if (mBlinkyManager.isConnected()) {
			disconnect();
		}
	}

	@Override
	public void onDataReceived(final String state) {
		mRXdata.postValue(state);
		Log.d(TAG, "RX: " + state);
		//Log.d(TAG, "onDataReceived: Сброс");
	}

	@Override
	public void onDataSent(final String state) {
		//mTXdata.postValue(state);
		mIsTXsent.postValue(true);
		Log.d("test", "TX: " + state);

		//Logger.a(testLogSession, "send something");
	}

	@Override
	public void onDeviceConnecting(final BluetoothDevice device) {
		mConnectionState.postValue(getApplication().getString(R.string.state_connecting));
	}

	@Override
	public void onDeviceConnected(final BluetoothDevice device) {
		mIsConnected.postValue(true);
		mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services));
	}

	@Override
	public void onDeviceDisconnecting(final BluetoothDevice device) {
		mIsConnected.postValue(false);
	}

	@Override
	public void onDeviceDisconnected(final BluetoothDevice device) {
		mIsConnected.postValue(false);
	}

	@Override
	public void onLinklossOccur(final BluetoothDevice device) {
		mIsConnected.postValue(false);
	}

	@Override
	public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
		mConnectionState.postValue(getApplication().getString(R.string.state_initializing));
	}

	@Override
	public void onDeviceReady(final BluetoothDevice device) {
		mIsSupported.postValue(true);
		mConnectionState.postValue(getApplication().getString(R.string.state_discovering_services_completed));  //, device.getName()));

		mOnDeviceReady.postValue(null);
	}

	@Override
	public boolean shouldEnableBatteryLevelNotifications(final BluetoothDevice device) {
		// Blinky doesn't have Battery Service
		return false;
	}

	@Override
	public void onBatteryValueReceived(final BluetoothDevice device, final int value) {
		// Blinky doesn't have Battery Service
	}

	@Override
	public void onBondingRequired(final BluetoothDevice device) {
		// Blinky does not require bonding
	}

	@Override
	public void onBonded(final BluetoothDevice device) {
		// Blinky does not require bonding
	}

	@Override
	public void onError(final BluetoothDevice device, final String message, final int errorCode) {

	}

	@Override
	public void onDeviceNotSupported(final BluetoothDevice device) {
		mIsSupported.postValue(false);
	}

	@Override
	public void log(int level, @NonNull String message) {

	}

	@Override
	public void log(int level, int messageRes, Object... params) {

	}
}

package com.example.progmprojet

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.ChannelListener
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.progmprojet.DeviceListFragment.DeviceActionListener

class WifiDirectActivity : AppCompatActivity(), ChannelListener, DeviceActionListener {
    private var isWifiP2pEnabled = false
    private var retryChannel = false
    private val intentFilter = IntentFilter()
    var channel: WifiP2pManager.Channel? = null
    var manager: WifiP2pManager? = null
    private var receiver: MyReceiver? = null

    /**
     * @param isWifiP2pEnabled the isWifiP2pEnabled to set
     */
    fun setIsWifiP2pEnabled(isWifiP2pEnabled: Boolean) {
        this.isWifiP2pEnabled = isWifiP2pEnabled
    }

    private fun initP2p(): Boolean {
        // Device capability definition check
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_WIFI_DIRECT)) {
            Log.e(TAG, "Wi-Fi Direct is not supported by this device.")
            return false
        }
        Log.d(TAG, "Wi-Fi Direct is supported by this device.")
        // Hardware capability check
        val wifiManager = applicationContext.getSystemService(WIFI_SERVICE) as WifiManager
        if (wifiManager == null) {
            Log.e(TAG, "Cannot get Wi-Fi system service.")
            return false
        }
        Log.d(TAG, "Wi-Fi system service retrieved.")
        if (!wifiManager.isP2pSupported) {
            Log.e(TAG, "Wi-Fi Direct is not supported by the hardware or Wi-Fi is off.")
            return false
        }
        Log.d(TAG, "Wi-Fi Direct is supported by the hardware and Wi-Fi is on.")
        manager = getSystemService(WIFI_P2P_SERVICE) as WifiP2pManager
        if (manager == null) {
            Log.e(TAG, "Cannot get Wi-Fi Direct system service.")
            return false
        }
        Log.d(TAG, "Wi-Fi Direct system service retrieved.")
        channel = manager!!.initialize(this, mainLooper, null)
        if (channel == null) {
            Log.e(TAG, "Cannot initialize Wi-Fi Direct.")
            return false
        }
        Log.d(TAG, "WiFi Direct initialization successful!")
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION -> if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Log.e(TAG, "Fine location permission is not granted!")
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_direct)

        // Indicates a change in the Wi-Fi Direct status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
        // Indicates the state of Wi-Fi Direct connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
        if (!initP2p()) {
            finish()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION
            )
            // After this point you wait for callback in
            // onRequestPermissionsResult(int, String[], int[]) overridden method
        }
    }

    /** register the BroadcastReceiver with the intent values to be matched  */
    public override fun onResume() {
        super.onResume()
        receiver = MyReceiver(manager, channel!!, this)
        registerReceiver(receiver, intentFilter)
    }

    public override fun onPause() {
        super.onPause()
        unregisterReceiver(receiver)
    }

    /**
     * Remove all peers and clear all fields. This is called on
     * BroadcastReceiver receiving a state change event.
     */
    fun resetData() {
        val fragmentList = fragmentManager
            .findFragmentById(R.id.frag_list) as DeviceListFragment
        val fragmentDetails = fragmentManager
            .findFragmentById(R.id.frag_detail) as DeviceDetailFragment
        fragmentList?.clearPeers()
        fragmentDetails?.resetViews()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.action_items, menu)
        return true
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.atn_direct_enable -> {
                if (manager != null && channel != null) {
                    // Since this is the system wireless settings activity, it's
                    // not going to send us a result. We will be notified by
                    // WiFiDeviceBroadcastReceiver instead.
                    startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS))
                } else {
                    Log.e(TAG, "channel or manager is null")
                }
                true
            }
            R.id.atn_direct_discover -> {
                if (!isWifiP2pEnabled) {
                    Toast.makeText(
                        this@WifiDirectActivity, R.string.p2p_off_warning,
                        Toast.LENGTH_SHORT
                    ).show()
                    return true
                }
                val fragment = fragmentManager
                    .findFragmentById(R.id.frag_list) as DeviceListFragment
                fragment.onInitiateDiscovery()
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
                manager!!.discoverPeers(channel, object : WifiP2pManager.ActionListener {
                    override fun onSuccess() {
                        Toast.makeText(
                            this@WifiDirectActivity, "Discovery Initiated",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onFailure(reasonCode: Int) {
                        Toast.makeText(
                            this@WifiDirectActivity, "Discovery Failed : $reasonCode",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showDetails(device: WifiP2pDevice?) {
        val fragment = fragmentManager
            .findFragmentById(R.id.frag_detail) as DeviceDetailFragment
        fragment.showDetails(device!!)
    }

    override fun connect(config: WifiP2pConfig?) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        manager!!.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            override fun onFailure(reason: Int) {
                Toast.makeText(
                    this@WifiDirectActivity, "Connect failed. Retry.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun disconnect() {
        val fragment = fragmentManager
            .findFragmentById(R.id.frag_detail) as DeviceDetailFragment
        fragment.resetViews()
        manager!!.removeGroup(channel, object : WifiP2pManager.ActionListener {
            override fun onFailure(reasonCode: Int) {
                Log.d(TAG, "Disconnect failed. Reason :$reasonCode")
            }

            override fun onSuccess() {
                fragment.view!!.visibility = View.GONE
            }
        })
    }

    override fun onChannelDisconnected() {
        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show()
            resetData()
            retryChannel = true
            manager!!.initialize(this, mainLooper, this)
        } else {
            Toast.makeText(
                this,
                "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    companion object {
        const val TAG = "wifidirectdemo"
        private const val PERMISSIONS_REQUEST_CODE_ACCESS_FINE_LOCATION = 1001
    }
}
package com.example.progmprojet

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pManager
import android.net.wifi.p2p.WifiP2pManager.PeerListListener
import android.os.Parcelable
import android.util.Log
import androidx.core.app.ActivityCompat

class MyReceiver
/**
 * @param manager WifiP2pManager system service
 * @param channel Wifi p2p channel
 * @param activity activity associated with the receiver
 */(
    private val manager: WifiP2pManager?, private val channel: WifiP2pManager.Channel,
    private val activity: WifiDirectActivity
) : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            // Determine if Wi-Fi Direct mode is enabled or not, alert
            // the Activity.
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                activity.setIsWifiP2pEnabled(true)
            } else {
                activity.setIsWifiP2pEnabled(false)
                activity.resetData()
            }
            Log.d(WifiDirectActivity.TAG, "P2P state changed - $state")
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                if (ActivityCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                manager.requestPeers(
                    channel, activity.fragmentManager
                        .findFragmentById(R.id.frag_list) as PeerListListener
                )
            }
            Log.d(WifiDirectActivity.TAG, "P2P peers changed")
        } /*else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {

            // Connection state changed! We should probably do something about
            // that.
        } */else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            System.out.println("CONNECT INFO3")
            if (manager == null) {
                System.out.println("CONNECT INFO86515641")
                return
            }
            val networkInfo = intent
                .getParcelableExtra<Parcelable>(WifiP2pManager.EXTRA_NETWORK_INFO) as NetworkInfo?
            System.out.println("CONNECT INFO2")
            System.out.println(networkInfo)
            if (networkInfo != null) {
                System.out.println(networkInfo.isConnected)
            }
            if (networkInfo != null && networkInfo.isConnected) {
                System.out.println("CONNECT INFO")
                // we are connected with the other device, request connection
                // info to find group owner IP
                val fragment = activity
                    .fragmentManager.findFragmentById(R.id.frag_detail) as DeviceDetailFragment
                manager.requestConnectionInfo(channel, fragment)
            } else {
                // It's a disconnect
                activity.resetData()
            }
        }else if ("com.example.progmprojet.ACTION_ENVOI_JEUX" == action){
            val jeu1 = intent?.getIntExtra("win",-5)
            if (jeu1 != null) {
                activity.win(jeu1)
            }
        }
    }
}
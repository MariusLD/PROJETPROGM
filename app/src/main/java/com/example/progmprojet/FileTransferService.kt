package com.example.progmprojet

import android.app.IntentService
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.example.progmprojet.DeviceDetailFragment.Companion.copyFile
import java.io.*
import java.net.InetSocketAddress
import java.net.Socket

/**
 * A service that process each file transfer request i.e Intent by opening a
 * socket connection with the WiFi Direct Group Owner and writing the file
 */
class FileTransferService : IntentService {
    constructor(name: String?) : super(name) {}
    constructor() : super("FileTransferService") {}

    /*
     * (non-Javadoc)
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    override fun onHandleIntent(intent: Intent?) {

        val context = applicationContext
        if (intent!!.action == ACTION_SEND_FILE) {
            val fileUri = intent.extras!!.getString(EXTRAS_FILE_PATH)
            val host = intent.extras!!.getString(EXTRAS_GROUP_OWNER_ADDRESS)
            val socket = Socket()
            val port = intent.extras!!.getInt(EXTRAS_GROUP_OWNER_PORT)
            val point = intent.extras!!.getInt("point")
            val score :Boolean= intent.extras!!.getBoolean("score")
            var jeu: ArrayList<Int> =ArrayList<Int>()
            if(!score){
                jeu.add(intent.extras!!.getInt("jeu1"))
                jeu.add(intent.extras!!.getInt("jeu2"))
                jeu.add(intent.extras!!.getInt("jeu3"))
            }
            //System.out.println("")
            try {
                Log.d(WifiDirectActivity.TAG, "Opening client socket - ")
                socket.bind(null)
                socket.connect(InetSocketAddress(host, port), SOCKET_TIMEOUT)
                Log.d(WifiDirectActivity.TAG, "Client socket - " + socket.isConnected)
                val stream = DataOutputStream(socket.getOutputStream())
                val inputstream = DataInputStream(socket.getInputStream())
                val cr = context.contentResolver
                var `is`: InputStream? = null

                try {

                    if(score){
                        stream.writeInt(point)
                        var gagne=inputstream.readInt()
                        val intent = Intent("com.example.progmprojet.ACTION_ENVOI_JEUX")
                        intent.putExtra("win", gagne)
                        sendBroadcast(intent)
                    }else {
                        if (jeu != null) {
                            for (i in jeu.indices) {
                                stream.write(jeu.get(i)) // écrire chaque élément du tableau
                            }
                        }
                    }
                    inputstream.close()
                    stream.close();
                } catch (e: FileNotFoundException) {
                    Log.d(WifiDirectActivity.TAG, e.toString())
                }
                Log.d(WifiDirectActivity.TAG, "Client: Data written")
            } catch (e: IOException) {
                Log.e(WifiDirectActivity.TAG, e.message!!)
            } finally {
                if (socket != null) {
                    if (socket.isConnected) {
                        try {
                            socket.close()
                        } catch (e: IOException) {
                            // Give up
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val SOCKET_TIMEOUT = 5000
        const val ACTION_SEND_FILE = "fr.esir.progm.wifidirectdemo.SEND_FILE"
        const val EXTRAS_FILE_PATH = "file_url"
        const val EXTRAS_GROUP_OWNER_ADDRESS = "go_host"
        const val EXTRAS_GROUP_OWNER_PORT = "go_port"
    }
}
package com.example.progmprojet

import android.app.Activity
import android.app.Fragment
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.wifi.WpsInfo
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.progmprojet.DeviceListFragment.DeviceActionListener
import java.io.*
import java.net.ServerSocket
import java.util.*
import kotlin.collections.ArrayList

/**
 * A fragment that manages a particular peer and allows interaction with device
 * i.e. setting up network connection and transferring data.
 */
class DeviceDetailFragment() : Fragment(), ConnectionInfoListener {
    private var mContentView: View? = null
    private var device: WifiP2pDevice? = null
    private var info: WifiP2pInfo? = null
    var progressDialog: ProgressDialog? = null
    var game: ArrayList<Int>? = null
    var compteur=0
    private var score=0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mContentView = inflater.inflate(R.layout.device_detail, null)
        mContentView!!.findViewById<View>(R.id.btn_connect).setOnClickListener(View.OnClickListener {
            val config = WifiP2pConfig()
            config.deviceAddress = device!!.deviceAddress
            config.wps.setup = WpsInfo.PBC
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
            progressDialog = ProgressDialog.show(
                activity,
                "Press back to cancel",
                "Connecting to :" + device!!.deviceAddress,
                true,
                true //                        new DialogInterface.OnCancelListener() {
                //
                //                            @Override
                //                            public void onCancel(DialogInterface dialog) {
                //                                ((DeviceActionListener) getActivity()).cancelDisconnect();
                //                            }
                //                        }
            )
            (activity as DeviceActionListener).connect(config)
        })
        mContentView!!.findViewById<View>(R.id.btn_disconnect).setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    (activity as DeviceActionListener).disconnect()
                }
            })
        mContentView!!.findViewById<View>(R.id.btn_start_client).setOnClickListener(
            object : View.OnClickListener {
                override fun onClick(v: View) {
                    // Allow user to pick an image from Gallery or other
                    // registered app
                    send()
                }
            })
        return mContentView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if ( resultCode == AppCompatActivity.RESULT_OK && data != null) {
            // Récupérez le résultat de l'activité enfant
            val resultat = data.getIntExtra("input", -5)
            if(resultat!=-5){
                score+=resultat
                System.out.println("SCORE :"+score)
                compteur++
            }

            if (info?.groupFormed == true && info?.isGroupOwner == false && compteur==3) {
                compteur=0
                sendScore()
            }
            // Utilisez le résultat ici
        }
    }
    fun loachgame(list : ArrayList<Int>){
        var minijeux = ArrayList<Class<*>>()
        minijeux.add(TapTaupe::class.java)
        minijeux.add(Quizz::class.java)
        minijeux.add(QuizzSound::class.java)
        minijeux.add(PieGame::class.java)
        minijeux.add(CutBle::class.java)
        //minijeux.add(SnakeWithoutSnake::class.java)
        for(i in list) {
            val serviceIntent=Intent(activity,minijeux.get(i))
            val acti : WifiDirectActivity= this.activity as WifiDirectActivity
            serviceIntent.putExtra("user", acti.user)
            startActivityForResult(serviceIntent, 1)

        }

    }


    fun win(score : Int){
        val serviceIntent = Intent(activity, WinLoose::class.java)
        serviceIntent.putExtra("win",score)
        serviceIntent.putExtra("score",this.score)
        activity.startActivity(serviceIntent)
        this.score=0
    }
    fun sendScore(){

        val statusText = mContentView!!.findViewById<View>(R.id.status_text) as TextView

        val serviceIntent = Intent(activity, FileTransferService::class.java)

        serviceIntent.action = FileTransferService.ACTION_SEND_FILE

        serviceIntent.putExtra(
            FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
            info!!.groupOwnerAddress.hostAddress
        )
        serviceIntent.putExtra("score",true)
        serviceIntent.putExtra("point",score)
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988)
        activity.startService(serviceIntent)
        //this.score=0
    }
    fun send(){

        val serviceIntent = Intent(activity, FileTransferService::class.java)
        serviceIntent.action = FileTransferService.ACTION_SEND_FILE
        serviceIntent.putExtra("score",false)
        serviceIntent.putExtra(
            FileTransferService.EXTRAS_GROUP_OWNER_ADDRESS,
            info!!.groupOwnerAddress.hostAddress
        )
        val random = Random()
        val set = mutableSetOf<Int>()
        while (set.size < 3) {
            val rt=random.nextInt(5)
            set.add(rt)
        }
        val numbers = set.toList()
        serviceIntent.putExtra("jeu1", numbers[0])
        serviceIntent.putExtra("jeu2", numbers[1])
        serviceIntent.putExtra("jeu3", numbers[2])
        serviceIntent.putExtra(FileTransferService.EXTRAS_GROUP_OWNER_PORT, 8988)
        activity.startService(serviceIntent)

        this.loachgame(numbers as ArrayList<Int>)
    }

    override fun onConnectionInfoAvailable(info: WifiP2pInfo) {

        if (progressDialog != null && progressDialog!!.isShowing) {
            progressDialog!!.dismiss()
        }
        this.info = info
        this.view!!.visibility = View.VISIBLE

        // The owner IP is now known.
        var view = mContentView!!.findViewById<View>(R.id.group_owner) as TextView
        view.text = (resources.getString(R.string.group_owner_text)
                + (if ((info.isGroupOwner == true)) resources.getString(R.string.yes) else resources.getString(
            R.string.no
        )))

        // InetAddress from WifiP2pInfo struct.
        view = mContentView!!.findViewById<View>(R.id.device_info) as TextView
        view.text = "Group Owner IP - " + info.groupOwnerAddress.hostAddress

        // After the group negotiation, we assign the group owner as the file
        // server. The file server is single threaded, single connection server
        // socket.
        if (info.groupFormed && info.isGroupOwner) {
            FileServerAsyncTask(activity, mContentView!!.findViewById(R.id.status_text),this)
                .execute()
        } else if (info.groupFormed) {
            // The other device acts as the client. In this case, we enable the
            // get file button.
            mContentView!!.findViewById<View>(R.id.btn_start_client).visibility = View.VISIBLE
            (mContentView!!.findViewById<View>(R.id.status_text) as TextView).text = resources
                .getString(R.string.client_text)
        }

        // hide the connect button
        mContentView!!.findViewById<View>(R.id.btn_connect).visibility =
            View.GONE
    }

    /**
     * Updates the UI with device data
     *
     * @param device the device to be displayed
     */
    fun showDetails(device: WifiP2pDevice) {
        this.device = device
        this.view!!.visibility = View.VISIBLE
        var view = mContentView!!.findViewById<View>(R.id.device_address) as TextView
        view.text = device.deviceAddress
        view = mContentView!!.findViewById<View>(R.id.device_info) as TextView
        view.text = device.toString()
    }

    /**
     * Clears the UI fields after a disconnect or direct mode disable operation.
     */
    fun resetViews() {
        mContentView!!.findViewById<View>(R.id.btn_connect).visibility = View.VISIBLE
        var view = mContentView!!.findViewById<View>(R.id.device_address) as TextView
        view.setText(R.string.empty)
        view = mContentView!!.findViewById<View>(R.id.device_info) as TextView
        view.setText(R.string.empty)
        view = mContentView!!.findViewById<View>(R.id.group_owner) as TextView
        view.setText(R.string.empty)
        view = mContentView!!.findViewById<View>(R.id.status_text) as TextView
        view.setText(R.string.empty)
        mContentView!!.findViewById<View>(R.id.btn_start_client).visibility =
            View.GONE
        view.visibility = View.GONE
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    class FileServerAsyncTask(private val context: Context, statusText: View, fragment: DeviceDetailFragment) :
        AsyncTask<Void?, Void?, String?>() {
        private val statusText: TextView
        private val fragment: DeviceDetailFragment
        private val game : ArrayList<Int>
        /**
         * @param context
         * @param statusText
         */
        init {
            this.statusText = statusText as TextView
            this.fragment=fragment
            this.game=ArrayList<Int>()

        }

        protected override fun doInBackground(vararg params: Void?): String? {
            try {
                val serverSocket = ServerSocket(8988)
                Log.d(WifiDirectActivity.TAG, "Server: Socket opened")
                val client = serverSocket.accept()
                Log.d(WifiDirectActivity.TAG, "Server: connection done")
                val f = File(
                    context.getExternalFilesDir("received"),
                    ("wifip2pshared-" + System.currentTimeMillis()
                            + ".jpg")
                )
                val dirs = File(f.parent)
                if (!dirs.exists()) dirs.mkdirs()
                f.createNewFile()
                Log.d(WifiDirectActivity.TAG, "server: copying files $f")

                //val inputstream = DataInputStream(client.getInputStream())
                val inputstream = client.getInputStream()

                for (i in 0..2) {
                    game.add(inputstream.read()) // lire chaque élément du tableau

                }

                serverSocket.close()
                return "true"
            } catch (e: IOException) {
                Log.e(WifiDirectActivity.TAG, (e.message)!!)
                return null
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        override fun onPostExecute(result: String?) {
            if (result != null) {
                statusText.text = "File copied - $result"

                val intent = Intent()
                intent.action = Intent.ACTION_VIEW

                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                ScoreServerAsyncTask(context, this.statusText ,this.fragment)
                    .execute()
                this.fragment.loachgame(this.game)

            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        override fun onPreExecute() {
            statusText.text = "Opening a server socket"
        }
    }

    /**
     * A simple server socket that accepts connection and writes some data on
     * the stream.
     */
    class ScoreServerAsyncTask(private val context: Context, statusText: View, fragment: DeviceDetailFragment) :
        AsyncTask<Void?, Void?, String?>() {
        private val statusText: TextView
        private val fragment: DeviceDetailFragment
        private var game : Int
        /**
         * @param context
         * @param statusText
         */
        init {
            this.statusText = statusText as TextView
            this.fragment=fragment
            this.game=-1

        }

        protected override fun doInBackground(vararg params: Void?): String? {
            try {
                val serverSocket = ServerSocket(8988)
                Log.d(WifiDirectActivity.TAG, "Server: Socket opened")
                val client = serverSocket.accept()
                Log.d(WifiDirectActivity.TAG, "Server: connection done")


                val stream = DataOutputStream(client.getOutputStream())
                val inputstream = DataInputStream(client.getInputStream())
                val scoreAdv = inputstream.readInt()
                var gagne=1
                if(scoreAdv<this.fragment.score){
                    gagne=-1
                    game=1
                }else if(scoreAdv==this.fragment.score){
                    gagne=0
                    game=0
                }
                stream.writeInt(gagne)
                serverSocket.close()
                return "true"
            } catch (e: IOException) {
                Log.e(WifiDirectActivity.TAG, (e.message)!!)
                return null
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        override fun onPostExecute(result: String?) {
            if (result != null) {
                statusText.text = "File copied - $result"

                val intent = Intent()
                intent.action = Intent.ACTION_VIEW

                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                this.fragment.win(game)
            }
        }

        /*
         * (non-Javadoc)
         * @see android.os.AsyncTask#onPreExecute()
         */
        override fun onPreExecute() {
            statusText.text = "Opening a server socket"
        }
    }

    companion object {
        protected val CHOOSE_FILE_RESULT_CODE = 20
        @JvmStatic
        fun copyFile(inputStream: InputStream, out: OutputStream): Boolean {
            val buf = ByteArray(1024)
            var len: Int
            try {
                while ((inputStream.read(buf).also { len = it }) != -1) {
                    out.write(buf, 0, len)
                }
                out.close()
                inputStream.close()
            } catch (e: IOException) {
                Log.d(WifiDirectActivity.TAG, e.toString())
                return false
            }
            return true
        }
    }
}
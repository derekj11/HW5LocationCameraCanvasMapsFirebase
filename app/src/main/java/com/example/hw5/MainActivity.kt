package com.example.hw5

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job
import java.sql.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    // capture location
    private var corroutineJob: Job? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var lastLocation: Location? = null
    lateinit var geocoder: Geocoder
    private var latlong: String = ""
    private var addressOutput = ""
    private var now: String = ""

    // result receiver
    private lateinit var resultReceiver: AddressResultReceiver

    // firebase reference
    private lateinit var database: DatabaseReference

    // recyclerview
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var myDataset: LinkedList<CapturedLocation> = LinkedList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewManager = LinearLayoutManager(this)
        viewAdapter = MyAdapter(myDataset, this)

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        resultReceiver = AddressResultReceiver(Handler())

        database = FirebaseDatabase.getInstance().reference

        val helper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                database.child(myDataset[viewHolder.adapterPosition].timeStamp!!).removeValue()

                myDataset.removeAt(viewHolder.adapterPosition)
                viewAdapter.notifyItemRemoved(viewHolder.adapterPosition)
            }

        })
        helper.attachToRecyclerView(recyclerView)

        fab.setOnClickListener {
            Log.d(TAG, "fab clicked")
            captureLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun captureLocation() {

        now = Calendar.getInstance().time.toGMTString()

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location == null) {
                Log.w(TAG, "onSuccess:null")
                return@addOnSuccessListener
            }

            lastLocation = location
            latlong = "${location.latitude},${location.longitude}"
            Log.d(TAG, latlong)

            // Determine whether a geocoder is available
            if (!Geocoder.isPresent()) {
                Toast.makeText(this@MainActivity, getString(R.string.no_geocoder_available), Toast.LENGTH_LONG).show()
                return@addOnSuccessListener
            }

            Log.d(TAG, "startIntentService")
            startIntentService()
        }
    }

    /**
     * Creates an Intent, adds location data to it as an extra, and starts the intent serveice for
     * fetching an address.
     */
    private fun startIntentService() {
        // Create an intent for passing to the intent service responsible for fetching the address.
        val intent = Intent(this, FetchAddressIntentService::class.java).apply {
            // Pass the result receiver as an extra to the service.
            putExtra(Constants.RECEIVER, resultReceiver)
            // Pass the location data as an extra to the service.
            putExtra(Constants.LOCATION_DATA_EXTRA, lastLocation)
        }

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        startService(intent)
    }

    private inner class AddressResultReceiver internal constructor(
        handler: Handler
    ) : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            addressOutput = resultData?.getString(Constants.RESULT_DATA_KEY) ?: ""

            addLocation()
        }
    }

    private fun addLocation() {
        Log.d(TAG, now)
        myDataset.addLast(CapturedLocation(latlong, addressOutput, now))
        database.child(now).setValue(myDataset.last)
        recyclerView.adapter?.notifyItemInserted(myDataset.size)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}

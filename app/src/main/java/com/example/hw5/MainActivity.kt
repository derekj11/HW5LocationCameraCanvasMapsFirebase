package com.example.hw5

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job

class MainActivity : AppCompatActivity() {

    // capture location
    private var corroutineJob: Job? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    var latlong: Location? = null
    lateinit var geocoder: Geocoder

    // firebase reference
    private lateinit var database: DatabaseReference

    // recyclerview
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        database = FirebaseDatabase.getInstance().reference

        viewManager = LinearLayoutManager(this)

        fab.setOnClickListener {
            captureLocation()
        }
    }

    @SuppressLint("MissingPermission")
    fun captureLocation() {
        var coordinates: String

        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            coordinates = "${location?.latitude}, ${location?.longitude}"
        }
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

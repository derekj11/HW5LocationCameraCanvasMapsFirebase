package com.example.hw5

import android.app.IntentService
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.ResultReceiver
import android.util.Log
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*

class FetchAddressIntentService : IntentService("FetchAddress") {

    private val TAG = "FetchAddressService"
    // The receiver where results are forwarded from the service.
    private var receiver: ResultReceiver? = null

    override fun onHandleIntent(intent: Intent?) {
        var errorMessage = ""

        receiver = intent?.getParcelableExtra(Constants.RECEIVER)

        // Check if the receiver was properly registered.
        if (intent == null || receiver == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.")
            return
        }

        // Get the location passed to this service through an extra.
        val location = intent.getParcelableExtra<Location>(Constants.LOCATION_DATA_EXTRA)

        // Make sure that the location data was really sent over through an extra. If it wasn't,
        // send an error message and return
        if (location == null) {
            errorMessage = getString(R.string.no_location_data_provided)
            Log.wtf(TAG, errorMessage)
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
            return
        }

        val geocoder = Geocoder(this, Locale.getDefault())

        var addresses: List<Address> = emptyList()

        try {
            addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
        } catch (ioException: IOException) {
            errorMessage = getString(R.string.service_not_available)
            Log.e(TAG, errorMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            errorMessage = getString(R.string.invalid_lat_long_used)
            Log.e(TAG, "$errorMessage. Latitude = ${location.latitude}, Longitude = ${location.longitude}", illegalArgumentException)
        }

        // Handle the case where no address is found.
        if (addresses.isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = getString(R.string.no_address_found)
                Log.e(TAG, errorMessage)
            }
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage)
        } else {
            val address = addresses[0]
            val addressFragments = with(address) {
                (0..maxAddressLineIndex).map { getAddressLine(it) }
            }

            Log.i(TAG, getString(R.string.address_found))
            deliverResultToReceiver(Constants.SUCCESS_RESULT, addressFragments.joinToString(separator = "\n"))
        }
    }

    private fun deliverResultToReceiver(resultCode: Int, message: String) {
        val bundle = Bundle().apply { putString(Constants.RESULT_DATA_KEY, message) }
        Log.d(TAG, message)
        receiver?.send(resultCode, bundle)
    }


}
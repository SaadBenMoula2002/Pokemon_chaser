package com.example.pockemon

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.pockemon.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        loadPockemons()
        checkPermisions()

    }
    val AccesLocation = 123
    fun checkPermisions(){

        if (ActivityCompat.checkSelfPermission(this,
            android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), AccesLocation)
            return
        }
        getUserLocation()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            AccesLocation->{
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getUserLocation()
                }else{
                    Toast.makeText(this, "Location access is deny", Toast.LENGTH_LONG).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    fun getUserLocation(){
        Toast.makeText(this, "Location access now", Toast.LENGTH_LONG).show()

        val mylocation = MyLocationListener()
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 3f,mylocation)

        val myThread = MyThread()
        myThread.start()
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera

    }
    var mylocation:Location? = null
    inner class MyLocationListener:LocationListener{

        constructor(){
            mylocation = Location("Me")
            mylocation!!.longitude = 0.0
            mylocation!!.latitude = 0.0
        }

        override fun onLocationChanged(location: Location) {
            mylocation = location
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

        }

        override fun onProviderEnabled(provider: String) {

        }

        override fun onProviderDisabled(provider: String) {

        }
    }

    var oldLocation:Location? = null
    inner class MyThread:Thread{
        constructor():super(){
            oldLocation = Location("oldLocation")
            oldLocation!!.longitude = 0.0
            oldLocation!!.latitude = 0.0
        }

        override fun run() {
            while (true){
                try {
                    if (oldLocation!!.distanceTo(mylocation!!)==0f){
                        continue
                    }
                    oldLocation = mylocation
                    runOnUiThread{
                        mMap.clear()
                        val sydney = LatLng(mylocation!!.latitude, mylocation!!.longitude)
                        mMap.addMarker(MarkerOptions()
                            .position(sydney)
                            .title("Me")
                            .snippet("here is my location")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.mario)))
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 10f))

                    for (i in 0 until listOfPockemons.size){
                        var newPockemon = listOfPockemons[i]

                        if(newPockemon.isCatch == false){
                            val pockLocation = LatLng(newPockemon.location!!.latitude, newPockemon.location!!.longitude)
                            mMap.addMarker(MarkerOptions()
                                .position(pockLocation)
                                .title(newPockemon.name)
                                .snippet(newPockemon.des + "," + newPockemon.power)
                                .icon(BitmapDescriptorFactory.fromResource(newPockemon.image!!)))

                            if (mylocation!!.distanceTo(newPockemon.location!!) < 10){
                                myPower += newPockemon.power!!
                                newPockemon.isCatch = true
                                listOfPockemons[i] = newPockemon
                                Toast.makeText(applicationContext,
                                    "You catch new pockemon, your new power is $myPower", Toast.LENGTH_LONG).show()

                            }
                        }
                    }
                }


                    Thread.sleep(1000)
                }catch (ex: java.lang.Exception){}

            }
        }
    }
    var myPower : Double = 0.0
    var listOfPockemons = ArrayList<Pockemon>()
    fun loadPockemons(){
        listOfPockemons.add(Pockemon(R.drawable.pikachu, "Pikachu","Pikachu living in japan", 55.0, 37.5016621152613, -121.7098))
        listOfPockemons.add(Pockemon(R.drawable.evoli, "Evoli","Evoli living in usa", 90.5, 37.3016621152613, -121.7098))
        listOfPockemons.add(Pockemon(R.drawable.snorlax, "chamander","chamander living in morocco", 33.5, 37.2016621152613, -121.7098))

    }
}
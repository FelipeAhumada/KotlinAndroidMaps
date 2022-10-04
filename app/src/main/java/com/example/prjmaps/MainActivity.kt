package com.example.prjmaps

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Location
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener  {
    private lateinit var map: GoogleMap

    //companion object con una variable constante dentro que será el código de respuesta para saber
    // //si al aceptarse permisos ha sido el nuestro.
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createMapFragment()
    }
    private fun createMapFragment(){
        val mapFragment = supportFragmentManager.findFragmentById((R.id.fragmentMap))  as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map  = googleMap
        createMarker()
        map.setOnMyLocationButtonClickListener(this)
        map.setOnMyLocationClickListener(this)
        enableMyLocation()
    }

    private fun createMarker() {
        val oficinaRechiken = LatLng( -33.4108095, -70.5729644 )
        map.addMarker(MarkerOptions().position(oficinaRechiken).title("Oficina del Capitan Ron Esponja"))
                //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_casa_bob)).position(oficinaRechiken))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(oficinaRechiken, 15f), 4000, null)

    }
    //Conocemos si los permisos estan activos o no.
    private fun isLocationPermissionsGranted() = ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


    //Activaremos la ubicación ,  según los persmisos,
    private fun enableMyLocation(){
        if (!::map.isInitialized) return
        if (isLocationPermissionsGranted()){
            map.isMyLocationEnabled = true
        } else{
            requestLocationPermission()
        }
    }

    //Si entra por el if de significa que ya había rechazado los permisos antes y por ello le mostramos un toast
    // avisándole de que vaya a los ajustes de la app y modifique los permisos.
    // Si por el contrario entra por el else significará que nunca le hemos pedido los permisos y lo haremos a través
    // de la función ActivityCompat.requestPermissions, pasándole la activity (this), el permiso o
    // los permisos que queremos que acepte (en este caso uno) y el código de localización que creamos en el
    // companion object.
    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!isLocationPermissionsGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {

       return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "Estas en ${p0.latitude} , ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }
}
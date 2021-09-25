package com.example.calculatortask.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.calculatortask.R
import com.example.calculatortask.adapters.AdapterRecyclerOperations
import com.example.calculatortask.data.models.OperationModel
import com.example.calculatortask.databinding.FragmentHomeBinding
import com.example.calculatortask.utils.showToast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

const  val LOCATION_PERMISSION_REQUEST_CODE = 100

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding : FragmentHomeBinding ?= null
    private val binding get ()= _binding!!
    private val homeViewModel : HomeViewModel by viewModels()
    private val adapterOperation : AdapterRecyclerOperations
                          by lazy {AdapterRecyclerOperations()  }
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationRequest: LocationRequest? = null
    private var mLocationCallback: LocationCallback? = null
    var userLocation : Location?= null
    private lateinit var geoCoder : Geocoder

    override fun onAttach(context: Context) {
        super.onAttach(context)
        geoCoder = Geocoder(context)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home , container ,false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)
        homeViewModel.getAllOperations()
        observers()
        onClicks()
        initGpsSetting()
        if (isLocationPermissionAllowed()){
            locationChecker()
        }else{
            askToLocationPermission()
        }
    }
    private fun onClicks(){
        binding.btnAddOperation.setOnClickListener {

            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCalculatorFragment())
        }
    }

    private fun observers(){
        homeViewModel.errorLiveData.observe(viewLifecycleOwner,{
            showToast(it)
        })

        homeViewModel.operationsLiveData.observe(viewLifecycleOwner,{
            adapterOperation.list = it as ArrayList<OperationModel>
            binding.recyclerOperations.adapter = adapterOperation
        })
    }
    @SuppressLint("MissingPermission")
    private fun getLocation (){

        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                userLocation = locationResult.lastLocation

                getLocationName(userLocation!!)

            }
        }
        mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun getLocationName (location :Location){

        lifecycleScope.launch(IO){
            val locationData =geoCoder.getFromLocation(userLocation?.latitude!!
                , userLocation?.longitude!! , 1)

            withContext(Main){
                binding.textLocation.text =
                    locationData[0].getAddressLine(0)
            }
        }

    }
    private fun initGpsSetting (){
        if (mFusedLocationClient == null)
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        if (mLocationRequest == null) {
            mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1)
                .setSmallestDisplacement(10f)// update each 5 matters
        }
    }

    private fun locationChecker (){

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest)
        val result: Task<LocationSettingsResponse> =
            LocationServices.getSettingsClient(requireActivity()).checkLocationSettings(builder.build())


        result.addOnCompleteListener(OnCompleteListener<LocationSettingsResponse?> { task ->
            try {
                val response = task.getResult(ApiException::class.java)
                // All location settings are satisfied. The client can initialize location
                // requests here.
                getLocation()
            } catch (exception: ApiException) {
                when (exception.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED ->                             // Location settings are not satisfied. But could be fixed by showing the
                        // user a dialog.
                        try {
                            // Cast to a resolvable exception.
                            val resolvable = exception as ResolvableApiException
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            resolvable.startResolutionForResult(
                                requireActivity(),
                                LocationRequest.PRIORITY_HIGH_ACCURACY
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        } catch (e: ClassCastException) {
                            // Ignore, should be an impossible error.
                        }
                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    }
                }
            }
        })

    }
    fun isLocationPermissionAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun askToLocationPermission() {

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LocationRequest.PRIORITY_HIGH_ACCURACY -> when (resultCode) {
                Activity.RESULT_OK ->                 // All required changes were successfully made
                    getLocation()
                Activity.RESULT_CANCELED ->                 // The user was asked to change settings, but chose not to
                    locationChecker()
                else -> {
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
            locationChecker()
        }


    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
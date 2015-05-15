package ro.pub.cs.systems.pdsd.lab10.googlemaps.service;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import ro.pub.cs.systems.pdsd.lab10.googlemaps.general.Constants;
import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

public class GetLocationAddressIntentService extends IntentService {
	
	protected ResultReceiver resultReceiver = null;
	
	public GetLocationAddressIntentService() {
		super(Constants.TAG);
		Log.i(Constants.TAG, "GetLocationAddressIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(Constants.TAG, "onHandleIntent() callback method has been invoked");
		
		String errorMessage = null;
		
		resultReceiver = intent.getParcelableExtra(Constants.RESULT_RECEIVER);
		if (resultReceiver == null) {
			errorMessage = "No result receiver was provided to handle the information";
			Log.e(Constants.TAG, "An exception has occurred: " + errorMessage);
			return;
		}
		
		Location location = intent.getParcelableExtra(Constants.LOCATION);
		if (location == null) {
			errorMessage = "No location data was provided";
			Log.e(Constants.TAG, "An exception has occurred: " + errorMessage);
			handleResult(Constants.RESULT_FAILURE, errorMessage);
			return;
		}
		
		List<Address> addressList = null;
		
		Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
		try {
			addressList = geoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), Constants.NUMBER_OF_ADDRESSES);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			errorMessage = "No geoCoder found!";
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			errorMessage = "Coordinates in wrong format!";
		}
		
		if (addressList == null || addressList.isEmpty()) {
			
			errorMessage = "No address found";
		}
		
		if(errorMessage != null && !errorMessage.isEmpty()) {
			handleResult(Constants.RESULT_FAILURE, errorMessage);
			return;
		}
		
		StringBuffer result = new StringBuffer();
		for (Address address: addressList) {
			for (int k = 0; k< address.getMaxAddressLineIndex(); k++){
				result.append(address.getAddressLine(k) + System.getProperty("line.separator"));
			}
		}
		
		handleResult(Constants.RESULT_SUCCESS, result.toString());
		
		// TODO: exercise 8
		// instantiate a Geocoder object
		// get the addresses list by calling the getFromLocation() method on Geocoder (supply latitude, longitude, Constants.NUMBER_OF_ADDRESSES)
		// handle IOException and IllegalArgumentException properly as well as no results supplied situation
		// iterate over the address list
		// concatenate all lines from each address (number of lines: getMaxAddressLineIndex(); specific line: getAddressLine()
		// call handleResult method with result (Constants.RESULT_SUCCESS, Constants.RESULT_FAILURE) and the address details / error message
		
		//errorMessage = "Not implemented yet";
		
		
	}
	
	private void handleResult(int resultCode, String message) {
		Bundle bundle = new Bundle();
		bundle.putString(Constants.RESULT, message);
		resultReceiver.send(resultCode, bundle);
	}

}

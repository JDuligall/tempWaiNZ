package com.example.gpstest;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
//import android.media.audiofx.BassBoost.Settings;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements LocationListener {

	GoogleMap googleMap;
	boolean isOffline = false;
	
	private LatLng previousLoc = new LatLng(0, 0);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Getting Google Play availability status
		int status = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(getBaseContext());

		// checkGPS
		isGPSenabled();
		if(!isNetworkAvailable()){
			//initial check
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("No internet connection found. GPS may not be accurate if indoors.")
			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					//Nothing should happen
				}
			});
			builder.create();
			builder.show();
			isOffline = true;
		}

			// Showing status
			if (status != ConnectionResult.SUCCESS) { // Google Play Services
														// are not available

				int requestCode = 10;
				Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status,
						this, requestCode);
				dialog.show();
			} else { // Google Play Services are available
				// Getting reference to the SupportMapFragment of
				// activity_main.xml
				SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.map);
				// Getting GoogleMap object from the fragment
				googleMap = fm.getMap();
				// Enabling MyLocation Layer of Google Map
				googleMap.setMyLocationEnabled(true);
				// Getting LocationManager object from System Service
				// LOCATION_SERVICE
				LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				// Creating a criteria object to retrieve provider
				Criteria criteria = new Criteria();
				// Getting the name of the best provider
				String provider = locationManager.getBestProvider(criteria,
						true);

				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 0, 10, this);
				// Getting Current Location
				Location location = googleMap.getMyLocation();// locationManager.getLastKnownLocation(provider);
				if (location != null) {
					onLocationChanged(location);
				}
				// locationManager.requestLocationUpdates(provider, 20000, 0,
				// this);
				// location = googleMap.getMyLocation();
			}

	}

	@Override
	public void onLocationChanged(Location location) {
		TextView tvLocation = (TextView) findViewById(R.id.tv_location);
		// Getting latitude of the current location
		double latitude = location.getLatitude();
		// Getting longitude of the current location
		double longitude = location.getLongitude();
		// Creating a LatLng object for the current location
		LatLng latLng = new LatLng(latitude, longitude);
		if (latLng.latitude != previousLoc.latitude
				|| latLng.longitude != previousLoc.latitude) {
			// Showing the current location in Google Map
			googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
			// Zoom in the Google Map
			googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
			// Setting latitude and longitude in the TextView tv_location
			tvLocation.setText("Latitude:" + latitude + ", Longitude:"
					+ longitude);
		}
		previousLoc = latLng;

	}
	
	/**Online connectivity check*/
	public boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

		// test for connection
		if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
				&& cm.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}
	}

	public void isGPSenabled() {
		LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			// Ask the user to enable GPS
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("Location Manager");
			builder.setMessage("Would you like to enable GPS?");
			builder.setPositiveButton("Yes",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Launch settings, allowing user to make a change
							Intent i = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivity(i);
						}
					});
			builder.setNegativeButton("No",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// No location service, no Activity
							// finish();
							cantContinueWithoutGPS();
						}
					});
			builder.create().show();
		}

	}

	public void cantContinueWithoutGPS() {
		// Log.d("woop","weep");
		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setTitle("Error");
		builder1.setMessage("You need GPS on to continue");
		builder1.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO dont do what youre about to do go backto the home screen
			}

		});
		builder1.create().show(); // must show it not just create yay!
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
}
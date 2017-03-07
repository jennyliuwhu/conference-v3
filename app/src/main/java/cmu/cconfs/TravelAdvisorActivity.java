package cmu.cconfs;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cmu.cconfs.model.parseModel.Weather;
import cmu.cconfs.parseUtils.helper.DirectionsJSONParser;
import cmu.cconfs.parseUtils.helper.JSONWeatherParser;
import cmu.cconfs.service.WeatherHttpClient;

//class AndroidPopupWindowActivity extends Activity {
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_travel);
//        final Button btnOpenPopup = (Button) findViewById(R.id.openpopup);
//        btnOpenPopup.setOnClickListener(new Button.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                LayoutInflater layoutInflater
//                        = (LayoutInflater) getBaseContext()
//                        .getSystemService(LAYOUT_INFLATER_SERVICE);
//                View popupView = layoutInflater.inflate(R.layout.popup, null);
//                final PopupWindow popupWindow = new PopupWindow(
//                        popupView,
//                        LayoutParams.WRAP_CONTENT,
//                        LayoutParams.WRAP_CONTENT);
//
//                Button btnDismiss = (Button) popupView.findViewById(R.id.dismiss);
//                btnDismiss.setOnClickListener(new Button.OnClickListener() {
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        popupWindow.dismiss();
//                    }
//                });
//
//                popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
//
//                popupView.setOnTouchListener(new OnTouchListener() {
//                    int orgX, orgY;
//                    int offsetX, offsetY;
//
//                    @Override
//                    public boolean onTouch(View v, MotionEvent event) {
//                        switch (event.getAction()) {
//                            case MotionEvent.ACTION_DOWN:
//                                orgX = (int) event.getX();
//                                orgY = (int) event.getY();
//                                break;
//                            case MotionEvent.ACTION_MOVE:
//                                offsetX = (int) event.getRawX() - orgX;
//                                offsetY = (int) event.getRawY() - orgY;
//                                popupWindow.update(offsetX, offsetY, -1, -1, true);
//                                break;
//                        }
//                        return true;
//                    }
//                });
//            }
//        });
//    }
//}

/**
 * todo refer to https://developer.android.com/reference/android/widget/PopupWindow.html
 * to to display weather information
 *
 * @author jialingliu
 */
public class TravelAdvisorActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    final Context context = this;
    static final int REQUEST_CODE_RECOVER_PLAY_SERVICES = 1001;
    private GoogleMap mMap;
    private LocationManager locationManager;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;
    LatLng currentLatLng;

    // text view for distance and duration
    TextView tvDistanceDuration;

    // location for New Montgomery St, San Francisco, CA
    // todo change to your conference place when ready to deploy
    private final double lat = 37.788019;
    private final double lon = -122.401890;

    private Marker destinationMarker;
    ArrayList<LatLng> markerPoints;
    private boolean isCleared = true;
    private Marker currentWeatherInfoMaker;

    // weather view
    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;

    private TextView hum;
    private ImageView imgView;

    String city;

    Geocoder geocoder;

    Location location;

    private Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel);
        geocoder = new Geocoder(this);

        tvDistanceDuration = (TextView) findViewById(R.id.distance_duration);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        ProgressDialog progressDialog = new ProgressDialog(TravelAdvisorActivity.this);
        progressDialog.setMessage("loading Map");
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);

        new Load(progressDialog).execute();
        // setup googlemaps
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        markerPoints = new ArrayList<>();

        city = "Mountain View, United States";

        cityText = (TextView) findViewById(R.id.cityText);
        condDescr = (TextView) findViewById(R.id.condDescr);
        temp = (TextView) findViewById(R.id.temp);
        hum = (TextView) findViewById(R.id.hum);
        press = (TextView) findViewById(R.id.press);
        windSpeed = (TextView) findViewById(R.id.windSpeed);
        windDeg = (TextView) findViewById(R.id.windDeg);
        imgView = (ImageView) findViewById(R.id.condIcon);

//        final Button btnOpenPopup = (Button)findViewById(R.id.openpopup);
//        btnOpenPopup.setOnClickListener(new Button.OnClickListener(){
//
//            @Override
//            public void onClick(View arg0) {
//                LayoutInflater layoutInflater
//                        = (LayoutInflater)getBaseContext()
//                        .getSystemService(LAYOUT_INFLATER_SERVICE);
//                View popupView = layoutInflater.inflate(R.layout.popup, null);
//                final PopupWindow popupWindow = new PopupWindow(
//                        popupView,
//                        LayoutParams.WRAP_CONTENT,
//                        LayoutParams.WRAP_CONTENT);
//
//                Button btnDismiss = (Button)popupView.findViewById(R.id.dismiss);
//                btnDismiss.setOnClickListener(new Button.OnClickListener(){
//
//                    @Override
//                    public void onClick(View v) {
//                        // TODO Auto-generated method stub
//                        popupWindow.dismiss();
//                    }});
//
//                popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
//
//            }});

        button = (Button) findViewById(R.id.buttonShowCustomDialog);

        // add button listener
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom);
                dialog.setTitle("Title...");

                // set the custom dialog components - text, image and button
                TextView text = (TextView) dialog.findViewById(R.id.text);
                text.setText("Android custom dialog example!");
                ImageView image = (ImageView) dialog.findViewById(R.id.image);
                image.setImageResource(R.drawable.ic_launcher);

                Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOK);
                // if button is clicked, close the custom dialog
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public void onMapSearch(View view) {
        EditText locationSearch = (EditText) findViewById(R.id.editText);
        System.out.println(locationSearch);
        String location = locationSearch.getText().toString().trim();
        List<Address> addressList = null;

        if (location.isEmpty() || location.length() == 0 || location.equals("")) {
            return;
        }
        try {
            addressList = geocoder.getFromLocationName(location, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address;
        LatLng latLng = new LatLng(lat, lon); // default location: conference location
        if (addressList != null) {
            address = addressList.get(0);
            String countryName = address.getCountryName();
            System.out.println("countryName: " + countryName);
            System.out.println("locality: " + address.getLocality());
            latLng = new LatLng(address.getLatitude(), address.getLongitude());
        }
        destinationMarker.remove();
        destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("destinationMarker"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        initMap();

        if (mMap != null) {

            //Initialize Google Play Services
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
            // Enable MyLocation Button in the Map
            // Setting onclick event listener for the map
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {
                    System.out.println("markerPoints size: " + markerPoints.size());
                    if (markerPoints.isEmpty()) {
                        if (locationManager != null) {
                            checkLocationPermission();
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                markerPoints.add(currentLatLng);
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(currentLatLng);
                                markerOptions.title("Current Position");
                                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
                                mCurrLocationMarker = mMap.addMarker(markerOptions);

                            } else {
                                System.out.println("location is null");
                            }
                        } else {
                            System.out.println("locationManager is null");
                        }
                    }
                    if (!isCleared) {
                        // TODO: 3/3/17 weather information pop-up window
                        try {
                            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
                            String cityName = addresses.get(0).getLocality();
                            String countryName= addresses.get(0).getCountryName();
                            city = cityName + ", " + countryName;
                            System.out.println("city should be: " + cityName);
                            System.out.println("country should be: " + countryName);
                            JSONWeatherTask task = new JSONWeatherTask();
                            task.execute(city);

                            if (currentWeatherInfoMaker != null) {
                                currentWeatherInfoMaker.remove();
                            }
                            currentWeatherInfoMaker = mMap.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)).title(city));

                        } catch (IOException e) {
                            System.out.println("Failed to get address for this point");
                        }

                        return;
                    }

                    destinationMarker.remove();

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);

                    /**
                     * For the start location, the color of marker is BLUE and
                     * for the end location, the color of marker is RED.
                     */
                    if(markerPoints.size()==1){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    }else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }

                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(options);

                    drawPolyLine();
                }
            });
        }
    }

    private void drawPolyLine() {
        // Checks, whether start and end locations are captured
        if (markerPoints.size() < 2) {
            return;
        }
        LatLng origin = markerPoints.get(0);
        LatLng dest = markerPoints.get(1);

        // Getting URL to the Google Directions API
        String url = getDirectionsUrl(origin, dest);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);
        isCleared = false;
    }

    private void initMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng latLng = new LatLng(lat, lon);
        destinationMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("New Montgomery St, San Francisco, CA"));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14));
        // permission check to enable my location
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public void clear(View view) {
        System.out.println("points clear button clicked");
        // clear the map
        mMap.clear();
        // clear destination
        markerPoints.remove(1);
        isCleared = true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    class Load extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;

        Load(ProgressDialog progressDialog) {
            this.progressDialog = progressDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // test initialization
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // check if enabled and if not send user to the GSP settings
            // Better solution would be to display a dialog and suggesting to
            // go to the settings
            if (!enabled) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
            checkPlayServices();

            return null;

        }

        @Override
        protected void onPostExecute(String file_url) {
            progressDialog.dismiss();
        }
    }
    private boolean checkPlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (status != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(status)) {
                showErrorDialog(status);
            } else {
                Toast.makeText(this, "This device is not supported.", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }



    void showErrorDialog(int code) {
        GooglePlayServicesUtil.getErrorDialog(code, this, REQUEST_CODE_RECOVER_PLAY_SERVICES).show();
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service

        return String.format("https://maps.googleapis.com/maps/api/directions/%s?%s",output, parameters);
    }
    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb = new StringBuilder();

            String line;
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("downloading url failed", e.toString());
        }finally{
            if (iStream != null) {
                iStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(15);
                lineOptions.color(Color.GRAY);
            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions == null) {
                System.out.println("got null polyLineOptions");
                Toast.makeText(getApplicationContext(),
                        "Invalid starting point or destination", Toast.LENGTH_SHORT).show();
            } else {
                tvDistanceDuration.setText("Distance:"+distance + "\nDuration:"+duration);
                System.out.println("Got distance and duration successfully");
                System.out.println("Distance:"+distance + ", Duration:"+duration);
                tvDistanceDuration.bringToFront();
                tvDistanceDuration.setTypeface(null, Typeface.BOLD);
                mMap.addPolyline(lineOptions);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        System.out.println();
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (markerPoints.size() == 2) {
            // update current latLng in list
            markerPoints.remove(0);
            markerPoints.add(0, currentLatLng);
        } else if (markerPoints.isEmpty()) {
            markerPoints.add(currentLatLng);
        }

        drawPolyLine();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {
                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
            }
            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    // weather task
    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconData != null && weather.iconData.length > 0) {
                Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(img);
            }
            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + "°C");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
            windDeg.setText("" + weather.wind.getDeg());
        }
    }
}


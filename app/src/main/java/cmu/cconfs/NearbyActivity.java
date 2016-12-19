package cmu.cconfs;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.w3c.dom.Text;

public class NearbyActivity extends AppCompatActivity {
    private static final int REQUEST_PLACE_PICKER = 1;

    private ImageView mPlaceImage;
    private TextView mNumberTv;
    private TextView mLocationTv;
    private TextView mWebsiteTv;
    private TextView mTypeTv;
    private TextView mRatingTv;
    private TextView mPriceLevelTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby);

        mPlaceImage = (ImageView) findViewById(R.id.image);
        mNumberTv = (TextView) findViewById(R.id.place_phone_number);
        mLocationTv = (TextView) findViewById(R.id.place_location);
        mWebsiteTv = (TextView) findViewById(R.id.place_website);
        mTypeTv = (TextView) findViewById(R.id.place_type);
        mRatingTv = (TextView) findViewById(R.id.place_rating);
        mPriceLevelTv = (TextView) findViewById(R.id.place_price_level);

        mNumberTv.setText("669-222-2245");
        try {
            PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
            Intent i = intentBuilder.build(this);
            startActivityForResult(i, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil
                    .getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException e) {
            Toast.makeText(this, "Google Play Services is not available.",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Toast.makeText(getApplicationContext(), "" + mNumberTv.toString(), Toast.LENGTH_LONG).show();
    }
}

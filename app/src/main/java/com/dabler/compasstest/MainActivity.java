package com.dabler.compasstest;

import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements CompassContract {

    CompassHelper compassHelper;
    LocationHelper locationHelper;

    private TextView distanceTextView;
    private EditText longitudeEditText;
    private EditText latitudeEditText;
    private Button navigateButton;
    private ImageView arrowView;
    private ImageView navigationArrow;

    private float oldNavigationPosition = 0;
    private float oldAzimuth = 0;

    private boolean navigate = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bind();
        SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        compassHelper = new CompassHelper(this, mSensorManager);
        locationHelper = new LocationHelper(this);
    }

    private void adjustArrow(ImageView arrow, float oldValue, float newValue) {
        Animation an = new RotateAnimation(-oldValue, -newValue,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);

        an.setDuration(250);
        an.setRepeatCount(0);
        an.setFillAfter(true);

        arrow.startAnimation(an);
    }

    public void Bind()
    {
        distanceTextView = (TextView)findViewById(R.id.distance_text_view);
        longitudeEditText = (EditText)findViewById(R.id.longitude_edit);
        latitudeEditText = (EditText)findViewById(R.id.latitude_edit);
        navigateButton = (Button)findViewById(R.id.navigate_confirm_button);
        arrowView = (ImageView)findViewById(R.id.main_image_hands);
        navigationArrow = (ImageView)findViewById(R.id.navigation_arrow);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String latitude = latitudeEditText.getText().toString();
                String longitude = longitudeEditText.getText().toString();
                try {
                    locationHelper.SetDestinationLocation(Float.valueOf(latitude), Float.valueOf(longitude));
                    navigate = true;
                }catch(NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Coordinates not entered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void CompassChanged(float azimuth) {
        adjustArrow(arrowView, oldAzimuth, azimuth);
        oldAzimuth = azimuth;
        if(navigate) {
            if(!locationHelper.IsLocationAvailable())
            {
                navigate = false;
                Toast.makeText(MainActivity.this, "Turn on GPS to continue", Toast.LENGTH_SHORT).show();
                return;
            }
            adjustArrow(navigationArrow, oldNavigationPosition, locationHelper.GetDegrees() - azimuth);
            oldNavigationPosition = locationHelper.GetDegrees() - azimuth;
            distanceTextView.setText("Distance left: "+String.format("%.2f", locationHelper.GetDistance()/1000)+" km");
            if(locationHelper.GetDistance() < 0.10f){
                navigate=false;
                distanceTextView.setText("Destination target reached");
            }
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        compassHelper.ResumeCompass();
        locationHelper.OnListenerResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        compassHelper.PauseCompass();
        locationHelper.OnListenerPause();
    }

    @Override
    public void onStart(){
        super.onStart();
        locationHelper.OnListenerStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        locationHelper.OnListenerStop();
    }

}

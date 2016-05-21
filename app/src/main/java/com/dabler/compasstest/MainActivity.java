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

import com.dabler.compasstest.Contracts.CompassContract;
import com.dabler.compasstest.Contracts.CompassListenerContract;
import com.dabler.compasstest.Contracts.CompassPresenterContract;
import com.dabler.compasstest.Contracts.CompassViewContract;
import com.dabler.compasstest.Contracts.LocationContract;
import com.dabler.compasstest.Contracts.LocationListenerContract;
import com.dabler.compasstest.Helpers.CompassHelper;
import com.dabler.compasstest.Helpers.LocationHelper;
import com.dabler.compasstest.Listeners.CompassListener;
import com.dabler.compasstest.Listeners.LocationListener;

public class MainActivity extends AppCompatActivity implements CompassViewContract {

    private CompassPresenterContract compassPresenter;

    private TextView distanceTextView;
    private EditText longitudeEditText;
    private EditText latitudeEditText;
    private Button navigateButton;
    private ImageView arrowView;
    private ImageView navigationArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bind();
        new CompassPresenter(this, this);
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
                    compassPresenter.SetDestinationLocation(Float.valueOf(latitude), Float.valueOf(longitude));
                }catch(NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Coordinates not entered", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        compassPresenter.OnResume();
        compassPresenter.start();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        compassPresenter.OnPause();
    }

    @Override
    public void onStart(){
        super.onStart();
        compassPresenter.OnStart();
    }

    @Override
    public void onStop(){
        super.onStop();
        compassPresenter.OnStop();
    }

    @Override
    public void setPresenter(CompassPresenterContract presenter) {
        compassPresenter = presenter;
    }

    @Override
    public void AdjustCompassArrow(float oldPosition, float newPosition) {
        adjustArrow(arrowView, oldPosition, newPosition);
    }

    @Override
    public void AdjustNavigationArrow(float oldPosition, float newPosition) {
        adjustArrow(navigationArrow, oldPosition, newPosition);
    }

    @Override
    public void ShowInformation(String information) {
        Toast.makeText(MainActivity.this, information, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void SetDistanceInformation(String information) {
        distanceTextView.setText(information);
    }
}

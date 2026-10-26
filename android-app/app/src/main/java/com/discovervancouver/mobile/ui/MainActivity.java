package com.discovervancouver.mobile.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.discovervancouver.mobile.BuildConfig;
import com.discovervancouver.mobile.R;
import com.discovervancouver.mobile.api.RecommendationApiClient;
import com.discovervancouver.mobile.api.RecommendationQuery;
import com.discovervancouver.mobile.model.Recommendation;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public final class MainActivity extends AppCompatActivity {

    private static final double DEFAULT_LATITUDE = 49.2827;
    private static final double DEFAULT_LONGITUDE = -123.1207;

    private double latitude = DEFAULT_LATITUDE;
    private double longitude = DEFAULT_LONGITUDE;

    private RecommendationApiClient apiClient;
    private RecommendationAdapter adapter;
    private Spinner categorySpinner;
    private Spinner budgetSpinner;
    private TextInputEditText atmosphereInput;
    private MaterialSwitch indoorSwitch;
    private MaterialSwitch openOnlySwitch;
    private SeekBar walkingRangeSeekBar;
    private TextView walkingRangeLabel;
    private TextView locationStatus;
    private TextView resultsHeading;
    private TextView messageView;
    private ProgressBar progressBar;
    private MaterialButton discoverButton;

    private final ActivityResultLauncher<String> locationPermissionLauncher =
        registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            granted -> {
                if (granted) {
                    updateFromLastKnownLocation();
                } else {
                    locationStatus.setText(R.string.location_default);
                }
            }
        );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        apiClient = new RecommendationApiClient(BuildConfig.API_BASE_URL);
        bindViews();
        configureInputs();
    }

    private void bindViews() {
        categorySpinner = findViewById(R.id.categorySpinner);
        budgetSpinner = findViewById(R.id.budgetSpinner);
        atmosphereInput = findViewById(R.id.atmosphereInput);
        indoorSwitch = findViewById(R.id.indoorSwitch);
        openOnlySwitch = findViewById(R.id.openOnlySwitch);
        walkingRangeSeekBar = findViewById(R.id.walkingRangeSeekBar);
        walkingRangeLabel = findViewById(R.id.walkingRangeLabel);
        locationStatus = findViewById(R.id.locationStatus);
        resultsHeading = findViewById(R.id.resultsHeading);
        messageView = findViewById(R.id.messageView);
        progressBar = findViewById(R.id.progressBar);
        discoverButton = findViewById(R.id.discoverButton);

        RecyclerView resultsList = findViewById(R.id.resultsList);
        adapter = new RecommendationAdapter();
        resultsList.setLayoutManager(new LinearLayoutManager(this));
        resultsList.setAdapter(adapter);
    }

    private void configureInputs() {
        setSpinnerValues(
            categorySpinner,
            List.of(
                "Any category",
                "Cafe",
                "Food",
                "Park",
                "Study",
                "Shopping",
                "Nightlife"
            )
        );
        setSpinnerValues(
            budgetSpinner,
            List.of("Any budget", "Free", "Low", "Medium", "High")
        );

        updateWalkingRangeLabel();
        walkingRangeSeekBar.setOnSeekBarChangeListener(
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(
                    SeekBar seekBar,
                    int progress,
                    boolean fromUser
                ) {
                    updateWalkingRangeLabel();
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) { }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) { }
            }
        );

        findViewById(R.id.locationButton).setOnClickListener(
            view -> requestLocation()
        );
        discoverButton.setOnClickListener(view -> loadRecommendations());
    }

    private void setSpinnerValues(Spinner spinner, List<String> values) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            values
        );
        spinnerAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        );
        spinner.setAdapter(spinnerAdapter);
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED) {
            updateFromLastKnownLocation();
            return;
        }

        locationPermissionLauncher.launch(
            Manifest.permission.ACCESS_COARSE_LOCATION
        );
    }

    private void updateFromLastKnownLocation() {
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationManager manager = (LocationManager) getSystemService(
            Context.LOCATION_SERVICE
        );
        Location location = manager.getLastKnownLocation(
            LocationManager.NETWORK_PROVIDER
        );
        if (location == null) {
            location = manager.getLastKnownLocation(
                LocationManager.GPS_PROVIDER
            );
        }

        if (location == null) {
            locationStatus.setText(
                "No recent location found; using downtown Vancouver"
            );
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        locationStatus.setText(R.string.location_ready);
    }

    private void loadRecommendations() {
        setLoading(true);
        RecommendationQuery query = new RecommendationQuery(
            latitude,
            longitude,
            selectedValue(categorySpinner, "Any category"),
            indoorSwitch.isChecked() ? Boolean.TRUE : null,
            walkingMinutes(),
            selectedValue(budgetSpinner, "Any budget"),
            textOf(atmosphereInput),
            openOnlySwitch.isChecked(),
            10
        );

        apiClient.getRecommendations(
            query,
            new RecommendationApiClient.Callback() {
                @Override
                public void onSuccess(
                    List<Recommendation> recommendations
                ) {
                    setLoading(false);
                    adapter.submitList(recommendations);
                    boolean empty = recommendations.isEmpty();
                    resultsHeading.setVisibility(
                        empty ? View.GONE : View.VISIBLE
                    );
                    messageView.setText(R.string.no_results);
                    messageView.setVisibility(
                        empty ? View.VISIBLE : View.GONE
                    );
                }

                @Override
                public void onError(String message) {
                    setLoading(false);
                    adapter.submitList(List.of());
                    resultsHeading.setVisibility(View.GONE);
                    messageView.setText(
                        "Could not load recommendations.\n" + message
                    );
                    messageView.setVisibility(View.VISIBLE);
                }
            }
        );
    }

    private String selectedValue(Spinner spinner, String emptyLabel) {
        String selected = spinner.getSelectedItem().toString();
        return selected.equals(emptyLabel) ? null : selected.toLowerCase();
    }

    private String textOf(TextInputEditText input) {
        if (input.getText() == null) {
            return null;
        }
        String value = input.getText().toString().trim();
        return value.isEmpty() ? null : value;
    }

    private int walkingMinutes() {
        return walkingRangeSeekBar.getProgress() + 5;
    }

    private void updateWalkingRangeLabel() {
        walkingRangeLabel.setText(getString(
            R.string.walking_range,
            walkingMinutes()
        ));
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        discoverButton.setEnabled(!loading);
        messageView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        apiClient.shutdown();
        super.onDestroy();
    }
}

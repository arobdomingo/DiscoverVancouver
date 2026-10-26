package com.discovervancouver.mobile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.discovervancouver.mobile.R;
import com.discovervancouver.mobile.model.Recommendation;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.stream.Collectors;

public final class PlaceDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECOMMENDATION =
        "com.discovervancouver.mobile.RECOMMENDATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_detail);

        Recommendation recommendation = getRecommendation();
        if (recommendation == null) {
            finish();
            return;
        }

        MaterialButton backButton = findViewById(R.id.backButton);
        MaterialButton mapsButton = findViewById(R.id.mapsButton);
        TextView name = findViewById(R.id.detailName);
        TextView meta = findViewById(R.id.detailMeta);
        TextView description = findViewById(R.id.detailDescription);
        TextView address = findViewById(R.id.detailAddress);
        TextView reasons = findViewById(R.id.detailReasons);
        TextView tags = findViewById(R.id.detailTags);

        backButton.setOnClickListener(view -> finish());
        mapsButton.setOnClickListener(view -> openMaps(recommendation));
        name.setText(recommendation.getName());
        meta.setText(String.format(
            Locale.CANADA,
            "%s • %d min walk • %s",
            recommendation.getCategory(),
            recommendation.getWalkingMinutes(),
            recommendation.isOpenNow() ? "Open now" : "Closed now"
        ));
        description.setText(recommendation.getDescription());
        address.setText(recommendation.getAddress());
        reasons.setText(recommendation.getReasons().stream()
            .map(reason -> "• " + reason)
            .collect(Collectors.joining("\n")));
        tags.setText(recommendation.getTags().stream()
            .map(tag -> "#" + tag)
            .collect(Collectors.joining("  ")));
    }

    private void openMaps(Recommendation recommendation) {
        String query = recommendation.getLatitude()
            + ","
            + recommendation.getLongitude()
            + "(" + Uri.encode(recommendation.getName()) + ")";
        Intent intent = new Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=" + query)
        );
        startActivity(Intent.createChooser(intent, "Open location with"));
    }

    @SuppressWarnings("deprecation")
    private Recommendation getRecommendation() {
        return (Recommendation) getIntent().getSerializableExtra(
            EXTRA_RECOMMENDATION
        );
    }
}

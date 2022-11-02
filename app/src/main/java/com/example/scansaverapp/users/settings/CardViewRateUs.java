package com.example.scansaverapp.users.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.scansaverapp.R;

public class CardViewRateUs extends Dialog {

    //rating
    private float userRate = 0;

    public CardViewRateUs(@NonNull Context context) {

        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.card_view_rate_us);

        RatingBar rateAppRatingBar = findViewById(R.id.rateAppRatingBar);
        AppCompatButton laterRateBtn = findViewById(R.id.laterRateBtn);
        AppCompatButton rateAppBtn = findViewById(R.id.rateAppBtn);
        AppCompatImageView ratingBarFace = findViewById(R.id.ratingBarFace);

        rateAppRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (rating <= 1) {
                    ratingBarFace.setImageResource(R.drawable.one_star);
                } else if (rating <= 2) {
                    ratingBarFace.setImageResource(R.drawable.two_star);
                } else if (rating <= 3) {
                    ratingBarFace.setImageResource(R.drawable.three_star);
                } else if (rating <= 4) {
                    ratingBarFace.setImageResource(R.drawable.four_star);
                } else if (rating <= 5) {
                    ratingBarFace.setImageResource(R.drawable.five_star);
                }

                //animation of emoji image
                animateRating(ratingBarFace);

                //set rating of user
                userRate = rating;

            }
        });

        rateAppBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

        laterRateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });

    }

    private void animateRating(ImageView ratingImage) {

        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1f, 0, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setFillAfter(true);
        scaleAnimation.setDuration(200);
        ratingImage.startAnimation(scaleAnimation);

    }
}

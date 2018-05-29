package com.android.view.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.R;
import com.android.service.model.PostalCodeRepository;
import com.android.viewmodel.NameViewModel;

public class LiveDataActivity extends AppCompatActivity {

    private NameViewModel mModel;
    private TextView mNameTextView;
    private Button mButton;
    private int i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_data);
        // Other code to setup the activity...
        // Get the ViewModel.
        mModel = ViewModelProviders.of(this).get(NameViewModel.class);
        mNameTextView = (TextView) findViewById(R.id.mNameTextView);
        mButton = (Button) findViewById(R.id.mButton);

        // Create the observer which updates the UI.
        Observer<PostalCodeRepository> nameObserver = new Observer<PostalCodeRepository>() {
            @Override
            public void onChanged(@Nullable PostalCodeRepository newName) {
                // Update the UI, in this case, a TextView.
                Log.e("----------", "onChanged: " + newName);
                mNameTextView.setText(newName.getAddress());
            }
        };

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String anotherName = "John Doe " + i++;
                Log.e("----------", "setOnClickListener: " + anotherName);
                PostalCodeRepository data = new PostalCodeRepository();
                data.setAddress(anotherName);
                mModel.getCurrentPostalData().setValue(data);
            }
        });


        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        mModel.getCurrentPostalData().observe(this, nameObserver);

    }
}

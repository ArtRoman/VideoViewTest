package ru.artroman.videotest.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ru.artroman.videotest.R;

/**
 * Activity with two buttons to launch activity with default or custom ViewView instance
 */
public class MainActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.main_button_default_videoview).setOnClickListener(this);
		findViewById(R.id.main_button_custom_videoview).setOnClickListener(this);
		findViewById(R.id.main_button_explayer_videoview).setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.main_button_default_videoview:
				startActivity(DefaultVideoActivity.class);
				break;

			case R.id.main_button_custom_videoview:
				startActivity(CustomVideoActivity.class);
				break;

			case R.id.main_button_explayer_videoview:
				startActivity(ExoplayerVideoActivity.class);
				break;
		}
	}

	private void startActivity(Class<? extends Activity> defaultVideoClass) {
		Intent intent = new Intent(this, defaultVideoClass);
		//intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}
}

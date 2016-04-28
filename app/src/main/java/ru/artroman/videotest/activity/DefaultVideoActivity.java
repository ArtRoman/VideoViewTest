package ru.artroman.videotest.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.Locale;

import ru.artroman.videotest.R;
import ru.artroman.videotest.utils.AssetsUtils;
import ru.artroman.videotest.utils.Log;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;

/**
 * Activity that uses default android VideoView instance
 */
public class DefaultVideoActivity extends Activity
		implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {

	private long counter = 0;
	private long initTime = System.currentTimeMillis();
	private long startActivityTime;
	private boolean isErrorOccurred;
	private VideoView mVideoView;
	private TextView textStatus;

	private Handler videoHandler = new Handler();
	private File externalVideoFile;

	@Override
	@SuppressWarnings("ConstantConditions")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_default);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

		startActivityTime = System.currentTimeMillis();
		externalVideoFile = AssetsUtils.getExternalVideoFile(this);
		textStatus = (TextView) findViewById(R.id.default_text_status);
		mVideoView = (VideoView) findViewById(R.id.default_video);
		mVideoView.setOnPreparedListener(this);
		initVideoView();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		videoHandler.removeCallbacks(videoTimeoutRunnable);
	}

	private void initVideoView() {
		if (externalVideoFile == null) return;
		Log.d("initializing player");
		mVideoView.setVideoPath(externalVideoFile.getAbsolutePath());
		videoHandler.postDelayed(videoTimeoutRunnable, 5000);
	}

	private void stopVideoView() {
		Log.d("stopping player");
		long timeDiffMs = System.currentTimeMillis() - initTime;
		initTime = System.currentTimeMillis();
		setStatus(getString(R.string.video_added_status), counter, timeDiffMs);

		videoHandler.removeCallbacks(videoTimeoutRunnable);
		mVideoView.stopPlayback(); // destroy video
		if (!isErrorOccurred) initVideoView(); // setup video
	}

	private void setStatus(String text, Object... args) {
		if (args != null && args.length > 0) text = String.format(Locale.getDefault(), text, args);
		textStatus.setText(text);
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		Log.d("OnPrepared");
		counter++;
		mVideoView.start();
		int delay = 0;
		if (counter % 100 == 0) {
			delay = 1000;
			setStatus(getString(R.string.video_showing_status), counter);
		}
		videoHandler.postDelayed(stopVideoViewRunnable, delay);
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		setStatus(getString(R.string.video_init_error), counter, what, extra);
		stopVideoView();
		return true;
	}

	/**
	 * Runnable to catch 5-sec timeout of video initialization
	 */
	private Runnable videoTimeoutRunnable = new Runnable() {
		@Override
		public void run() {
			// video timeout occurred
			long timeDiffSec = (System.currentTimeMillis() - startActivityTime) / 1000;
			setStatus(getString(R.string.video_adding_error), counter, timeDiffSec);
			isErrorOccurred = true;
		}
	};

	/**
	 * Runnable to destroy video
	 */
	private Runnable stopVideoViewRunnable = new Runnable() {
		@Override
		public void run() {
			stopVideoView();
		}
	};
}

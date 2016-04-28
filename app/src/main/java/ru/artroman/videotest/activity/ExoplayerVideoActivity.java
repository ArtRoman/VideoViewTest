package ru.artroman.videotest.activity;

import com.google.android.exoplayer.ExoPlaybackException;
import com.google.android.exoplayer.ExoPlayer;
import com.google.android.exoplayer.MediaCodecAudioTrackRenderer;
import com.google.android.exoplayer.MediaCodecSelector;
import com.google.android.exoplayer.MediaCodecVideoTrackRenderer;
import com.google.android.exoplayer.extractor.ExtractorSampleSource;
import com.google.android.exoplayer.upstream.Allocator;
import com.google.android.exoplayer.upstream.DataSource;
import com.google.android.exoplayer.upstream.DefaultAllocator;
import com.google.android.exoplayer.upstream.FileDataSource;

import android.app.Activity;
import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.TextView;

import java.io.File;
import java.util.Locale;

import ru.artroman.videotest.R;
import ru.artroman.videotest.utils.AssetsUtils;
import ru.artroman.videotest.utils.Log;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static com.google.android.exoplayer.MediaCodecVideoTrackRenderer.MSG_SET_SURFACE;

/**
 * Activity that uses Exoplayer for displaying video
 */
public class ExoplayerVideoActivity extends Activity implements ExoPlayer.Listener {

	private long counter = 0;
	private long initTime = System.currentTimeMillis();
	private long startActivityTime;
	private boolean isErrorOccurred;
	private SurfaceView mSurfaceView;
	private TextView textStatus;
	private ExoPlayer player;

	private Handler videoHandler = new Handler();
	private File externalVideoFile;

	private final static int RENDERER_COUNT = 2; // video renderer + audio renderer
	private final static int BUFFER_SEGMENT_SIZE = 64 * 1024;
	private final static int BUFFER_SEGMENT_COUNT = 256;
	private final static int BUFFER_SIZE = BUFFER_SEGMENT_SIZE * BUFFER_SEGMENT_COUNT;

	private final static MediaCodecSelector CODEC_SELECTOR = MediaCodecSelector.DEFAULT;
	private final static int SCALING_MODE = MediaCodec.VIDEO_SCALING_MODE_SCALE_TO_FIT;

	@Override
	@SuppressWarnings("ConstantConditions")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exoplayer);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getWindow().addFlags(FLAG_KEEP_SCREEN_ON);

		startActivityTime = System.currentTimeMillis();
		externalVideoFile = AssetsUtils.getExternalVideoFile(this);
		textStatus = (TextView) findViewById(R.id.exoplayer_text_status);
		mSurfaceView = (SurfaceView) findViewById(R.id.exoplayer_surface);
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
		stopVideoView(false);
		videoHandler.removeCallbacks(videoTimeoutRunnable);
	}

	private void initVideoView() {
		if (externalVideoFile == null) return;
		Log.d("initializing player");
		videoHandler.postDelayed(videoTimeoutRunnable, 5000);

		Allocator allocator = new DefaultAllocator(BUFFER_SEGMENT_SIZE);
		DataSource dataSource = new FileDataSource();
		Uri videoFileUri = Uri.fromFile(externalVideoFile);
		ExtractorSampleSource sampleSource = new ExtractorSampleSource(videoFileUri, dataSource, allocator, BUFFER_SIZE);
		MediaCodecVideoTrackRenderer videoRenderer = new MediaCodecVideoTrackRenderer(this, sampleSource, CODEC_SELECTOR, SCALING_MODE);
		MediaCodecAudioTrackRenderer audioRenderer = new MediaCodecAudioTrackRenderer(sampleSource, CODEC_SELECTOR);

		player = ExoPlayer.Factory.newInstance(RENDERER_COUNT);
		player.addListener(this);
		player.prepare(videoRenderer, audioRenderer);
		player.sendMessage(videoRenderer, MSG_SET_SURFACE, mSurfaceView.getHolder().getSurface());
		player.setPlayWhenReady(true);
	}

	private void stopVideoView(boolean shouldInitAgain) {
		Log.d("stopping player");
		long timeDiffMs = System.currentTimeMillis() - initTime;
		initTime = System.currentTimeMillis();
		setStatus(getString(R.string.video_added_status), counter, timeDiffMs);

		videoHandler.removeCallbacks(videoTimeoutRunnable);
		player.stop();
		player.removeListener(this);
		player.release(); // Don’t forget to release when done!

		if (!isErrorOccurred && shouldInitAgain) initVideoView(); // setup video
	}

	private void setStatus(String text, Object... args) {
		if (args != null && args.length > 0) text = String.format(Locale.getDefault(), text, args);
		textStatus.setText(text);
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
			stopVideoView(true);
		}
	};

	/**
	 * Exoplayer events
	 */
	@Override
	public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
		Log.d("Exoplayer onPlayerStateChanged: playWhenReady=%s, playbackState=%s", playWhenReady, getStateName(playbackState));
		if (playWhenReady && playbackState == ExoPlayer.STATE_READY) {
			counter++;
			int delay = 0;
			if (counter % 100 == 0) {
				delay = 1000;
				setStatus(getString(R.string.video_showing_status), counter);
			}
			videoHandler.postDelayed(stopVideoViewRunnable, delay);
		}
	}

	@Override
	public void onPlayWhenReadyCommitted() {
		Log.d("Exoplayer onPlayWhenReadyCommitted");
	}

	@Override
	public void onPlayerError(ExoPlaybackException error) {
		Log.d("Exoplayer ExoPlaybackException: error");
		error.printStackTrace();
		setStatus(getString(R.string.video_init_error), counter, 0, 0);
		stopVideoView(false);
	}

	private static String getStateName(int state) {
		switch (state) {
			case ExoPlayer.STATE_BUFFERING:
				return "buffering";
			case ExoPlayer.STATE_ENDED:
				return "ended";
			case ExoPlayer.STATE_IDLE:
				return "idle";
			case ExoPlayer.STATE_PREPARING:
				return "preparing";
			case ExoPlayer.STATE_READY:
				return "ready";
			default:
				return "unknown";
		}
	}
}

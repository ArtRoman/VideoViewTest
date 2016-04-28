# VideoViewTest
Testing video playback issues on Android platform.

Android up to 4.4 has VideoView init limit (2k-8k, depending on device), then it gets MediaPlayer.onPrepared event timeout and strange memory issues: throwing TransactionTooLargeException in any time of using external services like PackageManager, stopping app when starting/closing activity and restarting app in another process (so device can keep up to 5 processes with your app at the same time), etc.

Copy-pasting the VideoView class without using MediaController and SubtitleController helps a lot, but on large numbers of init times it can be not stable, too.

Using ExoPlayer seems to be a good solution, but it also have hardware problems on some devices when using some SurfaceViews simultaneously (OMX.amlogic.avc.decoder.awesome ERROR 0x80001000).

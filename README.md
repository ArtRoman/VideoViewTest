# VideoViewTest
Testing video playback issues on Android platform.

Android up to 4.4 has VideoView init limit (2k-8k, depending from device), then it got MediaPlayer.onPrepared event timeout and some memory issues: throwing TransactionTooLargeException, stopping activity with killing process and restarting it in another process (so you can have up to 5 processes with your app), etc.

Copy-pasting the VideoView class without MediaController and SubtitleController helpes a lot, but on large numbers of inits it can be not stable, too.

Using ExoPlayer is a good solution, but it also have hardware problems on some devices when using some SurfaceViews simultaneously.

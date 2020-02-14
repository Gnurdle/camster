## Camster

Security cam grabs an image every 5 seconds.

Said cam ftps this to a server directory by <date>/images/P<time>

This is a (shadow=)cljs project to turn all but the last days worth of shots into a video (with ffmpeg) for the day

Decimates the image directory afterward, leaving only the video

It's a very simple clojurescript project, using node's affordances to interact with the filesystem.

Might be useful either as an example, or hacked for your situation.
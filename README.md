## Camster

Our (cheap) security cams grab an image every 5 seconds.

Said cams then FTPs these to a server directory by \<date\>/images/P\<time\>

This is a (shadow-)cljs project to turn all but the last days worth of shots into a video (with ffmpeg) for the day

Decimates the image directory afterward, leaving only the video

It's a very simple clojurescript project, using node's affordances to interact with the filesystem.

Might be useful either as an example, or hacked for your situation.

First time I contemplated using cljs over python for such chores - mostly to see of things went

uses https://github.com/zeit/ncc to bundle all the dependencies so that a single node script can be deployed

<p align="center">
	<img height="128" src="https://github.com/Razzula/turtle-cog/blob/main/icon.png">
</p>
<h1 align="center">Turtlecog</h1>

A RPi robotics project. Control a robot using an Android app, via sockets.

## Contents
- `controller\` is an Android Studio Java project, which acts as the the remote control for the robot.
- The `.py` scripts are to be run on the Raspberry Pi, and act as the receiving server for the app.

## Notes
`server.py` assumes there are two motors, controlled by GPIO pins 11,13,15 and 22,16,18

## Acknowledgments
- Logo - Joshua Bond

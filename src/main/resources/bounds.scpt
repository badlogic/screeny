#!/usr/bin/env osascript
tell application "System Events"
	set proc to name of first process whose frontmost is true
	tell window 1 of process proc
		-- get window initial position
		copy position to {ini_x, ini_y}
		copy size to {win_w, win_h}
	end tell
end tell

return {ini_x,ini_y,win_w,win_h}

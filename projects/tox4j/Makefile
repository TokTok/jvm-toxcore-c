capture/output.mp4: capture/video/000.jpg
	ffmpeg -r 25 -i capture/video/%03d.jpg $@

upload: capture/output.mp4
	curl -i -F name="$<" -F file="@$<" "https://uguu.se/api.php?d=upload-tool"
	@echo

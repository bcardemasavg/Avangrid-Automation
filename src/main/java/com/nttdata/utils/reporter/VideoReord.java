package com.nttdata.utils.reporter;

import org.monte.media.FormatKeys;

import java.awt.Dimension;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;

import org.monte.media.Format;
import org.monte.media.FormatKeys.MediaType;
import org.monte.media.VideoFormatKeys;
import org.monte.media.math.Rational;
import org.monte.screenrecorder.ScreenRecorder;

import com.nttdata.utils.runner.MainRun;

public class VideoReord {

	public static final String REC_VIDEOS_FILES_FOLDER = "rec_videos";
	public static File file = null;
	public String name;

	private ScreenRecorder screenRecorder;

	public VideoReord(String name) {
		this.name = name;
	}

	public void startRecording() throws Exception {
		file = new File(MainRun.REPORT_FOLDER + File.separator + REC_VIDEOS_FILES_FOLDER);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int width = screenSize.width;
		int height = screenSize.height;

		Rectangle captureSize = new Rectangle(0, 0, width, height);

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();

		this.screenRecorder = new SpecializedScreenRecorder(gc, captureSize,
				new Format(FormatKeys.MediaTypeKey, MediaType.FILE, FormatKeys.MimeTypeKey, FormatKeys.MIME_AVI),
				new Format(FormatKeys.MediaTypeKey, MediaType.VIDEO, FormatKeys.EncodingKey,
						VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, VideoFormatKeys.CompressorNameKey,
						VideoFormatKeys.ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE, VideoFormatKeys.DepthKey, 24, FormatKeys.FrameRateKey,
						Rational.valueOf(15), VideoFormatKeys.QualityKey, 1.0f, FormatKeys.KeyFrameIntervalKey,
						15 * 60),
				new Format(VideoFormatKeys.MediaTypeKey, MediaType.VIDEO, FormatKeys.EncodingKey, "black",
						FormatKeys.FrameRateKey, Rational.valueOf(30)),
				null, file, name);
		this.screenRecorder.start();
		file = ((SpecializedScreenRecorder) this.screenRecorder).fileName;
		System.out.println("Recording Video: " + file.getAbsolutePath());

	}

	public void stopRecording() throws Exception {
		this.screenRecorder.stop();
		System.out.println("Recording Video: " + file.getAbsolutePath());
	}
}

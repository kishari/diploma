package hu.messaging.client.media.video;

import hu.messaging.client.Resources;

import java.io.IOException;
import java.util.Vector;

import javax.media.CannotRealizeException;
import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.IncompatibleSourceException;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.NoDataSourceException;
import javax.media.NoPlayerException;
import javax.media.NoProcessorException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;

public class VideoRecorder {

	private static final String defaultVideoDeviceName = "Microsoft WDM Image Capture";
	private static final String defaultAudioDeviceName = "DirectSoundCapture";
	private static final String defaultVideoFormatString = "size=640x480, encoding=yuv, maxdatalength=614400";
	private static final String defaultAudioFormatString = "linear, 44100.0 hz, 16-bit, stereo, littleendian, signed";
	
	private CaptureDeviceInfo captureVideoDevice = null;
	private CaptureDeviceInfo captureAudioDevice = null;
	private VideoFormat captureVideoFormat = null;
	private AudioFormat captureAudioFormat = null;
	
	private MediaLocator videoMediaLocator = null;
	private DataSource videoDataSource = null;
	
	private MediaLocator audioMediaLocator = null;
	private DataSource audioDataSource = null;
	
	private DataSource mixedDataSource = null;
	
	private FileTypeDescriptor outputType = null;
	private Format outputFormat[] = null;
	
	private ProcessorModel processorModel = null;
	private Processor processor = null;
	
	private DataSource dataOutput = null;
	private MediaLocator destination = null;
	
	private DataSink dataSink = null;
	private DataSinkAdapter dataSinkListener = null;

	private boolean enabled = false;
	public VideoRecorder() {
		Vector<CaptureDeviceInfo> deviceList = CaptureDeviceManager.getDeviceList(null);
		
		for (int x = 0; x < deviceList.size(); x++) {
			CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceList.elementAt(x);
			//System.out.println(deviceInfo.getName());

			Format deviceFormat[] = deviceInfo.getFormats();
			for (int y = 0; y < deviceFormat.length; y++) {
				//System.out.println("capture format = " + DeviceInfo.formatToString(deviceFormat[y]));
				// search for default video device
				if (captureVideoDevice == null) {
					if (deviceFormat[y] instanceof VideoFormat) {
						if (deviceInfo.getName().indexOf(defaultVideoDeviceName) >= 0) {
							captureVideoDevice = deviceInfo;
						}
					}
				}

				// search for default video format
				if (captureVideoDevice == deviceInfo) {
					if (captureVideoFormat == null) {
						if (DeviceInfo.formatToString(deviceFormat[y]).indexOf(
								defaultVideoFormatString) >= 0) {
							captureVideoFormat = (VideoFormat) deviceFormat[y];
						}
					}
				}
				
				// search for default audio device
				if (captureAudioDevice == null) {
					if (deviceFormat[y] instanceof AudioFormat) {
						if (deviceInfo.getName().indexOf(defaultAudioDeviceName) >= 0) {
							captureAudioDevice = deviceInfo;
						}
					}
				}

				// search for default audio format
				if (captureAudioDevice == deviceInfo) {
					if (captureAudioFormat == null) {
						if (DeviceInfo.formatToString(deviceFormat[y]).indexOf(
								defaultAudioFormatString) >= 0) {
							captureAudioFormat = (AudioFormat) deviceFormat[y];
						}
					}
				}
			}
		}
	}
	
	public void init() {
		videoMediaLocator = captureVideoDevice.getLocator();
		
		try {
			videoDataSource = Manager.createCloneableDataSource(Manager.createDataSource(videoMediaLocator));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (NoDataSourceException e) {
			e.printStackTrace();
			System.exit(0);
		}

		if (!DeviceInfo.setFormat(videoDataSource, captureVideoFormat)) {
			System.out.println("Error: unable to set video format - program aborted");
			System.exit(0);
		}


		// setup audio data source
		// -----------------------
		audioMediaLocator = captureAudioDevice.getLocator();
		
		try {
			audioDataSource = Manager.createDataSource(audioMediaLocator);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (NoDataSourceException e) {
			e.printStackTrace();
			System.exit(0);
		}

		if (!DeviceInfo.setFormat(audioDataSource, captureAudioFormat)) {
			System.out.println("Error: unable to set audio format - program aborted");
			System.exit(0);
		}


		// merge the two data sources
		// --------------------------
		try {
			DataSource dArray[] = new DataSource[2];
			dArray[0] = videoDataSource;
			dArray[1] = audioDataSource;
			mixedDataSource = javax.media.Manager.createMergingDataSource(dArray);
		} catch (IncompatibleSourceException e) {
			e.printStackTrace();
			System.exit(0);
		}


		// create a new processor
		// ----------------------
		// setup output file format  ->> msvideo
		outputType = new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO);

		// setup output video and audio data format
		outputFormat = new Format[2];
		outputFormat[0] = new VideoFormat(VideoFormat.YUV);

        AudioFormat audioFormat = Resources.getVideoAudioFormat();
		outputFormat[1] = audioFormat;

		// create processor
		processorModel = new ProcessorModel(mixedDataSource, outputFormat, outputType);


		try {
			processor = Manager.createRealizedProcessor(processorModel);
			//player = Manager.createRealizedPlayer(videoMediaLocator);
			
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (NoProcessorException e) {
			e.printStackTrace();
			System.exit(0);
		} catch (CannotRealizeException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// get the output of the processor
		dataOutput = processor.getDataOutput();

		// create a File protocol MediaLocator with the location
		// of the file to which bits are to be written
		destination = new MediaLocator("file:testcam.avi");

		// create a datasink to do the file
		dataSink = null;
		dataSinkListener = null;
		try {
			dataSink = Manager.createDataSink(dataOutput, destination);
			dataSinkListener = new DataSinkAdapter();
			dataSink.addDataSinkListener(dataSinkListener);
		} catch (NoDataSinkException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void startCapture() {
		enabled = true;
		new Thread() {
			public void run() {
				try {
					dataSink.open();
					dataSink.start();
					System.out.println("datasink open and start");
				} catch (IOException e) {
					e.printStackTrace();
				}
				processor.start();
				while (enabled) {
					try {
						Thread.currentThread().sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
			}
		}.start();			
	}
	
	public void stopCapture() {
		enabled = false;
		// stop and close the processor when done capturing...
		// close the datasink when EndOfStream event is received...
		processor.stop();
		processor.close();

		dataSinkListener.waitEndOfStream(10);
		dataSink.close();
	}

	public CaptureDeviceInfo getCaptureVideoDevice() {
		return captureVideoDevice;
	}

	public Processor getProcessor() {
		return processor;
	}

	public DataSource getDataOutput() {
		return dataOutput;
	}

	public DataSource getVideoDataSource() {
		return videoDataSource;
	}
	
	
}

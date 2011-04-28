package hu.messaging.client.media.video;

import hu.messaging.client.Resources;
import hu.messaging.client.gui.dialog.CaptureDialog;
import hu.messaging.client.gui.util.FileUtils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Observable;
import java.util.Vector;

import javax.media.*;
import javax.media.control.FrameGrabbingControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.SourceCloneable;
import javax.media.util.BufferToImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class VideoRecorder extends Observable{

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
	
	private VisualComponent visualComponent = null;
	
	public void dispose() {
		visualComponent.player.close();
		visualComponent.dispose();
	}
	
	public VideoRecorder(CaptureDialog cDialog) {
		this.addObserver(cDialog);
		
		Vector<CaptureDeviceInfo> deviceList = CaptureDeviceManager.getDeviceList(null);
		
		for (int x = 0; x < deviceList.size(); x++) {
			CaptureDeviceInfo deviceInfo = (CaptureDeviceInfo) deviceList.elementAt(x);

			Format deviceFormat[] = deviceInfo.getFormats();
			for (int y = 0; y < deviceFormat.length; y++) {

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
		
		visualComponent = new VisualComponent();
	}
	
	private void init() {
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
		destination = new MediaLocator("file:" + Resources.getMessagesDirectoryPath() + "capturedVideo.avi");

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
		this.visualComponent.startCapture();
		
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
						Thread.sleep(10);
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
		
		visualComponent.stopCapture();
		
		byte[] capturedVideo = FileUtils.readFileToByteArray(new File(Resources.getMessagesDirectoryPath() + "capturedVideo.avi"));
		this.setChanged();
		this.notifyObservers(capturedVideo);
		
	}
	
	public void takeAnImage() {
		Buffer buff;
		BufferToImage btoi=null;
		FrameGrabbingControl fgc = (FrameGrabbingControl) visualComponent.player.getControl("javax.media.control.FrameGrabbingControl");
		buff = fgc.grabFrame();
        btoi= new BufferToImage((VideoFormat)buff.getFormat());
        Image img = btoi.createImage(buff);
        saveJPG(img, Resources.getMessagesDirectoryPath() + "capturedPicture.jpg");
        byte[] capturedPicture = FileUtils.readFileToByteArray(new File(Resources.getMessagesDirectoryPath() + "capturedPicture.jpg"));
        this.setChanged();
		this.notifyObservers(capturedPicture);
	}
	
	private  void saveJPG(Image img, String s) {
        BufferedImage bi= new BufferedImage(img.getWidth(null),img.getHeight(null),BufferedImage.TYPE_INT_RGB);
        Graphics2D g2=bi.createGraphics();
        g2.drawImage(img, null, null);
        FileOutputStream out=null;
        try{
            out = new FileOutputStream(s);
        }
        catch(java.io.FileNotFoundException ex){
            ex.printStackTrace();
        }
        JPEGImageEncoder encoder= JPEGCodec.createJPEGEncoder(out);
        JPEGEncodeParam param= encoder.getDefaultJPEGEncodeParam(bi);
        param.setQuality(0.5f, false);
        encoder.setJPEGEncodeParam(param);
        try{
            encoder.encode(bi);
            out.close();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }
	
	private class VisualComponent extends JFrame {
		private DataSource ds = null;
		private Player player;

		private JPanel videoPanel = null;
		
		public VisualComponent() {
			try {
					// Create a Player for Media Located by MediaLocator
				player = Manager.createRealizedPlayer(captureVideoDevice.getLocator());
				
				if (player != null) {
					player.start();
					
					setPreferredSize(new Dimension(640, 480));
					setTitle("Capture video");
					setLayout(new BorderLayout());
					Component c = player.getVisualComponent();
					c.setSize(100, 100);
					
					videoPanel = new JPanel();
					videoPanel.setLayout(new BorderLayout());
					videoPanel.add(c, BorderLayout.CENTER);
					
					add(videoPanel, BorderLayout.CENTER);
					pack();
					setVisible(true);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void startCapture() {
			try {
				player.close();
				
				init();
				ds = ((SourceCloneable)videoDataSource).createClone();
				player = Manager.createRealizedPlayer(ds);
				player.start();
				
				Component c = player.getVisualComponent();
				videoPanel.remove(0);
				videoPanel.add(c, BorderLayout.CENTER);

			}catch(IOException e) {
				e.printStackTrace();
			} catch (CannotRealizeException e) {
				e.printStackTrace();
			}
			catch (NoPlayerException e) {
				e.printStackTrace();
			}

		}
		
		private void stopCapture() {
			try{
				player.close();
				player = Manager.createRealizedPlayer(captureVideoDevice.getLocator());
				
				player.start();
				
				Component c = player.getVisualComponent();
				videoPanel.remove(0);
				videoPanel.add(c, BorderLayout.CENTER);
			}catch(IOException e) {
				e.printStackTrace();
			} catch (CannotRealizeException e) {
				e.printStackTrace();
			}
			catch (NoPlayerException e) {
				e.printStackTrace();
			}
		}

		public Player getPlayer() {
			return player;
		}
	}
	
	
}

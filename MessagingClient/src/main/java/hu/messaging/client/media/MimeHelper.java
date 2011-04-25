package hu.messaging.client.media;

import java.util.HashMap;
import java.util.*;

public class MimeHelper {
	
	private static final Map<String, String> mimeTypesExtensionsMap = new HashMap<String, String>(){
		{
			put("image/gif", "gif");
			put("image/jpeg", "jpg");
	        put("image/bmp", "bmp");
	        put("audio/mpeg", "mp3");
	        put("audio/x-wav", "wav");
	        put("video/x-msvideo", "avi");
	        put("video/mpeg", "mpeg");
		}
	};
	
	private static final Map<String, String> extensionsMimTypesMap = new HashMap<String, String>(){
		{
			put("gif", "image/gif");
			put("jpg", "image/jpeg");
			put("jpeg", "image/jpeg");
			put("jpe", "image/jpeg");
	        put("bmp", "image/bmp");
	        put("mp3", "audio/mpeg");
	        put("wav", "audio/x-wav");
	        put("avi", "video/x-msvideo");
	        put("mpeg", "video/mpeg");
	        put("mpe", "video/mpeg");
		}
	};
	
	public static String getMIMETypeByExtension(String extension) {
		String ext = extension != null ? extension.toLowerCase().trim() : "";
		String mimeType = extensionsMimTypesMap.get(ext) == null ? "" : extensionsMimTypesMap.get(ext);
		return mimeType;
	}
	
	public static String getExtensionByMIMEType(String mimeType) {
		String mmt = mimeType != null ? mimeType.toLowerCase().trim() : "";
		String extension = mimeTypesExtensionsMap.get(mmt) == null ? "" : mimeTypesExtensionsMap.get(mmt);
		return extension;
	}

}

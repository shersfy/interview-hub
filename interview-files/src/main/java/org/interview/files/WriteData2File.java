package org.interview.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;


public class WriteData2File {
	
	
	public void writeTextAndStream2File(String text, InputStream input, File file) throws IOException {
		text = text==null?"":text;
		OutputStream output = null;
		try {
			output = new FileOutputStream(file, false);
			IOUtils.write(text, output);
			if(input!=null) {
				IOUtils.copy(input, output);
			}
			output.flush();
			
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
}

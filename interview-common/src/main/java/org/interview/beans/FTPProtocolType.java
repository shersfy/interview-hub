package org.interview.beans;

public enum FTPProtocolType {
	/**简单文件传输协议**/
	FTP,
	/**SSH文件传输协议**/
	SFTP;

	public static FTPProtocolType indexOf(int index){
		switch (index) {
		case 1:
			return FTP;
		case 2:
			return SFTP;
		}
		return FTP;
	}

	public int index(){
		return this.ordinal()+1;
	}
}

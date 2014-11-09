package com.ostrichmyself.txtReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.apache.http.util.EncodingUtils;

import android.util.Log;

public class TxtSource {
	private static final String TAG = "TxtSource";
	private static final String defaultCode = "UTF-8";
	private	 RandomAccessFile raf;
	private int maxRowNum;
	
	private byte[][] bufferList;
	private String[] stringList;
	private final int BUFFERSIZE = 1000;
	
	public boolean initFail = false;
	
	public static enum READ_DIRECTION {
		FORWARD,
		BACKWARD,
		RANDOM,
	};
	
	private int curBuf = 0;
	private int curStringPos = 0;

	public TxtSource(String fileName, int rowMax) throws IOException{
		Log.d(TAG, "TxtSource init !!");
		File file = new File(fileName);
		if (!file.exists()){
			Log.d(TAG, "file is not exist");
			initFail = true;
		}
		raf = new RandomAccessFile(file, "r");
		
		maxRowNum = rowMax;
		bufferList = new byte[2][BUFFERSIZE];
		stringList = new String[2];
		if(-1 == updateBuffer(0)) {
			Log.d(TAG, "update Buffer failed!!");
			initFail = true;
		}
	}

	//which: 0 means both, 1 means one
	private int updateBuffer(int which) throws IOException {
		Log.d(TAG, "updateBuffer !! which(" + which + ")");
		int res = -1;
		if (which == 0){  //update both buffer
			res = raf.read(bufferList[0]);
			//Log.d(TAG, "updateBuffer 22 !! read " + res + " in buffer 0");
			if (res == -1 ){
				return -1;
			}
			stringList[0] = EncodingUtils.getString(bufferList[0], defaultCode);
			
			res = raf.read(bufferList[1]);
			//Log.d(TAG, "updateBuffer 33 !! read " + res + " in buffer 1");
			if (res == -1 ){
				return -1;
			}
			stringList[1] = EncodingUtils.getString(bufferList[1], defaultCode);
			
			curBuf = 0;
			curStringPos = 0;
			
		} else if (which == 1){  //update the other one buffer
			int needToFlush = (curBuf + 1)%2;
			res = raf.read(bufferList[needToFlush]);
			if (res == -1 ){
				return -1;
			}
			//Log.d(TAG, "updateBuffer 44 !! read " + res + " in buffer " + curBuf);
			stringList[needToFlush] = EncodingUtils.getString(bufferList[needToFlush], defaultCode);
		}
		
		return res;
	}
	
	public int read(READ_DIRECTION direction, int pos) throws IOException{
		Log.d(TAG, "read !!");
		switch (direction){
		case FORWARD:
			break;
		case BACKWARD:
			//inputStream.reset();
			//updateBuffer(0);
			break;
		case RANDOM:
			break;
		default:
			break;
		}
		return 0;
	}
	
	public String getLine() throws IOException{
		Log.d(TAG, "getLine start ! curStringPos : " + curStringPos);
		String temp;
		String res;
		if (curStringPos + maxRowNum < stringList[curBuf].length()) {    //get string from one buffer
			temp = stringList[curBuf].substring(curStringPos, curStringPos + maxRowNum);
			
			int lineEnd = temp.indexOf("\n");
			//Log.d(TAG, "lineEnd : " + lineEnd);
			if (lineEnd != -1){
				res = temp.substring(0, lineEnd);
				curStringPos += lineEnd + 1;
			} else {
				res = temp;
				curStringPos += maxRowNum;
			}
			//Log.d(TAG, "getLine end.  res : " + res);
			return res;
			
		} else {
			//get string from two buffers, it needs to consider change curBuf and update buffer
			Log.d(TAG, "get string between two buffers");
			int otherBuffer = (curBuf + 1)%2;
			temp = stringList[curBuf].substring(curStringPos)
					+ stringList[otherBuffer].substring(0, maxRowNum - (stringList[curBuf].length() - curStringPos));
			
			int lineEnd = temp.indexOf("\n");
			if (lineEnd != -1){
				res = temp.substring(0, lineEnd);
				if (lineEnd < (stringList[curBuf].length() - curStringPos)) {
					curStringPos += lineEnd + 1;
				} else {
					curStringPos = lineEnd - (stringList[curBuf].length() - curStringPos);
					curBuf = (curBuf + 1)%2;
				}
			} else {
				res = temp;
				curStringPos = maxRowNum - (stringList[curBuf].length() - curStringPos);
				curBuf = (curBuf + 1)%2;
			}
			updateBuffer(1);
			return res;
		}
	}
}

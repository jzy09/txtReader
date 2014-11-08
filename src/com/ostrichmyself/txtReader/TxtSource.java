package com.ostrichmyself.txtReader;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.util.EncodingUtils;

import android.util.Log;

public class TxtSource {
	private static final String TAG = "TxtSource";
	private static final String defaultCode = "UTF-8";
	private	InputStream inputStream;
	private int maxRowNum;
	
	private byte[][] bufferList;
	private String[] stringList;
	private final int BUFFERSIZE = 1000;
	
	public enum READ_DIRECTION {
		FORWARD,
		BACKWARD,
		RANDOM,
	};
	
	private int curBuf = 0;
	private int curStringPos = 0;

	public TxtSource(InputStream is, int rowMax) throws IOException{
		Log.d(TAG, "TxtSource init !!");
		inputStream = is;
		maxRowNum = rowMax;
		
		bufferList = new byte[2][BUFFERSIZE];
		stringList = new String[2];
		if(-1 == updateBuffer(0)) {
			Log.d(TAG, "update Buffer failed!!");
			return;
		}
	}

	private int updateBuffer(int which) throws IOException {  //which: 0 means both, 1 means one
		// TODO Auto-generated method stub
		Log.d(TAG, "updateBuffer !! which(" + which + ")");
		int res = -1;
		if (which == 0){  //update both buffer
			res = inputStream.read(bufferList[0]);
			//Log.d(TAG, "updateBuffer 22 !! read " + res + " in buffer 0");
			if (res == -1 ){
				return -1;
			}
			stringList[0] = EncodingUtils.getString(bufferList[0], defaultCode);
			
			res = inputStream.read(bufferList[1]);
			//Log.d(TAG, "updateBuffer 33 !! read " + res + " in buffer 1");
			if (res == -1 ){
				return -1;
			}
			stringList[1] = EncodingUtils.getString(bufferList[1], defaultCode);
			
		} else if (which == 1){  //update the other one buffer
			int needToFlush = (curBuf + 1)%2;
			res = inputStream.read(bufferList[needToFlush]);
			if (res == -1 ){
				return -1;
			}
			//Log.d(TAG, "updateBuffer 44 !! read " + res + " in buffer " + curBuf);
			stringList[needToFlush] = EncodingUtils.getString(bufferList[needToFlush], defaultCode);
		}
		
		return res;
	}
	
	public int read(int direction, int pos, int size){
		Log.d(TAG, "read !!");
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

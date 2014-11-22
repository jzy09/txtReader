package com.ostrichmyself.txtReader;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.util.EncodingUtils;

import android.util.Log;

public class TxtSource {
	private static final String TAG = "TxtSource";
	private static final String defaultCode = "UTF-8";
	private	 RandomAccessFile raf;
	private int maxRowNum;
	private int maxColumeNum;
	
	private List<String> strBufferList;
	private int strBufferListCurIndex;
	private final int BUFFERSIZE = 2000;
	private long bufferHeadFilePos;
	private long bufferEndFilePos;
	
	public boolean initFail = false;
	
	public static enum READ_DIRECTION {
		FORWARD,
		BACKWARD,
		RANDOM,
	};
	
	public TxtSource(String fileName, int rowMax) throws IOException{
		Log.d(TAG, "TxtSource init !!");
		File file = new File(fileName);
		if (!file.exists()){
			Log.d(TAG, "file is not exist");
			initFail = true;
		}
		raf = new RandomAccessFile(file, "r");
		
		maxRowNum = rowMax;
		strBufferListCurIndex = 0;
		strBufferList = new ArrayList<String>();
		bufferHeadFilePos = 0;
		bufferEndFilePos = 0;
		
		if(-1 == updateBuffer(0)) {
			Log.d(TAG, "update Buffer failed!!");
			initFail = true;
		}
	}
	
	public void setMaxColumeNum(int size){
		maxColumeNum = size;
	}

	//which: 0 means both, 1 means one
	private int updateBuffer(int which) throws IOException {
		Log.d(TAG, "updateBuffer !! which(" + which + ")");
		int res = -1;
		
		byte buffer[] = new byte[BUFFERSIZE];
		if (which == 0) {
			bufferHeadFilePos = raf.getFilePointer();
			res = raf.read(buffer);
			bufferEndFilePos = raf.getFilePointer();
		} else if (which == 1) {
			if (bufferHeadFilePos - BUFFERSIZE >= 0) {
				raf.seek(bufferHeadFilePos - BUFFERSIZE);
				bufferHeadFilePos = raf.getFilePointer();
				res = raf.read(buffer);
				raf.seek(bufferEndFilePos);
			} else {
				Log.d(TAG, "The page is the first page!");
				return res;
			}
		}
		if (res == -1 ){
			return -1;
		}
		String strBuffer = EncodingUtils.getString(buffer, defaultCode);
		
		List<String> strBufferList2 = new ArrayList<String>();
		int strBufferIndex = 0;
		int strLen = strBuffer.length();
		Log.d(TAG, "strLen : " + strLen);
		int lineEnd = 0;
		while (true){
			Log.d(TAG, "get strBufferIndex: " + strBufferIndex);
			String tempStr = strBuffer.substring(strBufferIndex);
			lineEnd = tempStr.indexOf("\n");
			Log.d(TAG, "lineEnd : " + lineEnd);
			if (lineEnd == -1) {
				//push all the string to list after deviding
				while (strBufferIndex < strLen - maxRowNum) {
					tempStr = strBuffer.substring(strBufferIndex);
					String strPush = tempStr.substring(0, maxRowNum);
					strBufferList2.add(strPush);
					strBufferIndex += maxRowNum;
				}
				if (strBufferIndex < strLen){
					String strPush = tempStr.substring(0);
					strBufferList2.add(strPush);
				}
				break;
			} else if (lineEnd >= 0 && lineEnd < maxRowNum){
				String strPush = tempStr.substring(0, lineEnd);
				strBufferList2.add(strPush);
				strBufferIndex += lineEnd + 1;		//need to consider '\r'
			} else {
				String strPush = tempStr.substring(0, maxRowNum);
				strBufferList2.add(strPush);
				strBufferIndex += maxRowNum;
			}
		}
		//Log.d(TAG, "222list strBufferList len : " + strBufferList.size() + " strBufferList2: "+ strBufferList2.size());
		if (which == 0){
			strBufferList.addAll(strBufferList2);
		} else if (which == 1) {
			strBufferList.addAll(0, strBufferList2);
		}
		//Log.d(TAG, "22list strBufferList len : " + strBufferList.size() + " strBufferList2: "+ strBufferList2.size());
		
		return res;
	}
	
	public int read(READ_DIRECTION direction, int pos) throws IOException{
		Log.d(TAG, "read !!");
		switch (direction){
		case FORWARD:
			break;
		case BACKWARD:
			Log.d(TAG, strBufferListCurIndex + " " + maxColumeNum);
			if (strBufferListCurIndex < (maxColumeNum * 2)){
				int size = strBufferList.size();
				if (updateBuffer(1) == -1){
					return -1;
				} else {
					Log.d(TAG, size + " " +  strBufferList.size() + " " + strBufferListCurIndex + " " + maxColumeNum);
					strBufferListCurIndex += strBufferList.size() - size - maxColumeNum * 2;
				}
			} else {
				strBufferListCurIndex -= maxColumeNum * 2;
			}
			break;
		case RANDOM:
			break;
		default:
			break;
		}
		return 0;
	}
	
	public String getLine() throws IOException{
		//Log.d(TAG, "getLine start ! strBufferListCurIndex : " + strBufferListCurIndex);
		String res = null;
		if (strBufferListCurIndex == strBufferList.size()){
			strBufferList.clear();
			updateBuffer(0);
			strBufferListCurIndex = 0;
		}
		res = (String)strBufferList.get(strBufferListCurIndex);
		strBufferListCurIndex++;
		return res;
	}
}

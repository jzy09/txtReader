package com.ostrichmyself.txtReader;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ViewPage extends Activity {
	private final String TAG = "ViewPage";
	private static final String defaultCode = "UTF-8";
	
	private int width = 0;    //page width
	private int height = 0;  //page height 
	private int wordSize = 20;   //default word size
	private int maxRowNum = 0;   //display text number each line
	private int maxColumeNum = 0;
	private int maxWordNumPerPage = 0;
	
	private ImageView page;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	private InputStream is;
	private Resources src;
	
	private long curPos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpage);
		curPos = 0;
	    
	    Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int res = waitForLayoutComplete();
				if (res == 0){
					try {
						startRead();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}					
			}});
	    
	    t.start();
	}

	protected int waitForLayoutComplete() {
		// TODO Auto-generated method stub
		ImageView im = (ImageView)findViewById(R.id.page);
		while(true){
			if (im.getWidth() > 0){
				width = im.getWidth();
				height = im.getHeight();
				Log.d(TAG, "The page width : " + width + " height : " + height);
				break;
			}
		}
		return 0;
	}

	protected void startRead() throws IOException {
		// TODO Auto-generated method stub
		initCanvas();
		preparePage();
		readPage(curPos);
	}
	
	private void readPage(long pos) throws IOException {
		// TODO Auto-generated method stub
		Log.d(TAG, "readPage!");
		clearCanvas();
		
		int x=0, y=0;
		String myText = convertStreamToString(pos);
		Log.d(TAG, "readPage! 22");
		String temp;
		int start = 0;
		int end = start + maxRowNum;
		for (int i = 0; i < 9; i++){
			Log.d(TAG, "readPage! i : " + i);
			temp = myText.substring(start, end);
			canvas.drawText(temp, x, y, paint);
			start += maxRowNum;
			end += maxRowNum;
			y += wordSize;
			Log.d(TAG, "readPage! i : " + i);
		}
		Log.d(TAG, "chux 000");
        page.setImageBitmap(bitmap);
        Log.d(TAG, "chux 111");
	}

	private void clearCanvas() {
		// TODO Auto-generated method stub
	    Paint paint2 = new Paint();  
	    paint2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
	    canvas.drawPaint(paint2);  
	    paint2.setXfermode(new PorterDuffXfermode(Mode.SRC));  
	}

	private void preparePage() {
		// TODO Auto-generated method stub
		src = this.getResources();
		is = (InputStream) src.openRawResource(R.raw.duo);
		page = (ImageView) findViewById(R.id.page);
        page.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					readPage(curPos);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
        });
	}

	private void initCanvas() {
		// TODO Auto-generated method stub
		bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		
		paint = new Paint();
		paint.setColor(Color.WHITE);
		paint.setTextSize(wordSize);
		
		maxColumeNum = height / wordSize;
		maxRowNum = width / wordSize;
		maxWordNumPerPage = maxColumeNum * maxRowNum;
		Log.d(TAG, "maxColumeNum:" + maxColumeNum + " maxRowNum:" + maxRowNum
				+ " maxWordNumPerPage:" + maxWordNumPerPage);
	}

	private String convertStreamToString(long pos) throws IOException {
		// TODO Auto-generated method stub
		byte[] b = new byte [maxWordNumPerPage];
		int i = is.read(b);
		is.skip(curPos);
		
		if (i == -1) {
			return null;
		}
		
		String res = EncodingUtils.getString(b, defaultCode);
		
		curPos += maxWordNumPerPage;
		return res;
	}
}

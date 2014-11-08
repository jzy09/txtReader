package com.ostrichmyself.txtReader;

import java.io.IOException;
import java.io.InputStream;

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
	private TxtSource txtSrc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpage);
	    
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
	}
	
	private void readPage(long pos) throws IOException {
		// TODO Auto-generated method stub
		Log.d(TAG, "readPage on pos : " + pos);
		clearCanvas();
		
		if (pos != 0){
			txtSrc.read(0, 0, 2000);
		}

		int x=0, y=50;
		String tempStr;
		while (y + wordSize < height){
			tempStr = txtSrc.getLine();
			if (tempStr == null) {
				return;
			}
			canvas.drawText(tempStr, x, y, paint);
			y += wordSize * 1.5;
		}
        page.setImageBitmap(bitmap);
	}

	private void clearCanvas() {
		// TODO Auto-generated method stub
	    Paint paint2 = new Paint();  
	    paint2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
	    canvas.drawPaint(paint2);  
	    paint2.setXfermode(new PorterDuffXfermode(Mode.SRC));  
	}

	private void preparePage() throws IOException {
		// TODO Auto-generated method stub
		Resources src = this.getResources();
		InputStream is = (InputStream) src.openRawResource(R.raw.duo);
		page = (ImageView) findViewById(R.id.page);
		txtSrc = new TxtSource(is, maxRowNum);
        page.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try {
					readPage(0);
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
}

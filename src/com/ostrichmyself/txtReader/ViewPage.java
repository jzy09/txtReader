package com.ostrichmyself.txtReader;

import java.io.IOException;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

public class ViewPage extends Activity {
	private final String TAG = "ViewPage";
	
	private int width = 0;    //page width
	private int height = 0;  //page height 
	private int wordSize = 20;   //default word size
	private int maxRowNum = 0;   //display text number each line
	private int maxColumeNum = 0;
	private int maxWordNumPerPage = 0;
	private int halfwidth;
	
	private ImageView page;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	private TxtSource txtSrc;
	private String fileSrcPath = null;
	
	private boolean pagePending = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpage);
		Bundle extras = getIntent().getExtras();
		fileSrcPath = extras.getString("bookname");
		if(fileSrcPath == null) {
			finish();
		}
	    
	    Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				int res = waitForLayoutComplete();
				if (res == 0){
					try {
						startRead();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}					
			}});
	    
	    t.start();
	}

	protected int waitForLayoutComplete() {
		ImageView im = (ImageView)findViewById(R.id.page);
		while(true){
			if (im.getWidth() > 0){
				width = im.getWidth();
				height = im.getHeight();
				halfwidth = width/2;
				Log.d(TAG, "The page width : " + width + " height : " + height);
				break;
			}
		}
		return 0;
	}

	protected void startRead() throws IOException {
		initCanvas();
		preparePage();
		//readNextPage();
	}
	
	private void readPage(long pos) throws IOException {
		Log.d(TAG, "readPage on pos : " + pos);
		clearCanvas();
		
		if (pos == 1){
			int res = txtSrc.read(TxtSource.READ_DIRECTION.BACKWARD, 0);
			if (res == -1){
				return;
			}
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
	    Paint paint2 = new Paint();  
	    paint2.setXfermode(new PorterDuffXfermode(Mode.CLEAR));  
	    canvas.drawPaint(paint2);  
	    paint2.setXfermode(new PorterDuffXfermode(Mode.SRC));  
	}

	private void preparePage() throws IOException {
		page = (ImageView) findViewById(R.id.page);
		txtSrc = new TxtSource(fileSrcPath, maxRowNum);
		if (txtSrc.initFail){
			String fail = "Source init failed !";
			Toast.makeText(this, fail, Toast.LENGTH_SHORT).show();
		}
		txtSrc.setMaxColumeNum(14);
		/*page.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				try {
					readPage(0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});*/
	}

	private void initCanvas() {
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
	
	public boolean onTouchEvent(MotionEvent event){
		//String s = "x:" + (event.getX() + " y:"+ event.getY());
		//Log.d(TAG, s);
		//Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
		switch (event.getAction()){
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			if (pagePending) {
				break;
			}
			pagePending = true;
			if(event.getX() < halfwidth){
				readPrePage();
			} else{
				readNextPage();
			}
			pagePending = false;
			break;
		}
		return super.onTouchEvent(event); 
	}

	private void readPrePage() {
		try {
			readPage(1);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readNextPage(){
		try {
			readPage(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

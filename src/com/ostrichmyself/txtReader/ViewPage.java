package com.ostrichmyself.txtReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class ViewPage extends Activity {
	private final int WORDNUM = 35;    //转化成图片时  每行显示的字数
	private final int WIDTH = 450;    //设置图片的宽度
	private final int HEIGHT = 800;
	private static final String defaultCode = "UTF-8";//gb2312;
	private final int pageWordNum = 200;
	
	private ImageView page;
	private Bitmap bitmap;
	private Canvas canvas;
	private Paint paint;
	//private InputStream is;
	private FileInputStream is;
	private Resources src;
	
	private long curPos;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpage);
		curPos = 0;
		
		try {
			startRead();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startRead() throws IOException {
		// TODO Auto-generated method stub
		initCanvas();
		preparePage();
		readPage(curPos);
	}
	
	private void readPage(long pos) throws IOException {
		// TODO Auto-generated method stub
		int x=5,y=30;
		String myText = convertStreamToString(pos);
		String temp;
		int start = 0;
		int end = start + 10;
		clearCanvas();
		for (int i = 0; i < 10; i++){
			temp = myText.substring(start, end);
			canvas.drawText(temp, x, y, paint);
			start += 10;
			end += 10;
			y += 40;
		}

        //canvas.save(Canvas.ALL_SAVE_FLAG);  
        //canvas.restore();
        page.setImageBitmap(bitmap);
        
        Log.d("chux", "chux  3 " + pos);
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
		is = (FileInputStream) src.openRawResource(R.raw.duo);
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
		bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		paint = new Paint();
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(40);
	}

	private String convertStreamToString(long pos) throws IOException {
		// TODO Auto-generated method stub
		byte[] b = new byte [500];
		Log.d("chux", "chux  1111 " + pos);
		int i = is.read(b);
		Log.d("chux", "chux  2222 " + pos);
		is.skip(curPos);
		//Log.d("chux", "chux  2222 " + b);
		//Log.d("chux", "chux read number : " + i);
		
		if (i == -1) {
			return null;
		}
		
		String res = EncodingUtils.getString(b, defaultCode);
		Log.d("chux", "chux read number : " + res);
		
		curPos += 100;
		return res;
	}
}

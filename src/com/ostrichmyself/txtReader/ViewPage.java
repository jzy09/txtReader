package com.ostrichmyself.txtReader;

import java.io.File;
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
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;

public class ViewPage extends Activity {
	private final int WORDNUM = 35;    //转化成图片时  每行显示的字数
	private final int WIDTH = 450;    //设置图片的宽度
	private final int HEIGHT = 800;
	private static final String defaultCode = "UTF-8";//gb2312;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.viewpage);
		
		try {
			startRead();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void startRead() throws IOException {
		// TODO Auto-generated method stub
		int x=5,y=30;
		Resources src = this.getResources();
		InputStream is = src.openRawResource(R.raw.duo);
		String myText = convertStreamToString(is);
		
		Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint();
		
		paint.setColor(Color.WHITE);
		paint.setTextSize(40);
		
		String temp;
		int start = 0;
		int end = start + 10;
		for (int i = 0; i < 20; i++){
			temp = myText.substring(start, end);
			canvas.drawText(temp, x, y, paint);
			start += 10;
			end += 10;
			y += 40;
		}
        
        canvas.save(Canvas.ALL_SAVE_FLAG);  
        canvas.restore();
        
        ImageView page = (ImageView) findViewById(R.id.page);
        page.setImageBitmap(bitmap);
	}
	
	private String convertStreamToString(InputStream is) throws IOException {
		// TODO Auto-generated method stub
		byte[] b = new byte [2000];
		int i = is.read(b);
		Log.d("chux", "chux read number : " + i);
		
		if (i == -1) {
			return null;
		}
		
		String res = EncodingUtils.getString(b, defaultCode);
		
		Log.d("chux", res);
		return res;
	}
}

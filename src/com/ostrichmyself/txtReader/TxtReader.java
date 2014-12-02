package com.ostrichmyself.txtReader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
/**
 * ���ı������
 * @author Administrator
 *
 */
public class TxtReader extends Activity {
    /** Called when the activity is first created. */
	private final static String TAG = "TxtReader";
	private Bundle bundle;
	private ImageView startImg;
	private LinearLayout allList;
	private Handler myhandler;
	private TextView imenuDefault;
	private TextView imenuLocalFile;
	private TextView imenuAbout;
	private LinearLayout defaultList;
	private TextView localFile;
	private TextView about;
	
	private final int AFTERSTARTIMG = 0;
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist);
        
        allList = (LinearLayout) this.findViewById(R.id.allList);      
        startImg = (ImageView) this.findViewById(R.id.startimg);
    	imenuDefault = (TextView) this.findViewById(R.id.menuDefaultList);
    	imenuLocalFile = (TextView) this.findViewById(R.id.menuLocalList);
    	imenuAbout = (TextView) this.findViewById(R.id.menuAbout);
    	defaultList = (LinearLayout) this.findViewById(R.id.defaultList);
    	localFile = (TextView) this.findViewById(R.id.localFile);
    	about = (TextView) this.findViewById(R.id.about);
        
        allList.setVisibility(View.GONE);	//View.VISIBLE
        defaultList.setVisibility(View.GONE);
        localFile.setVisibility(View.GONE);
        about.setVisibility(View.GONE);
        
        imenuDefault.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "click 1st menu");
				defaultList.setVisibility(View.VISIBLE);
		        localFile.setVisibility(View.GONE);
		        about.setVisibility(View.GONE);
			}
        });
        
        imenuLocalFile.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "click 2nd menu");
				defaultList.setVisibility(View.GONE);
		        localFile.setVisibility(View.VISIBLE);
		        about.setVisibility(View.GONE);
			}
        });
        
        imenuAbout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "click 3rd menu");
				defaultList.setVisibility(View.GONE);
		        localFile.setVisibility(View.GONE);
		        about.setVisibility(View.VISIBLE);
			}
        });
        
        
        ListView booklist = (ListView) this.findViewById(R.id.booklist);
        ArrayAdapter<String> arraydapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData());
        booklist.setAdapter(arraydapter);
        
        booklist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.d(TAG, "click!!!");
				Intent in = new Intent(TxtReader.this, ViewPage.class);
				bundle = new Bundle(); //bundle用来在activity中传递数据
				bundle.putString("bookname", "楚留香");
				in.putExtras(bundle);  //将数据结构放入intent中
	        	startActivityForResult(in, 0);
			}
        });
        
        try {
			copyAssetsToSdcard("/sdcard/txtReader/");
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        myhandler = new Handler(){
        	public void handleMessage(Message msg) {
        		switch (msg.what) {
        		case AFTERSTARTIMG:
        			startImg.setVisibility(View.GONE);
        	        allList.setVisibility(View.VISIBLE);
        	        defaultList.setVisibility(View.VISIBLE);
        			break;   
                }   
                super.handleMessage(msg);   
           }
        };
        
        Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				SystemClock.sleep(3000);
				Message message = new Message();
				message.what = AFTERSTARTIMG;
				myhandler.sendMessage(message);
		        
			}});
	    
	    t.start();
    }

	private void copyAssetsToSdcard(String DesPath) throws IOException {
		// Check the dest path exit and judge if it needs to copy.
		File des = new File(DesPath);
		if (!des.exists()){
			if(!des.mkdir()){
				String warning = "Can't copy file to sdcard !";
				Toast.makeText(this, warning, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		File copyDone = new File(des, "copyDone");
		if (copyDone.exists()){
			return;
		} else {
			copyDone.createNewFile();
		}
		
		//copy begin!
		String[] files = null;
		files = this.getResources().getAssets().list("");
		
		for(int i = 0; i < files.length; i++){
			String filename = files[i];
			Log.d(TAG, "filename : " + filename);
			File outFile = new File(des, filename);
			if (outFile.exists()){
				Log.d(TAG, "filename : " + filename + "is exist");
				continue;
			}
			
			InputStream in = getAssets().open(filename);
			OutputStream out = new FileOutputStream(outFile);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0){
				out.write(buf, 0, len);
			}
			
			in.close();
			out.close();
		}
	}

	private List<String> getData() {
		List<String> data = new ArrayList<String>();
        data.add("七种武器系列");
        data.add("楚留香系列");
        data.add("陆小凤系列");
        data.add("小李飞刀系列");
        
        return data;
	}
}
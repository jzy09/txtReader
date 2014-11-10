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
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booklist);
        
        ListView booklist = (ListView) this.findViewById(R.id.booklist);
        ArrayAdapter arraydapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getData());
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
			File outFile = new File(des, filename);
			if (outFile.exists()){
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
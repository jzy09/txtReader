package com.ostrichmyself.txtReader;

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
/**
 * ���ı������
 * @author Administrator
 *
 */
public class TxtReader extends Activity {
    /** Called when the activity is first created. */
	
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
				// TODO Auto-generated method stub
				Log.d("chu", "click!!!");
				
				Intent in = new Intent(TxtReader.this, ViewPage.class);
				bundle = new Bundle(); //bundle用来在activity中传递数据
				bundle.putString("bookname", "楚留香");
				in.putExtras(bundle);  //将数据结构放入intent中
	        	startActivityForResult(in, 0);
			}
        });
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
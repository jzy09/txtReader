package com.ostrichmyself.txtReader;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		menu.removeItem(R.id.gb2312);
		menu.removeItem(R.id.utf8);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			doAbout();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	//�������ڿ�
	private void doAbout() {
		Dialog dialog = new AlertDialog.Builder(TxtReader.this).setTitle(
				R.string.aboutTitle).setMessage(R.string.aboutInfo)
				.setPositiveButton(R.string.aboutOK,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface, int i) {
								// ��ť�¼�
							}
						}).create();
		dialog.show();
	}
	
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}



	class OpenFileAction implements OnClickListener
    {

		public void onClick(View v) {
			
			Intent in = new Intent(TxtReader.this, ListAllFileActivity.class);
        	startActivityForResult(in, 0);
		}
    	
    }
}
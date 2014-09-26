package com.ostrichmyself.txtReader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.util.EncodingUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * ���ļ��ķ���
 * @author Administrator
 *
 */
public class ViewFile extends Activity {
	
	private String filenameString;
	private static final String gb2312 = "GB2312";
	private static final String utf8 = "UTF-8";
	private static final String defaultCode = "UTF-8";//gb2312;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filebrowser);
		try {
			//Log.d("chux", "chux");
			Bundle bunde = this.getIntent().getExtras();
			filenameString = bunde.getString("bookname");
			refreshGUI();
		} catch (Exception e) {
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.gb2312:
			refreshGUI();
			break;
		case R.id.utf8:
			refreshGUI();
			break;
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
		Dialog dialog = new AlertDialog.Builder(ViewFile.this).setTitle(
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

	private void refreshGUI()
	{
		TextView tv = (TextView) findViewById(R.id.view_contents);
		Log.d("chux", "chux refreshGUI");
		String fileContent = getStringFromFile();
		tv.setText(fileContent);
	}
	
	public String getStringFromFile()
	{
		try {
			Resources r = this.getResources();
			InputStream is = r.openRawResource(R.raw.duo);
			String myText = convertStreamToString(is);
			
			is.close();			
			return myText;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
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

	// ��ȡ�ļ�����
	public byte[] readFile(String fileName) throws Exception {
		byte[] result = null;
		FileInputStream fis = null;
		try {
			File file = new File(fileName);
			fis = new FileInputStream(file);
			result = new byte[fis.available()];
			fis.read(result);
		} catch (Exception e) {
		} finally {
			fis.close();
		}
		return result;
	}

}

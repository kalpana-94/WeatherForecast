package com.example.weatherforecast.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import com.example.weatherforecast.R;

public class ProgressBarDialog extends Dialog implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener, DialogInterface.OnCancelListener
{
	private Context context;

	public ProgressBarDialog(Context context, int iTheme)
	{
	    super(context, iTheme);
		this.context = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
	    super.onCreate(savedInstanceState);   
	    setContentView(R.layout.dialog_progressbar);

		init();
	}

	private void init()
	{
		setOnShowListener(this);
		setOnCancelListener(this);
		setOnDismissListener(this);
	}

	@Override
	public void onShow(DialogInterface dialogInterface) {

		showProgress(findViewById(R.id.li_progress).getRootView());
	}

	@Override
	public void onDismiss(DialogInterface dialogInterface) {
		hideProgress(findViewById(R.id.li_progress).getRootView());
	}

	@Override
	public void onCancel(DialogInterface dialogInterface) {
		hideProgress(findViewById(R.id.li_progress).getRootView());
	}

	public void showProgress(View view)
	{
		view.setVisibility(View.VISIBLE);
	}

	public void hideProgress(View view)
	{
		view.setVisibility(View.GONE);
	}
}

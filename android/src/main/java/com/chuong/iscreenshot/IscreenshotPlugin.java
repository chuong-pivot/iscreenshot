package com.chuong.iscreenshot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.embedding.engine.renderer.FlutterRenderer;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.FlutterView;

/**
 * IscreenshotPlugin
 */
public class IscreenshotPlugin implements MethodCallHandler, FlutterPlugin, ActivityAware {
	private static final String TAG = "IscreenshotPlugin";

	private Context context;
	private MethodChannel channel;
	private Activity activity;
	private Object renderer;

	private boolean ssError = false;
	private String ssPath;

	// Default constructor for old registrar
	public IscreenshotPlugin() {
	} // IscreenshotPlugin()

	// Condensed logic to initialize the plugin
	private void initPlugin(Context context, BinaryMessenger messenger, Activity activity, Object renderer) {
		this.context = context;
		this.activity = activity;
		this.renderer = renderer;

		this.channel = new MethodChannel(messenger, "iscreenshot");
		this.channel.setMethodCallHandler(this);
	} // initPlugin()

	// New v2 listener methods
	@Override
	public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
		this.channel.setMethodCallHandler(null);
		this.channel = null;
		this.context = null;
	} // onDetachedFromEngine()

	@Override
	public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
		Log.println(Log.INFO, TAG, "Using *NEW* registrar method!");

		initPlugin(
				flutterPluginBinding.getApplicationContext(),
				flutterPluginBinding.getBinaryMessenger(),
				null,
				flutterPluginBinding.getFlutterEngine().getRenderer()
		); // initPlugin()
	} // onAttachedToEngine()

	// Old v1 register method
	// FIX: Make instance variables set with the old method
	public static void registerWith(Registrar registrar) {
		Log.println(Log.INFO, TAG, "Using *OLD* registrar method!");

		IscreenshotPlugin instance = new IscreenshotPlugin();

		instance.initPlugin(
				registrar.context(),
				registrar.messenger(),
				registrar.activity(),
				registrar.view()
		); // initPlugin()
	} // registerWith()


	// Activity condensed methods
	private void attachActivity(ActivityPluginBinding binding) {
		this.activity = binding.getActivity();
	} // attachActivity()

	private void detachActivity() {
		this.activity = null;
	} // attachActivity()


	// Activity listener methods
	@Override
	public void onAttachedToActivity(ActivityPluginBinding binding) {
		attachActivity(binding);
	} // onAttachedToActivity()

	@Override
	public void onDetachedFromActivityForConfigChanges() {
		detachActivity();
	} // onDetachedFromActivityForConfigChanges()

	@Override
	public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
		attachActivity(binding);
	} // onReattachedToActivityForConfigChanges()

	@Override
	public void onDetachedFromActivity() {
		detachActivity();
	} // onDetachedFromActivity()


	// MethodCall, manage stuff coming from Dart
	@Override
	public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
		String filePath = call.argument("saveScreenshotPath");
		
		if(filePath == null || filePath.isEmpty()) {
			Log.println(Log.INFO, TAG, "Require save screenshot path argument!");

			result.success(null);

			return;
		} 

		if(!call.method.equals("takeScreenshot")) {
			Log.println(Log.INFO, TAG, "Method not implemented!");

			result.notImplemented();

			return;
		} 

		takeScreenshotOld(filePath);

		if( this.ssError || this.ssPath == null || this.ssPath.isEmpty() ) {
			result.success(null);

			return;
		}

		result.success(this.ssPath);
	}

	private String writeBitmap(Bitmap bitmap, String saveScreenshotPath) {
		try {
			File imageFile = new File(saveScreenshotPath);
			FileOutputStream oStream = new FileOutputStream(imageFile);

			bitmap.compress(Bitmap.CompressFormat.PNG, 100, oStream);
			oStream.flush();
			oStream.close();

			return saveScreenshotPath;
		} catch (Exception ex) {
			Log.println(Log.INFO, TAG, "Error writing bitmap: " + ex.getMessage());
		}

		return null;
	}

	// private void takeScreenshot(String saveScreenshotPath) {
	// 	Log.println(Log.INFO, TAG, "Trying to take screenshot [new way]");

	// 	if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
	// 		this.ssPath = null;
	// 		this.ssError = true;

	// 		return;
	// 	}

	// 	try {
	// 		Window window = this.activity.getWindow();
	// 		View view = this.activity.getWindow().getDecorView().getRootView();

	// 		Bitmap bitmap = Bitmap.createBitmap(
	// 				view.getWidth(),
	// 				view.getHeight(),
	// 				Bitmap.Config.ARGB_8888
	// 		);

	// 		Canvas canvas = new Canvas(bitmap);
	// 		view.draw(canvas);

	// 		int[] windowLocation = new int[2];
	// 		view.getLocationInWindow(windowLocation);

	// 		PixelListener listener = new PixelListener();

	// 		PixelCopy.request(
	// 				window,
   //           new Rect(
   //                   windowLocation[0],
   //                   windowLocation[1],
   //                   windowLocation[0] + view.getWidth(),
   //                   windowLocation[1] + view.getHeight()
   //           ),
	// 				bitmap,
	// 				listener,
	// 				new Handler()
	// 		);

	// 		if( listener.hasError() ) {
	// 			this.ssError = true;
	// 			this.ssPath = null;

	// 			return;
	// 		}

	// 		String path = writeBitmap(bitmap, saveScreenshotPath);

	// 		if( path == null || path.isEmpty() ) {
	// 			this.ssPath = null;
	// 			this.ssError = true;

	// 			Log.println(Log.INFO, TAG, "The bitmap cannot be written, invalid path.");

	// 			return;
	// 		}

	// 		this.ssError = false;
	// 		this.ssPath = path;
	// 	} catch (Exception ex) {
	// 		Log.println(Log.INFO, TAG, "Error taking screenshot: " + ex.getMessage());
	// 	}
	// }

	private void takeScreenshotOld(String saveScreenshotPath) {
		Log.println(Log.INFO, TAG, "Trying to take screenshot [old way]");

		try {
			View view = this.activity.getWindow().getDecorView().getRootView();

			view.setDrawingCacheEnabled(true);

			Bitmap bitmap = null;

			if (this.renderer.getClass() == FlutterView.class) {
				bitmap = ((FlutterView) this.renderer).getBitmap();
			} else if(this.renderer.getClass() == FlutterRenderer.class ) {
				bitmap = ( (FlutterRenderer) this.renderer ).getBitmap();
			}

			if(bitmap == null) {
				this.ssError = true;
				this.ssPath = null;

				Log.println(Log.INFO, TAG, "The bitmap cannot be created");

				return;
			}

			view.setDrawingCacheEnabled(false);

			String path = writeBitmap(bitmap, saveScreenshotPath);

			if(path == null || path.isEmpty() ) {
				this.ssError = true;
				this.ssPath = null;

				Log.println(Log.INFO, TAG, "The bitmap cannot be written, invalid path.");

				return;
			}

			this.ssError = false;
			this.ssPath = path;
		} catch (Exception ex) {
			Log.println(Log.INFO, TAG, "Error taking screenshot: " + ex.getMessage());
		}
	}
}

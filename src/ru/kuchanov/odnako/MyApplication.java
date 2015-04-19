/*
 06.04.2015
MyApplication.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako;

import org.acra.*;
import org.acra.annotation.*;

import com.yandex.metrica.YandexMetrica;

import android.app.Application;

/**
 * here we initialize ACRA crah reporter
 * 
 * @see https://github.com/ACRA/acra
 */
@ReportsCrashes(
formKey = "", // This is required for backward compatibility but not used
formUri = "http://kuchanov.ru/acra/report.php",
customReportContent = { ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.STACK_TRACE, ReportField.LOGCAT },
mode = ReportingInteractionMode.TOAST,
//logcatArguments = { "-t", "100", "-v", "long", "ActivityManager:I", "MyApp:D", "*:S" },
//logcatArguments=logCatArgs,
resToastText = R.string.crash_toast_text)
public class MyApplication extends Application
{
	private static final String API_KEY = "39630";

	@Override
	public void onCreate()
	{
		super.onCreate();
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
//		ACRAConfiguration conf = ACRA.getConfig();
//		conf.setLogcatArguments(logCatArgs);
//		ACRA.setConfig(conf);
		//Initialize YandexMetrika
		YandexMetrica.initialize(getApplicationContext(), API_KEY);

	}

	String[] logCatArgs = "tag:^(?!AudioSystemEx) tag:^(?!MediaRecorderEx) tag:^(?!Adreno-EGL) tag:^(?!LGMtpDatabaseJNI) tag:^(?!MediaProfilesEx-JNI) tag:^(?!Atlas) tag:^(?!SurfaceControlEx) tag:^(?!MediaPlayerEx-jni) tag:^(?!BubblePopupHelper) tag:^(?!dalvikvm) tag:^(?!libEGL) tag:^(?!Open) tag:^(?!Google) tag:^(?!resour) tag:^(?!Chore) tag:^(?!EGL) tag:^(?!SocketStream) tag:^(?!WifiStateMachine) tag:^(?!chromium) tag:^(?!audio_hw_primary) tag:^(?!InputEventReceiver) tag:^(?!ActivityManager) tag:^(?!AudioManagerAndroid) tag:^(?!LibraryLoader) tag:^(?!WebViewFactory) tag:^(?!JavaBinder) tag:^(?!art) tag:^(?!ViewRootImpl) tag:^(?!InputMethodManagerService) tag:^(?!ACDB-LOADER) tag:^(?!WindowManager) tag:^(?!WebViewChromiumFactoryProvider) tag:^(?!GCM) tag:^(?!CalendarProvider2) tag:^(?!View) tag:^(?!libc-netbsd) tag:^(?!Timeline) tag:^(?!CliptrayUtils) tag:^(?!BrowserStartupController)"
	.split(" ");
}
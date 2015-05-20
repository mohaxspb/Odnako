/*
 06.04.2015
MyApplication.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import ru.kuchanov.odnako.utils.AcraReportSender;
import android.app.Application;

import com.yandex.metrica.YandexMetrica;

/**
 * here we initialize ACRA crash reporter
 * 
 * @see https://github.com/ACRA/acra
 */
@ReportsCrashes(
//formKey = "",
//formUri = "http://kuchanov.ru/acra/report.php",
//formUriBasicAuthLogin = "test", // optional
//formUriBasicAuthPassword = "test", // optional
formUri = MyApplication.FROM_URI_DEFAULT,//"http://kuchanov.ru/acra/test.php",
customReportContent = {
		ReportField.SHARED_PREFERENCES,
		ReportField.APP_VERSION_CODE,
		ReportField.APP_VERSION_NAME,
		ReportField.ANDROID_VERSION,
		ReportField.PHONE_MODEL,
		ReportField.STACK_TRACE,
		ReportField.BRAND,
		ReportField.DISPLAY,
		ReportField.DEVICE_ID,
		ReportField.INSTALLATION_ID,
		ReportField.PACKAGE_NAME,
		ReportField.PRODUCT,
		ReportField.REPORT_ID,
		ReportField.USER_APP_START_DATE,
		ReportField.USER_CRASH_DATE,
		ReportField.USER_IP,
		ReportField.CUSTOM_DATA,
		ReportField.LOGCAT },
mode = ReportingInteractionMode.TOAST,
resToastText = R.string.crash_toast_text)
public class MyApplication extends Application
{
	static final String LOG = MyApplication.class.getSimpleName();

	public static final String FROM_URI_DEFAULT = "http://kuchanov.ru/acra/test.php";

	private static final String API_KEY = "39630";

	@Override
	public void onCreate()
	{
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
		AcraReportSender yourSender = new AcraReportSender(FROM_URI_DEFAULT);
		ACRA.getErrorReporter().setReportSender(yourSender);

		//Initialize YandexMetrika
		YandexMetrica.initialize(getApplicationContext(), API_KEY);
		super.onCreate();
	}
}
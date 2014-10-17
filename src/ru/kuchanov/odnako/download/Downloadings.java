package ru.kuchanov.odnako.download;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import ru.kuchanov.odnako.DrawerGroupClickListenerNew;
import ru.kuchanov.odnako.DrawerItemClickListenerNew;
import ru.kuchanov.odnako.ExpListAdapter;
import ru.kuchanov.odnako.FillMenuList;
import ru.kuchanov.odnako.MainActivityNew;
import ru.kuchanov.odnako.PrefActivity;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.utils.AddAds;
import ru.kuchanov.odnako.utils.CheckConnections;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.ads.AdView;

public class Downloadings extends ActionBarActivity implements OnClickListener, OnCheckedChangeListener
{
	ActionBarActivity act;
	
	public static boolean IS_PRO = false;

	public final static String EXTRA_CATEGORY = "category";
	public final static String EXTRA_NUM_TO_LOAD = "num_to_load";
	public final static String EXTRA_NUM_OF_PAGES_TO_LOAD = "num_of_pages_to_load";
	
	public final static String ALL_CATEGORY = "all_category";
	public final static String ALL_NUM_TO_LOAD = "all_num_to_load";
	public final static String ALL_NUM_OF_PAGES_TO_LOAD = "all_num_of_pages_to_load";

	private DrawerLayout mDrawerLayout;
	private ExpandableListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;

	SharedPreferences pref;

	ArrayList<ArrayList<String>> groups;
	ArrayList<ArrayList<String>> groupsLinks;

	String[] category;
	String[] quontity = { "5", "10", "20", "30" };

	public static String CAT_TO_LOAD = "www.odnako.org/blogs";
	public static Integer ARTICLES_TO_LOAD = 5;
	public static Integer ARTICLES_PAGES_TO_LOAD = 1;

	static int DIALOG_TIME = 1;
	int myHour;
	int myMinute;
	Calendar cal;

	boolean setTimeFlag = false;

	TimePickerDialog tpd;

	CheckBox autoLoadCB;
	TextView autoLoadTV;

	boolean initial;

	AdView adView;

	protected void onCreate(Bundle savedInstanceState)
	{
		System.out.println("Downloadings onCreate");
		super.onCreate(savedInstanceState);
		
		this.act=this;

		//Задаёт дефолт настройки из xml и вешает листенер их изменения
		PreferenceManager.setDefaultValues(this, R.xml.pref, false);
		pref = PreferenceManager.getDefaultSharedPreferences(this);

		//set allDown enability depend from IS_PRO		
		////
		
		setAppearence();
		setNavDraw();

		category = this.getResources().getStringArray(R.array.categories_to_load);
		ArrayAdapter<String> adapter;
		if (pref.getString("theme", "dark").equals("dark"))
		{
			adapter = new ArrayAdapter<String>(this, R.layout.spiner_layout_dark, category);
		}
		else
		{
			adapter = new ArrayAdapter<String>(this, R.layout.spiner_layout_ligth, category);
		}
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spiner_layout_ligth, category);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinner = (Spinner) findViewById(R.id.cat_spinner);
		spinner.setAdapter(adapter);
		// заголовок
		spinner.setPrompt("Категория");
		// устанавливаем обработчик нажатия
		spinner.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				if (position > 0)
				{
					if (Downloadings.IS_PRO)
					{
						Downloadings.CAT_TO_LOAD = Downloadings.this.getResources().getStringArray(R.array.categories_to_load_links)[position];
					}
					else
					{
						if (pref.getBoolean("adsOn", false))
						{
							if (position == 1 || position == 2)
							{
								Downloadings.CAT_TO_LOAD = Downloadings.this.getResources().getStringArray(R.array.categories_to_load_links)[position];
							}
							else
							{
								Toast.makeText(getBaseContext(), "Только в 'Однако+' версии", Toast.LENGTH_SHORT).show();
								spinner.setSelection(0);
							}
						}
						else
						{
							Toast.makeText(getBaseContext(), "Только в 'Однако+' версии", Toast.LENGTH_SHORT).show();
							spinner.setSelection(0);
						}
					}
				}
				else
				{
					Downloadings.CAT_TO_LOAD = Downloadings.this.getResources().getStringArray(R.array.categories_to_load_links)[position];
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
			}
		});
		spinner.setSelection(0);

		ArrayAdapter<String> adapterQuont;
		if (pref.getString("theme", "dark").equals("dark"))
		{
			adapterQuont = new ArrayAdapter<String>(this, R.layout.spiner_layout_dark, this.quontity);
		}
		else
		{
			adapterQuont = new ArrayAdapter<String>(this, R.layout.spiner_layout_ligth, quontity);
		}
		adapterQuont.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		final Spinner spinnerQuont = (Spinner) findViewById(R.id.quont_spinner);
		spinnerQuont.setAdapter(adapterQuont);
		// заголовок
		spinnerQuont.setPrompt("Количество");
		// устанавливаем обработчик нажатия
		spinnerQuont.setOnItemSelectedListener(new OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				// показываем позицию нажатого элемента

				if (position > 0)
				{
					if (Downloadings.IS_PRO)
					{
						Downloadings.ARTICLES_TO_LOAD = Integer.valueOf(quontity[position]);
					}
					else
					{
						if (pref.getBoolean("adsOn", false))
						{
							if (position == 1)
							{
								Downloadings.ARTICLES_TO_LOAD = Integer.valueOf(quontity[position]);
							}
							else
							{
								Toast.makeText(getBaseContext(), "Только в 'Однако+' версии", Toast.LENGTH_SHORT).show();
								spinnerQuont.setSelection(0);
							}
						}
						else
						{
							Toast.makeText(getBaseContext(), "Только в 'Однако+' версии", Toast.LENGTH_SHORT).show();
							spinnerQuont.setSelection(0);
						}
					}
				}
				else
				{
					Downloadings.ARTICLES_TO_LOAD = Integer.valueOf(quontity[position]);
				}
				if (Integer.valueOf(quontity[position]) <= MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE)
				{
					Downloadings.ARTICLES_PAGES_TO_LOAD = 1;
				}
				else
				{
					Downloadings.ARTICLES_PAGES_TO_LOAD = Integer.valueOf(quontity[position]) / MainActivityNew.DEFAULT_NUM_OF_ARTS_ON_PAGE;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0)
			{
			}
		});

		Button downBtn = (Button) this.findViewById(R.id.down_button);
		downBtn.setOnClickListener(this);
		Button delBtn = (Button) this.findViewById(R.id.delete_button);
		delBtn.setOnClickListener(delBtnCL);

		autoLoadCB = (CheckBox) findViewById(R.id.check_box_auto_load);
		autoLoadCB.setOnCheckedChangeListener(this);
		boolean loadOnTimeIsOn = pref.getBoolean("load_on_time", false);
		if (loadOnTimeIsOn)
		{
			initial = true;
			autoLoadCB.setText(R.string.on);
			autoLoadCB.setChecked(true);
		}
		else
		{
			autoLoadCB.setText(R.string.off);
			autoLoadCB.setChecked(false);
		}
		Button timeDialogBtn = (Button) this.findViewById(R.id.time_dialog_button);
		timeDialogBtn.setOnClickListener(this);

		/////set onTimerAutoLoad
		onTimerAutoLoadOn();
		
		//allAutoLoad
		Button allLoadInfo=(Button)this.findViewById(R.id.down_all_info);
		Button allLoadBtn=(Button)this.findViewById(R.id.many_down_button);
		allLoadInfo.setOnClickListener(allLoadInfoCL);
		allLoadBtn.setOnClickListener(allLoadCL);
		////

		//adMob
		adView = (AdView) this.findViewById(R.id.adView);
		AddAds addAds = new AddAds(this, this.adView);
//		addAds.addAd("adsOnDownloadings"); old ver
		addAds.addAd();
		//end of adMob
	}
	
	//end of onCreate
	
	OnClickListener allLoadInfoCL=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Dialog dialog = new Dialog(act);

			
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
			///Message is main text in dialog, version feachers
			String message = act.getResources().getString(R.string.allLoadInfoMessage);

			builder.setMessage(message).setCancelable(true)
			.setPositiveButton("Всё понятно!", new DialogInterface.OnClickListener()
			{

				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			dialog = builder.create();
			dialog.show();
		}
	};
	OnClickListener allLoadCL=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			if(!CheckConnections.isInternetByWiFiOn(act))
			{
				System.out.println("LoadAll by mobile internet");
			}
			else
			{
				System.out.println("LoadAll by Wi-Fi internet");
			}
			
			System.out.println("downloadAll");
			if(IS_PRO)
			{
				Intent serviceIntent = new Intent(act, ru.kuchanov.odnako.download.AllDownloadService.class);
				serviceIntent.putExtra(Downloadings.ALL_CATEGORY, act.getResources().getStringArray(R.array.categories_to_load_links));
				serviceIntent.putExtra(Downloadings.ALL_NUM_TO_LOAD, 30);
				serviceIntent.putExtra(Downloadings.ALL_NUM_OF_PAGES_TO_LOAD, 1);
				startService(serviceIntent);
			}
			else
			{
				Toast.makeText(getBaseContext(), "Только в 'Однако+' версии", Toast.LENGTH_SHORT).show();
			}
			
			
		}
	};
	protected void onTimerAutoLoadOn()
	{
		/////set onTimerAutoLoad
		SimpleDateFormat sdf = new SimpleDateFormat("hh:ss a", Locale.US);
		Date date1 = null;
		try
		{
			date1 = sdf.parse("07:00 AM");
		} catch (ParseException e)
		{
			e.printStackTrace();
		}
		long timeToUpdate = 0;
		if (IS_PRO)
		{
			timeToUpdate = pref.getLong("timepref_auto_backup", date1.getTime());
		}
		else
		{
			timeToUpdate = date1.getTime();
		}

		cal = Calendar.getInstance();
		Date date = new Date(timeToUpdate);
		cal.setTime(date);
		System.out.println(cal.getTime());
		System.out.println(cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE));

		myHour = cal.get(Calendar.HOUR_OF_DAY);
		myMinute = cal.get(Calendar.MINUTE);
		autoLoadTV = (TextView) this.findViewById(R.id.auto_load_tv);
		String hourToToast = String.valueOf(myHour);
		if (myHour < 10)
		{
			hourToToast = "0" + String.valueOf(myHour);
		}
		String minuteToToast = String.valueOf(myMinute);
		if (myMinute < 10)
		{
			minuteToToast = "0" + String.valueOf(myMinute);
		}
		/////
		String CAT_TO_LOAD_TV = pref.getString(Downloadings.EXTRA_CATEGORY, "");
		Integer ARTICLES_TO_LOAD_TV = pref.getInt(Downloadings.EXTRA_NUM_TO_LOAD, 5);
		String[] checkArr = Downloadings.this.getResources().getStringArray(R.array.categories_to_load_links);
		for (int i = 0; i < checkArr.length; i++)
		{
			if (checkArr[i].equals(CAT_TO_LOAD_TV))
			{
				CAT_TO_LOAD_TV = Downloadings.this.getResources().getStringArray(R.array.categories_to_load)[i];
			}
		}

		autoLoadTV.setText("Автозагрузка статей в " + hourToToast + ":" + minuteToToast + "\n" + "Раздел: " + CAT_TO_LOAD_TV + "\n" + "Количество: " + String.valueOf(ARTICLES_TO_LOAD_TV));
	}
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		if (isChecked)
		{
			//boolean load_on_time=pref.getBoolean("load_on_time", false);
			if (!initial)
			{
				pref.edit().putBoolean("load_on_time", true).commit();
				///
				String string_date = String.valueOf(Downloadings.this.myHour) + ":" + String.valueOf(Downloadings.this.myMinute);
				SimpleDateFormat f = new SimpleDateFormat("hh:mm", Locale.US);
				Date d = null;
				try
				{
					d = f.parse(string_date);
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				long milliseconds = d.getTime();
				pref.edit().putLong("timepref_auto_backup", milliseconds).commit();
				//Update existing alarm
				setAlarmManager(milliseconds);
			}
			else
			{
				initial = false;
			}
			autoLoadCB.setText(R.string.on);
			
			onTimerAutoLoadOn();
		}
		else
		{
			autoLoadCB.setText(R.string.off);
			pref.edit().putBoolean("load_on_time", false).commit();
			cancelAlarmManager();
		}
	}

	@SuppressWarnings("deprecation")
	protected Dialog onCreateDialog(int id)
	{
		if (id == DIALOG_TIME)
		{
			System.out.println("onCreateDialog");
			SimpleDateFormat sdf = new SimpleDateFormat("hh:ss a", Locale.US);
			Date date1 = null;
			try
			{
				date1 = sdf.parse("07:00 AM");
			} catch (ParseException e)
			{
				e.printStackTrace();
			}
			long timeToUpdate = pref.getLong("timepref_auto_backup", date1.getTime());
			cal = Calendar.getInstance();
			Date date = new Date(timeToUpdate);
			cal.setTime(date);
			System.out.println(cal.getTime());

			myHour = cal.get(Calendar.HOUR_OF_DAY);
			myMinute = cal.get(Calendar.MINUTE);
			System.out.println("Time is " + myHour + " hours " + myMinute + " minutes");
			tpd = new TimePickerDialog(this, myCallBack, myHour, myMinute, DateFormat.is24HourFormat(this));
			tpd.setTitle("Выберите время");

			tpd.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (which == DialogInterface.BUTTON_POSITIVE)
					{
						setTimeFlag = true;
					}
				}
			});
			tpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Отмена", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (which == DialogInterface.BUTTON_NEGATIVE)
					{
						setTimeFlag = false;
						dialog.dismiss();
						dialog.cancel();
						dialog = null;
					}
				}
			});
			return tpd;
		}
		return super.onCreateDialog(id);
	}
	OnTimeSetListener myCallBack = new OnTimeSetListener()
	{
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute)
		{
			System.out.println("myCallBack called");
			if (setTimeFlag)
			{
				myHour = hourOfDay;
				myMinute = minute;
				///
				String string_date = String.valueOf(Downloadings.this.myHour) + ":" + String.valueOf(Downloadings.this.myMinute);
				SimpleDateFormat f = new SimpleDateFormat("HH:mm", Locale.US);
				Date d = null;
				try
				{
					d = f.parse(string_date);
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				long milliseconds = d.getTime();
				pref.edit().putLong("timepref_auto_backup", milliseconds).commit();
				//Update existing alarm
				setAlarmManager(milliseconds);
				///
				setTimeFlag = false;
				System.out.println("Time is " + myHour + " hours " + myMinute + " minutes");
				String hourToToast = String.valueOf(myHour);
				if (myHour < 10)
				{
					hourToToast = "0" + String.valueOf(myHour);
				}
				String minuteToToast = String.valueOf(myMinute);
				if (myMinute < 10)
				{
					minuteToToast = "0" + String.valueOf(myMinute);
				}
				String CAT_TO_LOAD_TV = pref.getString(Downloadings.EXTRA_CATEGORY, "");
				Integer ARTICLES_TO_LOAD_TV = pref.getInt(Downloadings.EXTRA_NUM_TO_LOAD, 5);
				String[] checkArr = Downloadings.this.getResources().getStringArray(R.array.categories_to_load_links);
				for (int i = 0; i < checkArr.length; i++)
				{
					if (checkArr[i].equals(CAT_TO_LOAD_TV))
					{
						CAT_TO_LOAD_TV = Downloadings.this.getResources().getStringArray(R.array.categories_to_load)[i];
					}
				}

				autoLoadTV.setText("Автозагрузка статей в " + hourToToast + ":" + minuteToToast + "\n" + "Раздел: " + CAT_TO_LOAD_TV + "\n" + "Количество: " + String.valueOf(ARTICLES_TO_LOAD_TV));
				//autoLoadTV.setText("Автозагрузка статей в "+hourToToast+":"+minuteToToast);
			}

		}
	};

	public void cancelAlarmManager()
	{
		System.out.println("cancel AlarmManager...");
		Intent intent2;
		intent2 = new Intent(this, TimerReciver.class);
		intent2.setAction("action_2");
		intent2.putExtra("extra", "extra_from_main");
		PendingIntent pIntent2;
		AlarmManager am;

		boolean alarmCanceled = (PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_NO_CREATE) == null);
		System.out.println(alarmCanceled);

		pIntent2 = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);//
		am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pIntent2);

		alarmCanceled = (PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_NO_CREATE) == null);
		System.out.println(alarmCanceled);
		if (alarmCanceled)
		{
			System.out.println("Alarm is already canceled");
		}
		else
		{
			System.out.println("Alarm is already canceled ERROR");
		}
	}

	public void setAlarmManager(long milliseconds)
	{
		System.out.println("Update or create new alarm...");
		Intent intent2;
		intent2 = new Intent(this, TimerReciver.class);
		intent2.setAction("action_2");
		intent2.putExtra("extra", "extra_from_main");
		PendingIntent pIntent2;
		AlarmManager am;
		pIntent2 = PendingIntent.getBroadcast(this, 0, intent2, PendingIntent.FLAG_UPDATE_CURRENT);//
		am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
		long timeToUpdate = milliseconds;//pref.getLong("timepref_auto_backup", 0);
		///Curent calendar to parce cur date
		Calendar cur_cal = Calendar.getInstance();
		cur_cal.setTimeInMillis(System.currentTimeMillis());
		//Calendar to user time
		Calendar userCal = Calendar.getInstance();
		Date d = new Date(timeToUpdate);
		userCal.setTime(d);
		//Calendar final
		Calendar cal = Calendar.getInstance();
		cal.set(cur_cal.get(Calendar.YEAR), cur_cal.get(Calendar.MONTH), cur_cal.get(Calendar.DATE), userCal.get(Calendar.HOUR_OF_DAY), userCal.get(Calendar.MINUTE));
		System.out.println("curent timeToFirstLoad: " + cal.getTime());
		//Check if first time has already gone
		if (cal.getTimeInMillis() < cur_cal.getTimeInMillis())
		{
			cal.add(Calendar.DATE, 1);
		}
		System.out.println("new timeToFirstLoad: " + cal.getTime());
		//Вывести время до следующей загрузки
		long calToCurCal = cal.getTimeInMillis() - cur_cal.getTimeInMillis();
		Date date = new Date(calToCurCal);
		Calendar newCal = Calendar.getInstance();
		//System.out.println(newCal.getTimeZone());;
		newCal.setTime(date);
		TimeZone timezone = TimeZone.getTimeZone("GMT+0");
		newCal.setTimeZone(timezone);
		System.out.println(newCal.getTime());
		int hourToNextLoad = newCal.get(Calendar.HOUR_OF_DAY);
		int minuteToNextLoad = newCal.get(Calendar.MINUTE);
		String timeToNextLoad = "До следующей загрузки: " + hourToNextLoad + "ч " + minuteToNextLoad + "мин";
		Toast.makeText(getApplicationContext(), timeToNextLoad, Toast.LENGTH_SHORT).show();

		am.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent2);
		////Put info to prefs to fill intent extra
		System.out.println(Downloadings.CAT_TO_LOAD + "/" + Downloadings.ARTICLES_TO_LOAD + "/" + Downloadings.ARTICLES_PAGES_TO_LOAD);
		pref.edit().putString(Downloadings.EXTRA_CATEGORY, Downloadings.CAT_TO_LOAD).commit();
		pref.edit().putInt(Downloadings.EXTRA_NUM_TO_LOAD, Downloadings.ARTICLES_TO_LOAD).commit();
		pref.edit().putInt(Downloadings.EXTRA_NUM_OF_PAGES_TO_LOAD, Downloadings.ARTICLES_PAGES_TO_LOAD).commit();

	}

	

	@SuppressWarnings("deprecation")
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.down_button:
				download();
			break;
			case R.id.time_dialog_button:
				if (IS_PRO)
				{
					boolean isOn = pref.getBoolean("load_on_time", false);
					if (isOn)
					{
						showDialog(DIALOG_TIME);
					}
				}
				else
				{
					Toast.makeText(getBaseContext(), "Только в 'Однако+' версии", Toast.LENGTH_SHORT).show();
				}
			break;
			default:
			break;
		}
	}

	protected void download()
	{
		System.out.println("Downloadings download");
		Intent serviceIntent = new Intent(this, ru.kuchanov.odnako.download.MyService.class);
		serviceIntent.putExtra(Downloadings.EXTRA_CATEGORY, Downloadings.CAT_TO_LOAD);
		serviceIntent.putExtra(Downloadings.EXTRA_NUM_TO_LOAD, Downloadings.ARTICLES_TO_LOAD);
		serviceIntent.putExtra(Downloadings.EXTRA_NUM_OF_PAGES_TO_LOAD, Downloadings.ARTICLES_PAGES_TO_LOAD);
		startService(serviceIntent);
	}
	
	OnClickListener delBtnCL=new OnClickListener()
	{
		
		@Override
		public void onClick(View v)
		{
			Dialog dialog = new Dialog(act);
			AlertDialog.Builder builder = new AlertDialog.Builder(act);
			String message = "Вы уверены, что хотите удалить все сохранённые статьи и картинки?";
			builder.setMessage(message).setCancelable(true)
			.setPositiveButton("Удалить", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					delete();
					dialog.dismiss();
				}
			})
			.setNegativeButton("Отмена", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					dialog.dismiss();
				}
			});
			dialog = builder.create();
			dialog.show();
		}
	};

	protected void delete()
	{
		System.out.println("Downloadings delete");
		Intent serviceIntent = new Intent(this, ru.kuchanov.odnako.download.DeleteService.class);
		startService(serviceIntent);
	}

	protected void setNavDraw()
	{
		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_new);

		// Находим наш list 
		mDrawerList = (ExpandableListView) findViewById(R.id.exListViewNew);
		mDrawerList.setCacheColorHint(0);

		//Создаем набор данных для адаптера
		FillMenuList fillMenuList = new FillMenuList();
		fillMenuList.setActivity(this);
		this.groups = fillMenuList.getGroups();
		this.groupsLinks = fillMenuList.getGroupsLinks();
		//Создаем адаптер и передаем context и список с данными
		ExpListAdapter adapter = new ExpListAdapter(getApplicationContext(), groups);
		mDrawerList.setAdapter(adapter);
		//Set on child click listener
		DrawerItemClickListenerNew drawerItemClickListenerNew = new DrawerItemClickListenerNew();
		drawerItemClickListenerNew.setVars(mDrawerLayout, mDrawerList, groups, groupsLinks, this);
		this.mDrawerList.setOnChildClickListener(drawerItemClickListenerNew);
		//Set on group click listener
		DrawerGroupClickListenerNew drawerGroupClickListenerNew = new DrawerGroupClickListenerNew();
		drawerGroupClickListenerNew.setVars(mDrawerLayout, mDrawerList, this);
		this.mDrawerList.setOnGroupClickListener(drawerGroupClickListenerNew);

		mDrawerList.expandGroup(1);

		// set a custom shadow that overlays the main content when the drawer opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(View view)
			{
				getSupportActionBar().setTitle(mTitle);
				supportInvalidateOptionsMenu();
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu();
			}
		};
		mDrawerToggle.setDrawerIndicatorEnabled(false);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	protected void setAppearence()
	{
		if (pref.getString("theme", "dark").equals("dark"))
		{
			this.setTheme(R.style.Theme_AppCompat_Light_DarkActionBar);
			this.setContentView(R.layout.activity_down_dark);
		}
		else
		{
			this.setTheme(R.style.Theme_AppCompat_Light);
			this.setContentView(R.layout.activity_down_ligth);
		}

		pref = PreferenceManager.getDefaultSharedPreferences(this);
		String scaleFactorString = pref.getString("scale", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		LayoutParams params = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		TextView categoryTV = (TextView) this.findViewById(R.id.cat_name);
		categoryTV.setTextSize(21 * scaleFactor);

		categoryTV.setLayoutParams(params);
		TextView quontityTV = (TextView) this.findViewById(R.id.quontity_tv);
		quontityTV.setTextSize(21 * scaleFactor);
		quontityTV.setLayoutParams(params);
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				return super.onOptionsItemSelected(item);
			case R.id.action_settings:
				item.setIntent(new Intent(this, PrefActivity.class));
				return super.onOptionsItemSelected(item);
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		if (pref.getString("theme", "dark").equals("dark"))
		{
			//getMenuInflater().inflate(R.menu.article_menu_dark, menu);
			getMenuInflater().inflate(R.menu.download_menu_dark, menu);
		}
		else
		{
			//getMenuInflater().inflate(R.menu.article_menu_ligth, menu);
			getMenuInflater().inflate(R.menu.download_menu_ligth, menu);
		}
		return true;
	}

	@Override
	public void setTitle(CharSequence title)
	{
		mTitle = title;
		getSupportActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}
	
	protected void onResume()
	{
		System.out.println("Downloadings onResume");
		super.onResume();
		adView.resume();
	}

	@Override
	public void onPause()
	{
		adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		adView.destroy();
		super.onDestroy();
	}

}

//@Override
//protected void onPrepareDialog(int id, Dialog dialog, Bundle args)
//{
//	switch (id)
//	{
//		case 1:
//			System.out.println("onPrepareDialog");
//	}
//}

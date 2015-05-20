package ru.kuchanov.odnako.fragments;

import java.io.IOException;

import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.callbacks.CallbackEasterEggMusic;
import ru.kuchanov.odnako.utils.AsyncTaskEasterEggMusic;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.text.Html;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.MDButton;

public class FragmentPreferenceAbout extends PreferenceFragment
{
	private final static String LOG = FragmentPreferenceAbout.class.getSimpleName();

	private PreferenceActivity act;

	private static String APP_NAME;
	private static String APP_TITLE;

	public final static String LINK_TO_PRO = "ru.kuchanov.odnakopro";

	private int verHistoryOKqount = 0;
	private String input = "";

	@Override
	public void onCreate(Bundle savedState)
	{
		super.onCreate(savedState);

		this.act = (PreferenceActivity) this.getActivity();

		APP_TITLE = act.getResources().getString(R.string.app_name);
		APP_NAME = this.act.getApplicationInfo().packageName;

		addPreferencesFromResource(R.xml.pref_about);

		//link_to_market
		Preference prefereces = findPreference("link_to_market");
		prefereces.setOnPreferenceClickListener(linkToMarket);

		//link_to_pro
		Preference preferecesPRO = findPreference("link_to_pro");
		preferecesPRO.setOnPreferenceClickListener(linkToPro);

		//share app
		Preference preferencesSHARE = findPreference("share_app");
		preferencesSHARE.setOnPreferenceClickListener(shareApp);

		//mail to me
		Preference prefMailToMe = findPreference("mail_to_me");
		prefMailToMe.setOnPreferenceClickListener(mailToMeCL);

		//version_history
		Preference prefVersionHistory = findPreference("version_history");
		prefVersionHistory.setOnPreferenceClickListener(this.versionHistoryCL);

		//version_history
		Preference prefUsedLibs = findPreference("used_libs");
		prefUsedLibs.setOnPreferenceClickListener(this.usedLibsCL);
	}

	protected OnPreferenceClickListener linkToMarket = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			try
			{
				act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_NAME)));
			} catch (Exception e)
			{
				String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
				System.out.println(marketErrMsg);
				Toast.makeText(act, marketErrMsg, Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};

	protected OnPreferenceClickListener linkToPro = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			MaterialDialog dialogGoPro;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);

			dialogGoProBuilder.title(R.string.go_pro_title)
			.content(Html.fromHtml(act.getResources().getString(R.string.pro_ver_adv)))
			.positiveText(R.string.go_pro_buy)
			.callback(new MaterialDialog.ButtonCallback()
			{
				@Override
				public void onPositive(MaterialDialog dialog)
				{
					try
					{
						act.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="
						+ LINK_TO_PRO)));
					} catch (Exception e)
					{
						String marketErrMsg = "Должен был запуститься Play Market, но что-то пошло не так...";
						Log.e(LOG, marketErrMsg);
						e.printStackTrace();
						Toast.makeText(act, marketErrMsg, Toast.LENGTH_SHORT).show();
					}
				}
			});
			dialogGoPro = dialogGoProBuilder.build();
			int textColor = act.getResources().getColor(R.color.black);
			((MDButton) dialogGoPro.getActionButton(DialogAction.POSITIVE)).setTextColor(textColor);
			dialogGoPro.getActionButton(DialogAction.POSITIVE).setBackgroundResource(R.drawable.md_btn_shape_green);
			dialogGoPro.show();
			return false;
		}
	};

	protected OnPreferenceClickListener shareApp = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			final Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			String webLinkToApp = "http://play.google.com/store/apps/details?id=ru.kuchanov.odnako";
			String sendingApp = "Отправлено из приложения 'Однако. Новости и Аналитика' для Android.";
			intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp + " " + sendingApp);
			intent.putExtra(Intent.EXTRA_TEXT, webLinkToApp);
			try
			{
				startActivity(Intent.createChooser(intent, "Поделиться приложением"));
			} catch (android.content.ActivityNotFoundException ex)
			{
				Toast.makeText(act, "Ошибка! =(", Toast.LENGTH_SHORT).show();
			}
			return false;
		}
	};

	protected OnPreferenceClickListener mailToMeCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			//EmailSending
			Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "mohax.spb@gmail.com", null));
			emailIntent.putExtra(Intent.EXTRA_SUBJECT, "От пользователя приложения \"Однако\"");
			startActivity(Intent.createChooser(emailIntent, "Написать письмо..."));

			return false;
		}
	};

	protected OnPreferenceClickListener versionHistoryCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			PackageManager m = act.getPackageManager();
			String app_ver = "";
			try
			{
				app_ver = m.getPackageInfo(act.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e)
			{
				// Exception won't be thrown as the current package name is
				// safe to exist on the system.
				throw new AssertionError();
			}

			String allVersionsDescriptionString;
			String[] allVerArr = act.getResources().getStringArray(R.array.version_history_arr);
			StringBuilder sb = new StringBuilder();
			for (String s : allVerArr)
			{
				sb.append(s);
			}
			allVersionsDescriptionString = sb.toString();

			MaterialDialog dialogVersionHistory;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);
			dialogGoProBuilder.title(APP_TITLE + ", версия " + app_ver)
			.content(Html.fromHtml(allVersionsDescriptionString))
			.positiveText("Это было интересно!")
			.callback(new MaterialDialog.ButtonCallback()
			{
				@Override
				public void onPositive(MaterialDialog dialog)
				{
					if (verHistoryOKqount < 2)
					{
						verHistoryOKqount++;
					}
					else
					{
						MaterialDialog dialogInputName;
						MaterialDialog.Builder dialogInputNameBuilder = new MaterialDialog.Builder(act);
						dialogInputNameBuilder.title("Ух-ты, музыкальная пасхалка!")
						.input("пароль", null, new MaterialDialog.InputCallback()
						{
							@Override
							public void onInput(MaterialDialog dialog, CharSequence editTextInput)
							{
								input = editTextInput.toString();
								Log.e(LOG, "onINPUT: " + input);

								AsyncTaskEasterEggMusic askServer = new AsyncTaskEasterEggMusic(callback, input);
								askServer.execute();
							}
						})
						.positiveText("Ну-ка...");
						dialogInputName = dialogInputNameBuilder.build();
						dialogInputName.show();
					}
				}
			});
			dialogVersionHistory = dialogGoProBuilder.build();
			dialogVersionHistory.show();

			return false;
		}
	};

	protected OnPreferenceClickListener usedLibsCL = new OnPreferenceClickListener()
	{
		@Override
		public boolean onPreferenceClick(Preference preference)
		{
			MaterialDialog dialogVersionHistory;
			MaterialDialog.Builder dialogGoProBuilder = new MaterialDialog.Builder(act);
			dialogGoProBuilder.title("Список использованных в разработке библиотек")
			.content(Html.fromHtml(act.getResources().getString(R.string.used_libs)))
			.positiveText("Это может пригодится...");
			dialogVersionHistory = dialogGoProBuilder.build();

			dialogVersionHistory.show();

			return false;
		}
	};

	CallbackEasterEggMusic callback = new CallbackEasterEggMusic()
	{
		@Override
		public void onAnswerFromServer(String answer)
		{
			Log.e(LOG, "answer from server in callback: " + answer);
			//			Log.i(LOG, "length is: " + answer.length() + " and must be: " + new String("mavrin-deja-vu").length());
			//			for (int i = 0; i < answer.toCharArray().length; i++)
			//			{
			//				char c = answer.toCharArray()[i];
			//				Log.d(LOG, "char code is :" + Character.codePointAt(answer.toCharArray(), i));
			//				Log.d(LOG, "char is :" + String.valueOf(Character.toChars(Character.codePointAt(answer.toCharArray(), i))[0]));
			//			}

			char zeroSizeSpace = Character.toChars(65279)[0];
			answer = answer.replaceAll(String.valueOf(zeroSizeSpace), "");
			final String songUrl = "http://kuchanov.ru/odnako/" + answer + ".mp3";
			Log.e(LOG, "songUrl: " + songUrl);

			final MediaPlayer mP = new MediaPlayer();
			try
			{
				final MaterialDialog dialogPlayer;
				MaterialDialog.Builder dialogPlayerBuilder = new MaterialDialog.Builder(act);
				dialogPlayerBuilder.title("Список использованных в разработке библиотек")
				.customView(R.layout.easter_egg_dialog, false);

				//.progress(false, 100, true);
				dialogPlayer = dialogPlayerBuilder.build();

				final SeekBar seekbar = (SeekBar) dialogPlayer.getCustomView().findViewById(R.id.seekbar);
				final TextView value = (TextView) dialogPlayer.getCustomView().findViewById(R.id.value);
				seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
				{
					@Override
					public void onStopTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar)
					{
					}

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
					{
						value.setText(String.valueOf(progress));
						if (fromUser)
						{
							mP.seekTo(progress);
						}
					}
				});

				mP.reset();
				mP.setOnPreparedListener(new OnPreparedListener()
				{
					@Override
					public void onPrepared(MediaPlayer mp)
					{
						if (!mP.isPlaying())
						{
							//							dialogPlayer.setProgress(0);
							//							dialogPlayer.setMaxProgress(mp.getDuration());
							seekbar.setProgress(0);
							seekbar.setMax(mp.getDuration());

							dialogPlayer.show();

							mP.start();
						}
						else
						{
							mP.pause();
						}
					}
				});
				mP.setOnBufferingUpdateListener(new OnBufferingUpdateListener()
				{
					@Override
					public void onBufferingUpdate(MediaPlayer mp, int percent)
					{
						seekbar.setSecondaryProgress(mp.getDuration()/100*percent);
						
						if (seekbar.getProgress() != seekbar.getMax())
						{
							// If the progress dialog is cancelled (the user closes it before it's done), break the loop
							if (dialogPlayer.isCancelled())
							{
								return;
							}
							value.setText(String.valueOf(mp.getCurrentPosition()));
							seekbar.setProgress(mp.getCurrentPosition());

							//							dialogPlayer.setProgress(mp.getCurrentPosition());
						}
					}
				});

				mP.setDataSource(songUrl);
				mP.prepare();
			} catch (IllegalArgumentException | SecurityException | IllegalStateException | IOException e)
			{
				e.printStackTrace();
			}
		}
	};
}
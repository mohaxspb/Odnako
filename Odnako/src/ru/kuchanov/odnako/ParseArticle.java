package ru.kuchanov.odnako;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.utils.ReadUnreadRegister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout.LayoutParams;

public class ParseArticle extends AsyncTask<Void, Integer, List<String>>
{

	ActionBarActivity act;
	Context ctx;
	ProgressDialog pd;
	String[] autorsInfoArr;
	public String title;
	String author;
	String artTxt;
	
	private String urlToArt;

	TextView artTextView;
	TextView artTitleTextView;
	TextView artAuthor;
	TextView artAuthorDescription;
	
	ImageView artAuthorDescriptionBtn;
	
	boolean fromFile=false;
	String path;
	
	SharedPreferences pref;
	
	public ParseArticle()
	{
		System.out.println("ParseArticle constructor");
		
	}

	public void setVars(String artTxt, ActionBarActivity act, TextView artTextView)
	{
		this.act = act;
		this.ctx=act;
		this.artTextView = artTextView;
		this.artTxt = artTxt;
		pref = PreferenceManager.getDefaultSharedPreferences(act);
	}
	public void setFilePath(boolean fromFile, String path)
	{
		this.path=path;
		this.fromFile=fromFile;
	}

	protected void onPreExecute()
	{
		this.urlToArt=ArticleActivity.artInfo[0];
		try
		{
			pd = new ProgressDialog(act);
			pd.setMessage("Работаю...");
			pd.setTitle("Загружаю статью");
			pd.show();
		} catch (Exception e)
		{
			System.out.println("Error in progressDialog created");
		}
	}

	// Фоновая операция
	protected List<String> doInBackground(Void... arg)
	{
		//this.url = arg[0];
		List<String> output = new ArrayList<String>();
		try
		{
			HtmlHelper hh;
			if(this.fromFile)
			{
				hh = new HtmlHelper(new URL("file://"+this.path));
			}
			else
			{
				hh = new HtmlHelper(new URL(ArticleActivity.artInfo[0]));
			}
			

			if (ArticleActivity.artInfo == null)
			{
				ArticleActivity.artInfo = new String[2];
			}
			//set authorsInfoArr
			this.autorsInfoArr=hh.getArcicleInfo();
			ArticleActivity.ART_AUTHOR_INFO=this.autorsInfoArr;
			//end
			this.title = autorsInfoArr[0];
			ArticleActivity.artInfo[1] = this.title;
			//test
			TagNode articleArr = hh.getArcicle();

			TagNode arrEl[] = articleArr.getChildTags();

			for (int b = 0; b < arrEl.length; b++)
			{
				if (arrEl[b].getName().equalsIgnoreCase("aside"))
				{
					// output.add(String.valueOf(arrEl.length));
				}
				else
				{
					FormatHtmlText format = new FormatHtmlText();
					output.add(format.formatNode(arrEl[b]));
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return output;
	}

	// Событие по окончанию парсинга
	protected void onPostExecute(List<String> output)
	{
		String artString = "";
		
		if (output.size() == 0)
		{
			Toast.makeText(act, "Не удалось связаться с odnako.org \n проверьте соединение с интернетом", Toast.LENGTH_LONG).show();
			title=ArticleActivity.artInfo[1];
			act.getSupportActionBar().setTitle(title);
			TextView artTitle = (TextView) act.findViewById(R.id.art_title);
			artTitle.setText(this.title);
		}
		else
		{
			//register as read
			ru.kuchanov.odnako.utils.ReadUnreadRegister readUnread;
			readUnread=new ReadUnreadRegister(act);
			readUnread.register(this.urlToArt);
			////end register as read
			act.getSupportActionBar().setTitle(title);
			ArticleActivity.artInfo[1] = this.title;
			for (String s : output)
			{
				artString += s;
			}
			artTxt = artString;
			
			///set text
			new ru.kuchanov.odnako.utils.SetTextViewHTML(act).SetText(artTextView, artTxt);
			artTextView.setLinksClickable(true);
			artTextView.setMovementMethod(LinkMovementMethod.getInstance());
			//Set Title of art
			artTitleTextView=(TextView)act.findViewById(R.id.art_title);
			artTitleTextView.setText(title);
			artTitleTextView.setTypeface(null,Typeface.BOLD);

			ArticleActivity.ART_TEXT = this.artTxt;
			//Art author
			artAuthor = (TextView) act.findViewById(R.id.art_author);
			if (this.autorsInfoArr != null)
			{
				if (!this.autorsInfoArr[2].equals("default"))
				{
					String autorsFieldText=null;
					autorsFieldText=this.autorsInfoArr[2];
					
					if (!this.autorsInfoArr[4].equals("default"))
					{
						autorsFieldText+="\n"+this.autorsInfoArr[4];
					}
					this.artAuthor.setText(autorsFieldText);
				}
				else
				{
					//this.artAuthor.setText("");
					this.artAuthor.setText(null);
					LayoutParams layParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
					this.artAuthor.setLayoutParams(layParams);
				}
				//set clickable 
				
				String theme = pref.getString("theme", "ligth");
				if (theme.equals("ligth"))
				{
					artAuthor.setTextColor(Color.BLACK);
				}
				else
				{
					artAuthor.setTextColor(Color.WHITE);
				}
				ImageView artAuthorAllArtsImg=(ImageView)act.findViewById(R.id.art_author_all_arts_btn);
				if (!autorsInfoArr[3].equals("default"))
				{
					artAuthorAllArtsImg.setOnClickListener(new OnClickListener()
					{
						public void onClick(View arg0)
						{
							if(!autorsInfoArr[3].equals("default"))
							{
								Intent intentToMain = new Intent(act, MainActivityNew.class);
								String[] infoToMain = new String[2];
								String linkToAutorsBlog=autorsInfoArr[3];
								if(linkToAutorsBlog.startsWith("http://"))
								{
									linkToAutorsBlog=linkToAutorsBlog.replace("http://", "");
								}
								infoToMain[0] = linkToAutorsBlog;
								infoToMain[1] = autorsInfoArr[2];
								intentToMain.putExtra(ArticleActivity.EXTRA_MESSAGE_TO_MAIN, infoToMain);
								act.startActivity(intentToMain);
							}
						}
					});
					ParseArticle.setImgViewSize(ctx, artAuthorAllArtsImg);
				}
				else
				{
					
					artAuthorAllArtsImg.setImageResource(0);
					artAuthorAllArtsImg.setOnClickListener(null);
					LayoutParams params=new LayoutParams(0,0);
					artAuthorAllArtsImg.setLayoutParams(params);
				}
				//end of set clicable
			}
			//end of Art author
			
			//art author description
			this.artAuthorDescription = (TextView) act.findViewById(R.id.art_author_description);
			this.artAuthorDescriptionBtn=(ImageView)act.findViewById(R.id.art_author_description_btn);
			this.artAuthorDescription.setText(null);
			LayoutParams layParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
			this.artAuthorDescription.setLayoutParams(layParams);
			if(!autorsInfoArr[5].equals("default"))
			{
				ParseArticle.setImgViewSize(ctx, artAuthorDescriptionBtn);
			}
			else
			{
				LayoutParams layParamsforDescrBtn = new LayoutParams(0, 0);
				artAuthorDescriptionBtn.setLayoutParams(layParamsforDescrBtn);
			}
			artAuthorDescriptionBtn.setOnClickListener(new OnClickListener()
			{
				public void onClick(View v)
				{
					SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(act);
					pref = PreferenceManager.getDefaultSharedPreferences(act);
					String theme=pref.getString("theme", "ligth");
					if (autorsInfoArr != null)
					{
						if(!autorsInfoArr[5].equals("default"))
						{
							if(artAuthorDescription.getLayoutParams().height==0)
							{
								LayoutParams layParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
								artAuthorDescription.setLayoutParams(layParams);
								String autorsFieldText=null;
								autorsFieldText=autorsInfoArr[5];
								artAuthorDescription.setText(Html.fromHtml(autorsFieldText));
								if(theme.equals("ligth"))
								{
									v.setBackgroundResource(R.drawable.ic_action_collapse_ligth);
								}
								else
								{
									v.setBackgroundResource(R.drawable.ic_action_collapse_dark);
								}
							}
							else
							{
								artAuthorDescription.setText(null);
								LayoutParams layParams = new LayoutParams(LayoutParams.WRAP_CONTENT, 0);
								artAuthorDescription.setLayoutParams(layParams);
								if(theme.equals("ligth"))
								{
									v.setBackgroundResource(R.drawable.ic_action_expand_ligth);
								}
								else
								{
									v.setBackgroundResource(R.drawable.ic_action_expand_dark);
								}
							}
						}
					}
				}
			});
			//end of art author description
			// ART_IMG
			ImageView artAuthorImage=(ImageView)act.findViewById(R.id.art_author_img);
			//ImageView artAuthorAllArtsImg=(ImageView)act.findViewById(R.id.art_author_all_arts_btn);
			if(!autorsInfoArr[1].equals("default"))
			{
				
				DownloadImageTask downFlag = (DownloadImageTask) new DownloadImageTask((ImageButton) null, (ImageView) artAuthorImage, (ActionBarActivity) act);
				downFlag.execute(this.autorsInfoArr[1]);
//				artAuthorImage.setPadding(10, 10, 10, 10);
//				artAuthorImage.setf
				ParseArticle.setImgViewSize(ctx, artAuthorImage);
				
			}
			else
			{
				LayoutParams params = new LayoutParams(0, 0);
				artAuthorImage.setLayoutParams(params);
			}
			//end of ART_IMG
		}
		try
		{
			pd.dismiss();
			pd.cancel();
			pd = null;
		} catch (Exception e)
		{
			//System.out.println(artTxt);
		}

		act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		//System.out.println(artTxt);
		
	}


	public String getResult()
	{
		return artTxt;
	}
	
	public static void setImgViewSize(Context ctx, ImageView iv)
	{
		final float scale = ctx.getResources().getDisplayMetrics().density;
		SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(ctx);
		pref = PreferenceManager.getDefaultSharedPreferences(ctx);
		String scaleFactorString = pref.getString("scale_art", "1");
		float scaleFactor = Float.valueOf(scaleFactorString);
		int pixels = (int) (75 * scaleFactor * scale + 0.5f);
		iv.setScaleType(ScaleType.FIT_XY);
		LayoutParams params = new LayoutParams(pixels, pixels);
		iv.setLayoutParams(params);
	}

}
package ru.kuchanov.odnako.utils;

import java.io.File;

import ru.kuchanov.odnako.ArticleActivity;
import ru.kuchanov.odnako.MyHtmlTagHandler;
import ru.kuchanov.odnako.ParseArticle;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.URLImageParser;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;

public class SetTextViewHTML
{
	Context context;
	TextView artTextView;
	//TextView artTitleTextView;
		

	public SetTextViewHTML(Context context)
	{
		this.context = context;
	}

	public void SetText(TextView artTextView, String html)
	{
		this.artTextView=artTextView;
		
		MyHtmlTagHandler tagHandler = new MyHtmlTagHandler();
		URLImageParser imgGetter = new URLImageParser(artTextView, context);
		CharSequence sequence = Html.fromHtml(html, imgGetter, tagHandler);
		SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
		URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
		for (URLSpan span : urls)
		{
			makeLinkClickable(strBuilder, span);
		}
		//////
		ImageSpan[] imgs = strBuilder.getSpans(0, sequence.length(), ImageSpan.class);
		for (ImageSpan span : imgs)
		{
			makeImgsClickable(strBuilder, span);
		}
		artTextView.setText(strBuilder);
		//artTextView.setText("skdgfkdsjgfksjdfkjsdh");
	}

	protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span)
	{
		int start = strBuilder.getSpanStart(span);
		int end = strBuilder.getSpanEnd(span);
		int flags = strBuilder.getSpanFlags(span);
		ClickableSpan clickable = new ClickableSpan()
		{
			@Override
			public void onClick(View view)
			{
				System.out.println("LINK CLICKED!");
				System.out.println("LINK CLICKED!" + span.getURL());
				
				//test scrolling to start
				ScrollView scrollView = (ScrollView)((ActionBarActivity)context).findViewById(R.id.art_scroll);
				scrollView.scrollTo(0, 0);
				
				if (span.getURL().startsWith("http://www.odnako.org"))
				{
					ArticleActivity.artInfo[0] = span.getURL();
					ArticleActivity.artInfo[1] = span.toString();
					ParseArticle parse = new ParseArticle();
					parse.setVars(ArticleActivity.ART_TEXT, (ActionBarActivity)context, artTextView);
					parse.execute();
				}
				else
				{
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(span.getURL()));
					context.startActivity(browserIntent);
				}
			}
		};
		strBuilder.setSpan(clickable, start, end, flags);
		strBuilder.removeSpan(span);
	}

	//	///////////////
	protected void makeImgsClickable(SpannableStringBuilder strBuilder, ImageSpan span)
	{
		final String image_src = span.getSource();
		final int start = strBuilder.getSpanStart(span);
		final int end = strBuilder.getSpanEnd(span);

		ClickableSpan click_span = new ClickableSpan()
		{
			@TargetApi(Build.VERSION_CODES.HONEYCOMB)
			@SuppressLint("NewApi")
			@Override
			public void onClick(View widget)
			{
				Dialog nagDialog = new Dialog(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
				nagDialog.setCancelable(true);
				nagDialog.setContentView(R.layout.preview_image);
				WebView web = (WebView) nagDialog.findViewById(R.id.web_preview_image);
				WebSettings zoomenable = web.getSettings();
				zoomenable.setBuiltInZoomControls(true);
				if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				{
					makeZoomable(web);
				}
				//////search if img file exists
				String dirToWrite = context.getFilesDir().getAbsolutePath();
				String formatedImgAdress;
				formatedImgAdress = image_src.replace("-", "_");
				formatedImgAdress = formatedImgAdress.replace("/", "_");
				formatedImgAdress = formatedImgAdress.replace(":", "_");
				File dirToWriteFile = new File(dirToWrite + "/images/");
				File blogsPageFile = new File(dirToWriteFile + "/" + formatedImgAdress);
				ArticleActivity.NAG_DIALOG_IMG_SOURSE = blogsPageFile.getAbsolutePath();
				System.out.println(ArticleActivity.NAG_DIALOG_IMG_SOURSE);
				ArticleActivity.NAG_DIALOG_IMG_SOURSE_URL = image_src;
				if (blogsPageFile.exists())
				{
					web.loadUrl("file:///" + blogsPageFile.getAbsolutePath());
				}
				else
				{
					web.loadUrl(image_src);
				}
				nagDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
				{
					@Override
					public void onCancel(DialogInterface dialog)
					{
						ArticleActivity.NAG_DIALOG_IMG_SOURSE = null;
						ArticleActivity.NAG_DIALOG_IMG_SOURSE_URL = null;
						System.out.println("nagDialog.onCancel ArtActivity");
					}
				});
				nagDialog.show();
			}
		};
		ClickableSpan[] click_spans = strBuilder.getSpans(start, end, ClickableSpan.class);

		if (click_spans.length != 0)
		{
			for (ClickableSpan c_span : click_spans)
			{
				strBuilder.removeSpan(c_span);
			}
		}
		strBuilder.setSpan(click_span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressWarnings({ "deprecation" })
	@SuppressLint("NewApi")
	void makeZoomable(WebView web)
	{
		web.canZoomIn();
		web.canZoomOut();
	}

}

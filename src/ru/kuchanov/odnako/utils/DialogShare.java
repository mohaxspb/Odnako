/*
 13.05.2015
DialogShare.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.util.Calendar;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import ru.kuchanov.odnako.Const;
import ru.kuchanov.odnako.R;
import ru.kuchanov.odnako.db.Article;
import ru.kuchanov.odnako.lists_and_utils.Actions;
import android.content.Context;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

public class DialogShare
{
	static final String LOG = DialogShare.class.getSimpleName();

	final static int SHARE_OPTION_URL = 0;
	final static int SHARE_OPTION_TEXT = 1;
	final static int SHARE_OPTION_TEXT_HTML = 2;

	public final static int SHARE_TYPE_URL = 3;
	public final static int SHARE_TYPE_TEXT = 4;
	public final static int SHARE_TYPE_ALL = 5;

	public static void showChoiceDialog(final Context ctx, final Article article, final int type)
	{
		MaterialDialog dialogShare;
		MaterialDialog.Builder dialogShareBuilder = new MaterialDialog.Builder(ctx);

		dialogShareBuilder.title("Чем поделиться?");

		switch (type)
		{
			case SHARE_TYPE_ALL:
				dialogShareBuilder.items(R.array.share_options);
			break;
			case SHARE_TYPE_TEXT:
				dialogShareBuilder.items(R.array.share_options_text_only);
			break;
		}

		dialogShareBuilder.itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice()
		{
			@Override
			public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text)
			{
				/**
				 * If you use alwaysCallSingleChoiceCallback(), which is
				 * discussed below, returning false here won't allow the newly
				 * selected radio button to actually be selected.
				 **/
				String[] shareOptions = ctx.getResources().getStringArray(R.array.share_options);
				String choosenOption = shareOptions[which];
				Log.i(LOG, choosenOption);

				switch (type)
				{
					case SHARE_TYPE_ALL:
					//do nothing
					break;
					case SHARE_TYPE_TEXT:
						//we don't want to copt onClickListner for list, so simply add +1 to which,
						//so we'll receive only text options in dialog
						which++;
					break;
				}
				StringBuilder sb = new StringBuilder();
				Calendar cal = Calendar.getInstance();
				String textToShare;
				String date;

				switch (which)
				{
					case SHARE_OPTION_URL:
						textToShare = article.getUrl();
						Actions.shareUrl(textToShare, ctx);
					break;
					case SHARE_OPTION_TEXT:
						//add title
						sb.append(article.getTitle());
						sb.append("\n\n");

						//Add link to article
						sb.append("Ссылка на статью: " + article.getUrl());
						sb.append("\n\n");

						//add pubDate
						cal.setTime(article.getPubDate());
						date = DateUtils.formatDateTime(ctx, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
						sb.append("Дата публикации статьи: " + date);
						sb.append("\n\n");

						//add author if is
						sb.append((article.getAuthorName().equals(Const.EMPTY_STRING) ? "" : "Автор: "
						+ article.getAuthorName()));
						sb.append("\n\n");

						HtmlCleaner hc = new HtmlCleaner();
						for (TagNode t : getArticlesTags(article))
						{
							switch (t.getName())
							{
								case "div":
								case "p":
									TagNode[] tags = t.getChildTags();
									for (TagNode tag : tags)
									{
										TagNode tagWithReplacedATags = HtmlTextFormatting
										.replaceATags(tag);
										sb.append(Html.fromHtml(
										"<"
										+ tagWithReplacedATags.getName()
										+ ">"
										+
										Html.fromHtml(hc.getInnerHtml(tagWithReplacedATags), null,
										new MyHtmlTagHandler()) +
										"</" + tagWithReplacedATags + ">", null, new MyHtmlTagHandler()));
									}
								break;
								case "img":
									String imgTagReplacement = "Ссылка на изображение: \n "
									+ t.getAttributeByName("src") + " \n\n";
									sb.append(imgTagReplacement);
								break;
								case "a":
									String aTagReplacement = t.getText().toString() + " ("
									+ t.getAttributeByName("href")
									+ ")  \n\n";
									sb.append(aTagReplacement);
								break;
							}

						}
						sb.append("\n\n");
						sb.append("Отправлено с помощью приложения \"Однако, Новости и аналитика\" \n"
						+ "https://play.google.com/store/apps/details?id=ru.kuchanov.odnako");

						textToShare = sb.toString();

						Actions.shareArtText(textToShare, ctx);
					break;
					case SHARE_OPTION_TEXT_HTML:
						//add title
						sb.append(article.getTitle());
						sb.append("\n\n");

						//Add link to article
						sb.append("Ссылка на статью: " + article.getUrl());
						sb.append("\n\n");

						//add pubDate
						cal.setTime(article.getPubDate());
						date = DateUtils.formatDateTime(ctx, cal.getTimeInMillis(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_YEAR);
						sb.append("Дата публикации статьи: " + date);
						sb.append("\n\n");

						//add author if is
						sb.append((article.getAuthorName().equals(Const.EMPTY_STRING) ? "" : "Автор: "
						+ article.getAuthorName()));
						sb.append("\n\n");

						//articles text
						sb.append(article.getArtText());

						sb.append("\n\n");
						sb.append("Отправлено с помощью приложения \"Однако, Новости и аналитика\" \n"
						+ "https://play.google.com/store/apps/details?id=ru.kuchanov.odnako");

						textToShare = sb.toString();
						Actions.shareArtText(textToShare, ctx);
					break;
				}
				return true;
			}
		});
		dialogShare = dialogShareBuilder.build();
		dialogShare.show();
	}

	public static TagNode[] getArticlesTags(Article article)
	{
		String articleString = article.getArtText();
		HtmlCleaner cleaner = new HtmlCleaner();
		TagNode articleTextTag = cleaner.clean(articleString);
		//it's unexpectable, but this TagNode have "head" and "body" tags...
		//So we only need innerHTML from "body" tag;
		TagNode[] articlesTags = articleTextTag.findElementByName("body", true).getChildTags();
		TagNode formatedArticle = HtmlTextFormatting.format(articlesTags);

		formatedArticle = HtmlTextFormatting.reduceTagsQuont(formatedArticle);

		return formatedArticle.getChildTags();
	}
}
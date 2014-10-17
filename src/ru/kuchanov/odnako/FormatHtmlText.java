package ru.kuchanov.odnako;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

public class FormatHtmlText
{
	String site = "http://www.odnako.org";

	public String formatNode(TagNode tagnode)
	{
		String formatedString = "";

		TagNode el = tagnode;
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		//formatedString = "<" + el.getName() + ">" + el.getText() + "</" + el.getName() + ">";
		if(el.getName().equals("a"))
		{
			//String formatedTag = "";
			//formatedTag = el.getText().toString();
			String formatedTagHREF = el.getAttributeByName("href");
			if (formatedTagHREF.startsWith("/"))
			{
				formatedTagHREF = site + formatedTagHREF;
				formatedString = el.getParent().getText()+"<" + el.getName() + " href='" + formatedTagHREF + "'>" + htmlCleaner.getInnerHtml(el) + "</" + el.getName() + ">";
			}
			else
			{
				formatedString = el.getParent().getText()+"<" + el.getName() + " href='" + formatedTagHREF + "'>" + htmlCleaner.getInnerHtml(el) + "</" + el.getName() + ">";
			}
			//formatedString = formatedString.replace(formatedTag, "<a " + "href='" + formatedTagHREF + "'>" + formatedTag + "</a>");
			//formatedString = el.getParent().getText()+"<" + el.getName() + " href='" + formatedTagHREF + "'>" + htmlCleaner.getInnerHtml(el) + "</" + el.getName() + ">";
		}
		else
		{
			formatedString = "<" + el.getName() + ">" + htmlCleaner.getInnerHtml(el) + "</" + el.getName() + ">";
		}
		
		// ///////FORMATING <STRONG> tags
		TagNode[] tagStrong = el.getElementsByName("strong", true);
		for (int v = 0; v < tagStrong.length; v++)
		{
			String formatedStrongTag = "";
			formatedStrongTag = tagStrong[v].getText().toString();
			formatedString = formatedString.replace(formatedStrongTag, "<strong>" + formatedStrongTag + "</strong>");
		}

		formatedString = formatedString.replace("<strong>", "<b>");
		formatedString = formatedString.replace("</strong>", "</b>");
		// ///////FORMATING <STRONG> tags

		// ///////FORMATING <a> tags
//		TagNode[] tagA = el.getElementsByName("a", true);
//		for (int v = 0; v < tagA.length; v++)
//		{
//			String formatedTag = "";
//			formatedTag = tagA[v].getText().toString();
//			String formatedTagHREF = tagA[v].getAttributeByName("href");
//			if (formatedTagHREF.startsWith("/"))
//			{
//				formatedTagHREF = site + formatedTagHREF;
//			}
//			formatedString = formatedString.replace(formatedTag, "<a " + "href='" + formatedTagHREF + "'>" + formatedTag + "</a>");
//		}
		// ///////FORMATING <a> tags
		
		// ///////FORMATING <iframe> tags
		TagNode[] iframe = el.getElementsByName("iframe", true);
		for (int v = 0; v < iframe.length; v++)
		{
			System.out.println(iframe.length+el.toString());
			//String formatedTag = "";
			//formatedTag = htmlCleaner.getInnerHtml(iframe[v]);//iframe[v].getText().toString();
			String formatedTagHREF = iframe[v].getAttributeByName("src");
			
			if(formatedTagHREF.startsWith("http"))
			{
				formatedString += "<p><a " + "href='"+formatedTagHREF + "'>—Ò˚ÎÍ‡ Ì‡ ‚Ë‰ÂÓ</a></p>";
			}
			else
			{
				formatedString += "<p><a " + "href='http:" + formatedTagHREF + "'>—Ò˚ÎÍ‡ Ì‡ ‚Ë‰ÂÓ</a></p>";
			}
			
		}
		// ///////FORMATING <iframe> tags

		// ///////FORMATING «¿◊◊◊®– Õ”“€… “≈ —“ tags
		TagNode[] tagSRTIKE = el.getElementsByAttValue("style", "text-decoration: line-through;", true, false);
		for (int v = 0; v < tagSRTIKE.length; v++)
		{
			String formatedTag = "";
			formatedTag = tagSRTIKE[v].getText().toString();
			formatedString = formatedString.replace(formatedTag, "<strike>" + formatedTag + "</strike>");
		}
		// ///////FORMATING «¿◊◊◊®– Õ”“€… “≈ —“ tags

		// ///////FORMATING li  tags
		TagNode[] tagLI = el.getElementsByName("li", true);
		for (int v = 0; v < tagLI.length; v++)
		{
			String formatedTag = "";
			formatedTag = tagLI[v].getText().toString();
			formatedString = formatedString.replace(formatedTag, "<li>" + formatedTag + "</li>");
		}
		// ///////FORMATING li  tags

		// ///////FORMATING p  tags
//		TagNode[] tagP = el.getElementsByName("p", true);
//		for (int v = 0; v < tagP.length; v++)
//		{
//			String formatedTag = "";
//			formatedTag = tagP[v].getText().toString();
//			formatedString = formatedString.replace(formatedTag, "<" + tagP[v].getName() + ">" + formatedTag + "</" + tagP[v].getName() + ">");
//		}
		////////FORMATING p  tags

		////////FORMATING img  tags
		TagNode[] tagImg = el.getElementsByName("img", true);
		for (int v = 0; v < tagImg.length; v++)
		{
			String imgSrc = "";
			imgSrc = tagImg[v].getAttributeByName("src");
			if (imgSrc.startsWith("/"))
			{
				imgSrc = site + imgSrc;
			}
			formatedString = "<p><" + tagImg[v].getName() + " src='" + imgSrc + "' /></p>";

		}
		////////FORMATING img tags
		
		////////FORMATING input  tags
		TagNode[] tagInput = el.getElementsByName("input", true);
		for (int v = 0; v < tagInput.length; v++)
		{
			String imgSrc = "";
			imgSrc = tagInput[v].getAttributeByName("src");
			if (imgSrc.startsWith("/"))
			{
				imgSrc = site + imgSrc;
			}
			formatedString = "<p><" + "img" + " src='" + imgSrc + "' /></p>";

		}
		////////FORMATING input tags

		/////////FORMATING blockquote  tags
		TagNode[] tagBlockQuote = el.getElementsByName("blockquote", true);
		for (int v = 0; v < tagBlockQuote.length; v++)
		{
			String formatedTag = "";
			formatedTag = tagBlockQuote[v].getText().toString();
			formatedString = formatedString.replace(formatedTag, "<" + tagBlockQuote[v].getName() + ">" + formatedTag + "</" + tagBlockQuote[v].getName() + ">");
		}
		////////FORMATING blockquote  tags
		//System.out.println(formatedString);
		return formatedString;
	}
}

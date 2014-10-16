package ru.kuchanov.odnako.utils;

import android.net.Uri;

public class FilenameUtilTest
{

	public String main(String urlToTransform)
	{
		Uri uriSmall = null;
		try
		{
			uriSmall = Uri.parse(urlToTransform);
		} catch (Exception e)
		{
			System.out.println("Error in formating russian chars in url adress");
			e.printStackTrace();
		}

		String protocol = uriSmall.getScheme();
		String site = uriSmall.getHost();
		String baseName = uriSmall.getPath();
		//String extension = uriSmall.getLastPathSegment();
		String fullPath = protocol + "://" + site + baseName;

		System.out.println("protocol : " + protocol);
		System.out.println("site : " + site);
		System.out.println("Basename : " + baseName);
		System.out.println("fullPath : " + fullPath);
		//System.out.println("extension : " + extension);

		return fullPath;
	}

}
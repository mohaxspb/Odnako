/*
 21.05.2015
MyPlayer.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import android.media.MediaPlayer;

public class MyPlayer
{
	static MediaPlayer mp;

	public static MediaPlayer getMediaPlayer()
	{
		if (mp == null)
		{
			mp = new MediaPlayer();
		}

		return mp;
	}

}

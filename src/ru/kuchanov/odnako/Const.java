/*
 03.03.2015
Const.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako;

public class Const
{

	public static class Action
	{
		/**
		 * const for intent action for requesting data from service
		 */
		public static final String DATA_REQUEST = Action.class.getName() + ".DATA_REQUEST";
		/**
		 * const for intent action for requesting if some task is running
		 */
		public static final String IS_LOADING = Action.class.getName() + ".IS_LOADING";
	}
}

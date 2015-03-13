/*
 03.03.2015
Const.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako;

public class Const
{
	public static final String EMPTY_STRING = "empty";

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

	public static class Error
	{
		public static final String CONNECTION_ERROR = "Ошибка соединения. Проверьте соединение с интернетом";
		public static final String CYRILLIC_ERROR = "Ошибка сайта из-за кириллицы в адресе. Загрузить невозможно и разработчик приложения тут не при чём(";
	}
}

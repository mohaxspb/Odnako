/*
 03.03.2015
Const.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako;

public final class Const
{
	public static final String EMPTY_STRING = "empty";

	public static boolean IS_PRO = false;

	public final static class Action
	{
		private static final String ACTION = "ru.kuchanov.odnako.Const.Action";

		/**
		 * const for intent action for requesting data from service
		 */
		public static final String DATA_REQUEST = ACTION + ".DATA_REQUEST";

		/**
		 * const for intent action for MULTIPLE data requesting from service
		 */
		public static final String DATA_REQUEST_MULTIPLE = ACTION + ".DATA_REQUEST_MULTIPLE";

		/**
		 * const for intent action for requesting if some task is running
		 */
		public static final String IS_LOADING = ACTION + ".IS_LOADING";
		/**
		 * const for intent action for notifying that article changed;
		 */
		public static final String ARTICLE_CHANGED = ACTION + ".ARTICLE_CHANGED";
		/**
		 * const for intent action for notifying that article loaded;
		 */
		public static final String ARTICLE_LOADED = ACTION + ".ARTICLE_LOADED";
		/**
		 * const for intent action for notifying that article loaded;
		 */
		public static final String ARTICLE_DELETED = ACTION + ".ARTICLE_DELETED";
		/**
		 * const for intent action for notifying that article is read;
		 */
		public static final String ARTICLE_READ = ACTION + ".ARTICLE_READ";

		/**
		 * const for downloading articles
		 */
		public static final String DATA_DOWNLOAD = ACTION + ".DATA_DOWNLOAD";

		/**
		 * const for requset for downloaded articles
		 */
		public static final String GET_DOWNLOADED = ACTION + ".GET_DOWNLOADED";
	}

	public static class Error
	{
		public static final String CONNECTION_ERROR = "Ошибка соединения. Проверьте соединение с интернетом";
		public static final String CANCELLED_ERROR = "Загрузка прервана";
		public static final String CYRILLIC_ERROR = "Ошибка сайта из-за кириллицы в адресе. Загрузить невозможно и разработчик приложения тут не при чём(";
	}
}

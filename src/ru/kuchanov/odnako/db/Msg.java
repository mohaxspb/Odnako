/*
 07.02.2015
Msg.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.db;

/**
 * Class for DB answers on requests. I.e. answers for searching for new arts, writing them and so on
 */
public class Msg
{
	public static final String DB_ANSWER_NO_ARTS_IN_CATEGORY = "we cant find any arts in category";
	
	public static final String DB_ANSWER_WRITE_FROM_TOP_NO_MATCHES = "load from top with no matches to previous arts";

	public static final String DB_ANSWER_WRITE_PROCESS_RESULT_ALL_RIGHT = "all right";
	
	public static final String DB_ANSWER_WRITE_FROM_BOTTOM_EXCEPTION = "we catch it, so start load from top!";

	public static final String DB_ANSWER_FROM_BOTTOM_LESS_30_HAVE_MATCH_TO_INITIAL = "we have LESS than 30, and have match to initial";
	public static final String DB_ANSWER_FROM_BOTTOM_LESS_30_NO_MATCH_TO_INITIAL = "we have LESS than 30, and have NO match to initial";
	public static final String DB_ANSWER_FROM_BOTTOM_INITIAL_ART_ALREADY_SHOWN = "initial art is already shown, so we must do nothing";
	public static final String DB_ANSWER_FROM_BOTTOM_LESS_30_NO_INITIAL = "we have LESS than 30, but no initial art";
	public static final String DB_ANSWER_FROM_BOTTOM_INFO_SENDED_TO_FRAG = "we have already send bottom info from DB to frag";
	public static final String DB_ANSWER_FROM_BOTTOM_NO_ARTS_AT_ALL = "no arts at all";
	public final static String DB_ANSWER_FROM_BOTTOM_LESS_THEN_30_FROM_TOP = "less then 30 from top";

	final public static String DB_ANSWER_NEVER_REFRESHED = "never refreshed";
	final public static String DB_ANSWER_REFRESH_BY_PERIOD = "refresh by period";
	final public static String DB_ANSWER_INFO_SENDED_TO_FRAG = "we have already send info from DB to frag";
	final public static String DB_ANSWER_NO_ENTRY_OF_ARTS = "no_entry_in_db";
	final public static String DB_ANSWER_UNKNOWN_CATEGORY = "no entries in Category and Author";

	public final static String MSG = "msg";

	public static final String ERROR = "error";

	public final static String NO_NEW = "no new";
	public final static String QUONT = "quont";
	public final static String NEW_QUONT = "new quont";
}

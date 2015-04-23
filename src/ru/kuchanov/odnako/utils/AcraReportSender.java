/*
 22.04.2015
AcraReportSender.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.collector.CrashReportData;
import org.acra.sender.ReportSender;
import org.acra.sender.ReportSenderException;

import android.content.Context;
import android.util.Log;

import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class AcraReportSender implements ReportSender
{
	private final static String LOG = AcraReportSender.class.getSimpleName() + "/";

	public AcraReportSender()
	{
		// initialize your sender with needed parameters
	}

	@Override
	public void send(Context ctx, CrashReportData report) throws ReportSenderException
	{
		Log.e(LOG, "send called");
		// Iterate over the CrashReportData instance and do whatever
		// you need with each pair of ReportField key / String value
		FormEncodingBuilder builder = new FormEncodingBuilder();

		ArrayList<ReportField> rfs = new ArrayList<ReportField>(report.keySet());
		ArrayList<String> props = new ArrayList<String>();
		for (int i = 0; i < rfs.size(); i++)
		{
			ReportField rf = rfs.get(i);
			String prop = report.getProperty(rf);
			props.add(prop);
			builder.add(rf.toString(), prop);
		}

		RequestBody formBody = builder.build();

		Request request = new Request.Builder()
		.url(ACRA.getConfig().formUri())
		.post(formBody)
		.build();

		try
		{
			OkHttpClient client = new OkHttpClient();
			Response response;
			response = client.newCall(request).execute();
			Log.e(LOG, response.body().string());
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}

//BACKEND PHP CODE

//<?php
//$user="p211659_db";
//$pass="DO YOU REALLY EXPECTED TO SEE HERE MY PASSWORD?.. REALLY?..";
//
//$db="p211659_db";
//$host="p211659.mysql.ihc.ru";
//$charset="utf-8";
//
//$dsn = "mysql:host=$host;dbname=$db;charset=$charset";
//$opt = array
//(
//	PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
//	PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
//);
//$pdo = new PDO($dsn, $user, $pass, $opt);
//
//$table = "acra_reports";
//try 
//{
//	 $sql ="CREATE TABLE IF NOT EXISTS $table (
//	 ID INT( 11 ) AUTO_INCREMENT PRIMARY KEY,";
//	foreach($_POST as $key => $value)
//	{
//		$sql.=" $key TEXT( 2000 ), ";
//	}
//	$sql = substr($sql, 0, -2);
//	$sql.=" );";
//	//echo $sql;
//	$pdo->exec($sql);
//print("Created $table Table.\n");
//} catch(PDOException $e) 
//{
//	echo $e->getMessage();//Remove or change message in production code
//}
//
//$sql = "INSERT INTO $table (";
//
//foreach($_POST as $key => $value)
//{
//	$sql.=" $key,";
//}
//$sql = substr($sql, 0, -1);
//$sql.=" ) VALUES (";
//foreach($_POST as $key => $value)
//{
//	$sql.=" :$key,";
//}
//$sql = substr($sql, 0, -1);
//$sql.=" )";
//                                      
//$stmt = $pdo->prepare($sql);
//
//foreach($_POST as $key => $value)
//{
//	$var=":".$key;
//	$stmt->bindParam($var, $_POST[$key], PDO::PARAM_STR);
//}
//$stmt->execute(); 
//?>
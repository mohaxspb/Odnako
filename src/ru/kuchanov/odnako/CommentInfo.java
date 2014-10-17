package ru.kuchanov.odnako;

public class CommentInfo
{
	Integer length = 12;

	String[] CommInfo;

	String name;
	String txt;
	String flag;
	String time;
	String like;
	String dislike;
	String avaImg;
	String city;
	String data_pid;
	String id;
	String padding;
	String numOfCommsPages;

	/*
	 * CommentInfo(String txt, String name, String flag, String time, String
	 * city, String like, String dislike, String avaImg, String data_pid, String
	 * id, String padding, String numOfCommsPages) { this.name = name; this.txt
	 * = txt; this.flag = flag; this.time = time; this.city = city; this.like =
	 * like; this.dislike = dislike; this.avaImg = avaImg;
	 * this.data_pid=data_pid; this.id=id; this.padding=padding;
	 * this.numOfCommsPages=numOfCommsPages; }
	 */
	CommentInfo(String[] commInfoArr)
	{

		
		this.name = commInfoArr[1];
		this.txt = commInfoArr[0];
		this.flag = commInfoArr[2];
		this.time = commInfoArr[3];
		this.city = commInfoArr[4];
		this.like = commInfoArr[5];
		this.dislike = commInfoArr[6];
		this.avaImg = commInfoArr[7];
		this.data_pid = commInfoArr[8];
		this.id = commInfoArr[9];
		this.padding = commInfoArr[10];
		this.numOfCommsPages = commInfoArr[11];
	}

	public void fillCommInfo(String[] commInfoArr)
	{
		this.name = commInfoArr[1];
		this.txt = commInfoArr[0];
		this.flag = commInfoArr[2];
		this.time = commInfoArr[3];
		this.city = commInfoArr[4];
		this.like = commInfoArr[5];
		this.dislike = commInfoArr[6];
		this.avaImg = commInfoArr[7];
		this.data_pid = commInfoArr[8];
		this.id = commInfoArr[9];
		this.padding = commInfoArr[10];
		this.numOfCommsPages = commInfoArr[11];
	}

	public Integer getLength()
	{
		return length;
	}

	public String[] getStrArr()
	{
		String[] StrArr = new String[length];
		StrArr[0] = txt;
		StrArr[1] = name;
		StrArr[2] = flag;
		StrArr[3] = time;
		StrArr[4] = city;
		StrArr[5] = like;
		StrArr[6] = dislike;
		StrArr[7] = avaImg;
		StrArr[8] = data_pid;
		StrArr[9] = id;
		StrArr[10] = padding;
		StrArr[11] = numOfCommsPages;

		return StrArr;
	}
}
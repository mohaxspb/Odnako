package ru.kuchanov.odnako.lists_and_utils;

public class CommentInfo
{
	String[] CommInfo;

	public String name, txt, flag, time, like, dislike, avaImg, city, data_pid, id, padding, numOfCommsPages;

	public CommentInfo(String name, String txt, String flag, String time, String city, String like, String dislike, String avaImg, String data_pid, String id, String padding, String numOfCommsPages)
	{
		this.name = name;
		this.txt = txt;
		this.flag = flag;
		this.time = time;
		this.city = city;
		this.like = like;
		this.dislike = dislike;
		this.avaImg = avaImg;
		this.data_pid = data_pid;
		this.id = id;
		this.padding = padding;
		this.numOfCommsPages = numOfCommsPages;

		this.CommInfo=new String[12];
		this.CommInfo[0] = this.name;
		this.CommInfo[1] = this.txt;
		this.CommInfo[2] = this.flag;
		this.CommInfo[3] = this.time;
		this.CommInfo[4] = this.city;
		this.CommInfo[5] = this.like;
		this.CommInfo[6] = this.dislike;
		this.CommInfo[7] = this.avaImg;
		this.CommInfo[8] = this.data_pid;
		this.CommInfo[9] = this.id;
		this.CommInfo[10] = this.padding;
		this.CommInfo[11] = this.numOfCommsPages;
	}

	public CommentInfo(String[] commInfoArr)
	{
		this.name = commInfoArr[0];
		this.txt = commInfoArr[1];
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

		this.CommInfo = commInfoArr;
	}

	public void fillCommInfo(String[] commInfoArr)
	{
		this.name = commInfoArr[0];
		this.txt = commInfoArr[1];
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

		this.CommInfo = commInfoArr;
	}

	public String[] getCommentInfoAsStringArr()
	{
		return this.CommInfo;
	}
}

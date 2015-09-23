package ru.kuchanov.odnako.download;

import java.util.ArrayList;

import ru.kuchanov.odnako.Const;
import android.os.Parcel;
import android.os.Parcelable;

public class CommentInfo implements Parcelable
{
	public final static String KEY_ALL_COMMENTS_LIST = "all comments list";
	public final static String KEY_COMMENT = "comment";
	//	String[] CommInfo;

	public String name, txt, flag, time, like, dislike, avaImg, city, data_pid, id, padding,
	numOfCommsPages = Const.EMPTY_STRING;

	public CommentInfo(String name, String txt, String flag, String time, String city, String like, String dislike,
	String avaImg, String data_pid, String id, String padding, String numOfCommsPages)
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

		//		this.CommInfo = new String[12];
		//		this.CommInfo[0] = this.name;
		//		this.CommInfo[1] = this.txt;
		//		this.CommInfo[2] = this.flag;
		//		this.CommInfo[3] = this.time;
		//		this.CommInfo[4] = this.city;
		//		this.CommInfo[5] = this.like;
		//		this.CommInfo[6] = this.dislike;
		//		this.CommInfo[7] = this.avaImg;
		//		this.CommInfo[8] = this.data_pid;
		//		this.CommInfo[9] = this.id;
		//		this.CommInfo[10] = this.padding;
		//		this.CommInfo[11] = this.numOfCommsPages;
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

		//		this.CommInfo = commInfoArr;
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

		//		this.CommInfo = commInfoArr;
	}

	//	public String[] getCommentInfoAsStringArr()
	//	{
	//		return this.CommInfo;
	//	}

	public static CommentInfo getDefaultCommentInfo()
	{
		CommentInfo defCommInfo;
		String[] defCommInfoArr;
		defCommInfoArr = new String[12];
		defCommInfoArr[0] = "name_" + String.valueOf(0);
		defCommInfoArr[1] = "comment_text_" + String.valueOf(0);
		defCommInfoArr[2] = "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg";
		defCommInfoArr[3] = "1 сентября 1939";
		defCommInfoArr[4] = "Saint-Petersburg";
		defCommInfoArr[5] = String.valueOf(0);
		defCommInfoArr[6] = String.valueOf(0);
		defCommInfoArr[7] = "https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg";
		defCommInfoArr[8] = "data_pid";
		defCommInfoArr[9] = "id";
		defCommInfoArr[10] = "0";
		defCommInfoArr[11] = "5";
		defCommInfo = new CommentInfo(defCommInfoArr);

		return defCommInfo;
	}

	public static ArrayList<CommentInfo> getDefaultArtsCommentsInfo(int numOfComments)
	{

		//fill Arraylist with artsInfo
		ArrayList<CommentInfo> curArtCommentsInfoList = new ArrayList<CommentInfo>();
		for (int i = 0; i < numOfComments; i++)
		{
			curArtCommentsInfoList.add(CommentInfo.getDefaultCommentInfo());
		}

		CommentInfo artInfoTEST = new CommentInfo("Юрий", "Тестовы коммент",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "1 сентября 1939", "Saint-Petersburg", "100", "0",
		"https://pp.vk.me/c9733/u77102/151125793/w_91f2635a.jpg", "100", "1000", "0", "5");

		curArtCommentsInfoList.set(1, artInfoTEST);

		return curArtCommentsInfoList;
	}

	public static ArrayList<ArrayList<CommentInfo>> getDefaultAllArtsCommentsInfo(int numOfArts, int numOfComments)
	{
		ArrayList<ArrayList<CommentInfo>> allArtsCommentsInfo = new ArrayList<ArrayList<CommentInfo>>(numOfArts);

		for (int i = 0; i < numOfArts; i++)
		{
			allArtsCommentsInfo.add(CommentInfo.getDefaultArtsCommentsInfo(numOfComments));
		}

		return allArtsCommentsInfo;
	}

	//////PARCEL implementation
	@Override
	public int describeContents()
	{
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(name);
		dest.writeString(txt);
		dest.writeString(flag);
		dest.writeString(time);
		dest.writeString(city);

		dest.writeString(like);

		dest.writeString(dislike);
		dest.writeString(avaImg);
		dest.writeString(data_pid);
		dest.writeString(id);
		dest.writeString(padding);
		dest.writeString(numOfCommsPages);
	}

	private CommentInfo(Parcel in)
	{
		this.name = in.readString();
		this.txt = in.readString();
		this.flag = in.readString();
		this.time = in.readString();
		this.city = in.readString();
		this.like = in.readString();
		this.dislike = in.readString();
		this.avaImg = in.readString();
		this.data_pid = in.readString();
		this.id = in.readString();
		this.padding = in.readString();
		this.numOfCommsPages = in.readString();
	}

	public static final Parcelable.Creator<CommentInfo> CREATOR = new Parcelable.Creator<CommentInfo>()
	{

		@Override
		public CommentInfo createFromParcel(Parcel source)
		{
			return new CommentInfo(source);
		}

		@Override
		public CommentInfo[] newArray(int size)
		{
			return new CommentInfo[size];
		}
	};

	/////////////////////////////
}

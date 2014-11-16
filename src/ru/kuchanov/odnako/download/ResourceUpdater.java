/*
 11.11.2014
ResourceUpdater.java
Created by Kuchanov Yuri,
mohax.spb@gmail.com
 */
package ru.kuchanov.odnako.download;

public class ResourceUpdater
{

	/**
	 * 
	 */
	public ResourceUpdater()
	{
		// TODO Auto-generated constructor stub
		//download all categories images
		//				String[] allCatUrls=this.getResources().getStringArray(R.array.all_categories_urls);
		//				for(int i=0; i<allCatUrls.length; i++)
		//				{
		//					ParseForAllCategoriesImages parse=new ParseForAllCategoriesImages(act);
		//					parse.execute(allCatUrls[i]);
		//				}
		//				String[] allCatImgsUrls=this.getResources().getStringArray(R.array.all_categories_imgs);
		//				for(String s: allCatImgsUrls)
		//				{
		//					String output=s.substring(s.lastIndexOf("/")+1);
		//					output=output.replace("-", "_");
		//					String dataToWrite = "<item><![CDATA[" + output + "]]></item>\n";
		//					WriteFile write = new WriteFile(dataToWrite, "allCategoriesImgsFilesNames", "all_category_imgs_files_names.txt", act);
		//					write.execute();
		//				}
		//				String[] allCategoriesMenuLinks=this.getResources().getStringArray(R.array.categories_links); 
		//				for(int i=0; i<allCategoriesMenuLinks.length; i++)
		//				{
		//					ParseForAllCategoriesImages parse = new ParseForAllCategoriesImages(act);
		//					parse.execute("http://"+allCategoriesMenuLinks[i]);
		//				}
		//				String[] allCatImgsUrls=this.getResources().getStringArray(R.array.categories_imgs_urls);
		//				for(String s: allCatImgsUrls)
		//				{
		//					String output=s.substring(s.lastIndexOf("/")+1);
		//					output=output.replace("-", "_");
		//					String dataToWrite = "<item><![CDATA[" + output + "]]></item>\n";
		//					WriteFile write = new WriteFile(dataToWrite, "categoriesImgsFilesNames", "categories_imgs_files_names.txt", act);
		//					write.execute();
		//				}
		
		
//		ParseForAllCategories parse = new ParseForAllCategories(act);
//		parse.execute("http://odnako.org/");
//		ParseForAllAuthors parse1 = new ParseForAllAuthors(act);
//		parse1.execute("http://odnako.org/authors/");
	}

}

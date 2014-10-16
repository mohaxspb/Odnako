package ru.kuchanov.odnako.download;

import java.io.File;

import ru.kuchanov.odnako.R;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

public class DeleteFilesTask extends AsyncTask<Void, Void, Void>
{
	Context context;

	public DeleteFilesTask(Context context)
	{
		this.context = context;
	}

	protected void onPreExecute()
	{
		System.out.println("DeleteFilesTask: onPreExecute");
	}

	protected Void doInBackground(Void... arg)
	{
		System.out.println("DeleteFilesTask: doInBackground");
		String[] typesOfFilesDirs = context.getResources().getStringArray(R.array.files_dir_values);
		for (int i = 0; i < typesOfFilesDirs.length; i++)
		{
			String curFilesDir = typesOfFilesDirs[i];
			File filesDirFile = new File(curFilesDir);
			if (filesDirFile.listFiles() != null)
			{
				for (File f : filesDirFile.listFiles())
				{
					if (f.isDirectory())
					{
						for (File child : f.listFiles())
						{
							child.delete();
						}
					}
					else
					{
						f.delete();
					}
					f.delete();
				}
			}
		}
		return null;
	}

	protected void onPostExecute(Void output)
	{
		System.out.println("DeleteFilesTask: onPostExecute");
		Intent serviceIntent = new Intent(context, ru.kuchanov.odnako.download.DeleteService.class);
		context.stopService(serviceIntent);
		Toast.makeText(context, "Память очищена!", Toast.LENGTH_SHORT).show();
	}
}

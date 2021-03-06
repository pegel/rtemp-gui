/*
    Copyright 2014 Peter Gelsbo and Andreas Nordmand Andersen

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	    http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */

package Static;
import java.io.File;
import java.util.ArrayList;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import Model.Setting;
import Model.Interfaces.IConfig;

@Root
public class Settings implements IConfig
{
	static Settings instance;
	
	@ElementList(inline=true)
	public ArrayList<Setting> settings;
	public String filename;
	private String absolutepath;
	
	public Settings(){	}
	
	public Settings(String filename)
	{
		settings = new ArrayList<Setting>();
		this.absolutepath = filename;
	}
	
	static
	{
		File dir = new File("settings");
		dir.mkdir();
		
		String filename= OSFinder.filePath("settings/settings.xml");
		try 
		{
			File file = new File(filename);
			instance = file.exists() ? XmlSerializer.Load(Settings.class, filename) : new Settings(filename);
		} 
		catch (Exception e) 
		{
			instance =  new Settings(filename);
		}
	}
	
	public static Settings getInstance() throws Exception
	{
		return instance;
	}
	
	public void save() throws Exception
	{	
		XmlSerializer.Save(this, this.absolutepath);
	}
	
	public String getSetting(String key)
	{
		for(Setting setting : this.settings) 
		{
			if(setting.getKey().equals(key.toLowerCase()))
			{
				return setting.getValue();
			}
		}
		return "default";
	}
	
	public Settings setSetting(String key, String value)
	{
		for( Setting setting : this.settings) 
		{
			if(setting.getKey().equals(key.toLowerCase()))
			{
				setting.setValue(value);
				return this;
			}
		}
		
		this.settings.add(new Setting(key.toLowerCase(), value));
		return this;
	}

	@Override
	public String getFileName() 
	{
		return this.filename;
	}

	@Override
	public void setFileName(String fileName) 
	{
		this.filename = fileName;
	}

	@Override
	public void setAbsolutePath(String filePath) {
		this.absolutepath = filePath;
		
	}

	@Override
	public void setParrentFolder(String parrentFolder) {
		// TODO Auto-generated method stub
		
	}
}

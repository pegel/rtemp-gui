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

package Controller;

import java.io.IOException;

import org.eclipse.swt.SWT;

import Controller.Listeners.CanvasMouseListener;
import Controller.Listeners.CanvasPaintListener;
import Controller.Listeners.DetailsListener;
import Controller.Listeners.DropdownMenuListener;
import Controller.Listeners.MenuListener;
import Controller.Listeners.TabListener;
import Controller.Listeners.ToolbarListener;
import Model.Model;
import Model.Aegean.Node;
import Model.IPCores.IPCore;
import Static.OSFinder;
import View.View;

public class Controller {
	private Model model;
	private View view;

	public Controller(Model model, View view)
	{
		this.model = model;
		this.view = view;

		view.addToolbarListener	(new ToolbarListener	(model,view, this));
		view.addDetailsListener	(new DetailsListener	(model,view));
		view.addMenuListener	(new MenuListener		(model,view, this));
		view.addCanvasListener	(new CanvasMouseListener		(model,view,this));
		view.addCanvasPaintListener (new CanvasPaintListener (model,view));
		view.addDropdownMenuListener(new DropdownMenuListener(model,view,this));
		view.addTabListener		(new TabListener(model,view));

		//open shell
		view.getShell().open();
		while (!view.getShell().isDisposed()) {
			if (!view.getDisplay().readAndDispatch ()) {
				view.getDisplay().sleep ();}
		}
		view.getDisplay().dispose ();
	}

	public void removeNode() {

		Node selectedNode = view.viewCanvas.getSelectedNode();
		if(selectedNode != null){
			model.removeNode(selectedNode);
			view.update();
		}
	}

	public void addNode() {
		Node selectedNode = view.viewCanvas.getSelectedNode();
		IPCore selectedIp = view.viewCpu.getSelectedIpcore();

		if (view.viewCanvas.hasSelection() && selectedIp != null) {
			if(selectedNode != null) {
				model.removeNode(selectedNode);
			}
			Node newNode = new Node();
			newNode.setIpTypeRef(selectedIp.getIpType());
			newNode.setLoc(this.view.viewCanvas.getSelectedPoint());


			if(selectedNode != null)
				newNode.setSpmSize(selectedNode.getSpmSize());

			model.addNode(newNode);
			view.update();
		} 
	}


	public void editSpm() {
		Node selectedNode = view.viewCanvas.getSelectedNode();
		
		if(selectedNode == null) 
			return;

		Object[] result = view.dialogEditSpm();
		boolean ok = (Integer)result[0] == SWT.OK ? true : false;
		if(ok) {
			String[] s = (String[])result[1];
			selectedNode.setSpmSize(s[0]);
		}
	}

	public void runCommand(String commandIn) {
		String filename= model.getAegean().getFileName().replaceAll(".xml", "");
		String cmd = "make -C " + OSFinder.filePath(model.getAegean().getParrentFolder()) + " AEGEAN_PLATFORM=" + filename +" "+commandIn;
		
		
		String[] command = {};
		

		if(OSFinder.isMac())
			command = new String[]{ "osascript", "-e", "tell application \"Terminal\" to do script " + "\"" + cmd + "\""};
		if(OSFinder.isLinux())
			command = new String[]{"x-terminal-emulator","-e","bash -l -c \""+cmd+"\";$SHELL"};
		if(OSFinder.isWindows())
			command = new String[]{"cmd /c start cmd.exe /K " + cmd};
		

		Process process = null;

		try {
			process = Runtime.getRuntime().exec(command);
			process.waitFor();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

package moulton.poly.comps;

import java.awt.Color;
import java.awt.Font;
import java.io.File;

import moulton.scalable.clickables.Button;
import moulton.scalable.containers.ListPanel;
import moulton.scalable.draggables.ScrollBar;
import moulton.scalable.popups.Popup;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;

public class PathFinderPopup extends Popup {
	private TextBox pathDisplay;
	private TextBox fileName;
	private Button okButton;
	private ListPanel contents;
	private Font smallFont;
	private ScrollBar contentBar;

	public PathFinderPopup(boolean load, String width, String height, String startFilePath) {
		super(width, height, Color.WHITE);
		
		blanketBackground = new Color(0x33dddddd, true);
		Font font = new Font("Arial", Font.PLAIN, 15);
		Button xButton = new Button("cancel","X",base,"width-20","0","20","20",font,Color.WHITE);
		xButton.setTouchedColor(Color.RED);
		addTouchResponsiveComponent(xButton);
		new Caption(load?"Load":"Save", base, "5", "18", font, Alignment.LEFT_ALIGNMENT).setYCentered(false);
		
		smallFont = new Font("Arial", Font.PLAIN, 12);
		pathDisplay = new TextBox("path", "", base, "25", "30", "?width", "20", smallFont, Color.WHITE);
		pathDisplay.setOutline(true);
		
		addTouchResponsiveComponent(new Button("pathUp","^",base, "0","30","20","20",font,Color.LIGHT_GRAY));
		fileName = new TextBox("fileName","",base,"5","height-25","width*.75-5","20",font,Color.WHITE);
		fileName.setOutline(true);
		fileName.setHint("file name");
		okButton = new Button(load?"doLoad":"doSave","Ok",base,"width*.75+5","height-25","width*.25-10","20",font,Color.LIGHT_GRAY);
		okButton.setEnabled(false);
		addTouchResponsiveComponent(okButton);
		
		contents = new ListPanel("20",base,"0","55","width-20","height-85","width-20",Color.WHITE);
		contents.setOutline(true);
		contentBar = new ScrollBar(true, base, "width-20","55","20","height-85",Color.LIGHT_GRAY);
		contentBar.setScrollRate(2);
		contents.setHeightScrollBar(contentBar);
		
		//load the given directory unless null
		if(startFilePath == null) {
			//load the user directory initially
			startFilePath = new File(System.getProperty("user.home")).toString();
		}else {
			if(startFilePath.indexOf('.') != -1) { //the name was included
				fileName.setMessage(startFilePath.substring(startFilePath.lastIndexOf('\\')+1));
				okButton.setEnabled(true);
				startFilePath = startFilePath.substring(0, startFilePath.lastIndexOf('\\'));
			}
		}
		setPath(startFilePath);
	}
	
	public void goUpDirectory() {
		String path = pathDisplay.getMessage();
		if(contentBar != null)
			contentBar.setOffset(0);
		if(path.lastIndexOf('\\') != path.indexOf('\\')) //if there are at least two \s
			setPath(path.substring(0, path.lastIndexOf('\\')));
		else {
			//if there is one, do not delete it
			int index = path.indexOf('\\');
			if(index != -1) {
				setPath(path.substring(0, path.lastIndexOf('\\')+1));
			}else
				System.err.println("Cannot go up to the parent directory!");
		}
	}
	
	public void setPath(String path) {
		pathDisplay.setMessage(path);
		contents.clearComponents();
		
		File[] subdirs = new File(path).listFiles();
		if(subdirs == null)
			return;
		Font italic = new Font(smallFont.getName(), Font.ITALIC, smallFont.getSize());
		for(int i=0; i<subdirs.length; i++) {
			File file = subdirs[i];
			String pathName = file.getPath();
			int lastSlash = pathName.lastIndexOf('\\');
			if(lastSlash != -1)
				pathName = pathName.substring(lastSlash+1);
			if(file.isDirectory())
				new Button("directoryButton", "  "+pathName, contents, 0, i, italic, Color.YELLOW).setAlignment(Alignment.LEFT_ALIGNMENT);
			else
				new Button("directoryButton", "  "+pathName, contents, 0, i, smallFont, Color.WHITE).setAlignment(Alignment.LEFT_ALIGNMENT);
		}
	}
	
	public String getPath() {
		return pathDisplay.getMessage()+"\\"+fileName.getMessage();
	}
	
	public void emptySelection(boolean empty) {
		okButton.setEnabled(!empty);
	}
	
	public void select(String name) {
		//if this leads to a directory, move to it. If a file, select it
		String fullPath = pathDisplay.getMessage();
		if(fullPath.charAt(fullPath.length()-1) != '\\') //bottom dirs already have the \\ preface
			fullPath += '\\';
		fullPath += name;
		File file = new File(fullPath);
		
		if(file.isDirectory()) {
			setPath(fullPath);
			if(contentBar != null)
				contentBar.setOffset(0);
		}else if(file.isFile()) {
			fileName.setMessage(name);
			emptySelection(false);
		}
	}

}

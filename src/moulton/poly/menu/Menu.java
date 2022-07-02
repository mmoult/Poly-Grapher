package moulton.poly.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import moulton.poly.PolyGrapher;
import moulton.poly.menu.popups.PathFinderPopup;
import moulton.poly.menu.popups.RotatePopup;
import moulton.poly.menu.popups.ScalePopup;
import moulton.poly.menu.popups.SkewPopup;
import moulton.poly.menu.popups.TranslatePopup;
import moulton.poly.shapes.PolygonView;
import moulton.poly.shapes.Shape;
import moulton.poly.shapes.ShapeListPanel;
import moulton.poly.shapes.VertexListPanel;
import moulton.poly.transformations.Rotate;
import moulton.poly.transformations.Scale;
import moulton.poly.transformations.Skew;
import moulton.poly.transformations.Transformation;
import moulton.poly.transformations.Translate;
import moulton.scalable.clickables.Button;
import moulton.scalable.clickables.Clickable;
import moulton.scalable.containers.Container;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;
import moulton.scalable.containers.PartitionPanel;
import moulton.scalable.containers.VirtualPanel;
import moulton.scalable.draggables.ScrollBar;
import moulton.scalable.geometrics.Line;
import moulton.scalable.popups.ConfirmationPopup;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;
import moulton.scalable.utils.GridFormatter;
import moulton.scalable.visuals.ImageButton;

public class Menu extends MenuManager implements ComponentListener{
	public static final int DEFAULT_WIDTH = 650;
	private final int DRAG_BUTTON_WIDTH = 5;
	private final int BANNER_HEIGHT = 30;
	private final int BUTTON_BAR_HEIGHT = 40;
	
	private int shapeCount = 0;
	private int windowWidth = DEFAULT_WIDTH;
	private String lastFilePath = null;
		
	private Font font;
	private PartitionPanel partition;
	private Caption pageSelected;
	private ShapeListPanel shapes;
	private VertexListPanel vertices;
	private Panel controlPane;
	private Panel shapeOptions;
	private CoordControl coordControl;
	private double[] perspective = null;
	private PolygonView view;
	private Line divider;
	
	private BufferedImage pinUp=null, pinDown = null;
	private BufferedImage yOriDown=null, yOriUp=null;
	private BufferedImage targetUnfocus=null, targetFocus=null;
	private BufferedImage magIn=null, magOut=null;
	private BufferedImage clickStar=null, clickMove=null;
	private BufferedImage unitAxis=null;
	private BufferedImage showAxes=null, hideAxes=null;
	
	private Color niceBlue = new Color(0x9FFF);
	Color darkishGray = new Color(127, 127, 127);

	public Menu(Container cont) {
		super(cont);
	}

	@Override
	public void createMenu() {
		this.menu = Panel.createRoot(null);
		Panel banner = new Panel(menu, "0", "0", "width", Integer.toString(BANNER_HEIGHT), Color.CYAN);
		Font boldFont = new Font("Arial", Font.BOLD, 17);
		font = new Font("Arial", Font.PLAIN, 12);
		new Caption("PolyGrapher", banner, 2, 0, boldFont, Alignment.CENTER_ALIGNMENT);
		addTouchComponent(new Button("credits", "Credits", banner, 0, 0, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button("clear", "New", banner, 1, 0, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button("save", "Save", banner, 3, 0, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button("load", "Load", banner, 4, 0, font, Color.LIGHT_GRAY));
		GridFormatter format = banner.getGridFormatter();
		format.specifyColumnWeight(2, 4.0);
		format.setMargin("5", "5");
		format.setFrame("5", "5");
		
		partition = new PartitionPanel(menu, "0", Integer.toString(BANNER_HEIGHT), "width", "?height", null);
		partition.setVerticalPartition("200");
		controlPane = new Panel(partition, 0, 0, new Color(0xE5E5E5));
		partition.setLeft(controlPane);
		Panel pageBanner = new Panel(controlPane, "0", "0", "width", "20", niceBlue);
		pageSelected = new Caption("Shapes", pageBanner, 0, 0, font, Alignment.CENTER_ALIGNMENT);
		shapes = new ShapeListPanel(this, controlPane, "12", "20", "?width", "?height", font, null);
		shapes.setHeightScrollBar(new ScrollBar(true, controlPane, "0","20","12","?height",Color.LIGHT_GRAY));
		
		Panel viewPanel = new Panel(partition, 0, 0, Color.WHITE);
		partition.setRight(viewPanel);
		String dragButtonWidth = Integer.toString(DRAG_BUTTON_WIDTH);
		addTouchComponent(new DragButton(this, viewPanel, "0", "0", dragButtonWidth, "height", Color.LIGHT_GRAY));
		String barHeight = Integer.toString(BUTTON_BAR_HEIGHT);
		view = new PolygonView(this, shapes, viewPanel, dragButtonWidth,"0","?width","?height-"+barHeight);
		
		Panel buttonBar = new Panel(viewPanel, dragButtonWidth, "height-"+barHeight, "width", "?height", darkishGray);
		coordControl = new CoordControl(this, buttonBar, "width-140", "0", "?width", "height", darkishGray);
		view.setCoordControl(coordControl);
		
		try {
			pinUp = ImageIO.read(getClass().getResource("/pin-up.png"));
			pinDown = ImageIO.read(getClass().getResource("/pin-down.png"));
			yOriDown = ImageIO.read(getClass().getResource("/y-down-graph.png"));
			yOriUp = ImageIO.read(getClass().getResource("/y-up-graph.png"));
			targetUnfocus = ImageIO.read(getClass().getResource("/center-unfocus.png"));
			targetFocus = ImageIO.read(getClass().getResource("/center-focus.png"));
			magIn = ImageIO.read(getClass().getResource("/mag-in.png"));
			magOut = ImageIO.read(getClass().getResource("/mag-out.png"));
			clickStar = ImageIO.read(getClass().getResource("/click-star.png"));
			clickMove = ImageIO.read(getClass().getResource("/click-move.png"));
			unitAxis = ImageIO.read(getClass().getResource("/square.png"));
			showAxes = ImageIO.read(getClass().getResource("/show-axes.png"));
			hideAxes = ImageIO.read(getClass().getResource("/hide-axes.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageButton pinX = new ImageButton("pinX", pinUp, buttonBar, "width-175", "0", "?width-140", "?height/2", Color.LIGHT_GRAY);
		pinX.setTouchedImage(pinDown);
		addTouchComponent(pinX);
		pinX.setClickAction(() -> {
			if(view.toggleXFixed()) {
				pinX.setTouchedImage(pinUp);
				pinX.setImage(pinDown);
				coordControl.lockX(true);
			} else {
				pinX.setTouchedImage(pinDown);
				pinX.setImage(pinUp);
				coordControl.lockX(false);
			}
			return true;
		});
		ImageButton pinY = new ImageButton("pinY", pinUp, buttonBar, "width-175", "height/2", "?width-140", "?height", Color.LIGHT_GRAY);
		pinY.setTouchedImage(pinDown);
		addTouchComponent(pinY);
		pinY.setClickAction(() -> {
			if(view.toggleYFixed()) {
				pinY.setTouchedImage(pinUp);
				pinY.setImage(pinDown);
				coordControl.lockY(true);
			} else {
				pinY.setTouchedImage(pinDown);
				pinY.setImage(pinUp);
				coordControl.lockY(false);
			}
			return true;
		});
		
		Color satBlue = new Color(0x8BC1E0);
		//width of 280 pixels since each button is 40, there are 7 of them = 280 pixels.
		final VirtualPanel buttonDisplay = new VirtualPanel(buttonBar, "15", "0", "?width-190", "height", "280", "height", darkishGray);
		ImageButton yOrient = new CaptionImageButton("yOrient", yOriDown, "Toggle the vertical direction of the y-axis.", buttonDisplay, 5, 0, satBlue, viewPanel);
		addTouchComponent(yOrient);
		yOrient.setClickAction(() -> {
			if(view.toggleInvertYAxis())
				yOrient.setImage(yOriUp);
			else
				yOrient.setImage(yOriDown);
			return true;
		});
		ImageButton centerImg = new CaptionImageButton("centerImg", targetUnfocus, "Focus the screen on the existent polygons.", buttonDisplay, 3, 0, satBlue, viewPanel);
		centerImg.setTouchedImage(targetFocus);
		addTouchComponent(centerImg);
		addTouchComponent(new CaptionImageButton("zoomOut", magOut, "Zoom out", buttonDisplay, 2, 0, satBlue, viewPanel));
		addTouchComponent(new CaptionImageButton("zoomIn", magIn, "Zoom in", buttonDisplay, 1, 0, satBlue, viewPanel));
		ImageButton clickType = new CaptionImageButton("clickType", clickStar, "Toggle between using mouse clicks to create vertices or move the perspective.", buttonDisplay, 0, 0, satBlue, viewPanel);
		clickType.setClickAction(() -> {
			if(view.toggleClickAction())
				clickType.setImage(clickStar);
			else
				clickType.setImage(clickMove);
			return true;
		});
		addTouchComponent(clickType);
		
		addTouchComponent(new CaptionImageButton("unitAxis", unitAxis, "Impose a 1:1 ratio between x and y axes.", buttonDisplay, 4, 0, satBlue, viewPanel));
		ImageButton axesToggle = new CaptionImageButton("showAxes", showAxes, "Toggle between displaying axes or not.", buttonDisplay, 6, 0, satBlue, viewPanel);
		axesToggle.setClickAction(() -> {
			if(view.toggleShowAxes())
				axesToggle.setImage(showAxes);
			else
				axesToggle.setImage(hideAxes);
			return true;
		});
		addTouchComponent(axesToggle);
		
		Font bigFont = new Font("Arial", Font.PLAIN, 20);
		Button bLeft = new Button("buttonLeft", "{", buttonBar, "1", "0", "14", "height", bigFont, niceBlue);
		bLeft.setClickAction(() -> {
			buttonDisplay.setXOffs(buttonDisplay.getXOffs() - 40);
			return true;
		});
		addTouchComponent(bLeft);
		Button bRight = new Button("buttonRight", "}", buttonBar, "width-190", "0", "?width-176", "height", bigFont, niceBlue);
		bRight.setClickAction(() -> {
			buttonDisplay.setXOffs(buttonDisplay.getXOffs() + 40);				
			return true;
		});
		addTouchComponent(bRight);
	}

	@Override
	public void clickableAction(Clickable c) {
		String id = c.getId();
		if(id.length() > 11 && id.substring(0, 11).equals("shapeTitle:")) {
			int num = Integer.parseInt(id.substring(11));
			createEditPage(shapes.getShape(num));
			return;
		}if(id.length() > 12 && id.substring(0, 12).equals("shapeDelete:")) {
			int num = Integer.parseInt(id.substring(12));
			shapes.removeShape(shapes.getShape(num));
			shapes.updateList();
			view.recenter();
			return;
		}if(id.length() > 8 && id.substring(0, 8).equals("shapeUp:")) {
			int num = Integer.parseInt(id.substring(8));
			shapes.shiftShape(num, num-1);
			shapes.updateList();
			return;
		}if(id.length() > 10 && id.substring(0, 10).equals("shapeDown:")) {
			int num = Integer.parseInt(id.substring(10));
			shapes.shiftShape(num, num+1);
			shapes.updateList();
			return;
		}if(id.length() > 13 && id.substring(0, 13).equals("deleteVertex:")) {
			int num = Integer.parseInt(id.substring(13));
			vertices.removeVertex(num);
			view.recenter();
			view.deselect();
			return;
		}
		if(id.length() > 11 && id.substring(0, 11).equals("vertexDown:")) {
			int num = Integer.parseInt(id.substring(11));
			vertices.shiftVertex(num, num+1);
			return;
		}if(id.length() > 9 && id.substring(0, 9).equals("vertexUp:")) {
			int num = Integer.parseInt(id.substring(9));
			vertices.shiftVertex(num, num-1);
			return;
		}switch(id) {
		case "save": //save all the shapes in an export friendly way
			createPopup(false);
			break;
		case "load": //load the shapes from file
			createPopup(true);
			break;
		case "clear": //clear the shapes from the content list
			clear();
			break;
		case "credits":
			setPopup(new CreditsPopup("350", "200", Color.WHITE));
			break;
		case "newShape":
			createEditPage(null);
			view.recenter();
			break;
		case "saveShape": //it should already be saved, we just return
			returnToShapeList();
			break;
		case "cancelShape":
			if(!vertices.cancel()) //if vertices could not cancel, the shape needs to be deleted
				shapes.removeShape(vertices.getShape());
			returnToShapeList();
			//revert back to previous perspective
			if(perspective != null) {
				view.setLowX(perspective[0]);
				view.setLowY(perspective[1]);
				view.setHighX(perspective[2]);
				view.setHighY(perspective[3]);
			}
			break;
		case "newVertex":
			vertices.addVertex(new double[] {0,0});
			view.recenter();
			break;
		
		case "centerImg":
			view.recenter();
			break;
		case "unitAxis":
			double[] viewCoords = view.getPerspective();
			double width = viewCoords[2]-viewCoords[0];
			double height = viewCoords[3]-viewCoords[1];
			double diff = height - width;
			//we will expand the smaller of the two
			if(diff > 0) { //expand width
				view.setLowX(viewCoords[0] - diff/2);
				view.setHighX(viewCoords[2] + diff/2);
			}else { //expand height
				view.setLowY(viewCoords[1] + diff/2);
				view.setHighY(viewCoords[3] - diff/2);
			}
			break;
		case "zoomOut":
			viewCoords = view.getPerspective();
			//so we want to show 1.5 the width and the height as before, but centered same
			//that means that each side needs to grow .25
			width = viewCoords[2]-viewCoords[0];
			height = viewCoords[3]-viewCoords[1];
			view.setLowX(viewCoords[0] - width/4);
			view.setHighX(viewCoords[2] + width/4);
			view.setLowY(viewCoords[1] - height/4);
			view.setHighY(viewCoords[3] + height/4);
			break;
		case "zoomIn":
			viewCoords = view.getPerspective();
			//we are going to show 2/3 the width and height as before, but centered same
			//that means each side needs to shrink 1/6
			width = viewCoords[2]-viewCoords[0];
			height = viewCoords[3]-viewCoords[1];
			view.setLowX(viewCoords[0] + width/6);
			view.setHighX(viewCoords[2] - width/6);
			view.setLowY(viewCoords[1] + height/6);
			view.setHighY(viewCoords[3] - height/6);
			break;
		
		//Path Finder Pop up actions
		case "doSave":
			String toPath = ((PathFinderPopup)popup).getPath();
			new FileRepresentation().save(toPath);
			setPopup(null);
			//if all this worked, save this as the new file directory
			lastFilePath = toPath;
			break;
		case "doLoad":
			String fromPath = ((PathFinderPopup)popup).getPath();
			new FileRepresentation().load(fromPath);
			//if all this worked, save this as the new file directory
			lastFilePath = fromPath;
			//fall through to cancel/quit
		case "cancel":
			setPopup(null);
			break;
		case "pathUp":
			((PathFinderPopup)popup).goUpDirectory();
			break;
		case "directoryButton":
			((PathFinderPopup)popup).select(((Button)c).getText().substring(2));
			break;
		case "fileName":
			((PathFinderPopup)popup).emptySelection(false);
			break;
			
		//transformation pop up
		case "translate":
			popup = new TranslatePopup(this);
			break;
		case "rotate":
			popup = new RotatePopup(this);
			break;
		case "scale":
			popup = new ScalePopup(this);
			break;
		case "skew":
			popup = new SkewPopup(this);
			break;
		case "doTranslate":
			//all the data for the transform is in the panel. We just have to get to it
			Panel base = c.getParent();
			TextBox deltaX = (TextBox)findComponent("deltaX", base);
			TextBox deltaY = (TextBox)findComponent("deltaY", base);
			transformShape(new Translate(Double.parseDouble(deltaX.getMessage()),
					Double.parseDouble(deltaY.getMessage())));
			break;
		case "doRotate":
			base = c.getParent();
			TextBox rotateAngle = (TextBox)findComponent("angle", base);
			TextBox centerX = (TextBox)findComponent("centerX", base);
			TextBox centerY = (TextBox)findComponent("centerY", base);
			transformShape(new Rotate(
					Double.parseDouble(rotateAngle.getMessage()),
					Double.parseDouble(centerX.getMessage()),
					Double.parseDouble(centerY.getMessage())));
			break;
		case "doScale":
			base = c.getParent();
			TextBox scaleX = (TextBox)findComponent("scaleX", base);
			TextBox scaleY = (TextBox)findComponent("scaleY", base);
			transformShape(new Scale(
					Double.parseDouble(scaleX.getMessage()),
					Double.parseDouble(scaleY.getMessage())));
			break;
		case "doSkew":
			base = c.getParent();
			Button horizSkew = (Button)findComponent("horizSkew", base);
			TextBox skewAngle = (TextBox)findComponent("skewAngle", base);
			transformShape(new Skew(
					Double.parseDouble(skewAngle.getMessage()),
					horizSkew.isClicked()));
			break;
			
		case "fullExit":
			((PolyGrapher)cont).running = false;
			break;
		}
	}
	
	private void createPopup(boolean shouldLoad) {
		PathFinderPopup pop = new PathFinderPopup(shouldLoad, "350", "200", lastFilePath);
		setPopup(pop);
	}
	
	private void transformShape(Transformation t) {
		//we want to modify the vertices according to the transformation given
		Shape changed = vertices.getShape();
		List<double[]> verts = vertices.getShape().getVertices();
		List<Point2D.Double> pts = new ArrayList<>(verts.size());
		for(double[] vert: verts)
			pts.add(new Point2D.Double(vert[0], vert[1]));
		//perform the transformation
		t.execute(pts);
		//update the shape
		for(int i=0; i<pts.size(); i++) {
			Point2D.Double pt = pts.get(i);
			vertices.getVertex(i)[0] = NumberControl.round(pt.x, -PolygonView.PRECISION_DIGITS);
			vertices.getVertex(i)[1] = NumberControl.round(pt.y, -PolygonView.PRECISION_DIGITS);
		}
		//reload the shape data
		returnToShapeList();
		createEditPage(changed);
		view.recenter();
		
		//now we want to center the view and remove the pop up
		popup = null;
	}
	
	private void clear() {
		returnToShapeList();
		shapes.clear();
		shapeCount = 0;
		shapes.updateList();
		//also refresh the perspective back to (-1, 1)
		view.setLowX(-1);
		view.setHighX(1);
		view.setLowY(-1);
		view.setHighY(1);
	}

	@Override
	public void lostFocusAction(Clickable c) {
		if(c instanceof TextBox) {
			TextBox box = (TextBox)c;
			String id = c.getId();
			if(id.length() > 8 && id.substring(0, 8).equals("vertexX:")) {
				int num = Integer.parseInt(id.substring(8));
				vertices.getVertex(num)[0] = Double.parseDouble(box.getMessage());
				vertices.updatePerimeter();
				view.select(vertices.getVertex(num));
				view.recenter();
				return;
			}if(id.length() > 8 && id.substring(0, 8).equals("vertexY:")) {
				int num = Integer.parseInt(id.substring(8));
				vertices.getVertex(num)[1] = Double.parseDouble(box.getMessage());
				vertices.updatePerimeter();
				view.select(vertices.getVertex(num));
				view.recenter();
				return;
			}
			switch(id) {
			case "titleBox":
				vertices.getShape().setTitle(box.getMessage());
				break;
			case "colorBox":
				String fullColor = box.getMessage();
				if(fullColor.length() > 6)
					fullColor = fullColor.substring(2);
				vertices.getShape().setColor(new Color(Integer.parseInt(fullColor, 16)));
				break;
			case "lowX":
				view.setLowX(Double.parseDouble(box.getMessage()));
				break;
			case "lowY":
				view.setLowY(Double.parseDouble(box.getMessage()));
				break;
			case "hiX":
				view.setHighX(Double.parseDouble(box.getMessage()));
				break;
			case "hiY":
				view.setHighY(Double.parseDouble(box.getMessage()));
				break;
			case "fileName":
				((PathFinderPopup)popup).emptySelection(((TextBox)c).getMessage().isEmpty());
				break;
			case "path":
				((PathFinderPopup)popup).setPath(((TextBox)c).getMessage());
				break;
			}
		}
	}
	
	private void createEditPage(Shape selected) { //should take a parameter of a shape to edit
		pageSelected.setText("Edit");
		controlPane.removeFreeComponent(shapes);
		shapes.clearTouchList();
		
		//save the current perspective in case of a cancel
		perspective = view.getPerspective();
		
		//create the edit page from the shape. If shape is null, create a new one
		boolean defaultShape = false;
		if(selected == null) { //create the default shape
			selected = new Shape("Shape"+shapeCount++, Color.BLACK, new double[][] {{0,0}});
			shapes.addShape(selected); //add to the list
			defaultShape = true;
		}
		shapes.getHeightScrollBar().setEnabled(false);
		
		vertices = new VertexListPanel(this, selected, controlPane, "12", "20", "?width", "?height-70", font, null, defaultShape);
		vertices.setHeightScrollBar(new ScrollBar(true, controlPane, "0","20","12","?height-70",Color.LIGHT_GRAY));
		
		shapeOptions = new Panel(controlPane, "0", "height-70", "?width", "?height", darkishGray);
		shapeOptions.getGridFormatter().setMargin("10", "10");
		shapeOptions.getGridFormatter().specifyRowWeight(0, 2.0);
		
		Panel transformations = new Panel(shapeOptions, 0, 0, null);
		divider = new Line(controlPane, "0", "height-70", "width", "?", Color.BLACK);
		addTouchComponent(new Button("translate", "Translate", transformations, 0,0, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button("rotate", "Rotate", transformations, 1,0, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button("scale", "Scale", transformations, 0,1, font, Color.LIGHT_GRAY));
		addTouchComponent(new Button("skew", "Skew", transformations, 1,1, font, Color.LIGHT_GRAY));
		
		Panel fileOptions = new Panel(shapeOptions, 0, 1, null);
		fileOptions.getGridFormatter().setMargin("10", null);
		Button button = new Button("saveShape","Save",fileOptions,0,0,font,Color.LIGHT_GRAY);
		button.setTextColor(Color.BLACK);
		addTouchComponent(button);
		button = new Button("cancelShape", "Cancel",fileOptions,1,0,font,Color.LIGHT_GRAY);
		button.setTextColor(Color.BLACK);
		addTouchComponent(button);
	}
	
	private void returnToShapeList() {
		//remove shapes panel if already in
		controlPane.removeFreeComponent(shapes);
		pageSelected.setText("Shapes");
		view.deselect();
		
		if (vertices != null) {			
			vertices.removeTouchResponsiveness(this);
			controlPane.removeFreeComponent(vertices.getHeightScrollBar());
			
			shapeOptions.removeTouchResponsiveness(this);
			controlPane.removeFreeComponent(vertices);
			controlPane.removeFreeComponent(shapeOptions);
			divider.setVisible(false);
		}
		
		controlPane.addFreeComponent(shapes);
		shapes.updateList();
	}

	@Override
	public void componentResized(ComponentEvent e) {
		//try to keep the same aspect ratio of the partition to the window width
		int newWidth = e.getComponent().getWidth();
		int partitionX = Integer.parseInt(partition.getVerticalPartition());
		double ratio = 0;
		if(windowWidth != 0)
			ratio = ((double)partitionX)/windowWidth;
		windowWidth = newWidth;
		//we want the partition to scale, but also have a real value instead of an expression so it can be altered
		partitionX = (int)(windowWidth*ratio);
		partition.setVerticalPartition(partitionX+"");
	}
	
	public double movePartition(double shiftValue) {
		int partitionX = Integer.parseInt(partition.getVerticalPartition());
		int before = partitionX;
		partitionX += (int)shiftValue;
		if(partitionX < 0)
			partitionX = 0;
		if(partitionX > windowWidth - DRAG_BUTTON_WIDTH)
			partitionX = windowWidth - DRAG_BUTTON_WIDTH;
		partition.setVerticalPartition(partitionX+"");
		return partitionX - before;
	}
	
	@Override
	public void mouseMoved(int x, int y) {
		super.mouseMoved(x, y);
		if(view != null && partition != null && popup==null) {
			int offsX = Integer.parseInt(partition.getVerticalPartition()) + DRAG_BUTTON_WIDTH;
			if(y < cont.getMenuHeight()-BUTTON_BAR_HEIGHT)
				view.informMouseXY(x-offsX, y-BANNER_HEIGHT);
			else
				view.informMouseXY(0, -1); //give it fake data to avoid display
		}
	}
	
	public void createVertexAt(double x, double y) {
		//check if the edit page is up. If not, create a new shape, thus pulling it up
		if(pageSelected.getText().equals("Shapes")) {
			createEditPage(null);
			vertices.removeVertex(0); //remove the default (0,0) point
		}
		//now we need to add the new vertex
		vertices.addVertex(new double[] {x, y});
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

	public void createExitPopup() {
		setPopup(new ConfirmationPopup("Are you sure you want to quit?", null, font, "fullExit", "cancel", this, true));
	}
	
	public PolygonView getPolyView() {
		return view;
	}
	
	//There was no good way to separate the file representation from the menu, which has all the data
	public class FileRepresentation {
		
		public void save(String path) {
			FileWriter fw = null;
			try {
				fw = new FileWriter(path);
				if(shapes != null) {
					for(Shape shape:shapes.getShapes()) {
						fw.write("\""+shape.getTitle()+"\":\n");
						String fullColor = Integer.toHexString(shape.getColor().getRGB());
						if(fullColor.length() > 6)
							fullColor = fullColor.substring(2);
						fw.write(" color: 0x"+fullColor);
						fw.write("\n vertices: {");
						boolean start = true;
						for(double[] vertex: shape.getVertices()) {
							if(!start) {
								fw.write(", ");
							}else
								start = false;
							fw.write("{"+vertex[0]+", "+vertex[1]+"}");
						}
						fw.write("}\n");
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(fw != null) {
					try {
						fw.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public void load(String path) {
			clear();
			Scanner scan = null;
			try {
				File file = new File(path);
				if(!file.exists()) {
					System.err.println("File specified does not exist!");
					return;
				}
				scan = new Scanner(new File(path));
				while(scan.hasNextLine()) {
					String line = scan.nextLine();
					if(line.isEmpty())
						continue;
					String title = null;
					Color color = null;
					LinkedList<double[]> vertices = new LinkedList<>();
					
					if(line.indexOf("\"") != -1) { //each shape should start with a title
						title = line.substring(line.indexOf('"')+1, line.lastIndexOf('"'));
						line = scan.nextLine();
						if(line.indexOf("color:") != -1) {
							color = new Color(Integer.parseInt(line.substring(line.indexOf("0x")+2), 16));
							line = scan.nextLine();
						}
					}
					char[] opens = {'{', '[', '('};
					char[] closes = {'}', ']', ')'};
					for(int i=0; i<opens.length; i++) {
						char open = opens[i];
						if(line.indexOf(open) != -1) { //load the vertices
							line = line.substring(line.indexOf(open) + 1); //get rid of all the beginning
							while(line.indexOf(open) != -1) {
								line = line.substring(line.indexOf(open) + 1);
								double first = Double.parseDouble(line.substring(0, line.indexOf(',')));
								line = line.substring(line.indexOf(',') + 1);
								double second = Double.parseDouble(line.substring(0, line.indexOf(closes[i])));
								vertices.add(new double[] {first, second});
							}
							break; //found successfully, so don't try the others
						}
					}
					
					//now try to create the shape
					if(title == null)
						title = "Shape" + shapeCount++;
					if(color == null)
						color = Color.BLACK;
					double[][] verts = new double[vertices.size()][2];
					vertices.toArray(verts);
					shapes.addShape(new Shape(title, color, verts));
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}catch(NoSuchElementException err) { //unexpected file termination
				err.printStackTrace();
			}finally {
				if(scan != null)
					scan.close();
			}
			shapes.updateList();
			if(view != null)
				view.recenter();
		}

	}
}

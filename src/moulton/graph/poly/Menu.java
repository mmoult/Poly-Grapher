package moulton.graph.poly;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;

import moulton.scalable.clickables.Button;
import moulton.scalable.clickables.Clickable;
import moulton.scalable.containers.Container;
import moulton.scalable.containers.MenuManager;
import moulton.scalable.containers.Panel;
import moulton.scalable.containers.PartitionPanel;
import moulton.scalable.draggables.ScrollBar;
import moulton.scalable.texts.Alignment;
import moulton.scalable.texts.Caption;
import moulton.scalable.texts.TextBox;
import moulton.scalable.utils.GridFormatter;

public class Menu extends MenuManager implements ComponentListener{
	public static final int DEFAULT_WIDTH = 600;
	private final int DRAG_BUTTON_WIDTH = 5;
	
	private int shapeCount = 0;
	private int windowWidth = DEFAULT_WIDTH;
	
	private Font font;
	
	private PartitionPanel partition;
	private Caption pageSelected;
	private ShapeListPanel shapes;
	private VertexListPanel vertices;
	private Panel controlPane;
	private Panel shapeOptions;
	private CoordControl coordControl;
	private PolygonView view;
	

	public Menu(Container cont) {
		super(cont);
	}

	@Override
	public void createMenu() {
		this.menu = Panel.createRoot(null);
		Panel banner = new Panel(menu, "0", "0", "width", "30", Color.CYAN);
		Font boldFont = new Font("Arial", Font.BOLD, 17);
		font = new Font("Arial", Font.PLAIN, 12);
		new Caption("PolyGrapher", banner, 2, 0, boldFont, Alignment.CENTER_ALIGNMENT);
		new Button("quit", "Quit", banner, 0, 0, font, Color.LIGHT_GRAY);
		new Button("clear", "Clear", banner, 1, 0, font, Color.LIGHT_GRAY);
		new Button("save", "Save", banner, 3, 0, font, Color.LIGHT_GRAY);
		new Button("load", "Load", banner, 4, 0, font, Color.LIGHT_GRAY);
		GridFormatter format = banner.getGridFormatter();
		format.specifyColumnWeight(2, 4.0);
		format.setMargin("5", "5");
		format.setFrame("5", "5");
		
		partition = new PartitionPanel(menu, "0", "30", "width", "?height", null);
		partition.setVerticalPartition("200");
		controlPane = new Panel(partition, 0, 0, new Color(0xE5E5E5));
		partition.setLeft(controlPane);
		Panel pageBanner = new Panel(controlPane, "0", "0", "width", "20", new Color(0x9FFF));
		pageSelected = new Caption("Shapes", pageBanner, 0, 0, font, Alignment.CENTER_ALIGNMENT);
		shapes = new ShapeListPanel(controlPane, "12", "20", "?width", "?height", font, null);
		shapes.setHeightScrollBar(new ScrollBar(true, controlPane, "0","20","12","?height",Color.LIGHT_GRAY));
		
		Panel viewPanel = new Panel(partition, 0, 0, Color.WHITE);
		partition.setRight(viewPanel);
		new DragButton(this, viewPanel, "0", "0", DRAG_BUTTON_WIDTH+"", "height", Color.LIGHT_GRAY);
		view = new PolygonView(shapes, viewPanel, ""+DRAG_BUTTON_WIDTH,"0","?width","height");
		
		coordControl = new CoordControl(viewPanel, "width-width/3","height-height/10","?width","?height", 
				new Color(100,100,100,150));
		view.setCoordControl(coordControl);
	}

	@Override
	protected void clickableAction(Clickable c) {
		String id = c.getId();
		if(id.length() > 11 && id.substring(0, 11).equals("shapeTitle:")) {
			int num = Integer.parseInt(id.substring(11));
			createEditPage(shapes.getShape(num));
			return;
		}if(id.length() > 12 && id.substring(0, 12).equals("shapeDelete:")) {
			int num = Integer.parseInt(id.substring(12));
			shapes.removeShape(shapes.getShape(num));
			shapes.updateList();
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
		case "quit":
			((PolyGrapher)cont).running = false;
			break;
		case "save": //save all the shapes in an export friendly way
			FileWriter fw = null;
			try {
				fw = new FileWriter("polygons.txt");
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
			break;
		case "load": //load the shapes from file
			clear();
			Scanner scan = null;
			try {
				scan = new Scanner(new File("polygons.txt"));
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
			break;
		case "clear": //clear the shapes from the content list
			clear();
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
			view.recenter();
			break;
		case "newVertex":
			vertices.addVertex(new double[] {0,0});
			view.recenter();
			break;
		}
	}
	
	private void clear() {
		returnToShapeList();
		shapes.clear();
		shapeCount = 0;
		shapes.updateList();
	}

	@Override
	protected void lostFocusAction(Clickable c) {
		if(c instanceof TextBox) {
			TextBox box = (TextBox)c;
			String id = c.getId();
			if(id.length() > 8 && id.substring(0, 8).equals("vertexX:")) {
				int num = Integer.parseInt(id.substring(8));
				vertices.getVertex(num)[0] = Double.parseDouble(box.getMessage());
				view.recenter();
				return;
			}if(id.length() > 8 && id.substring(0, 8).equals("vertexY:")) {
				int num = Integer.parseInt(id.substring(8));
				vertices.getVertex(num)[1] = Double.parseDouble(box.getMessage());
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
			}
		}
	}
	
	private void createEditPage(Shape selected) { //should take a parameter of a shape to edit
		pageSelected.setText("Edit");
		controlPane.removeFreeComponent(shapes);
		//create the edit page from the shape. If shape is null, create a new one
		boolean defaultShape = false;
		if(selected == null) { //create the default shape
			selected = new Shape("Shape"+shapeCount++, Color.BLACK, new double[][] {{0,0}});
			shapes.addShape(selected); //add to the list
			defaultShape = true;
		}
		
		vertices = new VertexListPanel(selected, controlPane, "12", "20", "?width", "height-40", font, null, defaultShape);
		vertices.setHeightScrollBar(shapes.getHeightScrollBar());
		
		shapeOptions = new Panel(controlPane, "12", "height-20", "?width", "?height", null);
		new Button("saveShape","Save",shapeOptions,0,0,font,Color.LIGHT_GRAY);
		new Button("cancelShape", "Cancel",shapeOptions,1,0,font,Color.LIGHT_GRAY);
		shapeOptions.getGridFormatter().setMargin("width/10", null);
	}
	
	private void returnToShapeList() {
		//remove shapes panel if already in
		controlPane.removeFreeComponent(shapes);
		
		pageSelected.setText("Shapes");
		controlPane.removeFreeComponent(vertices);
		controlPane.removeFreeComponent(shapeOptions);
		shapes.setHeightScrollBar(shapes.getHeightScrollBar());
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
		if(view != null && partition != null) {
			int offsX = Integer.parseInt(partition.getVerticalPartition()) + DRAG_BUTTON_WIDTH;
			view.informMouseXY(x-offsX, y-30); //30 is height of the banner
		}
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}

}

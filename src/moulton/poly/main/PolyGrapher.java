package moulton.poly.main;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import moulton.scalable.containers.Container;

public class PolyGrapher extends JPanel implements Container, MouseListener, KeyListener, MouseMotionListener, MouseWheelListener{
	//Re-positioning, saving, loading, deleting vertices
	//figure out tabs for vertices maybe? Or I could just make an enter chain
	private Menu manager = null;
	private static final long serialVersionUID = 1L;
	public boolean running = true;
	private JFrame frame;
	
	public static void main(String args[]){
		new PolyGrapher();
	}
	
	public PolyGrapher(){
		frame = new JFrame("PolyGrapher");
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.add(this);
		manager = new Menu(this);
		manager.createMenu();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		addMouseListener(this);
		addKeyListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(manager);
		
		ArrayList<Image> icons = new ArrayList<>(4);
		Toolkit tk = Toolkit.getDefaultToolkit();
		icons.add(tk.getImage(getClass().getClassLoader().getResource("icons/polygon_icon_24.png")));
		icons.add(tk.getImage(getClass().getClassLoader().getResource("icons/polygon_icon_32.png")));
		icons.add(tk.getImage(getClass().getClassLoader().getResource("icons/polygon_icon_48.png")));
		icons.add(tk.getImage(getClass().getClassLoader().getResource("icons/polygon_icon_256.png")));
		frame.setIconImages(icons);
		
		frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                manager.createExitPopup();
            }
        });
		
		//start run loop so the screen can refresh
		run();
	}
	
	private void run() {
		//We don't have any animations except for text boxes, which refresh ~ 1/sec
		//Therefore we don't have to force refreshes often
		final int WAIT_MS = 800;
		long currentTime;
		long lastTime = System.currentTimeMillis();
		while(running) {
			currentTime = System.currentTimeMillis();
			long diff = currentTime-lastTime;
			if(diff > WAIT_MS) {
				this.repaint();
				lastTime = System.currentTimeMillis();
			}else {
				try {
					Thread.sleep(WAIT_MS - diff);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		//any wrap up of resources
		System.exit(0);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(manager != null)
			manager.render(g);
		requestFocus();
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Menu.DEFAULT_WIDTH, 420);
	}

	@Override
	public int getMenuWidth() {
		return getWidth();
	}

	@Override
	public int getMenuHeight() {
		return getHeight();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(manager != null){
			manager.mouseMoved(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(manager != null){
			manager.mouseMoved(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(manager != null){
			manager.keyTyped(e.getKeyChar());
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(manager != null){
			manager.keyPressed(e.getExtendedKeyCode());
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {
		if(manager != null){
			manager.mousePressed(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(manager != null){
			manager.mouseReleased(e.getX(), e.getY());
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(manager != null) {
			manager.mouseScrolled(e.getX(), e.getY(), e.getWheelRotation());
			repaint();
		}
	}

	@Override
	public void setCursor(int cursorType) {
		Cursor curs = frame.getCursor();
		int type = curs.getType();
		if(cursorType != type)
			frame.setCursor(Cursor.getPredefinedCursor(cursorType));
	}

}
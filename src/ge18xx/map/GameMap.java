package ge18xx.map;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;

public class GameMap extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	
	protected MapCell map[][];

	public GameMap () {
		// TODO Auto-generated constructor stub
	}

	public GameMap (String text) {
		super (text);
		// TODO Auto-generated constructor stub
	}

	public GameMap (Icon image) {
		super (image);
		// TODO Auto-generated constructor stub
	}

	public GameMap (String text, int horizontalAlignment) {
		super (text, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	public GameMap (Icon image, int horizontalAlignment) {
		super (image, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	public GameMap (String text, Icon icon, int horizontalAlignment) {
		super (text, icon, horizontalAlignment);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void stateChanged (ChangeEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited (MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTypeName () {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {
		// TODO Auto-generated method stub

	}

}

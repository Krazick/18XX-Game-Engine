package ge18xx.map;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ge18xx.company.Corporation;
import ge18xx.company.TokenCompany;
import geUtilities.xml.LoadableXMLI;
import geUtilities.xml.XMLDocument;

public class GameMap extends JLabel implements LoadableXMLI, MouseListener, MouseMotionListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	
	protected MapCell map[][];
	public Hex18XX hex;

	public GameMap () {
	}

	public GameMap (String text) {
		super (text);
	}

	public GameMap (Icon image) {
		super (image);
	}

	public GameMap (String text, int horizontalAlignment) {
		super (text, horizontalAlignment);
	}

	public GameMap (Icon image, int horizontalAlignment) {
		super (image, horizontalAlignment);
	}

	public GameMap (String text, Icon icon, int horizontalAlignment) {
		super (text, icon, horizontalAlignment);
	}

	@Override
	public void stateChanged (ChangeEvent e) {
	}

	@Override
	public void mouseDragged (MouseEvent e) {
	}

	@Override
	public void mouseMoved (MouseEvent e) {
	}

	@Override
	public void mouseClicked (MouseEvent e) {
	}

	@Override
	public void mousePressed (MouseEvent e) {
	}

	@Override
	public void mouseReleased (MouseEvent e) {
	}

	@Override
	public void mouseEntered (MouseEvent e) {
	}

	@Override
	public void mouseExited (MouseEvent e) {
	}

	@Override
	public String getTypeName () {
		return null;
	}

	@Override
	public void loadXML (XMLDocument aXMLDocument) throws IOException {

	}

	public void buildMapArray (int aCols, int aRows) {
		map = new MapCell [aRows] [aCols];
	}

	public void setMapCell (int aRow, int aCol, String aDirection, HexMap hexMap) {
		MapCell tMapCell;
		
		tMapCell = new MapCell (this, aDirection);
		tMapCell.setOffsetCoordinates (aCol, aRow);
		setMapCell (aRow, aCol, tMapCell);
	}
	
	public void setMapCell (int aRow, int aCol, String aDirection) {
		MapCell tMapCell;
		
		tMapCell = new MapCell (this, aDirection);
		tMapCell.setOffsetCoordinates (aCol, aRow);
		setMapCell (aRow, aCol, tMapCell);
	}

	public void setMapCell (int aRow, int aCol, MapCell aMapCell) {
		map [aRow] [aCol] = aMapCell;
	}

	// Methods to be Overridden by HexMap
	
	public void toggleSelectedMapCell (MapCell aSelectedMapCell) {

	}
	
	public TokenCompany getTokenCompany (String aAbbrev) {
		return TokenCompany.NO_TOKEN_COMPANY;
	}

	public int getCurrentPhase () {
		return 0;
	}
	
	public void redrawMap () {
		
	}
	
	public Corporation getOperatingCompany () {
		return Corporation.NO_CORPORATION;
	}
	
	public boolean isTileAvailableForMapCell (MapCell aMapCell) {
		return false;
	}
	
	public Corporation getCorporationByID (int aCorporationID) {
		return Corporation.NO_CORPORATION;
	}

	public Corporation getCorporation (String aCorporationAbbrev) {
		return Corporation.NO_CORPORATION;
	}

	public boolean mapCellIsInSelectableSMC (MapCell mapCell) {
		return false;
	}
}

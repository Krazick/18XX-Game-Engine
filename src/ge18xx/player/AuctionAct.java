package ge18xx.player;

public class AuctionAct {
	public final static String NO_ACT = "NO ACT";
	public final static  String RAISE_ACT = "RAISE ACT";
	public final static  String PASS_ACT = "PASS ACT";
	String act;
	
	public AuctionAct () {
		setNoAct ();
	}

	public boolean isNoAct () {
		return (NO_ACT.equals (act));
	}
	
	public boolean isRaiseAct () {
		return (RAISE_ACT.equals (act));
	}
	
	public boolean isPassAct () {
		return (PASS_ACT.equals (act));
	}
	
	private void setAct (String aAct) {
		act = aAct;
	}
	
	public void setNoAct () {
		setAct (NO_ACT);
	}
	
	public void setRaise () {
		setAct (RAISE_ACT);
	}
	
	public void setPass () {
		setAct (PASS_ACT);
	}
}

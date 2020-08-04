package ge18xx.train;

import ge18xx.player.CashHolderI;

public interface TrainHolderI {
	public void addTrain (Train aTrain);
	
	public CashHolderI getCashHolder ();
	
	/* Get the name of the Entity Holding the Train - Company vs Bank, vs BankPool */
	public String getName ();
	
	/* Get the Selected Train from the Portfolio */
	public Train getSelectedTrain ();
	
	/* Get the Train Name, and Quantity in form "NAME (QTY), NAME (QTY), ..." with
	 * order of smallest (cheapest) train first */
	public String getTrainNameAndQty (String aStatus);
	
	/* Get the a Train Object with this Name */
	public Train getTrain (String aName);
	
	public TrainPortfolio getTrainPortfolio ();
	
	/* Get the count of the number of Trains with this Name */
	public int getTrainQuantity (String aName);
	
	/* Test if a Train Object with the name is held by this Entity */
	public boolean hasTrainNamed (String aName);
	
	/* Remove a Train Object with the name from the Entity, return True if success,
	 * False if this Entity does not have a train of this name	 */
	public boolean removeTrain (String aName);
	
	/* Remove a Train Object with the name from the Entity, return True if success,
	 * False if this Entity does not have a train of this name	 */
	public boolean removeSelectedTrain ();

	public int getLocalSelectedTrainCount ();
}

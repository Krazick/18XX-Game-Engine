package ge18xx.train;

import ge18xx.company.Coupon;
import ge18xx.player.CashHolderI;
import ge18xx.round.action.ActorI;

public interface TrainHolderI extends ActorI {
	public static final TrainHolderI NO_TRAIN_HOLDER = null;

	public void addTrain (Train aTrain);

	public default CashHolderI getCashHolder () {
		return CashHolderI.NO_CASH_HOLDER;
	}

	/*
	 * Get the name of the Entity Holding the Train - Company vs Bank, vs BankPool
	 */
	@Override
	public String getName ();

	/* Get the Selected Train from the Portfolio */
	public Train getSelectedTrain ();

	/*
	 * Get the Train Name, and Quantity in form "NAME (QTY), NAME (QTY), ..." with
	 * order of smallest (cheapest) train first
	 */
	public String getTrainNameAndQty (String aStatus);

	/* Get the a Train Object with this Name */
	public Coupon getTrain (String aName);

	public TrainPortfolio getTrainPortfolio ();

	/* Get the count of the number of Trains with this Name */
	public int getTrainCount (String aName);

	/* Test if a Train Object with the name is held by this Entity */
	public boolean hasTrainNamed (String aName);

	/*
	 * Remove a Train Object with the name from the Entity, return True if success,
	 * False if this Entity does not have a train of this name
	 */
	public boolean removeTrain (String aName);

	/*
	 * Remove a Train Object with the name from the Entity, return True if success,
	 * False if this Entity does not have a train of this name
	 */
	public boolean removeSelectedTrain ();

	public int getTrainLimit ();

	public int getLocalSelectedTrainCount ();

	@Override
	public default boolean isATrainCompany () {
		return false;
	}
}

package vtc.tools.varstats;

public class VarStatsException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7052008735448836061L;

	public VarStatsException() {}

    //Constructor that accepts a message
    public VarStatsException(String message)
    {
       super(message);
    }
}

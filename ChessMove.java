package chai;

public class ChessMove {
	
	public short actualMove;
	public int value;
	
	public ChessMove(short actualMove, int value){
		this.actualMove = actualMove;
		this.value = value;
	}
	
	// set the value of the chess
	private void setValue(int value){
		this.value = value;
	}
	
	private double getValue(){
		return value;
	}
	
	// set the move of the chess
	private void setMove(short move){
		this.actualMove = move;
	}
	
}
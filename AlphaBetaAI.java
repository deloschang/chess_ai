package chai;

import java.util.HashMap;

import chesspresso.move.IllegalMoveException;
import chesspresso.position.Position;

// AI that implements basic minimax algorithm + alpha beta algorithm
public class AlphaBetaAI implements ChessAI {
	
	int DEPTH = 5; // basic depth that AI can search at
	int AI_player; // stores int associated with the player AI
	int DOMINATION_CONSTANT = 2; // domination weight 
	int REPEAT_CONSTANT = 1; // value to subtract if same move has been seen.
	
	HashMap<Integer, Double> transTable;
	public AlphaBetaAI(){
		transTable = new HashMap<Integer, Double>();
	}
	
	public short getMove(Position position) {
		// called once per move
		AI_player = position.getToPlay(); // this is the AI
		
		// start the game-tree search
		ChessMove bestMove = maxVal(position, DEPTH, Integer.MIN_VALUE,
				Integer.MAX_VALUE);
		return bestMove.actualMove;
	}
	
	private ChessMove minVal(Position position, int depth, int alpha, int beta){
		int terminalState = terminalTest(position);
		if (terminalState == 1){
			// win situation
			return new ChessMove( (short) 0, Integer.MAX_VALUE);
			
		} else if (terminalState == 0){
			// draw situation
			return new ChessMove( (short) 0, 0);
			
		} else if (terminalState == -1){
			// loss situation
			return new ChessMove( (short) 0, Integer.MIN_VALUE);
			
		} else {
			// not a terminal state (value of 2)
			// reached last level depth, return a random value
			if (depth == 0){
				// cutoff by depth, so return a random value r
				// where r i smaller than the value of MAX
				// and larger than value for min
				return new ChessMove( (short) 0, (int)evalFunc(position));
			} else {
				// minimizer wants to get the lowest possible
				ChessMove bestMove = new ChessMove( (short)0, Integer.MAX_VALUE);
				for (short possMove: position.getAllMoves()){
					try {
						position.doMove(possMove);
						ChessMove possBestMove = maxVal(position, depth-1, alpha, beta);
						possBestMove.actualMove = possMove;
						
						if (possBestMove.value < bestMove.value){
							bestMove.actualMove = possMove;
							bestMove.value = possBestMove.value;
						}
					} catch (IllegalMoveException e){
						e.printStackTrace();
					}
					position.undoMove();
					
					// alpha-beta pruning
					if (bestMove.value <= alpha){
						return bestMove;
					}
					
					beta = Math.max(bestMove.value, beta);
				}
				
				return bestMove;
			}
		
		}
	}
	
	private double evalFunc(Position position){
		double material;
		// Use the transtable to memoize instead 
		// of calling getMaterial() method each
		// time
		if( transTable.containsKey(position.hashCode()) ){
			material = transTable.get(position.hashCode()) - REPEAT_CONSTANT;
		} else {
			material = position.getMaterial() + position.getDomination() / DOMINATION_CONSTANT;
			transTable.put(position.hashCode(), material);
		}
		
		if (position.getToPlay() == AI_player){
			return material;
		} else {
			return -1 * material;
		}
		
	}
	
	// pass alpha beta values through 
	private ChessMove maxVal(Position position, int depth, int alpha, int beta){
		int terminalState = terminalTest(position);
		if (terminalState == 1){
			// win situation
			return new ChessMove( (short) 0, Integer.MAX_VALUE);
			
		} else if (terminalState == 0){
			// draw situation
			return new ChessMove( (short) 0, 0);
			
		} else if (terminalState == -1){
			// loss situation
			return new ChessMove( (short) 0, Integer.MIN_VALUE);
			
		} else {
			// not a terminal state (value of 2)
			// reached last level depth, return a random value
			if (depth == 0){
				// cutoff by depth, so return a random value r
				// where r i smaller than the value of MAX
				// and larger than value for min
				return new ChessMove( (short) 0, (int)evalFunc(position));
				
			} else {
				// maximizer wants to get the highest possible
				ChessMove bestMove = new ChessMove( (short)0, Integer.MIN_VALUE);
				for (short possMove: position.getAllMoves()){
					try {
						position.doMove(possMove);
						ChessMove possBestMove = minVal(position, depth - 1, alpha, beta);
						possBestMove.actualMove = possMove;
						
						if (possBestMove.value > bestMove.value ){
							bestMove.actualMove = possMove;
							bestMove.value = possBestMove.value;
						}
					} catch (IllegalMoveException e){
						e.printStackTrace();
					}
					position.undoMove();
					
					if (bestMove.value >= beta){
						return bestMove;
					}
					
					alpha = Math.max(bestMove.value, alpha);
					
				}
				return bestMove;
			}
		}
		
		
	}
	
	
	// tests whether position is in terminal state (win, loss, draw)
	// returns 1 if win;
	// returns 0 if draw;
	// returns -1 if loss;
	// returns null if not terminal state
	private int terminalTest(Position position){
		// isTerminal accounts for possibility of draw
		
		// if current player is in check and can't move, it's a checkmate
		if (AI_player == position.getToPlay()){
			// Position is not checkmate but >50 moves (in isTerminal)
			if (position.isStaleMate()){
				return 0;
			}
			
			// If the position is checkmate, then AI has lost
			if (position.isTerminal() && position.isMate()){
				return -1; //loss
			}
		} else {
			// Position is not checkmate but >50 moves (in isTerminal)
			if (position.isStaleMate()){
				return 0;
			}
			
			// If the position is checkmate, then AI has won (not AI's turn)
			if (position.isTerminal() && position.isMate()){
				return 1; //win
			}
		}
		return 2;
	}
	
}

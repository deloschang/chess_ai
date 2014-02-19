package chai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;

import chesspresso.game.Game;
import chesspresso.move.IllegalMoveException;
import chesspresso.pgn.PGNReader;
import chesspresso.pgn.PGNSyntaxError;
import chesspresso.position.Position;

// AI that implements basic minimax algorithm
public class MiniMaxAI implements ChessAI {
	
	int DEPTH; // basic depth that AI can search at
	int AI_player; // stores int associated with the player AI
	OpeningBook loadBook;
	
	public MiniMaxAI(int depth) {
		loadBook = new OpeningBook();
		
//		loadBook.openingBook[2].gotoStart();
//		System.out.println(openingBook[2].getNextMove().getSAN());
		
		DEPTH = depth;
	}
	
	public short getMove(Position position) {
		// called once per move
		AI_player = position.getToPlay(); // this is the AI
		
		// start the game-tree search
		ChessMove bestMove = new ChessMove( (short)0, Integer.MIN_VALUE);
		
		for (short possMove : position.getAllMoves()){
			// each move begins with depth of 1
			try { 
				// try this move
				position.doMove(possMove);
				
				int possMoveValue = minVal(position, DEPTH);
				if (possMoveValue > bestMove.value){
					// best move and values found so far
					bestMove.value = possMoveValue;
					bestMove.actualMove = possMove;
				}
				position.undoMove();
				
			} catch (IllegalMoveException e){
				e.printStackTrace();
			}
		}
		
		return bestMove.actualMove;
	}
	
	private int minVal(Position position, int depth){
		int terminalState = terminalTest(position);
		if (terminalState == 1){
			// win situation
			return Integer.MAX_VALUE;
			
		} else if (terminalState == 0){
			// draw situation
			return 0;
			
		} else if (terminalState == -1){
			// loss situation
			return Integer.MIN_VALUE;
			
		} else {
			// not a terminal state (value of 2)
			// reached last level depth, return a random value
			if (depth == 0){
				// cutoff by depth, so return a random value r
				// where r i smaller than the value of MAX
				// and larger than value for min
				return evalFunc(position);
			} else {
				// minimizer wants to get the lowest possible
				int bestValue = Integer.MAX_VALUE;
				for (short possMove: position.getAllMoves()){
					try {
						position.doMove(possMove);
						int possMoveValue = maxVal(position, depth-1);
						if (possMoveValue < bestValue){
							bestValue = possMoveValue;
						}
					} catch (IllegalMoveException e){
						e.printStackTrace();
					}
					position.undoMove();
				}
				
				return bestValue;
			}
		
		}
	}
	
	private int evalFunc(Position position){
		if (position.getToPlay() == AI_player){
			return position.getMaterial();
		} else {
			return -1 * position.getMaterial();
		}
	}
	
	private int maxVal(Position position, int depth){
		int terminalState = terminalTest(position);
		if (terminalState == 1){
			// win situation
			return Integer.MAX_VALUE;
			
		} else if (terminalState == 0){
			// draw situation
			return 0;
			
		} else if (terminalState == -1){
			// loss situation
			return Integer.MIN_VALUE;
			
		} else {
			// not a terminal state (value of 2)
			// reached last level depth, return a random value
			if (depth == 0){
				// cutoff by depth, so return a random value r
				// where r i smaller than the value of MAX
				// and larger than value for min
				return evalFunc(position);
				
			} else {
				// maximizer wants to get the highest possible
				int bestValue = Integer.MIN_VALUE;
				for (short possMove: position.getAllMoves()){
					try {
						position.doMove(possMove);
						int possMoveValue = minVal(position, depth - 1);
						if (possMoveValue > bestValue){
							bestValue = possMoveValue;
						}
					} catch (IllegalMoveException e){
						e.printStackTrace();
					}
					position.undoMove();
				}
				return bestValue;
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

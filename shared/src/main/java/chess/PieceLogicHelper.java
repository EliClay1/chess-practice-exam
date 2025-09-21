package chess;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

public class PieceLogicHelper {

    List<ChessMove> listOfMoves = new ArrayList<>(List.of());

    Collection<ChessMove> definePieceLogic(ChessBoard board, ChessPosition currentPosition, ChessPiece currentPiece) {
        // diagonals first
        int[][] royaltyMovements = {{1,1}, {-1,1}, {-1,-1}, {1,-1}, {1,0}, {0,1}, {-1,0}, {0,-1}};
        int[][] bishopMovements = {{1,1}, {-1,1}, {-1,-1}, {1,-1}};
        int[][] rookMovements = {{1,0}, {0,1}, {-1,0}, {0,-1}};
        int[][] knightMovements = {{2,1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}, {2,-1}};
        int[][] pawnMovements = {{1,0}, {1,1}, {1,-1}}; // TODO - the two is technically because of first move.

        if (currentPiece.getPieceType() == ChessPiece.PieceType.BISHOP) {
            for (var dir : bishopMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), true);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.KING) {
            for (var dir : royaltyMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), false);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.QUEEN) {
            for (var dir : royaltyMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), true);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            for (var dir : knightMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), false);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.ROOK) {
            for (var dir : rookMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                directionalHelper(board, currentPosition, currentPosition, dRow, dCol, currentPiece.getTeamColor(), true);
            }
        }

        if (currentPiece.getPieceType() == ChessPiece.PieceType.PAWN) {
            for (var dir : pawnMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                pawnHelper(board, currentPosition, dRow, dCol, currentPiece.getTeamColor());
            }
        }

        return listOfMoves;
    }

    boolean isWithinBoardBounds(ChessBoard board, ChessPosition position) {
        int row = position.getRow();
        int col = position.getColumn();
        return (row > 0 && row <= board.gameBoard.length) && col > 0 && col <= board.gameBoard.length;
    }

    int getDirection(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            return 1;
        } else {
            return -1;
        }
    }

    boolean isStartingPiece(ChessPosition position, ChessGame.TeamColor team) {
        int row = position.getRow();
        if (row == 2 && team == ChessGame.TeamColor.WHITE) {
            return true;
        }
        if (row == 7 && team == ChessGame.TeamColor.BLACK) {
            return true;
        }
        return false;
    }

    void directionalHelper(ChessBoard board, ChessPosition currentPosition, ChessPosition savePosition, int dRow, int dCol, ChessGame.TeamColor currentTeamColor, boolean recurse) {
        int nextRow = savePosition.getRow() + dRow;
        int nextCol = savePosition.getColumn() + dCol;

        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);

        if (!isWithinBoardBounds(board, nextPosition)) {
            return;
        }

        ChessPiece nextPositionPiece = board.getPiece(nextPosition);

        if (nextPositionPiece == null) {
            listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
            if (recurse) {
                directionalHelper(board, currentPosition, nextPosition, dRow, dCol, currentTeamColor, true);
            }
        } else if (nextPositionPiece.getTeamColor() != currentTeamColor) {
            listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
        }
    }

    void pawnHelper(ChessBoard board, ChessPosition currentPosition, int dRow, int dCol, ChessGame.TeamColor currentTeamColor) {
        int direction = getDirection(currentTeamColor);
        boolean atStart = isStartingPiece(currentPosition, currentTeamColor);
        int nextRow = currentPosition.getRow() + dRow * direction;
        int nextCol = currentPosition.getRow() + dCol * direction;
        List<ChessPiece.PieceType> promotionOptions = List.of(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK);

        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);
        if (!isWithinBoardBounds(board, nextPosition)) {
            return;
        }
        ChessPiece nextPositionPiece = board.getPiece(nextPosition);

        // TODO Promotional Functionality
        if ((nextRow == 8 && currentTeamColor == ChessGame.TeamColor.WHITE && nextPositionPiece == null) ||
                (nextRow == 0 && currentTeamColor == ChessGame.TeamColor.BLACK && nextPositionPiece == null)) {
            for (var piece : promotionOptions) {
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, piece));
            }
        }


        if (((nextRow == currentPosition.getRow() + direction) && nextPositionPiece == null)) {
            listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
        }











        // check if there is a pawn directly in front. This includes checking if the row above / below is in bounds.


        /*
        * if atStart, it needs to do a double check to see the position ahead of it. This is entirely
        * dependent on the direction, which in this case could be multiplied * 2 to get the second position.
        * realistically this needs to happen at the end.
        * */

    }
}

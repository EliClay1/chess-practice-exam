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
            for (var dir : rookMovements) {
                int dRow = dir[0];
                int dCol = dir[1];
                pawnHelper(board, currentPosition, currentPiece.getTeamColor(), dRow, dCol);
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

    void pawnHelper(ChessBoard board, ChessPosition currentPosition, ChessGame.TeamColor currentTeamColor, int dRow, int dCol) {
        int direction = getDirection(currentTeamColor);
        int nextRow = currentPosition.getRow() + direction;
        int nextCol = currentPosition.getColumn() + dCol;

        ChessPosition nextPosition = new ChessPosition(nextRow, nextCol);

        if (!isWithinBoardBounds(board, nextPosition)) {
            return;
        }

        ChessPiece nextPositionPiece = board.getPiece(nextPosition);

        // Check forward
        if (dRow > 0) {
            if (nextPositionPiece == null) {
                //promotion
                if (promotion(nextRow, currentTeamColor, currentPosition, nextPosition)) {
                    return;
                }
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
                if (isStartingPiece(currentPosition, currentTeamColor)) {
                    int nextNextRow = nextRow + direction;
                    ChessPosition nextNextPosition = new ChessPosition(nextNextRow, nextCol);
                    ChessPiece nextNextPositionPiece = board.getPiece(nextNextPosition);
                    if (nextNextPositionPiece == null ) {
                        listOfMoves.add(new ChessMove(currentPosition, nextNextPosition, null));
                    }
                }
            }
        }
        // otherwise it is diagonal
        if (dRow == 0) {
            if (nextPositionPiece != null && nextPositionPiece.getTeamColor() != currentTeamColor) {
                if (promotion(nextRow, currentTeamColor, currentPosition, nextPosition)) {
                    return;
                }
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, null));
            }
        }
    }

    boolean promotion(int nextRow, ChessGame.TeamColor currentTeamColor, ChessPosition currentPosition, ChessPosition nextPosition) {
        List<ChessPiece.PieceType> promotionOptions = List.of(ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK);

        if ((nextRow == 8 && currentTeamColor == ChessGame.TeamColor.WHITE) ||
                (nextRow == 1 && currentTeamColor == ChessGame.TeamColor.BLACK)) {
            for (var piece : promotionOptions) {
                listOfMoves.add(new ChessMove(currentPosition, nextPosition, piece));
            }
            return true;
        }
        return false;
    }
}

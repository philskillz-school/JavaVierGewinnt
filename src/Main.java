import java.util.Scanner;

/**
 * Beschreiben Sie hier die Klasse Main.
 * 
 * @author Philipp Will
 * @version 12.03.2024
 */
public class Main
{
    enum Winner {
        noWin,
        win,
        draw
    }
    

    String player1;
    String player2;
    Scanner s;

    byte bWidth = 7;
    byte bHeight = 6;
    byte toWin = 8;
    short stonesPlaced = 0; // byte*byte = short
    char[][] board = new char[bWidth][bHeight]; // [spalte][zeile]
    char playerTurn = 'x';
    
    /**
     * Konstruktor für Objekte der Klasse Main
     */
    public Main()
    {
        s = new Scanner(System.in);
        for (byte i=0; i<bWidth; i++) { // spalten
            for (byte j=0; j<bHeight; j++) { // zeilen
                board[i][j] = ' ';
            }
        }

        player1 = eingabeText("Wie heißt Spieler 1?: ");
        player2 = eingabeText("Wie heißt Spieler 2?: ");
        
        ausgabeSpielfeld();
        
        while (true) {
            Winner w = placeStone();
            System.out.println();
            ausgabeSpielfeld();
            
            if (w == Winner.draw) {
                System.out.println("Unentschieden!");
                break;
            }
            
            if (w == Winner.win) {
                System.out.println("Spieler " + playerTurn + " hat gewonnen");
                break;
            }
            
            playerTurn = playerTurn == 'x' ? 'o' : 'x';
        }
        
        System.out.println("Spiel zu ende.");
    }

    /**
     * Platzieren eines Steins
     *
     * @return        der Gewinner
     */
    Winner placeStone() {
        int col = getCol();
        stonesPlaced++;
        
        // Plaziert den Stein immer ganz unten
        int placedRow = -1;
        for (int row=bHeight-1; row>=0; row--) { // geht von "unten" (größter index) nach oben (index 0)
            if (board[col][row] == ' ') {
                board[col][row] = playerTurn;
                placedRow = row;
                break;
            }
        }

        return spielerGewonnen(col, placedRow);
    }

    /**
     * Gibt die Spalte zurück, in die der Stein gelegt werden soll.
     * Sie wird von der Konsole eingelesen.
     *
     * @return        die Spalte
     */
    int getCol() {
        while (true) {
            int col = eingabeZahl("( Spieler " + playerTurn + " ) " + "Gib die Spalte ein (1-" + bWidth + "): ")-1; // ändern
            if (col < 0 || col >= bWidth) {
                System.out.println("Spalte nicht vorhanden. Wähle eine andere.");
                continue;
            }
            if (board[col][0] == ' ') {
                return col;
            }
            System.out.println("Spalte ist voll. Wähle eine andere.");
        }
    }

    /**
     * Gibt das Spielfeld aus
     */
    void ausgabeSpielfeld() {
        System.out.println("|-" + String.join("-|-", repeat("-", bWidth).split("")) + "-|");
        for (byte i = 0; i< bHeight; i++) { // zeilen
            String[] row = new String[bWidth];
            for (byte j = 0; j< bWidth; j++) { // spalten
                row[j] = String.valueOf(board[j][i]);
            }
            System.out.println("| " + String.join(" | ", row) + " |");
        }
        System.out.println("|-" + String.join("-|-", repeat("-", bWidth).split("")) + "-|");
    }

    /**
     * Überprüft, ob ein Spieler gewonnen hat
     *
     * @param  spalte    die Spalte, in die der Stein gelegt wurde
     * @param  zeile    die Zeile, in die der Stein gelegt wurde
     * @return        der Gewinner
     */
    Winner spielerGewonnen(int spalte, int zeile) {
        if (stonesPlaced < 2*toWin-1) {
            return Winner.noWin;
        }

        Winner w = spielerGewonnenSpalte(spalte, zeile);
        if (w == Winner.win) {
            System.out.println("Gewonnen durch Spalte");
            return w;
        }

        w = spielerGewonnenZeile(spalte, zeile);
        if (w == Winner.win) {
            System.out.println("Gewonnen durch Zeile");
            return w;
        }

        w = spielerGewonnenDiagonalRechtsUnten(spalte, zeile);
        if (w == Winner.win) {
            System.out.println("Gewonnen durch DiagonalRechtsUnten");
            return w;
        }

        w = spielerGewonnenDiagonalLinksUnten(spalte, zeile);
        if (w == Winner.win) {
            System.out.println("Gewonnen durch DiagonalLinksUnten");
            return w;
        }

        if (stonesPlaced >= bWidth * bHeight) {
            return Winner.draw;
        }

        return Winner.noWin; // ändern
    }

    /**
     * Überprüft, ob ein Spieler in einer Spalte gewonnen hat
     *
     * @param  spalte    die Spalte, in die der Stein gelegt wurde
     * @param  zeile    die Zeile, in die der Stein gelegt wurde
     * @return        der Gewinner
     */
    Winner spielerGewonnenSpalte(int spalte, int zeile) {
        if (bHeight-zeile < toWin) {
            return Winner.noWin;
        }
        
        int count = 1; // der Stein, der gerade gelegt wurde
        for (int z=zeile+1; z<bHeight; z++) {
            if (board[spalte][z] != playerTurn) {
                break;
            }
            count++;
        }

        if (count >= toWin) {
            return Winner.win;
        }
        return Winner.noWin;
    }

    /**
     * Überprüft, ob ein Spieler in einer Zeile gewonnen hat
     *
     * @param  spalte    die Spalte, in die der Stein gelegt wurde
     * @param  zeile    die Zeile, in die der Stein gelegt wurde
     * @return        der Gewinner
     */
    Winner spielerGewonnenZeile(int spalte, int zeile) {
        int count = 0; // der Stein, der gerade gelegt wurde
        for (int col=spalte-1; col>=0; col--) { // nach links
            if (board[col][zeile] != playerTurn) {
                break;
            }
            count++;
        }
        // formel für die mindestens steine links von spalte für 4 bei einer größe von board.length:
        // 4 - board.length + spalte (erklärung bild.png)
        if (count >= toWin-1) {
            return Winner.win;
        }
        if (count < (toWin - bWidth + spalte)) {
            System.out.println("Kein Gewinner. Sonderlogik. Spalte: " + spalte + " ");
            return Winner.noWin;
        }
        for (int col=spalte+1; col<bWidth;col++) { // nach rechts
            if (board[col][zeile] != playerTurn) {
                break;
            }
            count++;
        }

        if (count >= toWin-1) { // einer wurde schon gezählt (der Stein, der gerade gelegt wurde)
            return Winner.win;
        }
        return Winner.noWin;
    }

    /**
     * Überprüft, ob ein Spieler in einer Diagonalen von rechts unten nach links oben gewonnen hat
     *
     * @param  spalte    die Spalte, in die der Stein gelegt wurde
     * @param  zeile    die Zeile, in die der Stein gelegt wurde
     * @return        der Gewinner
     */
    Winner spielerGewonnenDiagonalRechtsUnten(int spalte, int zeile) {
        int count = 0;
        for (int col=spalte-1, row=zeile-1; col>=0 && row>0; col--, row--) { // nach links oben
            if (board[col][row] != playerTurn) {
                break;
            }
            count++;
        }
        if (count >= toWin-1) {
            return Winner.win;
        }
        if (count < (toWin - bWidth + spalte)) {
            System.out.println("Kein Gewinner. Sonderlogik. Spalte: " + spalte + " ");
            return Winner.noWin;
        }
        for (int col=spalte+1, row=zeile+1; col<bWidth && row<bHeight; col++, row++) { // nach rechts unten
            if (board[col][row] != playerTurn) {
                break;
            }
            count++;
        }

        if (count >= toWin-1) {
            return Winner.win;
        }

        return Winner.noWin;
    }

    /**
     * Überprüft, ob ein Spieler in einer Diagonalen von links unten nach rechts oben gewonnen hat
     *
     * @param  spalte    die Spalte, in die der Stein gelegt wurde
     * @param  zeile    die Zeile, in die der Stein gelegt wurde
     * @return        der Gewinner
     */
    Winner spielerGewonnenDiagonalLinksUnten(int spalte, int zeile) {
        int count = 0;
        for (int col=spalte-1, row=zeile+1; col>=0 && row<bHeight; col--, row++) { // nach links unten
            if (board[col][row] != playerTurn) {
                break;
            }
            count++;
        }
        if (count >= toWin-1) {
            return Winner.win;
        }
        if (count < (toWin - bWidth + spalte)) {
            System.out.println("Kein Gewinner. Sonderlogik. Spalte: " + spalte + " ");
            return Winner.noWin;
        }
        for (int col=spalte+1, row=zeile-1; col<bWidth && row>0; col++, row--) { // nach rechts oben
            if (board[col][row] != playerTurn) {
                break;
            }
            count++;
        }

        if (count >= toWin-1) {
            return Winner.win;
        }

        return Winner.noWin;
    }

    /**
     * Wiederholt einen String n-mal
     *
     * @param  s    der String
     * @param  n    die Anzahl
     * @return        der wiederholte String
     */
    String repeat(String s, int n) {
        return new String(new char[n]).replace("\0", s);
    }

    /**
     * Holt sich eine Texteingabe von der Konsole
     * 
     * @param  pText    der Text zum Ausgeben
     * @return        der eingegebene Text
     */
    String eingabeText(String pText) {
        System.out.println(pText);
        return s.next();
    }
    
    /**
     * Holt sich eine Zahleingabe von der Konsole
     * 
     * @param  pText    der Text zum Ausgeben
     * @return        die eingegebene Zahl
     */
    int eingabeZahl(String pText) {
        System.out.print(pText);
        return s.nextInt();
    }
    
    public static void main(String[] args) {
        new Main();
    }
}

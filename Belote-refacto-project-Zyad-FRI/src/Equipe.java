public class Equipe {
    private int id;
    private int num;
    private int idTournoi;
    private String player1;
    private String player2;

    public Equipe(int id, int num, String player1, String player2, int idTournoi) {
        this.id = id;
        this.num = num;
        this.player1 = player1;
        this.player2 = player2;
        this.idTournoi = idTournoi;
    }

    public Equipe(int num, String player1, String player2) {
        this.num = num;
        this.player1 = player1;
        this.player2 = player2;
    }

    // Getters
    public int getId() { return id; }
    public int getIdTournoi() { return idTournoi; }
    public int getNum() { return num; }
    public String getPlayer1() { return player1; }
    public String getPlayer2() { return player2; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setIdTournoi(int idTournoi) { this.idTournoi = idTournoi; }
    public void setNum(int num) { this.num = num; }
    public void setPlayer1(String player1) { this.player1 = player1; }
    public void setPlayer2(String player2) { this.player2 = player2; }

    @Override
    public String toString() {
        return String.format("Équipe %d: %s & %s", num, player1, player2);
    }
}
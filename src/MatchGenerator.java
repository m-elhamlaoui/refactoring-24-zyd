import java.util.Vector;

// Interface for Match Generator
interface IMatchGenerator {
    Vector<Vector<Match>> generateMatches(Vector<Equipe> equipes);
}



public class MatchGenerator implements IMatchGenerator {


    public MatchGenerator() {
    }


    // This method generates matches for a given list of teams.
    @Override
    public Vector<Vector<Match>> generateMatches(Vector<Equipe> equipes) { 
        int numTeams = equipes.size();
        int numRounds = numTeams - 1;
        Vector<Vector<Match>> matchesByRound = new Vector<>();
        for (int round = 0; round < numRounds; round++) {
            Vector<Match> matchesInRound = new Vector<>();
            for (int i = 0; i < numTeams / 2; i++) {
                Equipe teamA = equipes.get(i);
                Equipe teamB = equipes.get(numTeams - 1 - i);   
                Match match = new Match(teamA.getNum(), teamB.getNum());
                matchesInRound.add(match);
            }
            matchesByRound.add(matchesInRound);
            rotateTeams(equipes);
        }
        return matchesByRound;
    }


    // This method rotates the teams in the list of teams.
    private static void rotateTeams(Vector<Equipe> equipes) {
        Equipe lastTeam = equipes.remove(equipes.size() - 1);
        equipes.add(1, lastTeam);
    }


}


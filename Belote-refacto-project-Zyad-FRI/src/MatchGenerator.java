import java.util.Vector;
import java.util.Collections;

public class MatchGenerator implements IMatchGenerator {
    
    @Override
    public Vector<Vector<Match>> generateMatches(Vector<Equipe> equipes) {
        if (equipes == null || equipes.size() < 2) {
            return new Vector<>();
        }

        int numTeams = equipes.size();
        int numRounds = numTeams - 1;
        Vector<Vector<Match>> matchesByRound = new Vector<>();
        Vector<Equipe> teams = new Vector<>(equipes);

        // Sort teams by number to ensure consistent pairing
        Collections.sort(teams, (a, b) -> Integer.compare(a.getNum(), b.getNum()));

        for (int round = 0; round < numRounds; round++) {
            Vector<Match> roundMatches = new Vector<>();
            
            // Generate matches for this round
            for (int i = 0; i < numTeams / 2; i++) {
                Equipe team1 = teams.get(i);
                Equipe team2 = teams.get(numTeams - 1 - i);
                Match match = new Match(team1.getNum(), team2.getNum());
                match.setTour(round + 1);
                roundMatches.add(match);
            }
            
            matchesByRound.add(roundMatches);
            rotateTeams(teams);
        }

        return matchesByRound;
    }

    private void rotateTeams(Vector<Equipe> teams) {
        if (teams.size() < 2) return;
        
        Equipe temp = teams.remove(1);
        teams.add(temp);
    }
}
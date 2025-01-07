import java.util.Vector;

/**
 * Strategy for match generation (Round-Robin style).
 */
public class MatchGenerator implements IMatchGenerator {

    @Override
    public Vector<Vector<Match>> generateMatches(Vector<Equipe> equipes) {
        int numTeams = equipes.size();
        int numRounds = numTeams - 1;
        Vector<Vector<Match>> matchesByRound = new Vector<>();

        // Round-robin (basic approach)
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

    private static void rotateTeams(Vector<Equipe> equipes) {
        Equipe lastTeam = equipes.remove(equipes.size() - 1);
        equipes.add(1, lastTeam);
    }
}

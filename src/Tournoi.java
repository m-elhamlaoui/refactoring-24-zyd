import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;

public class Tournoi {
	String statuttnom;
	Statement st;
	private int  statut;
	private int  Id;
	private int  nbMatchs;
	private int  nbTours;
	private String nomTournoi;
	private int step;
	private MatchGenerator mGen = new MatchGenerator();


	private Vector<Vector<Match>> tours = null;
	private Vector<Equipe> teams = null;
	private Vector<Match> matches  = null;
	private Vector<Integer>teamsIds  = null; 


	private static TournoiDAO tournoiDAO=new TournoiDAO();
	private static EquipesDAO equipesDAO= new EquipesDAO();
	private static MatchDAO matchDAO= new MatchDAO();

	public Tournoi(String nt){
		mGen = new MatchGenerator();
		this.nomTournoi = nt;
		Tournoi t = tournoiDAO.getByName(nt);
		this.Id = t.getId();
		this.statut = t.getStatut();
		statuttnom = "Inconnu";


		switch(this.statut){
		case 0:
			statuttnom = "Inscription des joueurs";
		break;
		case 1:
			statuttnom = "Génération des matchs";
		break;
		case 2:
			statuttnom = "Matchs en cours";
		break;
		case 3:
			statuttnom = "Terminé";
		break;
		}
	}


	public Tournoi(int nbMatchs, String nomTournoi, int statut) {
		this.nbMatchs = nbMatchs;
		this.nomTournoi = nomTournoi;
		this.statut = statut;
	}

	public Tournoi(int Id, int nbMatchs, String nomTournoi, int statut) {
		this(nbMatchs, nomTournoi, statut);
		this.Id = Id;
	}

	public void setEquipes(List<Equipe> equipes) {
		this.teams = new Vector<>(equipes);
	}

	public void setMatchs(List<Match> matchs) {
		this.matches = new Vector<>(matchs);
	}

	public int getId() {
		return Id;
	}

	public void setNbTours(int nbTours){
		this.nbTours = nbTours;
	}

	public void setNbMatchs(int nbMatchs){
		this.nbMatchs = nbMatchs;
	}

	public void setStatut(int statut){
		this.statut = statut;
	}

	public Vector<Vector<Match>> getTours(){
		return tours;
	}

	public int getStep(){
		return step;
	}


	
	public Match getMatch(int index){
		return matches.get(index);
	}
	public int getNbMatchs(){
		if(matches == null) return 0;
		return matches.size();
	}
	public Equipe getEquipe(int index){
		return teams.get(index);
	}
	public int getNbEquipes(){
		if(teams == null) return 0;
		return teams.size();
	}

	public Vector<Equipe> getEquipes(){
		return teams;
	}

	public int  getStatut(){
		return statut;
	}
	public String getNStatut(){
		return statuttnom;
	}
	public String getNom() {
		return this.nomTournoi;
	}

	public Vector<Match> getMatchs(){
		return matches;
	}

	public int getNbTours(){
		return nbTours;
	}

	public void computeRank(){
		HashMap<Integer, Integer> rank = new HashMap<Integer, Integer>();
		for (Equipe e:teams){
			int matchs_gagnes=0;
			int matchs_nuls=0;
			int score=0;
			Vector<Match> matchs = equipesDAO.getMatchsGagne(Id,e.getId());
			matchs_gagnes=matchs.size();
			matchs = equipesDAO.getMatchsNul(Id,e.getId());
			matchs_nuls=matchs.size();
			score+=matchs_nuls;
			score+=matchs_gagnes*3;
			rank.put(e.getId(), score);
		}
		System.out.println(rank);
	}

	public void genererMatchs(){
		int nbt = getNbTours();
		if(nbt == 0){
			nbt++;
		}
		Vector<Vector<Match>> ms;
		Vector<Equipe> equipes = this.getEquipes();
		System.out.println(equipes);
		ms = mGen.generateMatches(equipes);
		this.tours = ms;
		Vector<Match> allMatchs = new Vector<Match>();
		this.matches = allMatchs;
		for (Vector<Match> m:ms){
			System.out.println(m.get(0).getTeam1() + " - " + m.get(0).getTeam2());
		}
		tournoiDAO.update(this);
		System.out.println("Done");
	}
	
	public boolean ajouterTour(){
		if(this.nbTours==getNbEquipes()-1){
			return false;
		}
		else{
			if(this.tours==null){
				this.tours=mGen.generateMatches(this.getEquipes());
			}
			nbTours++;
			Vector<Match> ms=tours.get(nbTours-1);
			for (Match m:ms){
				m.setIdTournoi(getId());
				System.out.println(m.getTournamentNumber());
				m.setTour(nbTours);
				matchDAO.create(m);
			}
			this.matches.addAll(ms);
		}
		return true;
	}
	public void updateMatch(Match m){
		matchDAO.update(m);
	}


	public void updateEquipe(Equipe e){
		equipesDAO.update(e);
	}


	public void supprimerTour(){
		int nbtoursav=this.getNbTours();
		//try{
		matchDAO.deleteMatchByTournoiIdAndTourNumber(this.Id, nbtoursav);	
			//st.executeUpdate("DELETE FROM matchs WHERE id_tournoi="+ Id+" AND num_tour=" + nbtoursav);
		//} catch (SQLException e) {
			//// TODO Auto-generated catch block
			//System.out.println("Erreur del tour : " + e.getMessage());
		//}
	}

	public int deleteTournoi(){
		try {
			System.out.println("ID du tournoi � supprimer:" + Id);
			tournoiDAO.delete(Id);
		} catch (Exception e) {
			System.out.println("Erreur inconnue");
		} 
		return 0;
	}

	//factory method for creating a new tournoi
	public static Tournoi createTournoi(String nom) throws Exception{
		if(nom == null || nom == "") throw new Exception("Nom de tournoi invalide");
		if(nom.length() < 3) throw new Exception("Nom de tournoi trop court");
		if(tournoiDAO.getByName(nom) != null) throw new Exception("Un tournoi du m�me nom existe d�j�");
		Tournoi t = new Tournoi(0, 0, nom, 0);
		try{
			tournoiDAO.create(t);
			System.out.println("Tournoi cr��");
		}catch(Exception e){
			e.printStackTrace();
			System.out.println("Error Creating Tournoi");
		}
		return t;
	}



	public static int creerTournoi(Statement s2){
		String s = (String)JOptionPane.showInputDialog(
                null,
                "Entrez le nom du tournoi",
                "Nom du tournoi",
                JOptionPane.PLAIN_MESSAGE);
		if(s == null || s == ""){
			return 1;
		}else{
			try {
				s =  mysql_real_escape_string(s);
				if(s.length() < 3){
					JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Nom trop court.");
					return 2;					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(s == ""){
				JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Ne pas mettre de caract�res sp�ciaux ou accents dans le nom");
				return 2;
			}else{
				ResultSet rs;
				try {
					rs = s2.executeQuery("SELECT Id FROM tournois WHERE nom_tournoi = '" + s + "';");
					if(rs.next()){
						JOptionPane.showMessageDialog(null, "Le tournoi n'a pas �t� cr��. Un tournoi du m�me nom existe d�j�");
						return 2;							
					}
					System.out.println("INSERT INTO tournois (Id, nb_matchs, nom_tournoi, statut) VALUES (NULL, 10, '"+s+"', 0)");
				s2.executeUpdate("INSERT INTO tournois (Id, nb_matchs, nom_tournoi, statut) VALUES (NULL, 10, '"+s+"', 0)");
				} catch (SQLException e) {
					System.out.println("Erreur requete insertion nouveau tournoi:" + e.getMessage());
				}
			}
		}
		return 0;
	}
	
	public void ajouterEquipe(Equipe e){
		if (this.teams == null) this.teams = new Vector<Equipe>();
		if (this.teamsIds == null) this.teamsIds = new Vector<Integer>();


		//int a_aj = this.teams.size()+1;
		for (int i=1;i <= this.teams.size(); i++){
			if(!teamsIds.contains(i)){
				//a_aj=i;
				break;
			}
		}
		equipesDAO.create(e);

		this.teams.add(e);
	}

	public void majEquipe(int index){
		equipesDAO.update(getEquipe(index));
	}


	public void supprimerEquipe(int ideq){
		System.err.println("Deleting team with id : " + ideq);
		int num = equipesDAO.getNumFromId(ideq, Id);
		equipesDAO.delete(ideq);
		if (num != -1) equipesDAO.updateNum(Id, num);
	}
    public static String mysql_real_escape_string( String str) 
            throws Exception
      {
          if (str == null) {
              return null;
          }
          if (str.replaceAll("[a-zA-Z0-9_!@#$%^&*()-=+~.;:,\\Q[\\E\\Q]\\E<>{}\\/? ]","").length() < 1) {
              return str;
          }
          String clean_string = str;
          clean_string = clean_string.replaceAll("\\n","\\\\n");
          clean_string = clean_string.replaceAll("\\r", "\\\\r");
          clean_string = clean_string.replaceAll("\\t", "\\\\t");
          clean_string = clean_string.replaceAll("\\00", "\\\\0");
          clean_string = clean_string.replaceAll("'", "''");
          return clean_string;
      }

	public static Vector<Vector<Match>> getMatchsToDo(int nbJoueurs, int nbTours){
		if( nbTours  >= nbJoueurs){
			System.out.println("Erreur tours < equipes");
			return null;
		}
		int[]   tabJoueurs;
		if((nbJoueurs % 2) == 1){
			tabJoueurs   = new int[nbJoueurs+1];
			tabJoueurs[nbJoueurs] = -1;
			for(int z = 0; z < nbJoueurs;z++){
				tabJoueurs[z] = z+1;
			}
			nbJoueurs++;
		}else{
			tabJoueurs   = new int[nbJoueurs];
			for(int z = 0; z < nbJoueurs;z++){
				tabJoueurs[z] = z+1;
			}
		}
		boolean quitter;
		int     i, increment  = 1, temp;
		Vector<Vector<Match>> retour = new Vector<Vector<Match>>();
		Vector<Match> vm;
		for( int r = 1; r <= nbTours;r++){
			if(r > 1){
				temp = tabJoueurs[nbJoueurs - 2];
				for(i = (nbJoueurs - 2) ; i > 0; i--){
					tabJoueurs[i] = tabJoueurs[i-1];
				}
				tabJoueurs[0] = temp;
			}
			i       = 0;
			quitter = false;
			vm = new Vector<Match>();
			while(!quitter){
				if (tabJoueurs[i] == -1 || tabJoueurs[nbJoueurs - 1  - i] == -1){
				}else{
					vm.add(new Match(tabJoueurs[i], tabJoueurs[nbJoueurs - 1  - i]));
				}
		        i+= increment;
				if(i >= nbJoueurs / 2){
					if(increment == 1){
						quitter = true;
						break;
					}else{
						increment = -2;
						if( i > nbJoueurs / 2){
							i = ((i > nbJoueurs / 2) ? i - 3 : --i) ;
						}
						if ((i < 1) && (increment == -2)){
							quitter = true;
							break;
						}
					}
				}
			}
			retour.add(vm);
		}
		return retour;
	}  
}

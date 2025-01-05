import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.StyledEditorKit;


public class Fenetre extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public JPanel panel;
	Statement s;
	
	private JTextArea gt;
	private JPanel ListeTournois;
	private Vector<String> noms_tournois;
	private JList<String> list;
	private JLabel        label;
	private JButton       creerTournoi;
	private JButton       selectTournoi;	
	private JButton       deleteTournoi;
	private JButton       btournois;
	private JButton       bequipes;
	private JButton       btours;
	private JButton       bmatchs;
	private JButton       bresultats;
	private JButton       bparams;
	
	private boolean tournois_trace  = false;
	private boolean details_trace   = false;
	private boolean equipes_trace   = false;
	private boolean tours_trace     = false;
	private boolean match_trace     = false;
	private boolean resultats_trace = false;

	private TournoiDAO tdao= new TournoiDAO();

	private CardLayout fen;
	final static String TOURNOIS = "Tournois";
    final static String DETAIL   = "Paramètres du tournoi";
    final static String EQUIPES  = "Equipes";
    final static String TOURS    = "Tours";
    final static String MATCHS   = "Matchs";
    final static String RESULTATS= "Resultats";
    public Tournoi t = null;
    
    private JLabel statut_slect = null;
    private final String statut_deft = "Gestion de tournois de Belote v1.0 - ";
	public Fenetre(Statement st){
		s = st;
		this.setTitle("Gestion de tournoi de Belote");
		setSize(800,400);
		this.setVisible(true);
		this.setLocationRelativeTo(this.getParent());
		
		JPanel contenu = new JPanel();
		contenu.setLayout(new BorderLayout());
		this.setContentPane(contenu);
		
		
		JPanel phaut = new JPanel();
		contenu.add(phaut,BorderLayout.NORTH);
		
		phaut.add(statut_slect = new JLabel());
		this.setStatutSelect("Pas de tournoi sélectionné");
		
		JPanel pgauche = new JPanel();
		pgauche.setBackground(Color.RED);
		pgauche.setPreferredSize(new Dimension(130,0));
		contenu.add(pgauche,BorderLayout.WEST);
		
		
		btournois    = new JButton("Tournois");
		bparams      = new JButton("Paramètres");
		bequipes     = new JButton("Equipes");
		btours       = new JButton("Tours");
		bmatchs      = new JButton("Matchs");
		bresultats   = new JButton("Résultats");
		
		int taille_boutons = 100;
		int hauteur_boutons = 30;
		btournois.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bparams.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bequipes.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		btours.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bmatchs.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		bresultats.setPreferredSize(new Dimension(taille_boutons,hauteur_boutons));
		
		pgauche.add(btournois);
		pgauche.add(bparams);
		pgauche.add(bequipes);
		pgauche.add(btours);
		pgauche.add(bmatchs);
		pgauche.add(bresultats);
		fen = new CardLayout();
		panel = new JPanel(fen);
		
		contenu.add(panel,BorderLayout.CENTER);
		
		btournois.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_select_tournoi();	
			}
		});
		btours.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_tours_tournoi();	
			}
		});
		bparams.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				tracer_details_tournoi();
			}
		});
		bequipes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_equipes();
			}
		});
		bmatchs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tracer_tournoi_matchs();
			}
		});
		bresultats.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DisplayResults();
			}
		});
		tracer_select_tournoi();
	}


	public void setStatutSelect(String t){
		statut_slect.setText(statut_deft + "" + t);
	}



	public void majboutons(){
		if( t == null){
			btournois.setEnabled(true);
			bequipes.setEnabled(false);
			bmatchs.setEnabled(false);
			btours.setEnabled(false);
			bresultats.setEnabled(false);
			bparams.setEnabled(false);			
		}else{
			switch(t.getStatut()){
			case 0:
				btournois.setEnabled(true);
				bequipes.setEnabled(true);
				bmatchs.setEnabled(false);
				btours.setEnabled(false);
				bresultats.setEnabled(false);
				bparams.setEnabled(true);	
			break;
			case 2:
				btournois.setEnabled(true);
				bequipes.setEnabled(true);
				bmatchs.setEnabled(t.getNbTours() > 0);
				btours.setEnabled(true);
				bresultats.setEnabled(true);			
				bparams.setEnabled(true);					
			break;
			}
		}
	}


	public void tracer_select_tournoi(){
		t = null;
		majboutons();
		int nbdeLignes = 0;
		noms_tournois = new Vector<String>();
       this.setStatutSelect("sélection d'un tournoi");
		ResultSet rs;
		try {
			rs = s.executeQuery("SELECT * FROM tournois;");
			while( rs.next() ){
				System.out.println(rs.getString("nom_tournoi"));
				System.out.println(rs.getInt("id_tournoi"));
				nbdeLignes++;
				noms_tournois.add(rs.getString("nom_tournoi"));
			}
			if( nbdeLignes == 0){
				System.out.println("Pas de résultats");
			}else{
			}
			rs.close();
		} catch (SQLException e) {
			System.out.println("Erreur lors de la requète :" + e.getMessage());
			e.printStackTrace();
		}
		if(tournois_trace){
			list.setListData(noms_tournois);
	        if(nbdeLignes == 0){
	        	selectTournoi.setEnabled(false);
	        	deleteTournoi.setEnabled(false);
	        }else{
	        	selectTournoi.setEnabled(true);
	        	deleteTournoi.setEnabled(true);
	        	list.setSelectedIndex(0);
	        }
			fen.show(panel, TOURNOIS);
		}else{
		    tournois_trace = true;
			JPanel t = new JPanel();
			t.setLayout(new BoxLayout(t, BoxLayout.Y_AXIS));
			panel.add(t,TOURNOIS);
			gt = new JTextArea("Gestion des tournois\nXXXXX XXXXXXXX, juillet 2012");
			gt.setAlignmentX(Component.CENTER_ALIGNMENT);
			gt.setEditable(false);
			t.add(gt);
			ListeTournois = new JPanel();
			t.add(ListeTournois);		
			list = new JList<String>(noms_tournois); 
			list.setAlignmentX(Component.LEFT_ALIGNMENT); 
			list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		    list.setVisibleRowCount(-1);
		    JScrollPane listScroller = new JScrollPane(list);
	        listScroller.setPreferredSize(new Dimension(250, 180));
	        label = new JLabel("Liste des tournois");
	        label.setLabelFor(list);
	        label.setAlignmentX(Component.LEFT_ALIGNMENT);
	        t.add(label);
	        t.add(listScroller);
	        t.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
	        Box bh = Box.createHorizontalBox();
	        t.add(bh);
			creerTournoi = new JButton("Créer un nouveau tournoi");
			selectTournoi = new JButton("Sélectionner le tournoi");
			deleteTournoi = new JButton("Supprimer le tournoi");
			bh.add(creerTournoi);
			bh.add(selectTournoi);	
			bh.add(deleteTournoi);
			
			t.updateUI();
	        if(nbdeLignes == 0){
	        	selectTournoi.setEnabled(false);
	        	deleteTournoi.setEnabled(false);
	        }else{
	        	list.setSelectedIndex(0);
	        }
	        creerTournoi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String s =(String)JOptionPane.showInputDialog(
		                    Fenetre.this,
		                    "Nom du tournoi",
		                    "Création d'un tournoi",
		                    JOptionPane.PLAIN_MESSAGE,
		                    null,
		                    null,
		                    "Tournoi sans nom");
					try {
						System.out.println(s);
						Tournoi.createTournoi(s);

					}catch(Exception ex){
						ex.printStackTrace();
					}
					Fenetre.this.tracer_select_tournoi();
				}
			});
	        deleteTournoi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String nt = Fenetre.this.list.getSelectedValue();
					System.out.println("Suppression du tournoi " + nt);
					System.out.println(tdao);
					tdao.deleteByName(nt);
					Fenetre.this.tracer_select_tournoi();
				}
			});
	        selectTournoi.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String nt = Fenetre.this.list.getSelectedValue();
					Tournoi t=tdao.getByName(nt);
					Fenetre.this.t =t;
					Fenetre.this.tracer_details_tournoi();
					Fenetre.this.setStatutSelect("Tournoi \" " + nt + " \"");
				}
			});
	        fen.show(panel, TOURNOIS);
		}
		
	}
    
	JLabel                     detailt_nom;
	JLabel                     detailt_statut;
	JLabel                     detailt_nbtours;
	
	public void tracer_details_tournoi(){
		if(t == null){
			return ;
		}
		majboutons();
		
		if(details_trace){
			detailt_nom.setText(t.getNom());
			detailt_statut.setText(t.getNStatut());
			detailt_nbtours.setText(Integer.toString(t.getNbTours()));
		}else{
			details_trace = false;
			JPanel p = new JPanel();
			p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
			p.add(new JLabel("Détail du tournoi"));
			panel.add(p, DETAIL);
			
			JPanel tab = new JPanel( new GridLayout(4,2));
			detailt_nom = new JLabel(t.getNom());
			tab.add(new JLabel("Nom du tournoi"));
			tab.add(detailt_nom);

			detailt_statut = new JLabel(t.getNStatut());
			tab.add(new JLabel("Statut"));
			tab.add(detailt_statut);
			
			detailt_nbtours = new JLabel(Integer.toString(t.getNbTours()));
			tab.add(new JLabel("Nombre de tours:"));
			tab.add(detailt_nbtours);
			p.add(tab);
			p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

		}
		fen.show(panel, DETAIL);
	}

	private AbstractTableModel eq_modele;
    private JButton            eq_ajouter;
    private JButton            eq_supprimer;
    private JButton            eq_valider;
    private JScrollPane        eq_js;
    JTable                     eq_jt;
    JPanel                     eq_p;
    BoxLayout                  eq_layout;
    JLabel                     eq_desc;

	public void tracer_tournoi_equipes(){
		if(t == null){
			return ;
		}
		majboutons();
		System.out.println("tracer_tournoi_equipes");
		//System.out.println(t.getEquipe(0));
		if(equipes_trace){
			eq_modele.fireTableDataChanged();
		}else{
			equipes_trace = true;
			eq_p      = new JPanel();
			eq_layout = new BoxLayout(eq_p, BoxLayout.Y_AXIS);
			eq_p.setLayout(eq_layout);
			eq_desc = new JLabel("Equipes du tournoi");
			eq_p.add(eq_desc);
			eq_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panel.add(eq_p, EQUIPES);
			eq_modele = new AbstractTableModel(){
				private static final long serialVersionUID = 1L;
				@Override
				public Object getValueAt(int arg0, int arg1) {
					//System.out.println("getvalueat");
					Object r = null;
					
					switch(arg1){
					case 0:
						r= t.getEquipe(arg0).getNum();
					break;
					case 1:
						r= t.getEquipe(arg0).getPlayer1();
					break;
					case 2:
						r= t.getEquipe(arg0).getPlayer2();
					break;
					}
					return r;
				}
				public String getColumnName(int col) {
				        if(col == 0){
				        	return "Numéro d'équipe";
				        }else if(col == 1){
				        	return "Joueur 1";
				        }else if(col == 2){
				        	return "Joueur 2";
				        }else{
				        	return "??";
				        }
				 }
				@Override
				public int getRowCount() {
					if(t == null)return 0;
					return t.getNbEquipes();
				}
				@Override
				public int getColumnCount() {
					return 3;
				}
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					Equipe e = t.getEquipe(rowIndex);
					if( columnIndex == 0){
						
					}else if( columnIndex == 1){
						e.setPlayer1((String)aValue);
					}else if( columnIndex == 2){
						e.setPlayer2((String)aValue);
					}
					t.majEquipe(rowIndex);
					fireTableDataChanged();
				}
			};
			eq_jt = new JTable(eq_modele);
			eq_js = new JScrollPane(eq_jt);
			eq_p.add(eq_js);
			JPanel bt    = new JPanel();
			eq_ajouter   = new JButton("Ajouter une équipe");
			eq_supprimer = new JButton("Supprimer une équipe");
			eq_valider   = new JButton("Valider les équipes");



			eq_ajouter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					String j1 = JOptionPane.showInputDialog("Nom du joueur 1 ?");
					String j2 = JOptionPane.showInputDialog("Nom du joueur 2 ?");
					int numEq = t.getNbEquipes() + 1;
					Equipe e = new Equipe(numEq,j1,j2);
					e.setIdTournoi(t.getId());
					t.ajouterEquipe(e);
					eq_valider.setEnabled(t.getNbEquipes() > 0 && t.getNbEquipes() % 2 == 0) ;
					eq_modele.fireTableDataChanged();
					if(t.getNbEquipes() > 0){
						eq_jt.getSelectionModel().setSelectionInterval(0, 0);
					}	
				}
			});
			eq_supprimer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if(Fenetre.this.eq_jt.getSelectedRow() != -1){
						t.supprimerEquipe(t.getEquipe(Fenetre.this.eq_jt.getSelectedRow()).getId());
					}

					eq_valider.setEnabled(t.getNbEquipes() > 0 && t.getNbEquipes() % 2 == 0) ;
					eq_modele.fireTableDataChanged();
					if(t.getNbEquipes() > 0){
						eq_jt.getSelectionModel().setSelectionInterval(0, 0);
					}					
				}
			});

			eq_valider.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					t.genererMatchs();
					t.setStatut(2);
					System.out.println();
					Fenetre.this.majboutons();
					Fenetre.this.tracer_tournoi_matchs();
				}
			});
			if(t.getNbEquipes() > 0){
				eq_jt.getSelectionModel().setSelectionInterval(0, 0);
			}
			bt.add(eq_ajouter);
			bt.add(eq_supprimer);
			bt.add(eq_valider);
			eq_p.add(bt);
			eq_p.add(new JLabel("Dans le cas de nombre d'équipes impair, créer une équipe virtuelle"));
		}
		if(t.getStatut() != 0){
			eq_ajouter.setEnabled(false);
			eq_supprimer.setEnabled(false);
			eq_valider.setEnabled(t.getStatut() == 1);
		}else{
			eq_ajouter.setEnabled(true);
			eq_supprimer.setEnabled(true);	
			eq_valider.setEnabled(t.getNbEquipes() > 0) ;
		}
		fen.show(panel, EQUIPES);
		
	}

	JTable                     tours_t;
	JScrollPane                tours_js;
	JPanel                     tours_p;
	BoxLayout                  tours_layout;
	JLabel                     tours_desc;
	
	JButton                    tours_ajouter;
	JButton                    tours_supprimer;
	JButton                    tours_rentrer;
	
	public void tracer_tours_tournoi(){
		if(t == null){
			return ;
		}
		majboutons();
		boolean peutajouter = true;
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;
		Vector<Vector<Match>> tours= t.getTours();
		System.out.println("tracer_tours_tournoi");
		System.out.println(tours);
		System.out.println(t.getNbTours());
		for(int i=0;i<t.getNbTours();i++){
			v = new Vector<Object>();
			v.add(i+1);
			v.add(tours.get(i).size());
			int termines = 0;
			for(int j=0;j<tours.get(i).size();j++){
				if(tours.get(i).get(j).getIsFinished()){
					termines++;
				}
			}
			v.add(termines);
			to.add(v);
		}
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro du tour");
		columnNames.add("Nombre de matchs");
		columnNames.add("Matchs joués");
		tours_t = new JTable(to,columnNames );
		if(tours_trace){
			tours_js.setViewportView(tours_t);
		}else{
			tours_trace  = true;
			tours_p      = new JPanel();
			tours_layout = new BoxLayout( tours_p, BoxLayout.Y_AXIS);
			tours_p.setLayout( tours_layout);
			tours_desc = new JLabel("Tours");
			tours_p.add(tours_desc);
			tours_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panel.add(tours_p, TOURS);
			tours_js = new JScrollPane();
			tours_js.setViewportView(tours_t);
			tours_p.add(tours_js);
			JPanel bt    = new JPanel();
			tours_ajouter   = new JButton("Ajouter un tour");
			tours_supprimer = new JButton("Supprimer le dernier tour");
			bt.add(tours_ajouter);
			bt.add(tours_supprimer);
			tours_p.add(bt);	
			tours_p.add(new JLabel("Pour pouvoir ajouter un tour, terminez tous les matchs du précédent."));
			tours_p.add(new JLabel("Le nombre maximum de tours est \"le nombre total d'équipes - 1\""));
			tours_ajouter.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					t.ajouterTour();
					Fenetre.this.tracer_tours_tournoi();								
				}
			});
			tours_supprimer.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					t.supprimerTour();
					Fenetre.this.tracer_tours_tournoi();				
				}
			});
		}
		if(to.size() == 0){
			tours_supprimer.setEnabled(false);
			tours_ajouter.setEnabled(true);
		}else{
			tours_supprimer.setEnabled( t.getNbTours() > 1);
			if(!peutajouter || t.getNbTours()  >= t.getNbEquipes()-1 ){
				tours_ajouter.setEnabled(false);
			}else
				tours_ajouter.setEnabled(true);
		}
		fen.show(panel, TOURS);
	}




	private AbstractTableModel match_modele;
    private JScrollPane        match_js;
    JTable                     match_jt;
    JPanel                     match_p;
    BoxLayout                  match_layout;
    JLabel                     match_desc;
    JPanel                     match_bas;
    JLabel                     match_statut;
    JButton                    match_valider;


	public void tracer_tournoi_matchs(){
		System.out.println("tracer_tournoi_matchs");
		System.out.println(t.getTours());
		if(t == null){
			return ;
		}
		if(t.getMatchs().size()==0){
			t.setMatchs(tdao.getMatchesByTournoiId(t.getId()));
		}
		majboutons();
		if(match_trace){
			//t.majMatch();
			match_modele.fireTableDataChanged();
			majStatutM();
		}else{
			match_trace = true;
			match_p      = new JPanel();
			match_layout = new BoxLayout(match_p, BoxLayout.Y_AXIS);
			match_p.setLayout(match_layout);
			match_desc = new JLabel("Matchs du tournoi");
			match_p.add(match_desc);
			match_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panel.add(match_p, MATCHS );
			match_modele = new AbstractTableModel() {
				private static final long serialVersionUID = 1L;
				@Override
				public Object getValueAt(int arg0, int arg1) {
					System.out.println("getvalueat");
					System.out.println("Tours 0");
					System.out.println(t.getTours().get(0));
					Object r = null;
					switch(arg1){
					case 0:
						r= t.getMatch(arg0).getTournamentNumber();
					break;
					case 1:
						r= t.getMatch(arg0).getTeam1();
					break;
					case 2:
						r= t.getMatch(arg0).getTeam2();
					break;
					case 3:
						r= t.getMatch(arg0).getScore1();
					break;
					case 4:
						r= t.getMatch(arg0).getScore2();
					break;
					}
					return r;
				}

				public String getColumnName(int col) {
				        if(col == 0){
				        	return "Tour";
				        }else if(col == 1){
				        	return "Équipe 1";
				        }else if(col == 2){
				        	return "Équipe 2";
				        }else if(col == 3){
				        	return "Score équipe 1";
				        }else if(col == 4){
				        	return "Score équipe 2";
				        }else{
				        	return "??";
				        }
				 }
				@Override
				public int getRowCount() {
					if(t == null)return 0;
					return t.getNbMatchs();
				}


				@Override
				public int getColumnCount() {
					return 5;
				}

				@Override
				public boolean isCellEditable(int x, int y){
					System.out.println("isCellEditable");
					System.out.println(x + " " + y);
					System.out.println(t.getMatch(x));
					boolean answer= (y>=3);
					System.out.println(answer);
					return answer;
				}

				@Override
				public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
					System.out.println("setValueAt");
					Match m = t.getMatch(rowIndex);
					if (columnIndex == 0) {
						return;
					} else if (columnIndex == 3 || columnIndex == 4) {
						try {
							String inputValue = JOptionPane.showInputDialog("Enter the score for Team " + (columnIndex == 3 ? "1" : "2"));
							if (inputValue != null) {
								int score = Integer.parseInt(inputValue);
								if (columnIndex == 3) {
									m.setScore1(score);
								} else {
									m.setScore2(score);
								}
								t.updateMatch(m);
								fireTableDataChanged();
								Fenetre.this.majStatutM();
								Fenetre.this.majboutons();
							}
						} catch (NumberFormatException e) {
							JOptionPane.showMessageDialog(null, "Invalid input. Please enter a valid integer.");
						}
					}
				}
			};
			match_jt = new JTable(match_modele);
			match_js = new JScrollPane(match_jt);
			match_p.add(match_js);
			System.out.println("truc2");
			match_bas = new JPanel();
			match_bas.add(match_statut = new JLabel("?? Matchs joués"));
			match_bas.add(match_valider = new JButton("Afficher les résultats"));
			match_valider.setEnabled(true);
			match_valider.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Fenetre.this.DisplayResults();
				}
			});
			match_p.add(match_bas);
			majStatutM();
		}
		fen.show(panel, MATCHS);
	}
    private JScrollPane        resultats_js;
    JTable                     resultats_jt;
    JPanel                     resultats_p;
    BoxLayout                  resultats_layout;
    JLabel                     resultats_desc;
    JPanel                     resultats_bas;
    JLabel                     resultats_statut;


	public void DisplayResults(){
		if(t == null){
			return ;
		}
		Vector< Vector<Object>> to =new Vector<Vector<Object>>();
		Vector<Object> v;		
		t.computeRank();
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("Numéro d'équipe");
		columnNames.add("Nom joueur 1");
		columnNames.add("Nom joueur 2");
		columnNames.add("Score");
		columnNames.add("Matchs gagnés");
		columnNames.add("Matchs joués");
		resultats_jt = new JTable(to,columnNames );		
		resultats_jt.setAutoCreateRowSorter(true);
		if(resultats_trace){
			resultats_js.setViewportView(resultats_jt);
		}else{
			resultats_trace = true;
			resultats_p      = new JPanel();
			resultats_layout = new BoxLayout(resultats_p, BoxLayout.Y_AXIS);
			resultats_p.setLayout(resultats_layout);
			resultats_desc = new JLabel("Résultats du tournoi");
			resultats_p.add(resultats_desc);
			resultats_p.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
			panel.add(resultats_p, RESULTATS );
			resultats_js = new JScrollPane(resultats_jt);
			resultats_p.add(resultats_js);
			resultats_bas = new JPanel();
			resultats_bas.add(resultats_statut = new JLabel("Gagnant:"));
			resultats_p.add(resultats_bas);
		}
		fen.show(panel, RESULTATS);
	}
	private void majStatutM(){
		int total=-1, termines=-1;
		try {
			ResultSet rs = s.executeQuery("Select count(*) as total, (Select count(*) from matchs m2  WHERE m2.id_tournoi = m.id_tournoi  AND m2.termine='oui' ) as termines from matchs m  WHERE m.id_tournoi=" + this.t.getId() +" GROUP by id_tournoi ;");
			if (!rs.next()) return;
			total    = rs.getInt(1);
			termines = rs.getInt(2);
		} catch (SQLException e) {
			e.printStackTrace();
			return ;
		}
		System.out.println("majStatutM");
		System.out.println(total + " " + termines);
		match_statut.setText(termines + "/" + total + " matchs terminés");
	}
}

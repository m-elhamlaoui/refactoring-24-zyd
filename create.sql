CREATE TABLE IF NOT EXISTS tournois (
  id_tournoi INT AUTO_INCREMENT PRIMARY KEY,
  nb_matchs INT,
  nom_tournoi VARCHAR(30),
  statut INT
);

CREATE TABLE IF NOT EXISTS equipes (
  id_equipe INT AUTO_INCREMENT PRIMARY KEY,
  num_equipe INT,
  id_tournoi INT,
  nom_j1 VARCHAR(30),
  nom_j2 VARCHAR(30),
  FOREIGN KEY (id_tournoi) REFERENCES tournois (id_tournoi) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS matchs (
  id_match INT AUTO_INCREMENT PRIMARY KEY,
  id_tournoi INT,
  num_tour INT,
  equipe1 INT,
  equipe2 INT,
  score1 INT,
  score2 INT,
  termine VARCHAR(3),
  FOREIGN KEY (id_tournoi) REFERENCES tournois (id_tournoi) ON DELETE CASCADE
);

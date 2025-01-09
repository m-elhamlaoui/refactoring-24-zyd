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
DROP TABLE IF EXISTS matchs;
CREATE TABLE matchs (
    id_match INT PRIMARY KEY AUTO_INCREMENT,
    id_tournoi INT NOT NULL,
    equipe1 INT NOT NULL,
    equipe2 INT NOT NULL,
    score1 INT DEFAULT 0,
    score2 INT DEFAULT 0,
    num_tour INT NOT NULL,
    termine VARCHAR(3) DEFAULT 'non',
    FOREIGN KEY (id_tournoi) REFERENCES tournois(id_tournoi),
    FOREIGN KEY (equipe1) REFERENCES equipes(id_equipe),
    FOREIGN KEY (equipe2) REFERENCES equipes(id_equipe),
    CHECK (score1 >= 0 AND score2 >= 0 AND (score1 + score2) <= 162)
);
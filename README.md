GestionApplication : gestion des incidents applicatifs

Application web de suivi des incidents sur les applications internes du CNAPS. Elle permet de signaler une application en panne, d'assigner la résolution à un développeur, de suivre l'avancement et de garder une trace de chaque action.

Projet réalisé avec Spring Boot (API REST), Angular (interface) et Oracle (base de données).

À quoi sert l'application ?

Quand une application du CNAPS tombe en panne ou se comporte mal, il faut que le problème soit signalé, pris en charge et résolu sans se perdre dans les e-mails et les appels. L'application organise ce cycle en trois étapes :

Signaler : un ticket décrit le problème (application concernée, description, priorité, capture d'écran).
Assigner : un administrateur transforme le ticket en tâche et la confie à un développeur.
Résoudre et tracer : le développeur fait avancer la tâche, ajoute un rapport, et chaque changement est enregistré dans l'historique.

Le tableau de bord donne à l'administrateur une vue d'ensemble : nombre de tickets par statut, tâches en cours, applications touchées.

Fonctionnalités
Domaine	Ce que l'application permet
Tickets	Créer un ticket avec priorité et image jointe, le filtrer par statut, le consulter, le modifier, changer son statut
Tâches	Créer une tâche liée à un ticket, l'assigner à un développeur, la réassigner (l'ancien développeur reste visible), la terminer avec un rapport, changer son statut avec un commentaire
Applications	Tenir le catalogue des applications (nom, description, version, statut, date de mise en production)
Utilisateurs et départements	Gérer les comptes (matricule, rôle, activation) et les départements
Historique	Voir chaque action sur un ticket ou une tâche : qui, quand, ancienne et nouvelle valeur
Tableaux de bord	Vue administrateur (statistiques par statut) et vue développeur (ses tâches, paginées)
Rôles
Rôle	Après connexion	Rôle dans le processus
ADMIN	/admin/dashboard	Supervise les tickets, crée et assigne les tâches, gère utilisateurs et applications
DEV	/dev/dashboard	Traite les tâches qui lui sont assignées et rédige le rapport
Autre (utilisateur)	/dashboard	Signale les incidents et suit ses tickets
Cycle de vie
Statuts d'un ticket : NOUVEAU, EN_COURS, RESOLU, FERME
Statuts d'une tâche : EN_ATTENTE, EN_COURS, TERMINEE
Priorités : BASSE, MOYENNE, HAUTE, CRITIQUE

Quand une tâche change de statut, le ticket associé est synchronisé et l'action est écrite dans l'historique.

Architecture
Navigateur ──► Frontend Angular (port 4200)
                    │  proxy /api
                    ▼
              Backend Spring Boot (port 8082)  ──►  Oracle XE (port 1521)
                    │
                    └── dossier uploads/ (images des tickets)
GestionAPP/
├── Backend/     # API REST Spring Boot 2.7 (Java 11), JPA / Hibernate, Oracle
│   └── src/main/java/mg/cnaps/gestion/
│       ├── controller/   # points d'entrée REST (/api/...)
│       ├── service/      # logique métier
│       ├── repository/   # accès aux données (Spring Data JPA)
│       ├── entity/       # Ticket, Tache, Application, Utilisateur, Departement, Historique
│       ├── dto/          # objets d'échange (connexion)
│       └── config/       # sécurité et CORS
├── Frontend/    # interface Angular 15 (Chart.js pour les graphiques)
└── BD/          # scripts SQL
Technologies
Backend : Java 11, Spring Boot 2.7.18, Spring Data JPA, Spring Security, driver Oracle ojdbc8
Frontend : Angular 15, TypeScript 4.9, RxJS, Chart.js
Base de données : Oracle Database XE
Installation
Prérequis
Java 11 ou plus et Maven
Node.js 16 ou plus (avec npm) et Angular CLI (npm install -g @angular/cli@15)
Oracle Database XE en marche sur 127.0.0.1:1521 (service XE)
1. Base de données

Crée l'utilisateur Oracle utilisé par l'application (en tant qu'administrateur de la base) :

sql
CREATE USER cnaps_user IDENTIFIED BY "ton_mot_de_passe";
GRANT CONNECT, RESOURCE, UNLIMITED TABLESPACE TO cnaps_user;

Les tables et les séquences sont créées automatiquement au premier démarrage du backend (spring.jpa.hibernate.ddl-auto=update). Les scripts du dossier BD/ sont des compléments (par exemple l'ajout de la colonne IMAGE_PATH sur TICKET).

Il faut ensuite créer au moins un compte administrateur en base (rôle ADMIN) pour pouvoir se connecter.

2. Backend

Dans Backend/src/main/resources/application.properties, adapte le mot de passe de la base, ou passe-le par une variable d'environnement :

properties
spring.datasource.password=${DB_PASSWORD}
powershell
cd Backend
$env:DB_PASSWORD="ton_mot_de_passe"
mvn spring-boot:run

L'API écoute sur http://localhost:8082/api. Les images envoyées sont stockées dans Backend/uploads/.

3. Frontend
powershell
cd Frontend
npm install
npm start

L'interface est disponible sur http://localhost:4200. Les appels /api sont redirigés vers le backend par proxy.conf.json.

API REST
Ressource	Routes principales
Authentification	POST /api/auth/login
Tickets	GET /api/tickets, /pagine, /stats-statut, /statut/{statut} ; POST, PUT /{id}, PUT /{id}/statut/{statut}, DELETE /{id}
Tâches	GET /api/taches, /developpeur/{id}, /ticket/{id}, /{id}/historique ; POST, PUT /{id}/statut, /terminer, /rapport, /reassigner, DELETE /{id}
Applications	GET, POST, PUT /{id}, DELETE /{id} sur /api/applications
Utilisateurs	GET /api/utilisateurs, /developpeurs ; POST, PUT, DELETE
Départements	GET, POST, PUT, DELETE sur /api/departements
Historique	GET /api/historiques/ticket/{idTicket}
Fichiers	POST /api/upload/image
Limites actuelles et pistes d'amélioration

Le projet est une base fonctionnelle. Avant un usage en production, voici les points à renforcer :

Sécurité des accès : toutes les routes de l'API sont actuellement ouvertes (permitAll). Il faudrait protéger les routes par rôle, par exemple avec des jetons JWT.
Mots de passe : ils sont comparés tels quels. À remplacer par un hachage (BCrypt via Spring Security).
Secrets : le mot de passe de la base ne doit pas être écrit en clair dans le dépôt. Utiliser des variables d'environnement.
Tests : ajouter des tests automatisés sur les services de tickets et de tâches.
Déploiement : ajouter un Dockerfile et un docker-compose.yml pour lancer tout le projet en une commande.
Auteur

Projet développé par Randrianarisoa Diary Mickael.

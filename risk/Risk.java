package risk;

import risk.vue.Fenetre;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.Collections;
import risk.controler.ConnexionDB;
import risk.model.*;

/**
 * 
 */
public class Risk {

	/**
	 * Classe principale du projet
	 * @param args
	 */
	public static void main(String[] args) {
		
        // Creation du plateau (objets continents et territoires)
        Monde monde = new Monde();
        ArrayList<Territoire> territoires = monde.getTerritoires();//              <======== ajouter la var territoires en input @raph 
        Fenetre vue = new Fenetre(territoires); // Crée une instance de Fenetre
        
        System.out.println("okk");
        
        // INITIALISATION D'UNE MANCHE
	    /** Deb - A supprimer apres test*/
		String dn = "2020-10-10";

		Joueur j1 = new Joueur("1", "AA", "aa", dn, monde.getMonde(), "jaunes");
		Joueur j2 = new Joueur("2", "BB", "bb", dn, monde.getMonde(), "rouges");
		Joueur j3 = new Joueur("3", "CC", "cc", dn, monde.getMonde(), "bleues");
		Joueur j4 = new Joueur("4", "DD", "dd", dn, monde.getMonde(), "noires");
		Joueur j5 = new Joueur("5", "EE", "ee", dn, monde.getMonde(), "violettes");
		Joueur j6 = new Joueur("6", "FF", "ff", dn, monde.getMonde(), "vertes");

		Joueur[] participants = {j1, j2, j3, j4, j5, j6};
		
		int nbUnitesAjout = 0; //Nombre d'unités a ajouté et enlever
	    /** Fin - A supprimer apres test*/
		
//		// Enregistrement des joueurs dans la base de données
//		String url = "jdbc:mysql://localhost:3306/risk";
//        String user = "root";
//        String password = "";
//
//        ConnexionDB dbRisk = new ConnexionDB(url, user, password);
//        
//        for (Joueur joueur : participants) {
//            if (!dbRisk.joueurExiste(joueur.getNom(), joueur.getPrenom())) {
//                dbRisk.insertJoueur(joueur.getNom(), joueur.getPrenom(), joueur.getDtNaissance());
//            } else {
//                System.out.println("Le joueur " + joueur.getNom() + " " + joueur.getPrenom() + " existe déjà.");
//            }
//        }
		
		//Mélanger les cartes de mission 打乱卡牌
		Mission m = new Mission();
		Collections.shuffle(m.getMissionListe(), new Random());
		
		//Distribution des cartes de mission aux joueurs 随机分配任务卡牌给玩家
		for(int i=0;i<participants.length;i++) {
			participants[i].DistribuerRandomMission(m.getMissionListe());
		}
		
		
		
		// DEBUT - ATTRIBUTION DES CARTES TERRITOIRES AUX JOUEURS
		// instances régiment pour infanterie, cavalerie et artillerie
		Regiment infanterie = new Regiment("Infanterie",1);
		Regiment cavalerie = new Regiment("Cavalerie",5);
		Regiment artilleire = new Regiment("Artillerie",10);
				
		List<String> typesRegiments = new ArrayList<>();
        typesRegiments.add("Infanterie");
        typesRegiments.add("Cavalerie");
        typesRegiments.add("Artillerie");
        
        List<Carte> cartes = new ArrayList<>();
        Random random = new Random();
        for (Territoire territoire : territoires) {
            int randRegiment = random.nextInt(typesRegiments.size());
            String typeRegiment = typesRegiments.get(randRegiment);
            cartes.add(new Carte(territoire, typeRegiment));
        }

        // afficher toutes les cartes
        for (Carte carte : cartes) {
            System.out.println("CARTE : Territoire : " + carte.getTerritoire() + ", Type de Régiment : " + carte.getTypeRegiment());
        }
        
        // déterminer le nombre de personnes à jouer et le nombre de cartes à distribuer par personne
        int nbParticipants = participants.length;
        System.out.println(nbParticipants);
        int carteJoueur = cartes.size() / nbParticipants;
        System.out.println(carteJoueur);
        // distribution des cartes
        Collections.shuffle(cartes);
        for (int i = 0; i < nbParticipants; i++) {
            List<Carte> mainJoueur = cartes.subList(i * carteJoueur, (i + 1) * carteJoueur);
            System.out.println("Joueur " + (i + 1) + " a reçu les cartes : " + mainJoueur);
            // Mise à jour des data 
            for (Carte carte : mainJoueur) {
            	participants[i].ajouterTerritoiresConquis(carte.getTerritoire());
            	participants[i].enleverNbRegimentsRestants(1);
            	carte.getTerritoire().ajouterNbRegiments(1);
            	carte.getTerritoire().setOccupant(participants[i]);
            	System.out.println("liste territoire :"+participants[i].getAllTerritoires());
            	System.out.println("Nb troupes a placer :"+participants[i].getNbRegimentsRestants());
            	System.out.println("Territoire occupé par :"+carte.getTerritoire().getOccupant()+" , nb de troupes : "+carte.getTerritoire().getNbRegiments());
            }
        }
		// DEBUT - ATTRIBUTION DES CARTES TERRITOIRES AUX JOUEURS
		
		// Creation d'une manche 
		Manche manche1 = new Manche(participants);
        System.out.println(manche1.toString());  
        
        // Determination du joueur qui commencera le tour et oganisation de l'ordre des joueurs dans la manche
        LancerDes des = new LancerDes();
        int resultatDes = des.lancerDes();
        System.out.println(resultatDes);        
        manche1.definirOrdreJoueur(resultatDes);
        System.out.println(manche1.toString());  
        System.out.println("--------------------------");  

        // DEBUT DE LA MANCHE
         boolean isFirstTour = true;
         boolean isWinner = false;
         boolean isObjectifCompleted = false;

         while (isWinner != true) {
        	 // POUR CHAQUE JOUEUR
        	 int indiceJoueur = 0;
        	 for (Joueur joueur : participants) {
	        	 System.out.println("Joueur " + joueur.getNom());
	        	 
	        	 // INSTANCIATION VARIABLE DE STOCKAGE
	        	 // Variable stockant si un nouveau territoire a ete conquis ou non au cours des attaques
	        	 Boolean isNouveauTerritoireConquis = false;
	        	 // Variable stockant les choix du joueur
	        	 String choixAction = "null"; // choix du joueur dans le menu (cf plus bas)
        		 String choixDeplacer = "null"; // variable pour stocker si il y a validation des modifications des troupes, si il veut ajouter ou encore retirer de nouvelles troupes 
        		 
	        	 // PLACEMENT DES 20 REGIMENTS POUR LE PREMIER TOUR (7 DEJA PLACES)
	        	 if (isFirstTour == true) {
        			 while (joueur.getNbRegimentsRestants() != 0) {
		        		 for (Territoire territoire : joueur.getAllTerritoires()) {
		        			 //Affichage de l'ajout d'unités sur un territoire retourne le nombre a ajouté
		        			 nbUnitesAjout = vue.premierTour(joueur, territoire);
		        			 territoire.ajouterNbRegiments(nbUnitesAjout);
		        			 joueur.enleverNbRegimentsRestants(nbUnitesAjout);
		        		 } 
		        	 }
		        	 if (joueur == participants[5]) {
		        		 isFirstTour = false;
		        	 }
	        	 }
	        	 // PROCESSUS NORMAL POUR LES AUTRES TOURS
	        	 else {
		        	 // MISE A JOUR DU NOMBRE DE REGIMENTS QUE PEUT POSITIONNER UN JOUEUR EN DEBUT DE TOUR
	        		 // => selon nombre de territoires occupés et de continents complets occupés
	        		 int nbRegimentAPlacer = joueur.calculerNbRegimentsAPlacer();
	        		 joueur.ajouterNbRegimentsRestants(nbRegimentAPlacer);
	        		 
		        	 // AJOUT NOUVEAUX REGIMENTS
		        	 while (joueur.getNbRegimentsRestants() != 0) {
		        		 
		        	 /** @Raph BESOIN - Methode retournant un territoire et une quantité pour choisir le territoire où ajouter les troupes
		        	  *  >> Rappel condition : territoire.occupant == null || territoire.occupant == joueur
		        	  *  Sinon retourner fenetre message erreur territoire deja occupé 
		        	  *  OU
		        	  *  Si galere je le bloque à la mano dans le main 
		        	  *  
		        	  *  Pour simplifier, l'algo c'est qu'à la phase d'ajout, il peuvent pas enlever quand ils posent ahah 
		        	  *  genre, il pose 2, puis 1 , quand il en a plus ca passe à autre chose :3
		        	  */	        	 
		    
		        	 Territoire destTerritoireAjout = monde.getTerritoires().get(0);                    //    <== changer valeur
		        	 int nbRegimentsAjoutes = 1;														//    <== changer valeur
		        	 System.out.println("*Debut* Territoire : "+destTerritoireAjout.getNom()+" - Nb : "+destTerritoireAjout.getNbRegiments());
		        	 System.out.println("*Debut* Joueur : "+joueur.getNom()+" - Nb : "+joueur.getNbRegimentsRestants());
		        	 destTerritoireAjout.ajouterNbRegiments(nbRegimentsAjoutes); // Ajout régiment au territoire
		        	 joueur.enleverNbRegimentsRestants(nbRegimentsAjoutes); // Retrait nb au nb de regiment à placer
		        	 System.out.println("*Fin* Territoire : "+destTerritoireAjout.getNom()+" - Nb : "+destTerritoireAjout.getNbRegiments());
		        	 System.out.println("*Fin* Joueur : "+joueur.getNom()+" - Nb : "+joueur.getNbRegimentsRestants());
		        	 }
		        	 
		        	 // CHOIX D ATTAQUER, MODIFIER SES TROUPES OU PASSER SON TOUR
		        	 // Tant que le tour du joueur n'est pas fini (continuer d'attaquer), on affiche la fenetre des choix 
		        	 while (choixAction == "Attaquer") {
			        	 /** @Raph BESOIN - Modifier ta methode choixJoueur pour qu'elle retourne le choix du joueur
			        	  *  Genre string "Attaquer", "Déplacer" ou "Passer tour"  par exeple 
			        	  *  OUTPUT : choixAction 
			        	  */
		        			
		        		 // LANCER UNE ATTAQUE
				            /** @Kun / @Yujie integrer dans le code : Si nouveau territoire conquis => isNouveauTerritoireConquis = true;*/
		        		 // Si le joueur clique sur l'option d'attaquer, il choisie le territoire d'attaque, de defense et le nombre de regiments pour attaquer
		        		 if (choixAction == "Attaquer") {
			        		   /** @Raph Demander choix pays attaquant, pays attaqué, nombre de troupes 
			        			*  OUTPUT : territoireAttaquant, territoireDefenseur, nbRegimentsAttaque
			        			*/
			        			Territoire territoireAttaquant = monde.getTerritoires().get(0);      // Données tests à mettre à jour avec output !!!!
			        			Territoire territoireDefenseur = monde.getTerritoires().get(1);
			        			int nbRegimentsAttaque = 2;
			        			int nbRegimentsRiposte = 1;
			        			// Creation du conflit 
			        			Conflit conflit = new Conflit(joueur, territoireAttaquant, territoireDefenseur, nbRegimentsAttaque);
			        			/** @Raph Demander defenseur nb de troupes riposte
			        			 *  Input : conflit.getBlablabla...
			        			 *  Output : nbRegimentsRiposte
			        			 */
			        			// Resultat du conflit
			        			conflit.resultatConflit(nbRegimentsRiposte);
		        		 }
		        		 // DEPLACER CERTAINS DE SES REGIMENTS
		        		 // Si le joueur clique sur l'option deplacer, il choisie autant de deplacement qu'il souhaite (tant que les territoires sont voisins)
		        		 // Lorsque qu'il valide les changements, son tour est automatiquement terminé
			        	 /** @Raph BESOIN - Modifier ta methode choixJoueur pour qu'elle retourne le choix du joueur
			        	  *  Genre string "Attaquer", "Déplacer" ou "Passer tour" c'est impec :)
			        	  */
		        		 else if (choixAction == "Déplacer") {
		        			while (choixDeplacer != "Valider") {
		        				/** @Raph choix joueur AJOUTER ou RETIRER (a chaque fin de choix si faisable) ou VALIDER
		        				 * => AJOUTER : territoire + nb regiments
		        				 * => RETIRER : territoire + nb regiments
		        				 *  >> Rappel conditions : 
		        				 *  - joueur occupe le territoire (cf Territoire.occupant == Joueur)
		        				 *  - pour le RETRAIT de troupes : nb de troupes retirées <= nb de troupes presentes (cd Territoire.nbRegiments)
		        				 *  - pour l'AJOUT : nb de troupes en stock >= nb de troupe à ajouter 
		        				 *  - pour VALIDER : nb de troupes en stock == 0 ? (franchement optionnel ahah, au pire le joueur prend de l'avance hein)
		        				 *  - pays voisins mais un peu relou :/
		        				 */ 
		        				
		        				// Choix d'AJOUTER des regiments
		        				if (choixDeplacer == "Ajouter") {
			        				Territoire territoireModifie = monde.getTerritoires().get(0);	// A modifier (pour test)
			        				int nbRegiments = 1;											// A modifier (pour test)
			        				
			        				joueur.enleverNbRegimentsRestants(nbRegiments);
			        				territoireModifie.ajouterNbRegiments(nbRegiments);
		        				}
			        			// Choix d'ENLEVER des regiments
		        				else if (choixDeplacer == "Retirer") {
			        				Territoire territoireModifie = monde.getTerritoires().get(0);	// A modifier (pour test)
			        				int nbRegiments = 1;											// A modifier (pour test)
			        				
			        				joueur.ajouterNbRegimentsRestants(nbRegiments);
			        				territoireModifie.enleverNbRegiments(nbRegiments);
			        				
			        				// Si le joueur a retiré toutes ses troupes d'un territoire on met à jour les data
			        				if (territoireModifie.getNbRegiments() == 0) {
			        					joueur.supprimerTerritoiresConquis(territoireModifie);
			        					territoireModifie.setOccupant(null);
			        				}
		        				}
		        			}
		        		 }
		        	 }
	        	 }
		         // Si le joueur a remporté tous les territoires
		         int nbTerritoiresConquis = (int) joueur.getAllTerritoires().size();
		         if (nbTerritoiresConquis == monde.getNbTerritoireTotal()) {
		        	 isWinner = true;	
		         }
		         // Si le joueur a complété son objectif
		         if (isObjectifCompleted == true) {
		        	 isWinner = true;	
		         }
		         // Si le joueur n'a plus de territoire il est eliminé 
		         if (nbTerritoiresConquis == 0) {
		        	int nouvelIndiceTableau = 0;
		        	// Copie des joueurs dans un tableau à jour (sans le joueur eliminé)
		        	Joueur[] participantsMaj = new Joueur[participants.length - 1];
		        	for (int k = 0; k < participants.length; k++) {
		        	    if (k != indiceJoueur) {
		        	    	participantsMaj[nouvelIndiceTableau] = participants[k];
		        	    	nouvelIndiceTableau = nouvelIndiceTableau+1;
		        	    }
		        	}
		        	// Mise à jour du tableau des participants
		        	participants = participantsMaj;
		         }	 
        	 isWinner = true;																	// A supprimer (for testing only)
        	 // Incrementation indice joueur de 1
        	 indiceJoueur = indiceJoueur+1;
        	 }
         }
        
        //TODO Pour l'actualisation de l'affichage du jouer à qui c'est le tour
        //vue.actualiserTour(tour);
         
        System.out.println("end");
	}
}


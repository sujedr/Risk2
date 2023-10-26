package risk.model;

import java.util.ArrayList;
import java.util.Collections;
import risk.model.HistoriqueJoueurs;

public class Archive_Conflit {
	private Territoire territoireAttaquant;
	private Territoire territoireDefenseur;
	private ArrayList<Integer> desAttaque;
	private ArrayList<Integer> desDefense;
	private int succes;
	
	public Archive_Conflit(Archive_Defense defense) {
		Archive_Attaque attaque=defense.getAttaque();
		Territoire territoireAttaquant=attaque.getTerritoireAttaquant();
		Territoire territoireDefenseur=attaque.getTerritoireDefenseur();
		ArrayList<Integer> desAttaque=attaque.getDesAttaque();
		ArrayList<Integer> desDefense=defense.getDesDefense();
		this.desAttaque=trierDesAttaqueAvecRetour(desAttaque);
		this.desDefense=trierDesAttaqueAvecRetour(desDefense);
		int nbSuivivant=ResultConflit(territoireAttaquant, territoireDefenseur, desAttaque, desDefense) ;
		succes=reglementDefaite(territoireAttaquant, territoireDefenseur,nbSuivivant);
	}
	
	private ArrayList<Integer> trierDesAttaqueAvecRetour(ArrayList<Integer> desAttaque) {
	    // 创建一个新的ArrayList并进行排序
	    ArrayList<Integer> sortedDesAttaque = new ArrayList<>(desAttaque);
	    Collections.sort(sortedDesAttaque, Collections.reverseOrder());
	    return sortedDesAttaque;
	}
	private int ResultConflit(Territoire territoireAttaquant, Territoire territoireDefenseur, ArrayList<Integer> desAttaque, ArrayList<Integer> desDefense) {
	    // 循环比较desDefense的长度次
	    int iterations = desDefense.size();
	    int nbSurvivant = desAttaque.size();
	    for (int i = 0; i < iterations; i++) {
	        int attaque = desAttaque.get(i);
	        int defense = desDefense.get(i);

	        // 如果desAttaque的值大于desDefense，则territoireDefenseur上的兵数减去1
	        if (attaque > defense) {
	            int nbRegimentsDefenseur = territoireDefenseur.getNbRegiments();
	            if (nbRegimentsDefenseur > 0) {
	                territoireDefenseur.setNbRegiments(nbRegimentsDefenseur - 1);
	            }
	        } else {
	            // 否则territoireAttaquant上的兵数减去1
	            int nbRegimentsAttaquant = territoireAttaquant.getNbRegiments();
	            if (nbRegimentsAttaquant > 0) {
	                territoireAttaquant.setNbRegiments(nbRegimentsAttaquant - 1);
	            }
	            nbSurvivant=nbSurvivant-1;
	        }
	    }
	    return nbSurvivant;
	}
	private int reglementDefaite(Territoire territoireAttaquant, Territoire territoireDefenseur,int nbSuivivant) {
		if(territoireDefenseur.getNbRegiments()==0) {
			territoireDefenseur.getOccupant().supprimerTerritoiresConquis(territoireDefenseur);
			territoireAttaquant.getOccupant().ajouterTerritoiresConquis(territoireDefenseur);
			territoireDefenseur.setOccupant(territoireAttaquant.getOccupant());
			territoireDefenseur.setNbRegiments(nbSuivivant);
			//TODO commentaire car erreur
			//elimination (territoireAttaquant, territoireDefenseur);
			return 1;
		}
		else {
			return 0;
		}
	}
	//TODO j'ai mis en commentaire car ça fait des erreurs
	/*
	private void elimination (Territoire territoireAttaquant, Territoire territoireDefenseur) {
		ArrayList <Territoire> tousTerritoireDefenseur=new ArrayList <Territoire>();
		tousTerritoireDefenseur=territoireDefenseur.getOccupant().getAllTerritoires();
		ArrayList <Territoire> tousterritoireAttaquant=new ArrayList <Territoire>();
		tousterritoireAttaquant=territoireAttaquant.getOccupant().getAllTerritoires();
		if(tousTerritoireDefenseur.size()==0) {
			//???????
			MaxValue=findMaxValue();
			historiqueJoueurs.put(MaxValue,territoireDefenseur.getOccupant());
			if(historiqueJoueurs.getClassementLength() ==6) {
				manche.setEstTerminee(true);
			}
		}
		if(tousterritoireAttaquant.size()==0) {
			//???????
			MaxValue=findMaxValue();
			historiqueJoueurs.put(MaxValue,territoireAttaquant.getOccupant());
			if(historiqueJoueurs.getClassementLength() ==6) {
				manche.setEstTerminee(true);
			}
		}
	} */

}

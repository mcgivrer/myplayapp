/**
 * Projet: myplayapp
 * Gestion de la bibliothèque des jeux
 */
package controllers;

import models.Game;
import models.User;
import org.junit.Before;
import play.data.validation.Valid;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Classe de gestion de la bibliothèque des jeux video commune aux utilisateurs.
 * @author frederic
 */
@With(Security.class)
public class GameLibrary extends Controller {
	/**
	 * récupération de l'utilisateur connecté.
	 */
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byUsername", Security.connected()).first();
            renderArgs.put("user", user);
        }
    }


    /**
     * Add a new game to the collection
     */
    public static void addGame(){
    	render();
    }
    
    /**
     * Save the input game to db.
     * @param game
     */
    public static void save(@Valid Game game){
    	game.save();
    	renderTemplate("GameLibrary/addGame.html");
    }

		/*
		 * Export la liste des jeux affichés dans le format souhaité
		 */
		public static void exportGamesList(String format){
			renderArgs.put("format", format);
			renderTemplate("Application/index.html");
		}
	
}
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
 * Classe de gestion de la bibliothèque des jeux video.
 * 
 * @author frederic
 */
@With(Security.class)
public class GameLibrary extends Controller {

	/**
	 * récupération de l'utilisateur connecté.
	 */
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byUsername", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	/**
	 * Add a new game to the collection
	 */
	@Check("USER")
	public static void addGame() {
		User user = (User) renderArgs.get("user");
		render(user);
	}

	/**
	 * Save the input game to db.
	 * 
	 * @param game
	 */
	public static void save(@Valid Game game) {
		User user = (User) renderArgs.get("user");
		game.save();
		renderTemplate("GameLibrary/addGame.html",user,game);
	}

}
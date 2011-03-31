/**
 * Project myplayapp tutorial
 */
package controllers;

import java.util.List;

import models.Game;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
 

/**
 * Contrôleur de la zone de sécurité
 * @author McGivrer
 */
@With(Secure.class)
public class Administration extends Controller {

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
	 * Login d'un utilisateur.
	 */
	public static void login(){
		renderTemplate("Application/index.html");
	}

	/**
	 * Page d'accueil du module d'administration.
	 */
	public static void index(){
		User user = (User)renderArgs.get("user");
		List<Game> games = null;
		if(user.isRole("ADMINISTRATOR")){
			games = Game.find(
					"select g from Game g "
							//+ "where " 
							+ "order by platform asc, title asc")
					.fetch(0,20);
		}else{
			games = Game.find(
					"select g from Game g " + "where " 
							/*+ "g.publish=true and"*/
							+ "g.author = ? "
							+ "order by platform asc, title asc", user)
					.fetch(0,20);
		}
		renderTemplate("admin/index.html",games,user);
	}
}

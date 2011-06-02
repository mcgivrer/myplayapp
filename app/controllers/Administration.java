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
		// Constitution de la liste des plateformes distinctes
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		//Constitution de la liste des jeux
		List<Game> games = null;
		if(user.isRole("ADMINISTRATOR")){
			games = Game.find(
					"select g from Game g "
							//+ "where " 
							+ "order by title asc")
					.fetch(0,20);
		}else{
			games = Game.find(
					"select g from Game g " + "where " 
							/*+ "g.publish=true and"*/
							+ "g.author = ? "
							+ "order by title asc", user)
					.fetch(0,20);
		}
		renderTemplate("admin/index.html",games,user,platforms);
	}
	
	/**
	 * Filtrage de la liste de jeux sur une <code>platform</code>.
	 * 
	 * @param platform
	 *            code de la platform sur laquelle filtrer la liste des jeux.
	 */
	public static void filterByPlatform(String platform) {
		session.put("platform", platform);
		
		User user = (User) renderArgs.get("user");
		List<Game> platforms = Game.find( "select distinct g.platform from Game g order by g.platform").fetch();
		List<Game> games = null;
		if(platform.equals("all")){
			games = Game.find(
					"select g from Game g order by title asc").fetch(10);
		}else{
			games = Game.find(
					"select g from Game g where g.platform=? order by title asc",platform).fetch(10);
		}
		renderTemplate("admin/gamelistsearch.html",games, platforms, user);
	}

	/**
	 * Recherche dans la liste de jeux de l'utilisateur connecté des jeux
	 * contenant la chaîne <code>search</code> dans le titre du jeu. Se base
	 * également sur la plateforme sélectionnée et sur l'utilisateur connecté.
	 * 
	 * @param search
	 *            nom ou partie du nom de jeu à rechercher
	 */
	public static void findByGameTitle(String search) {
		// récupération de l'utilisateur connecté
		User user = (User) renderArgs.get("user");
		// recupération de la plateforme (si présente)
		String platform = (String) session.get("platform");
		// Constitution de la liste des plateformes distinctes
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		// recherche des jeux correspondant à game.title=%search% et
		// game.platform=platefom
		List<Game> games = Game.find(
				"select g from Game g " + "where lower(g.title) like ? "
						+ "order by g.title ",
				"%" + search.toLowerCase() + "%").fetch();
		// rendu de la page
		renderTemplate("admin/gamelistsearch.html", games, user, platforms, platform);
	}
}

package controllers;

import static play.modules.excel.Excel.renderExcel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.i18n.Messages;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Controleur principale de notre application. Gère les liste de jeux et
 * platformes à afficher sur la page d'accueil.
 * 
 * @author McGivrer
 */
@With(Security.class)
public class Application extends Controller {

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
	 * Affichage de la page d'accueil.
	 */
	public static void index() {
		User user = (User) renderArgs.get("user");
		String platform = (String) session.get("filterPlatform");
		renderArgs.put("filterPlatform",
				Messages.get("home.platforms.filter.showAll.pageTitle"));
		if (user != null) {
			Logger.debug(user.username + ": " + user.firstname + " "
					+ user.lastname);
			List<Game> platforms = Game
					.find("select distinct g.platform from Game g where g.author = ? order by g.platform",
							user).fetch();
			List<Game> games = null;
			if (platform != null) {
				games = Game.find(
						"select g from Game g " + "where " + "g.publish=true "
								+ "and g.author = ? " + "and g.platform = ? "
								+ "order by platform asc, title asc", user,
						platform).fetch();
			} else {
				games = Game.find(
						"select g from Game g " + "where " + "g.publish=true "
								+ "and g.author = ? "
								+ "order by platform asc, title asc", user)
						.fetch();
			}
			Logger.debug(
					"Number of retrieved games: %d, Number of platforme: %d",
					games.size(), platforms.size());
			render(games, platforms);
		} else {
			render();
		}
	}

	/**
	 * Affichage des détails du jeu dont l'<code>id</code> est passé.
	 * 
	 * @param id
	 *            identifiant du jeu à visualiser.
	 */
	public static void details(Long id) {
		User user = (User) renderArgs.get("user");
		List<Game> platforms = Game
				.find("select distinct g.platform from Game g where g.author = ? order by g.platform",
						user).fetch();
		List<Game> games = Game
				.find("select g from Game g where g.publish=true and g.author = ? order by platform, title",
						user).fetch();
		Game game = Game.findById(id);
		Logger.debug("Game details displayed for '%s/%s'", game.platform,
				game.title);
		render(game, games, platforms);
	}

	/**
	 * Filtrage de la liste de jeux sur une <code>platform</code>.
	 * 
	 * @param platform
	 *            code de la platform sur laquelle filtrer la liste des jeux.
	 */
	public static void filterByPlatform(String platform) {
		User user = (User) renderArgs.get("user");
		renderArgs.put("filterPlatform", platform);

		List<Game> games = null;
		if (platform.equals("*")) {
			session.remove("filterPlatform");
			games = Game
					.find("select g from Game g where g.publish=true and g.author=? order by title asc",
							user).fetch();
		} else {
			session.put("filterPlatform", platform);
			games = Game
					.find("select g from Game g where g.publish=true and g.author=? and g.platform=? order by title asc",
							user, platform).fetch();
		}
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		render(games, platforms);
	}


	/**
	 * Export la liste des jeux affichés dans le format souhaité
	 */
	public static void exportGamesList(){
		// Récupération de l'utilisateur connecté.
		User user = (User) renderArgs.get("user");
		Logger.debug("Export game list for user "+user.username);
		// Si un utilisateur connecté.
		if (user != null) {
			Logger.debug(user.username + ": " + user.firstname + " " + user.lastname);
			// Création de la liste des jeux
			List<Game> games = Game.find("select g from Game g "
								+"where "
								+"g.publish=true "
								+"and g.author = ? "
								+"order by platform asc, title asc", user).fetch();
			Logger.debug("Number of exported games: %d", games.size());
			// Preparation de la date de génération
			Date dateOfTheDay = new Date();
			DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
			String date = df.format(dateOfTheDay);
			// Définition du nom de fichier (pour FF)
			renderArgs.put("filename","mygames.xls");
			renderExcel(user, games, date);
		}
	}
	
	/**
	 * Recherche dans la liste de jeux de l'utilisateur connecté des jeux
	 * contenant la chaîne <code>search</code> dans le titre du jeu. Se base
	 * également sur la plateforme sélectionnée et sur l'utilisateur connecté.
	 * 
	 * @param search
	 *            nom ou partie du nom de jeu à rechercher
	 */
	public static void filterByGameTitle(String search) {
		renderArgs.put("search", search);
		// récupération de l'utilisateur connecté
		User user = (User) renderArgs.get("user");
		// recupération de la plateforme (si présente)
		//String platform = (String) renderArgs.get("filterPlatform");
		// Constitution de la liste des plateformes distinctes
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		// recherche des jeux correspondant à game.title=%search% et
		// game.platform=platefom
		List<Game> games = Game.find(
				"select g from Game g " + "where lower(g.title) like ? "
						+ "and g.author=? " + "and g.publish=true "
						+ "order by g.title ",
				"%" + search.toLowerCase() + "%", user).fetch();
		// rendu de la page
		renderTemplate("Application/index.html", games, platforms);
	}

}

package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Game;
import models.GameList;
import models.GameListItem;
import models.User;
import play.Logger;
import play.Play;
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
		List<Game> games = null;
		List<Game> platforms = null;
		List<GameList> gameslists =null;

		// Récupération de l'utilisateur connecté
		User user = (User) renderArgs.get("user");
		
		// Recupération de la plateforme filtrée
		String platform = (String) session.get("filterPlatform");
		renderArgs.put("filterPlatform",
				Messages.get("home.platforms.filter.showAll.pageTitle"));

		if (user != null && !user.username.equals("")) {
			// Un utilisateur est connecté ?
			Logger.debug(user.username + ": " + user.firstname + " "
					+ user.lastname);

			// On récupère également la liste de ses listes de jeux.
			gameslists= GameList
					.find("select gl from GameList gl where gl.user = ? order by gl.title asc",
							user).fetch();

			// Récupération de l'ID la liste sélectionnée si il existe en session
			String gameListId = (String) session.get("gameListId");
			
			// on récupère une liste par défaut sélectionnée, ou la première de la liste.
			GameList gameList = (gameListId!=null ? (GameList)GameList.findById(Long.parseLong(gameListId)) : (GameList)gameslists.get(0));
			Logger.debug(
					"Selected game list: %s",gameList.title);			
			
			// on récupère les plateformes sur lesquels ses jeux tournent
			platforms = Game
					.find("select distinct g.platform from Game g where g.author = ? order by g.platform",
							user).fetch();
			platform = ((String) session.get("filterPlatform")!=null?(String) session.get("filterPlatform"):"*");
			// et la liste de ses jeux
			if(platform!=null){
				games = GameLibrary.findGamesForUserAndPlatform(platform, user);
			}else{
				games = GameLibrary.getGamesFromGameList(gameList.id, user);
			}
			Logger.debug("Number of retrieved games: %d, Number of platforme: %d",games.size(), platforms.size());
			
			// rendu de la page pour l'utilisateur connecté
			render(games, platforms, platform, user, gameslists, gameList);
		} else {
			// Aucun utilisateur connecté, on affiche les derniers jeux ajoutés.
			platforms = Game
					.find("select distinct g.platform from Game g order by g.platform").fetch();
			games = Game
					.find("select g from Game g where g.publish=true order by createdAt desc, title asc")
					.fetch(0, Integer.parseInt(Play.configuration.getProperty("home.default.game.list.size", "6")));
			
			// rendu de la page par défaut.
			render(games, platforms);
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
		List<Game> games = GameLibrary.findGamesForUserAndPlatform("*", user);
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
		renderArgs.put("filteredPlatform", platform);
		List<Game> games = GameLibrary.findGamesForUserAndPlatform(platform, user);
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		List<GameList> gameslists = new ArrayList<GameList>();
		if (user != null) {
			gameslists = GameList
					.find("select gl from GameList gl where gl.user = ? order by gl.title asc",
							user).fetch();
		}
		render(games, platforms, user, gameslists, platform);
	}

	/**
	 * Filtrage de la liste de jeux sur une <code>GameList.id</code>.
	 * 
	 * @param gameListId
	 *            code de la platform sur laquelle filtrer la liste des jeux.
	 */
	public static void filterByGameList(Long gameListId) {
		User user = (User) renderArgs.get("user");
		String platform = session.get("filteredPlatform");
		List<Game> games = GameLibrary.getGamesFromGameList(gameListId, user);
		GameList gameList = GameList.findById(gameListId);
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		List<GameList> gameslists = GameList
				.find("select gl from GameList gl where gl.user = ? order by gl.title asc",
						user).fetch();
		render(games, platforms, platform, user, gameslists, gameList);
	}

	
	/**
	 * Test de rendu user pour chapitre 9.1 - Création d'un tag.
	 */
	public static void userDetails() {
		User userConnected = (User) renderArgs.get("user");
		render(userConnected);
	}
}

package controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import models.Game;
import models.GameList;
import models.GameListItem;
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
			List<Game> games = getGames(platform,user);
			List<GameList> gameslists= GameList.find("select gl from GameList gl where gl.user = ? order by gl.title asc", user).fetch();
			
			Logger.debug(
					"Number of retrieved games: %d, Number of platforme: %d",
					games.size(), platforms.size());
			render(games, platforms,gameslists);
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
		List<Game> games = getGames("*",user);
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
		
		List<Game> games = getGames(platform,user);
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		List<GameList> gameslists= GameList.find("select gl from GameList gl where gl.user = ? order by gl.title asc", user).fetch();
		render(games, platforms, user, gameslists);
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
		List<Game> games = getGamesFromGameList(gameListId,user);
		GameList gamelist = GameList.findById(gameListId);
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		List<GameList> gameslists= GameList.find("select gl from GameList gl where gl.user = ? order by gl.title asc", user).fetch();
		render(games, platforms, user, gameslists, gamelist);
	}
	
	
	/**
	 * Constitue la liste des jeux d'une list <code>gameListId</code> d'un utilisateur <code>user</code>
	 * @param gameListId
	 * @param user
	 * @return
	 */
	private static List<Game> getGamesFromGameList(Long gameListId, User user) {
		GameList gameList = GameList.findById(gameListId);
		List<GameListItem> gamesItems = gameList.games;
		List<Game> games = new ArrayList<Game>();
		for (GameListItem gameItem : gamesItems) {
			games.add(gameItem.game);
		}
		return games;
	}

	/**
	 * Usage interne : constitution de la liste des jeux.
	 * @param platform
	 *            code de la platform sur laquelle filtrer la liste des jeux.
	 * @param user
	 *            Utilisateur connecté
	 */
	public static List<Game> getGames(String platform, User user){
		renderArgs.put("filterPlatform", platform);

		List<Game> games = null;
		if (platform==null || platform.equals("*")) {
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
		return games;
	}
	
	/**
	 * Test de rendu user pour chapitre 9.1 - Création d'un tag. 
	 */
	public static void userDetails(){
		User userConnected = (User) renderArgs.get("user");
		render(userConnected);
	}
}

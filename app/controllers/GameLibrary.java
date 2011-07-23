/**
 * Projet: myplayapp
 * Gestion de la bibliothèque des jeux
 */
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import models.Game;
import models.GameList;
import models.GameListItem;
import models.User;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import shared.ThumbnailGenerator;

import com.mysql.jdbc.Messages;

/**
 * Classe de gestion de la bibliothèque des jeux vidéo ainsi que des listes
 * de jeux des utilisateurs.
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
	 * Ajout d'un jeu à la collection
	 */
	//@Check("USER,MODERATOR,ADMINISTRATOR")
	public static void addGame() {
		User user = (User) renderArgs.get("user");
		render(user);
	}

	/**
	 * Sauve vers la base de données les données saisies pour le jeu.
	 * 
	 * @param game
	 */
	public static void save(@Valid Game game) {
		User user = (User) renderArgs.get("user");
		game.save();
		renderTemplate("GameLibrary/addGame.html", user, game);
	}

	/**
	 * Recherche dans la liste de jeux de l'utilisateur connecté des jeux
	 * contenant la chaîne <code>search</code> dans le titre du jeu. Se base
	 * également sur la plate-forme sélectionnée et sur l'utilisateur connecté.
	 * 
	 * @param search
	 *            nom ou partie du nom de jeu à rechercher
	 */
	public static void findByGameTitle(String search) {
		renderArgs.put("search", search);
		// récupération de l'utilisateur connecté
		User user = (User) renderArgs.get("user");
		// TODO Récupération de la plate-forme (si présente)
		// String platform = (String) renderArgs.get("filterPlatform");
		// Constitution de la liste des plateformes distinctes
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		// recherche des jeux correspondant à game.title=%search% et
		List<Game> games = Game.find(
				"select g from Game g " + "where lower(g.title) like ? "
						+ "and g.author=? " + "and g.publish=true "
						+ "order by g.platform, g.title ",
				"%" + search.toLowerCase().trim() + "%", user).fetch();
		// rendu de la page
		renderTemplate("GameLibrary/search.html", games, user, platforms);
	}
	
	/**
	 * Recherche sur le titre des jeux et retourne le résulats au format JSON 
	 * @see GameLibrary#retrieveGamesWhereTitleLike(User, String)
	 * @param search
	 */
	public static void ajaxSearchGameTitle(String search){
		// récupération de l'utilisateur connecté
		User user = (User) renderArgs.get("user");
		List<Game> games = GameLibrary.retrieveGamesWhereTitleLike(user, search);
		renderJSON(games);
	}

	
	/**
	 * Recherche dans la liste de jeux de l'utilisateur connecté des jeux
	 * contenant la chaîne <code>search</code> dans le titre du jeu. 
	 * Se base sur l'utilisateur <code>user</code> connecté.
	 * @param user
	 * @param search
	 * @return List<Game>
	 */
	private static List<Game> retrieveGamesWhereTitleLike(User user, String search){
		List<Game> games =null;
		// recherche des jeux correspondant à game.title=%search%
		
		//L'utilisateur est-il connecté ?
		if(user!=null){
			// oui, alors, on cherche dans ses listes de jeux
			games = Game.find(
				"select g.game from GameListItem g where lower(g.game.title) like ? "
						+ "and g.game.author=? and g.game.publish=true "
						+ "order by g.game.platform, g.game.title ",
				"%" + search.toLowerCase() + "%", user).fetch();
		}else{
			// non ?  alors on cherche dans les fiches de jeux.
			games = Game.find(
					"select g from Game g where lower(g.title) like ? "
							+ "and g.publish=true "
							+ "order by g.platform, g.title ",
					"%" + search.toLowerCase() + "%").fetch();
		}
		return games;
	}
	
	
	/**
	 * Recherche dans la liste de jeux de l'utilisateur connecté des jeux
	 * contenant la chaîne <code>search</code> dans le titre du jeu. Se base
	 * également sur la plateforme sélectionnée et sur l'utilisateur connecté.
	 * 
	 * @param search
	 *            nom ou partie du nom de jeu à rechercher
	 */
	public static void findByGameId(Long id) {
		Game game = Game.findById(id);
		// rendu de la page
		renderTemplate("Application/show.html", game);
	}
	


	/**
	 * Render Picture <code>type</code>/<code>number</code> for game
	 * <code>id</code>
	 * 
	 * @param id
	 *            game unique id
	 * @param type
	 *            type of picture (cover, screenshot, art)
	 * @param number
	 *            image number for the selected type
	 */
	public static void getPicture(Long id, String type, Long number, String size) {
		Game game = Game.findById(id);
		FileInputStream output;
		try {
			File of = Play.getFile(GameLibrary.getPicturePath(
					"/public/images/games/", game, type, number, size));
			
			if(!of.exists()){
				ThumbnailGenerator th = ThumbnailGenerator.getInstance();
				try {
					th.create(
							Play.getFile(GameLibrary.getPicturePath("/public/images/games/", game, type, number, "")),
							Play.getFile(GameLibrary.getPicturePath("/public/images/games/", game, type, number, size)),
							size);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			output = new FileInputStream(of);
			renderBinary(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Compose Base path for game pictures.
	 * 
	 * @param basePath
	 *            Chemin de base pour les images de jeux
	 * @param game
	 *            Jeu concerné
	 * @return
	 */
	public static String getGamePicturesPath(String basePath, Game game) {
		return basePath + File.separator + game.id + File.separator;
	}

	/**
	 * Compose Picture filename and path, based on <code>basePath</code>.
	 * 
	 * @param basePath
	 *            Chemin de base pour les images de jeux
	 * @param game
	 *            Jeu concerné
	 * @param type
	 *            type d'image (cover, screenshot, artwork)
	 * @param number
	 *            numéro d'image (si necessaire, sinon, null ou 0
	 * @param size
	 *            Taille cible de l'image (pour retourner la bonne taille de
	 *            thumbnail.
	 * @return
	 */
	public static String getPicturePath(String basePath, Game game,
			String type, Long number, String size) {
		return GameLibrary.getGamePicturesPath(basePath, game)
				+ GameLibrary.getPictureName(game, type, number, size);
	}

	/**
	 * Get Picture name based on Game information, picture type , picture number
	 * and target size.
	 * 
	 * @param game
	 *            Jeu concerné
	 * @param type
	 *            type d'image (cover, screenshot, artwork)
	 * @param number
	 *            numéro d'image (si nécessaire, sinon null ou 0)
	 * @param size
	 *            Taille cible de l'image (pour retourner la bonne taille de
	 *            thumbnail.
	 * @return
	 */
	public static String getPictureName(Game game, String type, Long number,
			String size) {
		return type + "/" + game.platform.toLowerCase() + "-"
				+ WS.encode(game.title.toLowerCase()) + "-" + type
				+ ((number == null) || (number == Long.valueOf(0L)) ? "" :"-" + number )
				+ (size != null && !size.equals("") ? "." + size : "") + ".jpg";
	}

	/**
	 * Upload a <code>picture</code> into a <code>type</code> for the game
	 * <code>id</code>.
	 * 
	 * @param id
	 *            game unique identifier
	 * @param type
	 *            type of picture to be uploaded
	 * @param picture
	 *            file data to store.
	 */
	public static void upload(Long id, String type, File picture) {
		// User connectedUser = (User)renderArgs.get("user");
		Game game = Game.findById(id);
		Long number = new Long(1);
		// remove existing file
		File remove = Play.getFile(GameLibrary.getPicturePath(
				"/public/images/games", game, type, number, null));
		if (remove.exists()) {
			remove.delete();
		}
		// move new file to right place
		if (!Play.getFile(
				GameLibrary.getGamePicturesPath("/public/images/games", game))
				.exists()) {
			Play.getFile(
					GameLibrary.getGamePicturesPath("/public/images/games",
							game)).mkdir();
		}
		if (!Play.getFile(
				GameLibrary.getGamePicturesPath("/public/images/games", game)
						+ File.separator + type).exists()) {
			Play.getFile(
					GameLibrary.getGamePicturesPath("/public/images/games",
							game) + File.separator + type).mkdir();
		}
		File output = Play.getFile(GameLibrary.getGamePicturesPath(
				"/public/images/games", game)
				+ GameLibrary.getPictureName(game, type, number, null));
		picture.renameTo(output);
		// update game model and save
		game.cover = picture.getName();
		game.save();
		// render page
		render("GameLibrary/addGame.html");
	}
	
	/**
	 * Affiche la fiche du jeu dont l'<code>id</code> est passé en paramètre.
	 * @param id
	 *           Identifiant unique du jeu à afficher.
	 */
	//@Check("USER,ADMINISTRATOR,MODERATOR")
	public static void show(Long id){
		User user = (User) renderArgs.get("user");
		Game game = Game.findById(id);
		render("GameLibrary/show.html",game,user);
	}
	
	/**
	 * Création d'une nouvelle liste.
	 */
	public static void createList(){
		User userConnected = (User)renderArgs.get("user");
		GameList gameList = new GameList("Default","",userConnected,null);

		flash("gameList.title",gameList.title);
	    flash("gameList.description",gameList.description);
		
		render(gameList,userConnected);
	}
	
	/**
	 * Sauvegarde de la liste de jeux nouvellement créée
	 * @param gameList
	 */
	public static void saveGameList(@Valid GameList gameList){
		User userConnected = (User)renderArgs.get("user");
		GameList other = (GameList)GameList.find("byTitleAndUser", gameList.title, userConnected).first();
		if(other!=null && gameList.id==other.id){
			validation.addError("gameList.title", "games.gamelist.error.create.title.already.exists");
		}else{		
			gameList.games = new ArrayList<GameListItem>();
			gameList.user = userConnected;
			gameList.save();
			flash("msg",Messages.getString("gamelist.create.message.gamelist.added"));
			renderArgs.put("gameListId",gameList.id.toString());
		}
		
	    flash("gameList.title",gameList.title);
	    flash("gameList.description",gameList.description);
	    
	    Application.index();
	    
	}
	
	/**
	 * Suppression de la liste de jeux.
	 * @param id
	 */
	public static void deleteGameList(Long id) {
	    Application.index();
	}
	
	/**
	 * Constitue la liste des jeux d'une list <code>gameListId</code> d'un
	 * utilisateur <code>user</code>
	 * 
	 * @param gameListId
	 * @param user
	 * @return
	 */
	public static List<Game> getGamesFromGameList(Long gameListId, User user) {
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
	 * 
	 * @param platform
	 *            code de la platform sur laquelle filtrer la liste des jeux.
	 * @param user
	 *            Utilisateur connecté
	 */
	public static List<Game> findGamesForUserAndPlatform(String platform, User user) {
		renderArgs.put("filterPlatform", platform);

		List<GameList > lgames = null;
		List<Game> games = new ArrayList<Game>();
		if (user != null) {
			lgames = GameList.find("select gl from GameList gl where gl.user=? order by title asc",
								user).fetch();
			
			if (platform == null || platform.equals("*")) {
				session.remove("filterPlatform");
			} else {
				session.put("filterPlatform", platform);
			}
			games = getListOfGamesFromGameLists(lgames, user, platform);
		} else {
			if(platform == null || platform.equals("*")){
				games = Game
				.find("select g from Game g where g.publish=true order by title asc").fetch(0,6);
			}else{
				games = Game
				.find("select g from Game g where g.publish=true and g.platform=? order by title asc",
						platform).fetch(0,6);
			}
		}
		return games;
	}

	/**
	 * Recompose la liste des jeux de la liste d'après la(les) listes <code>lgames</code>) de 
	 * l'utilisateur <code>user</code> pour la <code>platform</code> passée.
	 * @param lgames
	 * @return
	 */
	public static List<Game> getListOfGamesFromGameLists(List<GameList> lgames, User user, String platform){
		List<Game> games = new ArrayList<Game>();
		for(GameList lgame : lgames){
			for(GameListItem ligame : lgame.games){
				if(ligame.game.publish 
					&& platform!=null 
					&& (ligame.game.platform.equals(platform)||platform.equals("*"))){
					games.add(ligame.game);
				}
			}
		}
		return games;
	}
}

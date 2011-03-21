/**
 * Projet: myplayapp
 * Gestion de la bibliothèque des jeux
 */
package controllers;

import static play.modules.excel.Excel.renderExcel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.Play;
import play.data.validation.Valid;
import play.mvc.Before;
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
		renderTemplate("GameLibrary/addGame.html", user, game);
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
		renderArgs.put("search", search);
		// récupération de l'utilisateur connecté
		User user = (User) renderArgs.get("user");
		// recupération de la plateforme (si présente)
		// String platform = (String) renderArgs.get("filterPlatform");
		// Constitution de la liste des plateformes distinctes
		List<Game> platforms = Game.find(
				"select distinct g.platform from Game g order by g.platform")
				.fetch();
		// recherche des jeux correspondant à game.title=%search% et
		// game.platform=platefom
		List<Game> games = Game.find(
				"select g from Game g " + "where lower(g.title) like ? "
						+ "and g.author=? " + "and g.publish=true "
						+ "order by g.platform, g.title ",
				"%" + search.toLowerCase() + "%", user).fetch();
		// rendu de la page
		renderTemplate("GameLibrary/search.html", games, user, platforms);
	}

	/**
	 * Export la liste des jeux affichés dans le format souhaité
	 */
	public static void exportGamesList() {
		// Récupération de l'utilisateur connecté.
		User user = (User) renderArgs.get("user");
		Logger.debug("Export game list for user " + user.username);
		// Si un utilisateur connecté.
		if (user != null) {
			Logger.debug(user.username + ": " + user.firstname + " "
					+ user.lastname);
			// Création de la liste des jeux
			List<Game> games = Game.find(
					"select g from Game g " + "where " + "g.publish=true "
							+ "and g.author = ? "
							+ "order by platform asc, title asc", user).fetch();
			Logger.debug("Number of exported games: %d", games.size());
			// Preparation de la date de génération
			Date dateOfTheDay = new Date();
			DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
			String date = df.format(dateOfTheDay);
			// Définition du nom de fichier (pour FF)
			renderArgs.put("filename", "mygames.xls");
			renderExcel(user, games, date);
		}
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
	public static void getPicture(Long id, String type, Long number) {
		Game game = Game.findById(id);
		FileInputStream output;
		try {
			File of = Play.getFile("/public/images/games/" + game.id + "/"
					+ type + "/" + number + ".jpg");
			output = new FileInputStream(of);
			renderBinary(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		int number = 1;
		// remove existing file
		File remove = Play.getFile("/public/images/games/" + game.id + "/"
				+ type + "/" + number + ".jpg");
		if (remove.exists()) {
			remove.delete();
		}
		// move new file to right place
		if (!Play.getFile("/public/images/games/" + game.id).exists()) {
			Play.getFile("/public/images/games/" + game.id).mkdir();
		}
		if (!Play.getFile("/public/images/games/" + game.id + "/" + type)
				.exists()) {
			Play.getFile("/public/images/games/" + game.id + "/" + type)
					.mkdir();
		}
		File output = Play.getFile("/public/images/games/" + game.id + "/"
				+ type + "/" + picture.getName());
		picture.renameTo(output);
		// update game model and save
		game.cover = picture.getName();
		game.save();
		// render page
		render("GameLibrary/addGame.html");
	}
}
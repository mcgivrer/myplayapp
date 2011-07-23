/**
 * 
 */
package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import models.Game;
import models.GameList;
import models.User;
import play.Logger;
import play.mvc.Controller;
import play.mvc.With;

/**
 * @author frederic
 *
 */

@With(ExcelControllerHelper.class)
public class GameExport extends Controller{
	/**
	 * Export la liste <code>gameListId</code> des jeux affichés 
	 * dans le format Excel (thanks to Excel-1.2.1 extension).
	 * le rendu se fait directement vers le fichier excel dont le nom est fixé
	 * via l'attribut <code>fileName</code>.
	 */
	public static void exportGamesList(Long gameListId) {
		// Récupération de l'utilisateur connecté.
		User user = (User) renderArgs.get("user");
		Logger.debug("Export game list for user " + user.username);
		// Si un utilisateur connecté.
		if (user != null) {
			request.format="xls";
			Logger.debug(user.username + ": " + user.firstname + " "
					+ user.lastname);
			GameList gl = GameList.findById(gameListId);
			// Création de la liste des jeux
			List<Game> games = GameLibrary.getGamesFromGameList(gameListId, user);
			
			Logger.debug("Number of exported games: %d", games.size());
			
			// Preparation de la date de génération
			Date dateOfTheDay = new Date();
			DateFormat df = new SimpleDateFormat("dd-mm-yyyy");
			String date = df.format(dateOfTheDay);
			
			// Définition du nom de fichier (pour FF)
			renderArgs.put("fileName", "gamelist-"+user.username+"-"+date+"-"+gl.title.replace(' ', '_')+".xls");
			// rendu du template de la liste de jeux au format excel
			renderTemplate("GameLibrary/exportGamesList.xls",user, games, date);
		}
	}
}

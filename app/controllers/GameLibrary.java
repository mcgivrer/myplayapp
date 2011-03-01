/**
 * 
 */
package controllers;

import models.Game;
import play.data.validation.Valid;
import play.mvc.Controller;

/**
 * @author frederic
 *
 */
public class GameLibrary extends Controller {


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
	
}

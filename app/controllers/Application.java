package controllers;

import java.util.List;

import models.Game;
import models.User;
import play.Logger;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(Security.class)
public class Application extends Controller {

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

    public static void index() {
        User user = (User)renderArgs.get("user");
        if(user!=null){
        	Logger.debug(user.username+": "+user.firstname+" "+user.lastname);
        	List<Game> platforms = Game.find("select distinct g.platform from Game g where g.author = ? order by g.platform",user).fetch();
        	List<Game> games = Game.find("select g from Game g where g.publish=true and g.author = ? order by platform, title", user).fetch();
        	Logger.debug("Number of retrieved games: %d, Number of platforme: %d",games.size(),platforms.size());
            render(games,platforms);
        }else{
            render();
        }
    }

    public static void details(Long id){
    	Game game = Game.findById(id);
    	render(game);
    }
    public static void filterByPlatform(String platform){
    	List<Game> platforms = Game.find("select distinct g.platform from Game g order by g.platform").fetch();
    	List<Game> games = Game.find("byPlatform",platform).fetch();
        render(games,platforms);
    }
    public static void login(){
    	render();
    }

    public static void logout(){
    	render();
    }
    public static void register(){
    	render();
    }

}


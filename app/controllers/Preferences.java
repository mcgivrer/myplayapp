/**
 * 
 */
package controllers;

import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 * @author frederic
 *
 */
@With(Security.class)
public class Preferences extends Controller {
	
	
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

	public static void update(String userLanguage){
		User user = (User)renderArgs.get("user");
		user.language = userLanguage;
		user.save();
		Administration.preferences();
	}
}

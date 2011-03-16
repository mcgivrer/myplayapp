/**
 * 
 */
package controllers;

import models.User;
import play.data.validation.Valid;
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

    /**
     * sauvegarde les préférences utilisateur.
     * @param userLanguage
     */
	public static void update(@Valid User user){		
		user.save();
		Preferences.show(user.getId());
	}
	
	/**
	 * Render image for user edited.
	 * @param id
	 */
	public static void getAvatarPicture(Long id){
		User user = User.findById(id);
		renderBinary(user.avatar.get());
	}

	
	
	/**
	 * Affichage des informations utilisateurs
	 * @param id
	 */
	public static void show(Long id){
		User user = (User)renderArgs.get("user");
		render(user);
	}
}
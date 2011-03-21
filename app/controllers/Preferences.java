/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import models.User;
import play.Play;
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
	 * Upload new avatar picture file for user id
	 * @param id
	 * @param file
	 */
	public static void upload(Long id, File avatar){
		User connectedUser = (User)renderArgs.get("user");
		User user = User.findById(id);
		
		if(user.id==connectedUser.id){
			// remove existing file
			File remove = Play.getFile("/public/images/users/"+user.image);
			if(remove.exists()){
				remove.delete();
			}
			// move new file to right place
			if(!Play.getFile("/public/images/users/"+user.username).exists()){
				Play.getFile("/public/images/users/"+user.username).mkdir();
			}
			File output = Play.getFile("/public/images/users/"+user.username+"/avatar_"+avatar.getName());
			avatar.renameTo(output);
			//update user model and save
			user.image = user.username+"/avatar_"+avatar.getName();
			user.save();
			// render page
			render("Preferences/show.html");
		}
	}
	
	/**
	 * Render image for user edited.
	 * @param id
	 */
	public static void getAvatarPicture(Long id){
		User user = User.findById(id);
		FileInputStream output;
		try {
			File of=  Play.getFile("/public/images/users/"+user.image);
			output = new FileInputStream(of);
			renderBinary(output);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
/**
 * 
 */
package controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import com.mysql.jdbc.Messages;

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
		if (Security.isConnected()) {
			User user = User.find("byUsername", Security.connected()).first();
			renderArgs.put("user", user);
		}
	}

	/**
	 * sauvegarde les préférences utilisateur.
	 * 
	 * @param userLanguage
	 */
	public static void update( @Valid User user ) {
		User other = (User)User.find("byUsername", user.username).first();
		
		if(other!=null && user.id!=other.id){
			validation.addError("user.username", "preferences.user.username.already.exists");
		}
		if(validation.hasErrors()) {
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
       		Preferences.show(user.getId());
		} else {
			user.save();
			Application.index();
		}
	}

	/**
	 * Upload new avatar picture file for user id
	 * 
	 * @param id
	 * @param file
	 */
	public static void upload(Long id, File avatar) {
		User connectedUser = (User) renderArgs.get("user");
		User user = User.findById(id);

		if (user.id == connectedUser.id) {
			// remove existing file
			File remove = Play.getFile("/public/images/users/" + user.id + "/"
					+ user.image);
			if (remove.exists()) {
				remove.delete();
			}
			// move new file to right place
			if (!Play.getFile("/public/images/users/" + user.id).exists()) {
				Play.getFile("/public/images/users/" + user.id).mkdir();
			}
			File output = Play.getFile("/public/images/users/" + user.id
					+ "/avatar_" + avatar.getName());
			avatar.renameTo(output);
			// update user model and save
			user.image = "avatar_" + avatar.getName();
			user.save();
			// render page
			Preferences.show(user.id);
		}
	}

	/**
	 * Render image for user edited.
	 * 
	 * @param id
	 */
	public static void getAvatarPicture(Long id) {
		User user = User.findById(id);
		FileInputStream output;
		File of;
		try {
			if (user != null && user.image != null && !user.image.equals("")) {
				of = Play.getFile("/public/images/users/" + user.id + "/"
						+ user.image);
				output = new FileInputStream(of);

				renderBinary(output);
			} else {
				of = Play
						.getFile("/public/images/icons/copenhagen/user_default.png");
				output = new FileInputStream(of);

				renderBinary(output);
			}
		} catch (FileNotFoundException e) {
			of = Play
					.getFile("/public/images/icons/copenhagen/user_default.png");
			try {
				output = new FileInputStream(of);

				renderBinary(output);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Affichage des informations utilisateurs
	 * 
	 * @param id
	 */
	public static void show(Long id) {
		User user = (User) renderArgs.get("user");
	    flash("user.image",user.image);
	    flash("user.username",user.username);
	    flash("user.firstname",user.firstname);
	    flash("user.lastname",user.lastname);
	    flash("user.email",user.email);
	    flash("user.webblog",user.webblog);
	    flash("user.password",user.password);
	    flash("user.passwordConfirm",user.password);
	    flash("user.language",user.language);
		render(user);
	}
}
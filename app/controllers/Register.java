/**
 * 
 */
package controllers;

import models.User;
import models.User.UserRole;
import play.i18n.Messages;
import play.mvc.Controller;

/**
 * @author frederic
 *
 */
public class Register extends Controller {
	
	/**
	 * First Step on registration
	 */
	public static void create(User user){
		if(user.username.equals(null)){
			user = new User("","","","","","","","",UserRole.USER,"en");
		}
		render(user);
	}
	
	public static void save(User user){
		user.role = User.UserRole.USER;
		validation.valid(user);
		user.save();
		renderTemplate("Register/create.html");
	}

}

/**
 * 
 */
package controllers;

import models.User;
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
		render();
	}
	
	public static void save(User user){
		user.role = User.UserRole.USER;
		user.save();
		validation.addError(user.username, "User created");
		renderTemplate("Register/create.html");
	}

}

/**
 * Project : myplayapp
 * Register controller.
 */
package controllers;

import models.User;
import models.User.UserRole;
import play.data.validation.Valid;
import play.mvc.Controller;

/**
 * Register function.
 * USed to register a new user to site.
 * @author frederic
 *
 */
public class Register extends Controller {
	
	/**
	 * First Step on registration
	 * @param user : User in case of validation of the form, used to redisplay form fields with errors.
	 */
	public static void create(User user){
		if(user!=null){
			user = new User("","","","","","","","",UserRole.USER,"en");
		}
		render(user);
	}
	
	/**
	 * Persists updated user to database. Need a validated User object.
	 * @param user
	 */
	public static void save(@Valid User user){
		user.role = User.UserRole.USER;
		user.save();
		renderTemplate("Register/create.html");
	}

}

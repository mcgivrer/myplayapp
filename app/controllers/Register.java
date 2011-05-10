/**
 * Project : myplayapp
 * Register controller.
 */
package controllers;

import com.mysql.jdbc.Messages;

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
	public static void create(){
		User user = new User();
		render(user);
	}
	
	/**
	 * Persists updated user to database. Need a validated User object.
	 * @param user
	 */
	public static void save(@Valid User user){
		
		User other = (User)User.find("byUsername", user.username).first();
		
		if(other!=null && user.id!=other.id){
			validation.addError("user.username", "preferences.user.username.already.exists");
		}else{
			user.role = User.UserRole.USER;
			user.save();
			flash("msg",Messages.getString("user.create.message.user.added"));
		}
	    flash("user.username",user.username);
	    flash("user.email",user.email);
	    flash("user.password",user.password);
	    flash("user.passwordConfirm",user.password);


		renderTemplate("Register/create.html");
	}
}
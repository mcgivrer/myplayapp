/**
 * Project myplayapp tutorial
 * Security implementation.
 */
package controllers;

import models.User;


/**
 * MyPlayApp Security implementation.
 * @author McGivrer
 *
 */
public class Security extends controllers.Secure.Security {
	/**
	 * Authentication method
	 * @param email
	 * @param password
	 * @return
	 */
	public static boolean authenticate(String email, String password){
		return User.connect(email,password)!=null;
	}
	/**
	 * Route to Controller/method on positive authentication
	 */
	static void onAuthenticated(){
		Application.index();	
	}
	/**
	 * Route to Controller/Method on Disconnection
	 */
	static void onDisconnected(){
		Application.index();
	}
	/**
	 * return if User have corresponding role.
	 * @param role
	 * @return boolean value.
	 */
	static boolean check(String role) {
	    return User.find("byUsername", connected()).<User>first().isRole(role);
	}
}
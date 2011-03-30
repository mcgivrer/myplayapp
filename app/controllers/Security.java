/**
 * Project myplayapp Play!framework tutorial
 * Object: Security implementation.
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
	 * return if User have one of the corresponding role listed with comma separator.
	 * @param role
	 * @return boolean value.
	 */
	static boolean check(String role) {
		String[] roles = role.split(",");
		for (String roleList : roles) {
			if(User.find("byUsername", connected()).<User>first().isRole(roleList)){
				return true;
			}
		}
	    return false;
	}
}

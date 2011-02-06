/**
 * Project myplayapp Play! tutorial 
 */
package controllers;

import play.mvc.With;

/**
 * CRUD sur les Users.
 * @author McGivrer
 */
@Check("ADMINISTRATOR")
@With(Security.class)
public class Users extends CRUD {

}

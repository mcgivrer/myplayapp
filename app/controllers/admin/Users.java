/**
 * Project myplayapp Play! tutorial 
 */
package controllers.admin;

import controllers.CRUD;
import controllers.Check;
import controllers.Security;
import play.mvc.With;

/**
 * CRUD sur les Users.
 * @author McGivrer
 */
@Check("ADMINISTRATOR")
@With(Security.class)
public class Users extends CRUD {

}

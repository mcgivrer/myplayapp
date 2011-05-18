/**
 * 
 */
package controllers.admin;

import play.mvc.With;
import controllers.CRUD;
import controllers.Check;
import controllers.Security;

/**
 * @author frederic
 *
 */
@Check("ADMINISTRATOR")
@With(Security.class)
public class Comments extends CRUD {

}

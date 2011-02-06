/**
 * Project myplayapp Play! tutorial
 * Admin zone
 */
package controllers;

import play.mvc.With;

/**
 * CRUD management for Game entity.
 * @author frederic
 *
 */
@Check("ADMINISTRATOR")
@With(Security.class)
public class Games extends CRUD {
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package controllers;

import models.User;
import org.junit.Before;
import play.mvc.Controller;
import play.mvc.With;

/**
 *
 * @author frederic
 */
@With(Security.class)
public class SecureController extends Controller{
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byUsername", Security.connected()).first();
            renderArgs.put("user", user);
        }
    }
}

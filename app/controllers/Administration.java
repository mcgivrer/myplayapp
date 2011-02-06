/**
 * Project myplayapp tutorial
 */
package controllers;
 
import play.*;
import play.mvc.*;
 
import java.util.*;
 
import models.*;
 
/**
 * Contrôleur de la zone de sécurité
 * @author McGivrer
 */
@With(Secure.class)
public class Administration extends Controller {
   
	/**
	 * récupération de l'utilisateur connecté.
	 */
    @Before
    static void setConnectedUser() {
        if(Security.isConnected()) {
            User user = User.find("byUsername", Security.connected()).first();
            renderArgs.put("user", user);
        }
    }   
    
    public static void index(){
    	render();
    }
    
    public static void logout(){
    	render();
    }
    
    public static void preferences(){
    	render();
    }
}
package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	List<Game> games = Game.findAll();
        render(games);
    }
    public static void login(){
    	render();
    }
    
    public static void logout(){
    	render();
    }
    public static void register(){
    	render();
    }

}
import java.util.HashMap;
import java.util.Map;

public class PlayerManager {

    Map<String, Number> memberShip = new HashMap<String, Number>();

    int getMemberShip(String user){
        // Read DB Here or load it before hand

        Number team = memberShip.get(user);
        if(team != null){
            return team.intValue();
        } else{
            return -1;
        }
    }
    void registerNewPlayer(){

        // Make DB Entry here

    }

    void setPlayerRole(int role){
        // Two roles 0 is default and 1 is Lord

       // Make DB Entry here

    }

}
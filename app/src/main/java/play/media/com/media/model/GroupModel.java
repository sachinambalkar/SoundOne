package play.media.com.media.model;

import java.io.Serializable;

/**
 * Created by Sachin on 09-03-2016.
 */
public class GroupModel implements Serializable
{
    String group_name;
    String song_name;
    String ip_address;
    String port_number;

    public GroupModel(String group_name, String song_name,String ip_address,String port_number){
        this.group_name=group_name;
        this.song_name=song_name;
        this.ip_address=ip_address;
        this.port_number=port_number;
    }

    public String getGroup_name(){
        return  this.group_name;
    }

    public String getSong_name(){
        return  this.song_name;
    }

    public String getIp_address(){return this.ip_address;}

    public String getPort_number(){return this.port_number;}
}

package world.ignite.piczilla.nodes;

/**
 * Created by Navratan Soni on 02-05-2015.
 */
public class ImageNode {
    private String unEscapedURL;

    public void setImageURL(String url){
        unEscapedURL = url;
    }

    public String getImageURL (){
        return unEscapedURL;
    }
}

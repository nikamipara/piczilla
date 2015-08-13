package world.ignite.piczilla.nodes;

public class ImageNode {
    private String unEscapedURL;

    public void setImageURL(String url){
        unEscapedURL = url;
    }

    public String getImageURL (){
        return unEscapedURL;
    }
}

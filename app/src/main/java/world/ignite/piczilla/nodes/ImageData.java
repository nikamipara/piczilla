package world.ignite.piczilla.nodes;

import java.util.List;

/**
 * Created by Navratan Soni on 02-05-2015.
 */
public class ImageData {

    private static List<ImageNode> imageList = null;

    public static List<ImageNode> getImageList() {
        return imageList;
    }

    public static void initImageList(List<ImageNode> imageList1) {
        imageList = imageList1;
    }

    public static ImageNode getImage(int position) {
        return imageList.get(position);
    }
}

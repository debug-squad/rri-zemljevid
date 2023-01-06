package si.feri.slidegame.screen.images;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Marker extends Image {
    public Marker(TextureRegion region){
        super(region);
    }

    public void setDrawable(TextureRegion region) {
        super.setDrawable(new TextureRegionDrawable(region));
    }
}

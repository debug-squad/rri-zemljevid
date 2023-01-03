package si.feri.slidegame.assets;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class AssetDescriptors {
    public static final AssetDescriptor<Skin> UI_SKIN =
            new AssetDescriptor<>(AssetPaths.UI_SKIN, Skin.class);

    //
    //
    //

    private AssetDescriptors() {
    }
}

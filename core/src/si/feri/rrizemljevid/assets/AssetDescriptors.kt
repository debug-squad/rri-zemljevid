package si.feri.rrizemljevid.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.graphics.g2d.TextureAtlas
import com.badlogic.gdx.scenes.scene2d.ui.Skin

object AssetDescriptors {
    val UI_SKIN = AssetDescriptor(AssetPaths.UI_SKIN, Skin::class.java)
    val GAMEPLAY = AssetDescriptor(AssetPaths.GAMEPLAY, TextureAtlas::class.java)
}
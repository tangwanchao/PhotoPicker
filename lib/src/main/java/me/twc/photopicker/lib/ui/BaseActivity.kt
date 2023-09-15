package me.twc.photopicker.lib.ui

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import com.blankj.utilcode.util.AdaptScreenUtils

/**
 * @author 唐万超
 * @date 2023/09/12
 */
open class BaseActivity : AppCompatActivity() {

    companion object {
        private const val DESIGN_WIDTH = 375
    }

    override fun getResources(): Resources {
        var resources = super.getResources()
        resources = AdaptScreenUtils.adaptWidth(resources, DESIGN_WIDTH)
        // 适配后,如果 xdpi 和 fontScale 不是我们想要的,我们修改他并更新
        resources.apply {
            val xdpi = this.getWantXdpi()
            val conf = this.configuration
            val displayMetrics = this.displayMetrics
            if (conf.fontScale != 1.0f || displayMetrics.xdpi != xdpi) {
                if (conf.fontScale != 1.0f) {
                    conf.fontScale = 1.0f
                }
                if (displayMetrics.xdpi != xdpi) {
                    displayMetrics.xdpi = xdpi
                }
                @Suppress("DEPRECATION")
                this.updateConfiguration(conf, displayMetrics)
            }
        }
        return resources
    }

    private fun Resources.getWantXdpi(): Float {
        return this.displayMetrics.widthPixels * 72f / DESIGN_WIDTH
    }
}
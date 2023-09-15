package me.twc.photopicker.lib.enums

/**
 * @author 唐万超
 * @date 2023/09/08
 *
 * 选择器支持的内容
 */
enum class SupportMedia {
    /**
     * 仅支持图片
     */
    IMAGE,

    /**
     * 仅支持视屏
     */
    VIDEO,

    /**
     * 支持视屏和图片
     */
    IMAGE_AND_VIDEO
}
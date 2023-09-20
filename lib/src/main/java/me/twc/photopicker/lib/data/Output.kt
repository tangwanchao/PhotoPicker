package me.twc.photopicker.lib.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * @author 唐万超
 * @date 2023/09/13
 */
@Parcelize
class Output(
    val items: List<BaseItem>
) : Parcelable
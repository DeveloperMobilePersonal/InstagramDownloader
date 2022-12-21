package instagram.photo.video.downloader.story.saver.base

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListViewHolder<M, V : ViewDataBinding>
constructor(var context: Context, var viewBinding: V) :
    RecyclerView.ViewHolder(viewBinding.root) {

    init {
        if (context is AppCompatActivity) {
            viewBinding.lifecycleOwner = context as LifecycleOwner
        }
    }

    open fun bind(model: M, position: Int) {

    }

}
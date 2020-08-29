package io.stawallet.signer.seed

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.stawallet.signer.R
import io.stawallet.signer.data.Seed
import kotlinx.android.synthetic.main.row_seed.view.*

class SeedListAdapter :
    PagedListAdapter<Seed, SeedListAdapter.ViewHolder>(ITEM_COMPARATOR) {

    var onItemClickListener: ((Seed) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        (holder as ViewHolder).bindTo(getItem(position))

    inner class ViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.row_seed, parent, false)
    ) {
        val cardView: CardView = itemView.seed_card
        val statusView: TextView = itemView.seed_status

        fun bindTo(seed: Seed?) {
            cardView.setOnClickListener { v ->
                seed?.let { onItemClickListener?.invoke(it) }
            }

            if (seed == null) return clear()

        }

        private fun clear() {
            statusView.text = ""
        }
    }

    companion object {
        val ITEM_COMPARATOR = object : DiffUtil.ItemCallback<Seed>() {
            override fun areContentsTheSame(oldItem: Seed, newItem: Seed): Boolean =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: Seed, newItem: Seed): Boolean =
                oldItem.fingerprint == newItem.fingerprint
        }
    }
}

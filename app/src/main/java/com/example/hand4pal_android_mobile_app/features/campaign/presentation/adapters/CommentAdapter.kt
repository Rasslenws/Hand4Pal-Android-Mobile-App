package com.example.hand4pal_android_mobile_app.features.campaign.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hand4pal_android_mobile_app.R
import com.example.hand4pal_android_mobile_app.features.campaign.domain.CommentDTO
import java.text.SimpleDateFormat
import java.util.Locale

class CommentAdapter(private val comments: List<CommentDTO>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val commentTextView: TextView = itemView.findViewById(R.id.commentTextView)
        private val commentAuthorTextView: TextView = itemView.findViewById(R.id.commentAuthorTextView)
        private val commentDateTextView: TextView = itemView.findViewById(R.id.commentDateTextView)

        fun bind(comment: CommentDTO) {
            commentTextView.text = comment.content
            commentAuthorTextView.text = comment.userName ?: "Anonyme"
            commentDateTextView.text = formatDate(comment.createdAt)
        }

        private fun formatDate(rawDate: String?): String {
            if (rawDate.isNullOrBlank()) return ""
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val formatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val date = parser.parse(rawDate)
                formatter.format(date!!)
            } catch (_: Exception) {
                rawDate.take(10)
            }
        }
    }
}

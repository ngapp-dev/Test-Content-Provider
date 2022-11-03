package com.ngapp.testcontentprovider.ui.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ngapp.testcontentprovider.data.Course
import com.ngapp.testcontentprovider.databinding.ItemCourseBinding

class CourseListAdapter(
    private val onItemClick: (course: Course) -> Unit,
    private val onItemEditButtonClick: (course: Course) -> Unit
) : ListAdapter<Course, CourseListAdapter.Holder>(CourseDiffUtilCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        return Holder(
            ItemCourseBinding.inflate(inflater, parent, false),
            onItemClick,
            onItemEditButtonClick
        )
    }

    override fun onBindViewHolder(
        holder: Holder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    class Holder(
        private val binding: ItemCourseBinding,
        private val onItemClick: (course: Course) -> Unit,
        private val onItemEditButtonClick: (course: Course) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            course: Course
        ) {
            binding.root.setOnClickListener {
                onItemClick(course)
            }
            binding.editButton.setOnClickListener {
                onItemEditButtonClick(course)
            }
            binding.titleTextView.text = course.title
        }

    }

    class CourseDiffUtilCallBack : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }

    }


}
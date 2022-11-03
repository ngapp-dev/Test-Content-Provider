package com.ngapp.testcontentprovider.ui.add

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ngapp.testcontentprovider.MainActivity
import com.ngapp.testcontentprovider.R
import com.ngapp.testcontentprovider.data.Course
import com.ngapp.testcontentprovider.databinding.FragmentAddCourseBinding
import com.ngapp.testcontentprovider.utils.ViewBindingFragment
import com.ngapp.testcontentprovider.utils.launchAndCollectIn

class CourseAddFragment : ViewBindingFragment<FragmentAddCourseBinding>(FragmentAddCourseBinding::inflate) {

    private val viewModel: CourseAddViewModel by viewModels()
    private val args: CourseAddFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args.course.let { course ->
            if (course != null) {
                (requireActivity() as MainActivity).supportActionBar?.title = getString(R.string.edit_course)
                binding.titleEditText.setText(course.title)
                binding.saveButton.setText(R.string.saveButtonSave)
                updateCourse(course)
            } else {
                updateCourse(null)
            }
        }

    }

    private fun updateCourse(course: Course?) {
        binding.saveButton.setOnClickListener {
            if (binding.titleEditText.text.isEmpty()) {
                binding.titleEditText.error = getString(R.string.addTitleError)
                return@setOnClickListener
            }
            val uCourse = Course(
                id = course?.id ?: 0L,
                title = binding.titleEditText.text.toString()
            )
            viewModel.updateCourse(uCourse)
        }
        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        viewModel.saveSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
            findNavController().navigate(
                CourseAddFragmentDirections.actionCourseAddFragmentToCourseListFragment()
            )
        }
    }
}
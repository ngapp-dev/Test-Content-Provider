package com.ngapp.testcontentprovider.ui.detail

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ngapp.testcontentprovider.R
import com.ngapp.testcontentprovider.data.Course
import com.ngapp.testcontentprovider.databinding.FragmentDetailCourseBinding
import com.ngapp.testcontentprovider.utils.launchAndCollectIn

class CourseDetail : Fragment() {
    private var _binding: FragmentDetailCourseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CourseDetailViewModel by viewModels()
    private val args: CourseDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailCourseBinding.inflate(inflater, container, false)
        val view = binding.root

        args.currentCourse.let {
            binding.detailedCourseIdTextView.text = "ID: ${it.id}"
            binding.detailedCourseTitleTextView.text = "Title: ${it.title}"

            binding.deleteButton.setOnClickListener {
                showOnDeleteAlertDialog(R.string.on_delete_alert_dialog, args.currentCourse)
            }
        }
        return view
    }

    private fun showOnDeleteAlertDialog(@StringRes messageResId: Int, currentCourse: Course) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.button_yes) { dialog, _ ->
                viewModel.delete(currentCourse.id)
                viewModel.deleteSuccessFlow.launchAndCollectIn(viewLifecycleOwner) {
                    findNavController().navigate(CourseDetailDirections.actionCourseDetailToCourseListFragment())
                }
            }
            .setNegativeButton(R.string.button_no) { dialog, _ ->
                viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
                    Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setMessage(messageResId)
            .show()
    }
}
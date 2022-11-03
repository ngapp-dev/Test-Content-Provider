package com.ngapp.testcontentprovider.ui.list

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ngapp.testcontentprovider.R
import com.ngapp.testcontentprovider.data.Course
import com.ngapp.testcontentprovider.databinding.FragmentListCourseBinding
import com.ngapp.testcontentprovider.ui.list.adapter.CourseListAdapter
import com.ngapp.testcontentprovider.utils.launchAndCollectIn

class CourseListFragment : Fragment() {

    private var _binding: FragmentListCourseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CourseListViewModel by viewModels()
    private lateinit var courseListAdapter: CourseListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentListCourseBinding.inflate(inflater, container, false)
        val view = binding.root
        createActionBarMenu()
        initList()
        bindViewModel()
        return view
    }

    private fun initList() {
        courseListAdapter = CourseListAdapter(
            onItemClick = { course ->
                openCourseDetail(course)
            },
            onItemEditButtonClick = { course ->
                editCourse(course)
            }
        )
        with(binding.courseList) {
            adapter = courseListAdapter
            setHasFixedSize(true)

            val linearLayoutManager = LinearLayoutManager(context)
            layoutManager = linearLayoutManager
        }
    }

    private fun bindViewModel() {
        viewModel.loadList()
        viewModel.courseFlow.launchAndCollectIn(viewLifecycleOwner) { courses ->
            courseListAdapter.submitList(courses)
            binding.courseList.scrollToPosition(0)
        }
        viewModel.toastFlow.launchAndCollectIn(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
        binding.addFAB.setOnClickListener { addCourse() }
    }

    private fun addCourse() {
        findNavController().navigate(
            CourseListFragmentDirections.actionCourseListFragmentToCourseAddFragment(
                null
            )
        )
    }

    private fun openCourseDetail(course: Course) {
        findNavController().navigate(
            CourseListFragmentDirections.actionCourseListFragmentToCourseDetail(
                course
            )
        )
    }

    private fun editCourse(course: Course) {
        findNavController().navigate(
            CourseListFragmentDirections.actionCourseListFragmentToCourseAddFragment(
                course
            )
        )
    }

    private fun createActionBarMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.action_bar_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.deleteAllButton -> {
                        showOnDeleteAlertDialog(R.string.on_delete_all_alert_dialog)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun showOnDeleteAlertDialog(@StringRes messageResId: Int) {
        AlertDialog.Builder(requireContext())
            .setPositiveButton(R.string.button_yes) { dialog, _ ->
                viewModel.deleteAllCourses()
            }
            .setNegativeButton(R.string.button_no) { dialog, _ ->
                dialog.dismiss()
            }
            .setMessage(messageResId)
            .show()
    }
}
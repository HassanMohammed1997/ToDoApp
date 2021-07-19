package com.semicolon.todoapp.ui.fragments

import android.graphics.Color
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.semicolon.todoapp.R
import com.semicolon.todoapp.data.Priority
import com.semicolon.todoapp.data.TodoEntity
import com.semicolon.todoapp.databinding.FragmentAddTodoBinding
import com.semicolon.todoapp.repo.MainViewModel
import com.semicolon.todoapp.ui.BaseFragment
import com.semicolon.todoapp.ui.MainActivity
import com.semicolon.todoapp.utils.hideSoftKeyboard
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTodoFragment : BaseFragment<FragmentAddTodoBinding>(R.layout.fragment_add_todo) {
    private val viewModel by viewModels<MainViewModel>()
    private var priority: String = ""
    private val args by navArgs<AddTodoFragmentArgs>()
    private var update: Boolean = false
    private lateinit var currentTodo: TodoEntity
    override fun onViewCreated() {
        val priorityItems = listOf("Low", "Medium", "High")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            priorityItems
        )
        (binding.priorityTextField.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        args.todo?.let { todo ->
            binding.todo = todo
            currentTodo = todo
            binding.prioritySpinner.setText(
                binding.prioritySpinner.adapter.getItem(
                    convertPriorityToPosition(todo.priority)
                ) as String, false
            )
            (requireActivity() as MainActivity).supportActionBar?.title = "Update Todo"
            update = true

        }


    }


    private fun convertPriorityToPosition(priority: Priority): Int {
        return when (priority) {
            Priority.LOW -> 0
            Priority.MEDIUM -> 1
            Priority.HIGH -> 2
        }
    }

    override fun setListenersForViews() {
        binding.prioritySpinner.setOnItemClickListener { parent, _, position, _ ->
            priority = parent?.getItemAtPosition(position) as String
            val spinnerTextView = parent.getChildAt(0) as TextView
            changePriorityTextColor(spinnerTextView, position)
        }
    }

    private fun changePriorityTextColor(spinnerTextView: TextView, position: Int) {
        when (position) {
            0 -> spinnerTextView.setTextColor(Color.RED)
            1 -> spinnerTextView.setTextColor(Color.YELLOW)
            2 -> spinnerTextView.setTextColor(Color.GREEN)
        }

    }

    override fun subscribeLiveDataObservers() {
    }

    override fun setOptionMenu(): Boolean = true

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_todo_add, menu)
        menu.findItem(R.id.menu_delete).isVisible = update
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        hideSoftKeyboard()
        if (item.itemId == R.id.menu_todo_add) {
            insertOrUpdateTodo()
        } else if (item.itemId == R.id.menu_delete) {
            deleteTodo()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTodo() {
        viewModel.deleteTodo(currentTodo)
        Toast.makeText(requireContext(), "Todo has been deleted", Toast.LENGTH_SHORT).show()
        findNavController().popBackStack()
    }

    private fun updateTodo() {
        args.todo?.let { todo ->
            todo.title = binding.title.text.toString()
            todo.description = binding.descriptionEditText.text.toString()
            todo.priority = Priority.parse(priority)
            viewModel.updateTodo(todo)
            Toast.makeText(requireContext(), "Todo updated successfully!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun insertOrUpdateTodo() {
        val title = binding.title.text.toString()
        val description = binding.descriptionEditText.text.toString()
        val validation = validateTodoData(title, description)
        if (validation) {
            if (!update) {
                val todoEntity = TodoEntity(
                    0,
                    title,
                    Priority.parse(priority),
                    description
                )
                viewModel.insertTodo(todoEntity)
                Toast.makeText(requireContext(), "Todo Added successfully!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                updateTodo()
            }

            findNavController().popBackStack()
        }

    }

    private fun validateTodoData(title: String, description: String): Boolean {
        if (title.isEmpty()) {
            binding.titleInputLayout.error = getString(R.string.required)
            return false
        }

        if (description.isEmpty()) {
            binding.descriptionTextField.error = getString(R.string.required)
            return false
        }

        return true
    }


}
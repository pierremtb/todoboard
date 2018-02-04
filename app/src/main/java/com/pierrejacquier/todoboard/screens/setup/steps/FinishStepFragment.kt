package com.pierrejacquier.todoboard.oldfeat.setup.steps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.commons.extensions.onTextChanged
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.databinding.SetupFragmentFinishStepBinding
import com.pierrejacquier.todoboard.screens.setup.BoardSetupActivity
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.main_board_item.view.*
import kotlinx.android.synthetic.main.setup_fragment_finish_step.*
import kotlinx.android.synthetic.main.setup_fragment_provider_auth_step.view.*

class FinishStepFragment : Fragment(), Step {

    private lateinit var binding: SetupFragmentFinishStepBinding

    override fun onSelected() {
        val parent = activity as BoardSetupActivity

        binding.boardTitle = ""

        newBoardNameEdit.onTextChanged {
            binding.includedBoardCard?.board = Board(parent.newBoardId, it, parent.newBoardAccessToken)
        }

        binding.includedBoardCard?.let {
            with(it) {
                board = Board(parent.newBoardId, parent.newBoardName, parent.newBoardAccessToken)
                user = parent.newBoardUser
                firstProjects = parent.selectableProjects.filter { it.selected }.take(5).toList()
                projectsCount = "${firstProjects!!.size} projects"
                configureBoardButton.visibility = View.INVISIBLE
                launchBoardButton.visibility = View.INVISIBLE
            }
        }
    }

    override fun verifyStep(): VerificationError? {
        return null
    }

    override fun onError(error: VerificationError) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = SetupFragmentFinishStepBinding.inflate(inflater)
        return binding.root
    }
}
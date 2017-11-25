package com.pierrejacquier.todoboard.oldfeat.setup.steps


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.screens.setup.BoardSetupActivity
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import kotlinx.android.synthetic.main.fragment_provider_auth_step.*

class ProviderAuthStepFragment : Fragment(), Step {

    override fun onSelected() {
    }

    override fun verifyStep(): VerificationError? {
        if (newUserGroup.visibility == View.VISIBLE) {
            return null
        }
        return VerificationError(activity?.resources?.getString(R.string.you_have_to_authorize_first))
    }

    override fun onError(error: VerificationError) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_provider_auth_step)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        todoistAuthButton.setOnClickListener({
            (activity as BoardSetupActivity).startTodoistAuth()
        })
    }
}

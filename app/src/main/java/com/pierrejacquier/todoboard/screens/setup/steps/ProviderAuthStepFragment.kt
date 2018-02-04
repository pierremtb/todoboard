package com.pierrejacquier.todoboard.oldfeat.setup.steps


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pierrejacquier.todoboard.screens.setup.BoardSetupActivity
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.RxBaseFragment
import com.pierrejacquier.todoboard.commons.extensions.inflate
import com.pierrejacquier.todoboard.screens.setup.steps.adapters.ExistingUsersAdapter
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.setup_fragment_provider_auth_step.*

class ProviderAuthStepFragment : RxBaseFragment(), Step {

    private lateinit var existingUsersAdapter: ExistingUsersAdapter

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
        return container?.inflate(R.layout.setup_fragment_provider_auth_step)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val parent = activity as BoardSetupActivity

        todoistAuthButton.setOnClickListener({ parent.startTodoistAuth() })

        existingUsersAdapter = ExistingUsersAdapter(parent.picasso)

        existingUsersAdapter.onClick = { user ->
            parent.newBoardAccessToken = user.token!!
            parent.prepareNewBoard()
        }

        with(existingUsersRV) {
            adapter = existingUsersAdapter
            layoutManager = LinearLayoutManager(context)
        }

        val sub = parent.database.usersDao().getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    existingUsersAdapter.items = it
                    adjustLayout()
                }
        subscriptions.add(sub)
    }

    private fun adjustLayout() {
        val usersVisibility =  if (existingUsersAdapter.items.isEmpty()) View.GONE else View.VISIBLE

        divider.visibility = usersVisibility
        existingUsersRV.visibility = usersVisibility
    }
}

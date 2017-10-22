package com.pierrejacquier.todoboard.features.setup

import android.content.Context
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.features.setup.steps.FinishStepFragment
import com.pierrejacquier.todoboard.features.setup.steps.ProviderAuthStepFragment
import com.pierrejacquier.todoboard.features.setup.steps.ProviderDetailsStepFragment

class SetupFragmentStepAdapter(fm: FragmentManager, context: Context) : AbstractFragmentStepAdapter(fm, context) {

    override fun getViewModel(position: Int): StepViewModel {
        val builder = StepViewModel.Builder(context)
                .setTitle(R.string.app_name)
        if (position == 1) {
            builder.setSubtitle("Optional")
        }
        return builder
                .create()
    }

    override fun createStep(position: Int): Step {
        when (position) {
            0 -> return ProviderAuthStepFragment()
            1 -> return ProviderDetailsStepFragment()
            2 -> return FinishStepFragment()
            else -> throw IllegalArgumentException("Unsupported position: " + position)
        }
    }

    override fun getCount() = 3
}

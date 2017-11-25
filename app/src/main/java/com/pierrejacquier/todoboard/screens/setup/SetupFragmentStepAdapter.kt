package com.pierrejacquier.todoboard.screens.setup

import android.content.Context
import android.support.v4.app.FragmentManager
import com.stepstone.stepper.Step
import com.stepstone.stepper.adapter.AbstractFragmentStepAdapter
import com.stepstone.stepper.viewmodel.StepViewModel
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.oldfeat.setup.steps.FinishStepFragment
import com.pierrejacquier.todoboard.oldfeat.setup.steps.ProviderAuthStepFragment
import com.pierrejacquier.todoboard.screens.setup.steps.ProviderDetailsStepFragment

class SetupFragmentStepAdapter(fm: FragmentManager, context: Context): AbstractFragmentStepAdapter(fm, context) {

    override fun getViewModel(position: Int): StepViewModel {
        val builder = StepViewModel.Builder(context)
                .setTitle(R.string.app_name)
        if (position == 1) {
            builder.setSubtitle("Optional")
        }
        return builder.create()
    }

    override fun createStep(position: Int): Step {
        return when (position) {
            0 -> ProviderAuthStepFragment()
            1 -> ProviderDetailsStepFragment()
            2 -> FinishStepFragment()
            else -> throw IllegalArgumentException("Unsupported position: " + position)
        }
    }

    override fun getCount() = 3
}

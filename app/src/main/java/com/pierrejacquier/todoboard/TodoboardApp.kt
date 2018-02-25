package com.pierrejacquier.todoboard

import android.app.Application
import com.pierrejacquier.todoboard.di.ContextModule
import com.pierrejacquier.todoboard.di.DaggerTodoboardAppComponent
import com.pierrejacquier.todoboard.di.TodoboardAppComponent
import timber.log.Timber
import android.app.Activity
import android.support.multidex.MultiDexApplication
import android.support.v4.app.Fragment
import com.pierrejacquier.todoboard.commons.TzInfoTypeAdapter
import paperparcel.Adapter
import paperparcel.ProcessorConfig

@ProcessorConfig(adapters = arrayOf(Adapter(TzInfoTypeAdapter::class)))
class TodoboardApp : MultiDexApplication() {
    companion object {
        fun withActivity(activity: Activity) = activity.application as TodoboardApp
        fun withFragment(fragment: Fragment) = fragment.activity?.application as TodoboardApp
    }

    lateinit var component: TodoboardAppComponent

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        component = DaggerTodoboardAppComponent.builder()
                .contextModule(ContextModule(this))
                .build()
    }
}
package com.pierrejacquier.todoboard.commons

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

open class RxBaseFragment : Fragment() {

    protected var subscriptions = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeDisposable()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }
}
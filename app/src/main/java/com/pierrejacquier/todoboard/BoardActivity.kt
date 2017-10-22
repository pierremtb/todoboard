package com.pierrejacquier.todoboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import com.pierrejacquier.todoboard.database.boards.Board
import kotlinx.android.synthetic.main.activity_board.*

fun Context.BoardIntent(id: Long): Intent {
    return Intent(this, BoardActivity::class.java).apply {
        putExtra(INTENT_ID, id)
    }
}

private const val INTENT_ID = "id"

class BoardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }
}

package com.pierrejacquier.todoboard.screens.main

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.pierrejacquier.todoboard.R
import com.pierrejacquier.todoboard.commons.consume
import kotlinx.android.synthetic.main.main_activity.*
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.LibsBuilder
import com.pierrejacquier.todoboard.screens.main.fragments.boards.BoardsListFragment
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import com.pierrejacquier.todoboard.commons.extensions.log
import com.pierrejacquier.todoboard.data.model.Board
import com.pierrejacquier.todoboard.data.model.BoardExtendedWithProjects
import com.pierrejacquier.todoboard.screens.board.BOARD_KEY
import com.pierrejacquier.todoboard.screens.board.BoardActivity
import com.pierrejacquier.todoboard.screens.board.getBoardIntent
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)

        if (savedInstanceState == null) {
            changeFragment(BoardsListFragment())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when(item!!.itemId) {
        R.id.action_about -> consume {
            LibsBuilder().withActivityStyle(Libs.ActivityStyle.LIGHT)
                    .withAboutIconShown(true)
                    .withAboutVersionShown(true)
                    .withAboutDescription(getString(R.string.aboutLibraries_description_text))
                    .start(this)
        }
        else -> false
    }

    private fun changeFragment(f: Fragment, cleanStack: Boolean = false) {
        val ft = supportFragmentManager.beginTransaction()
        if (cleanStack) {
            clearBackStack()
        }
        ft.setCustomAnimations(R.anim.slide_in_up, 0)
        ft.replace(R.id.fragmentSpace, f)
        ft.addToBackStack(null)
        ft.commit()
    }

    private fun clearBackStack() {
        val manager = supportFragmentManager
        if (manager.backStackEntryCount > 0) {
            val first = manager.getBackStackEntryAt(0)
            manager.popBackStack(first.id, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }
    }

    override fun onBackPressed() {
        val fragmentManager = supportFragmentManager
        if (fragmentManager.backStackEntryCount > 1) {
            fragmentManager.popBackStack()
        } else {
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    fun updateAppShortcuts(boards: List<Board>) {
        val shortcutManager = getSystemService(ShortcutManager::class.java)
        val shortcuts = boards.map {
            ShortcutInfo.Builder(this, it.id.toString())
                    .setShortLabel(it.name)
                    .setLongLabel(it.name)
                    .setIcon(Icon.createWithResource(this, R.mipmap.shortcut))
                    .setIntents(arrayOf(
                        Intent(this, MainActivity::class.java).apply {
                            action = Intent.ACTION_VIEW
                        },
                        Intent(this, BoardActivity::class.java).apply {
                            putExtra(BOARD_KEY, it.id)
                            action = Intent.ACTION_VIEW
                        }
                    ))
                    .build()
        }

        shortcutManager.dynamicShortcuts = shortcuts
    }
}

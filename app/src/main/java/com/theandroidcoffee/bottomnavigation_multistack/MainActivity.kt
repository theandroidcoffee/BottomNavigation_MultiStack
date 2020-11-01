/*
 *
 *  * Copyright 2020,
 *  *                 ☕ Marco Finadri - theandroidcoffee.com ☕
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *  http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.theandroidcoffee.bottomnavigation_multistack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

	private var currentNavController: LiveData<NavController>? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		if (savedInstanceState == null)
			initBottomNavigationView()
	}

	override fun onRestoreInstanceState(savedInstanceState: Bundle) {
		super.onRestoreInstanceState(savedInstanceState)
		initBottomNavigationView()
	}

	private fun initBottomNavigationView() {
		val bottomNavigationView =
			findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

		val navGraphs = listOf(
			R.navigation.browser,
			R.navigation.contacts,
			R.navigation.adder
		)

//      Use this if you are using NavigationExtensions.java:
//		val navController = NavigationExtensions.setupWithNavController(
//			bottomNavigationView,
//			navGraphs,
//			supportFragmentManager,
//			R.id.fragment_container_view,
//			intent
//		)

//		Use this if you want to use NavigationExtensions.kt:
		val controller = bottomNavigationView.setupWithNavController(
			navGraphIds = navGraphs,
			fragmentManager = supportFragmentManager,
			containerId = R.id.fragment_container_view,
			intent = intent
		)

		controller.observe(this, { navController ->
			setupActionBarWithNavController(navController)
		})

		currentNavController = controller
	}

	override fun onSupportNavigateUp(): Boolean {
		return currentNavController?.value?.navigateUp() ?: false
	}
}

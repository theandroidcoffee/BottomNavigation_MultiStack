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

package com.theandroidcoffee.bottomnavigation_multistack.adder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdderViewModel : ViewModel() {

	private val _result = MutableLiveData(0)
	val result: LiveData<Int> = _result

	fun add(toAdd: Int) {
		_result.value = (_result.value ?: 0) + toAdd
	}
}
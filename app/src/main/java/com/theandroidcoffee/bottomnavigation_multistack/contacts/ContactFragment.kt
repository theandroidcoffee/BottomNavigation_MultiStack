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

package com.theandroidcoffee.bottomnavigation_multistack.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.theandroidcoffee.bottomnavigation_multistack.R
import com.theandroidcoffee.bottomnavigation_multistack.contacts.PersonAdapter.Companion.CONTACT_KEY
import com.theandroidcoffee.bottomnavigation_multistack.databinding.FragmentContactBinding
import com.theandroidcoffee.bottomnavigation_multistack.model.Contact

class ContactFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding = DataBindingUtil.inflate<FragmentContactBinding>(
			inflater, R.layout.fragment_contact, container, false
		)

		val contact: Contact = arguments?.get(CONTACT_KEY) as Contact

		binding.contactNameTextView.text = contact.name
		binding.contactImageView.setImageResource(contact.imageId)

		return binding.root
	}
}
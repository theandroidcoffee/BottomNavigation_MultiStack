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
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.theandroidcoffee.bottomnavigation_multistack.R
import com.theandroidcoffee.bottomnavigation_multistack.databinding.FragmentContactListBinding
import com.theandroidcoffee.bottomnavigation_multistack.databinding.ItemContactBinding
import com.theandroidcoffee.bottomnavigation_multistack.model.Contact

class ContactListFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding = DataBindingUtil.inflate<FragmentContactListBinding>(
			inflater, R.layout.fragment_contact_list, container, false
		)

		val contacts = listOf(
			Contact("Debra Espresso", R.drawable.person_1),
			Contact("Ben Doppio", R.drawable.person_2),
			Contact("Lara Lungo", R.drawable.person_3),
			Contact("John Ristretto", R.drawable.person_4),
			Contact("Jeanna Macchiato", R.drawable.person_5),
			Contact("Mary Corretto", R.drawable.person_6),
			Contact("Rachel Romano", R.drawable.person_7),
			Contact("Rick Cappuccino", R.drawable.person_8),
			Contact("Tara Affogato", R.drawable.person_9),
			Contact("Larry Marocchino", R.drawable.person_10)
		)

		binding.contactsRecyclerView.apply {
			setHasFixedSize(true)
			adapter = PersonAdapter(contacts)
		}

		return binding.root
	}
}

class PersonAdapter(private val contacts: List<Contact>) :
	RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

	companion object {
		const val CONTACT_KEY = "contact_key"
	}

	class ViewHolder(item: View) : RecyclerView.ViewHolder(item) {
		val binding = DataBindingUtil.bind<ItemContactBinding>(item)
	}

	override fun onCreateViewHolder(
		parent: ViewGroup, viewType: Int
	): ViewHolder = ViewHolder(
		DataBindingUtil.inflate<ItemContactBinding>(
			LayoutInflater.from(parent.context),
			R.layout.item_contact, parent, false
		).root
	)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		val contact = contacts[position]
		val binding = holder.binding
		if (binding != null) {
			binding.contactNameTextView.text = contact.name
			binding.contactImageView.setImageResource(contact.imageId)

			binding.root.setOnClickListener {
				val bundle = bundleOf(CONTACT_KEY to contact)

				holder.itemView.findNavController()
					.navigate(R.id.action_contactListFragment_to_contactFragment, bundle)
			}
		}
	}

	override fun getItemCount(): Int = contacts.size

}
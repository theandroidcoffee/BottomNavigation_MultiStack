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

package com.theandroidcoffee.bottomnavigation_multistack;

import android.content.Intent;
import android.util.SparseArray;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Objects;

/**
 * Manages the various graphs needed for a [BottomNavigationView].
 * <p>
 * This sample is a workaround until the Navigation Component supports multiple back stacks.
 */
public class NavigationExtensions {

    public static LiveData<NavController> setupWithNavController(BottomNavigationView bottomNavigationView,
                                                                 List<Integer> navGraphIds,
                                                                 FragmentManager fragmentManager,
                                                                 int containerId,
                                                                 Intent intent) {
        // Map of tags
        final SparseArray<String> graphIdToTagMap = new SparseArray<>();
        // Result. Mutable live data with the selected controlled
        final MutableLiveData<NavController> selectedNavController = new MutableLiveData<>();

        int firstFragmentGraphId = 0;

        // First create a NavHostFragment for each NavGraph ID
        for (int index = 0; index < navGraphIds.size(); index++) {
            final String fragmentTag = getFragmentTag(index);

            // Find or create the Navigation host fragment
            final NavHostFragment navHostFragment = obtainNavHostFragment(
                    fragmentManager, fragmentTag, navGraphIds.get(index), containerId);

            // Obtain its id
            final int graphId = navHostFragment.getNavController().getGraph().getId();

            if (index == 0)
                firstFragmentGraphId = graphId;

            // Save to the map
            graphIdToTagMap.put(graphId, fragmentTag);

            // Attach or detach nav host fragment depending on whether it's the selected item.
            if (bottomNavigationView.getSelectedItemId() == graphId) {
                // Update livedata with the selected graph
                selectedNavController.setValue(navHostFragment.getNavController());
                attachNavHostFragment(fragmentManager, navHostFragment, index == 0);
            } else detachNavHostFragment(fragmentManager, navHostFragment);
        }

        // Now connect selecting an item with swapping Fragments
        final String[] selectedItemTag = {graphIdToTagMap.get(bottomNavigationView.getSelectedItemId())};
        final String firstFragmentTag = graphIdToTagMap.get(firstFragmentGraphId);
        final boolean[] isOnFirstFragment = {Objects.equals(selectedItemTag[0], firstFragmentTag)};

        // When a navigation item is selected
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            // Don't do anything if the state is state has already been saved.
            if (!fragmentManager.isStateSaved()) {
                final String newlySelectedItemTag = graphIdToTagMap.get(item.getItemId());
                if (!Objects.equals(selectedItemTag[0], newlySelectedItemTag)) {
                    // Pop everything above the first fragment (the "fixed start destination")
                    fragmentManager.popBackStack(firstFragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    final NavHostFragment selectedFragment =
                            (NavHostFragment) fragmentManager.findFragmentByTag(newlySelectedItemTag);

                    // Exclude the first fragment tag because it's always in the back stack.
                    if (!Objects.equals(firstFragmentTag, newlySelectedItemTag)) {
                        // Commit a transaction that cleans the back stack and adds the first fragment
                        // to it, creating the fixed started destination.
                        if (selectedFragment != null) {
                            final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction()
                                    .attach(selectedFragment)
                                    .setPrimaryNavigationFragment(selectedFragment);

                            for (int i = 0; i < graphIdToTagMap.size(); i++) {
                                final String fragmentTagIter = graphIdToTagMap.valueAt(i);
                                if (!Objects.equals(fragmentTagIter, newlySelectedItemTag)) {
                                    final Fragment fragmentByTag = fragmentManager.findFragmentByTag(firstFragmentTag);
                                    if (fragmentByTag != null)
                                        fragmentTransaction.detach(fragmentByTag);
                                }
                            }

                            fragmentTransaction.addToBackStack(firstFragmentTag)
                                    .setReorderingAllowed(true)
                                    .commit();
                        }
                    }
                    selectedItemTag[0] = newlySelectedItemTag;
                    isOnFirstFragment[0] = Objects.equals(selectedItemTag[0], firstFragmentTag);
                    if (selectedFragment != null) {
                        selectedNavController.setValue(selectedFragment.getNavController());
                        return true;
                    }
                }
            }
            return false;
        });

        // Optional: on item reselected, pop back stack to the destination of the graph
        setupItemReselected(bottomNavigationView, graphIdToTagMap, fragmentManager);

        // Handle deep link
        setupDeepLinks(bottomNavigationView, navGraphIds, fragmentManager, containerId, intent);

        // Finally, ensure that we update our BottomNavigationView when the back stack changes
        final int finalFirstFragmentGraphId = firstFragmentGraphId;
        fragmentManager.addOnBackStackChangedListener(() -> {
            if (!isOnFirstFragment[0] && !isOnBackStack(fragmentManager, firstFragmentTag))
                bottomNavigationView.setSelectedItemId(finalFirstFragmentGraphId);

            // Reset the graph if the currentDestination is not valid (happens when the back
            // stack is popped after using the back button).
            final NavController navControllerValue = selectedNavController.getValue();
            if (navControllerValue != null)
                if (navControllerValue.getCurrentDestination() == null)
                    navControllerValue.navigate(navControllerValue.getGraph().getId());
        });
        return selectedNavController;
    }

    private static void setupDeepLinks(BottomNavigationView bottomNavigationView,
                                       List<Integer> navGraphIds,
                                       FragmentManager fragmentManager,
                                       int containerId,
                                       Intent intent) {
        for (int index = 0; index < navGraphIds.size(); index++) {
            final String fragmentTag = getFragmentTag(index);

            // Find or create the Navigation host fragment
            final NavHostFragment navHostFragment = obtainNavHostFragment(
                    fragmentManager, fragmentTag, navGraphIds.get(index), containerId);

            // Handle Intent
            if (navHostFragment.getNavController().handleDeepLink(intent) &&
                    bottomNavigationView.getSelectedItemId() != navHostFragment.getNavController().getGraph().getId())
                bottomNavigationView.setSelectedItemId(navHostFragment.getNavController().getGraph().getId());
        }
    }

    private static void setupItemReselected(BottomNavigationView bottomNavigationView,
                                            SparseArray<String> gridIdToTagMap,
                                            FragmentManager fragmentManager) {
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            final String newlySelectedItemTag = gridIdToTagMap.get(item.getItemId());
            final NavHostFragment selectedFragment =
                    (NavHostFragment) fragmentManager.findFragmentByTag(newlySelectedItemTag);
            if (selectedFragment != null) {
                final NavController navController = selectedFragment.getNavController();
                navController.popBackStack(
                        navController.getGraph().getStartDestination(), false);
            }
        });
    }

    private static void detachNavHostFragment(FragmentManager fragmentManagerm,
                                              NavHostFragment navHostFragment) {
        fragmentManagerm.beginTransaction()
                .detach(navHostFragment)
                .commitNow();
    }

    private static void attachNavHostFragment(FragmentManager fragmentManager,
                                              NavHostFragment navHostFragment,
                                              boolean isPrimaryNavFragment) {
        fragmentManager.beginTransaction()
                .attach(navHostFragment)
                .setPrimaryNavigationFragment(isPrimaryNavFragment ? navHostFragment : null)
                .commitNow();
    }

    private static NavHostFragment obtainNavHostFragment(FragmentManager fragmentManager,
                                                         String fragmentTag,
                                                         int navGraphId,
                                                         int containerId) {
        // If the Nav Host fragment exists, return it
        final Fragment existingFragment = fragmentManager.findFragmentByTag(fragmentTag);
        if (existingFragment instanceof NavHostFragment)
            return (NavHostFragment) existingFragment;

        // Otherwise, create it and return it.
        final NavHostFragment navHostFragment = NavHostFragment.create(navGraphId);
        fragmentManager.beginTransaction()
                .add(containerId, navHostFragment, fragmentTag)
                .commitNow();
        return navHostFragment;
    }

    private static boolean isOnBackStack(FragmentManager fragmentManager,
                                         String backStackName) {
        final int backStackCount = fragmentManager.getBackStackEntryCount();
        for (int index = 0; index < backStackCount; index++)
            if (Objects.equals(fragmentManager.getBackStackEntryAt(index).getName(), backStackName))
                return true;
        return false;
    }

    private static String getFragmentTag(int index) {
        return "bottomNavigation$" + index;
    }

}
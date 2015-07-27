/*
 *  Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 *  This file is part of Alfresco Mobile for Android.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.alfresco.mobile.android.application.fragments.fileexplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.alfresco.mobile.android.application.R;
import org.alfresco.mobile.android.application.activity.BaseActivity;
import org.alfresco.mobile.android.application.fragments.MenuFragmentHelper;
import org.alfresco.mobile.android.application.fragments.builder.AlfrescoFragmentBuilder;
import org.alfresco.mobile.android.platform.io.AlfrescoStorageManager;
import org.alfresco.mobile.android.ui.fragments.AlfrescoFragment;
import org.alfresco.mobile.android.ui.utils.UIUtils;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class FileExplorerMenuFragment extends AlfrescoFragment
{
    public static final String TAG = FileExplorerMenuFragment.class.getName();

    private View currentSelectedButton = null;

    // ///////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    // ///////////////////////////////////////////////////////////////////////////
    public static FileExplorerMenuFragment newInstanceByTemplate(Bundle b)
    {
        FileExplorerMenuFragment cbf = new FileExplorerMenuFragment();
        cbf.setArguments(b);
        b.putBoolean(ARGUMENT_BASED_ON_TEMPLATE, true);
        return cbf;
    }

    // ///////////////////////////////////////////////////////////////////////////
    // LIFECYCLE
    // ///////////////////////////////////////////////////////////////////////////
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.app_fileexplorer_menu, container, false);
        initClickListener(rootView);

        return rootView;
    }

    @Override
    public void onResume()
    {
        UIUtils.displayTitle(getActivity(), getString(R.string.menu_local_files));
        super.onResume();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // UTILS
    // ///////////////////////////////////////////////////////////////////////////
    private void initClickListener(View rootView)
    {
        for (Integer buttonId : FILEEXPLORER_SHORTCUTS)
        {
            rootView.findViewById(buttonId).setOnClickListener(menuClickListener);
        }
    }

    private static final List<Integer> FILEEXPLORER_SHORTCUTS = new ArrayList<Integer>(7)
    {
        private static final long serialVersionUID = 1L;

        {
            add(R.id.shortcut_alfresco_downloads);

            add(R.id.shortcut_local_sdcard);
            add(R.id.shortcut_local_downloads);

            add(R.id.shortcut_library_office);
            add(R.id.shortcut_library_audios);
            add(R.id.shortcut_library_videos);
            add(R.id.shortcut_library_images);
        }
    };

    private OnClickListener menuClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            File currentLocation = null;
            int mediatype = -1;
            switch (v.getId())
            {
                case R.id.shortcut_alfresco_downloads:
                    currentLocation = AlfrescoStorageManager.getInstance(getActivity()).getDownloadFolder(
                            ((BaseActivity) getActivity()).getCurrentAccount());
                    break;
                case R.id.shortcut_local_sdcard:
                    currentLocation = Environment.getExternalStorageDirectory();
                    break;
                case R.id.shortcut_local_downloads:
                    currentLocation = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    break;
                case R.id.shortcut_library_office:
                    mediatype = MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;
                    break;
                case R.id.shortcut_library_audios:
                    mediatype = MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO;
                    break;
                case R.id.shortcut_library_videos:
                    mediatype = MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;
                    break;
                case R.id.shortcut_library_images:
                    mediatype = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
                    break;
                default:
                    break;
            }

            if (currentSelectedButton != null)
            {
                UIUtils.setBackground(
                        currentSelectedButton,
                        FileExplorerMenuFragment.this.getResources().getDrawable(
                                R.drawable.btn_default_holo_light_underline));
            }

            if (currentLocation != null)
            {
                FileExplorerFragment.with(getActivity()).file(currentLocation).display();
            }
            else if (mediatype >= 0)
            {
                LibraryFragment.with(getActivity()).mediaType(mediatype).display();
            }

            UIUtils.setBackground(
                    v,
                    FileExplorerMenuFragment.this.getResources().getDrawable(
                            R.drawable.alfrescohololight_btn_default_holo_light));
            currentSelectedButton = v;
        }
    };

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        if (!MenuFragmentHelper.canDisplayFragmentMenu(getActivity())) { return; }
        menu.clear();
    }

    // ///////////////////////////////////////////////////////////////////////////
    // BUILDER
    // ///////////////////////////////////////////////////////////////////////////
    public static Builder with(FragmentActivity appActivity)
    {
        return new Builder(appActivity);
    }

    public static class Builder extends AlfrescoFragmentBuilder
    {
        // ///////////////////////////////////////////////////////////////////////////
        // CONSTRUCTORS
        // ///////////////////////////////////////////////////////////////////////////
        public Builder(FragmentActivity activity)
        {
            super(activity);
            this.extraConfiguration = new Bundle();
        }

        public Builder(FragmentActivity appActivity, Map<String, Object> configuration)
        {
            super(appActivity, configuration);
            sessionRequired = false;
            menuIconId = R.drawable.ic_download_dark;
            menuTitleId = R.string.menu_local_files;
            templateArguments = new String[] {};
        }

        // ///////////////////////////////////////////////////////////////////////////
        // CLICK
        // ///////////////////////////////////////////////////////////////////////////
        protected Fragment createFragment(Bundle b)
        {
            return newInstanceByTemplate(b);
        }
    }
}

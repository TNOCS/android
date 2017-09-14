package org.policetracks.android.injection.components;

import org.policetracks.android.injection.modules.ActivityModule;
import org.policetracks.android.injection.modules.ViewModelModule;
import org.policetracks.android.injection.scopes.PerActivity;
import org.policetracks.android.ui.configuration.ConfigurationActivity;
import org.policetracks.android.ui.contacts.ContactsActivity;
import org.policetracks.android.ui.diary.DiaryActivity;
import org.policetracks.android.ui.interventions.InterventionsActivity;
import org.policetracks.android.ui.load.LoadActivity;
import org.policetracks.android.ui.map.MapActivity;
import org.policetracks.android.ui.status.StatusActivity;

import dagger.Component;

/* Copyright 2016 Patrick Löwenstein
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
@PerActivity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class, ViewModelModule.class})
public interface ActivityComponent {
    void inject(DiaryActivity activity);
    void inject(MapActivity activity);
    void inject(ContactsActivity activity);
    void inject(StatusActivity activity);
    void inject(ConfigurationActivity activity);
    void inject(LoadActivity activity);
    void inject(InterventionsActivity activity);
}

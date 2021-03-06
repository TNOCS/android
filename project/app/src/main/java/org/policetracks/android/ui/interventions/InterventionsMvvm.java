package org.policetracks.android.ui.interventions;

import android.content.Context;
import android.databinding.ObservableList;

import org.policetracks.android.db.Intervention;
import org.policetracks.android.ui.base.view.MvvmView;
import org.policetracks.android.ui.base.viewmodel.MvvmViewModel;

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
public interface InterventionsMvvm {

    interface View extends MvvmView {
    }

    interface ViewModel<V extends MvvmView> extends MvvmViewModel<V> {
        ObservableList<Intervention> getInterventions();
        InterventionsAdapter getInterventionsAdapter();
        void setInterventionsAdapter(InterventionsAdapter interventionsAdapter);
        void addIntervention(Context context);
        void checkInterventions();
        void updateAdapter();
        void setDayId(Long dayId);
        void onInterventionClick(Intervention iv, Context context, boolean longClick);
    }
}

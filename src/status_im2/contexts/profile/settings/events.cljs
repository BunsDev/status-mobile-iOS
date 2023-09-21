(ns status-im2.contexts.profile.settings.events
  (:require [utils.re-frame :as rf]
            [re-frame.core :as re-frame]
            [native-module.core :as native-module]))

(re-frame/reg-fx
  :profile.settings/blank-preview-flag-changed
  (fn [flag]
    (native-module/set-blank-preview-flag flag)))

(rf/defn switch-preview-privacy-mode-flag
  [{:keys [db]}]
  (let [private? (get-in db [:profile/profile :preview-privacy?])]
    {:profile.settings/blank-preview-flag-changed private?}))
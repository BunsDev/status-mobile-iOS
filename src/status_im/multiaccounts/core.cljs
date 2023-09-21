(ns status-im.multiaccounts.core
  (:require [clojure.string :as string]
            [re-frame.core :as re-frame]
            [native-module.core :as native-module]
            [quo.platform :as platform]
            [quo2.foundations.colors :as colors]
            [status-im.bottom-sheet.events :as bottom-sheet]
            [status-im.multiaccounts.update.core :as multiaccounts.update]
            [status-im2.common.theme.core :as theme]
            [status-im2.constants :as constants]
            [status-im2.contexts.shell.jump-to.utils :as shell.utils]
            [status-im2.setup.hot-reload :as hot-reload]
            [taoensso.timbre :as log]
            [utils.re-frame :as rf]))

;; validate that the given mnemonic was generated from Status Dictionary
(defn displayed-photo
  [{:keys [images]}]
  (or (:thumbnail images)
      (:large images)
      (first images)))

(re-frame/reg-fx
 ::webview-debug-changed
 (fn [value]
   (when platform/android?
     (native-module/toggle-webview-debug value))))

(rf/defn confirm-wallet-set-up
  {:events [:multiaccounts.ui/wallet-set-up-confirmed]}
  [cofx]
  (multiaccounts.update/multiaccount-update cofx
                                            :wallet-set-up-passed?
                                            true
                                            {}))

(rf/defn confirm-home-tooltip
  {:events [:multiaccounts.ui/hide-home-tooltip]}
  [cofx]
  (multiaccounts.update/multiaccount-update cofx
                                            :hide-home-tooltip?
                                            true
                                            {}))

(rf/defn switch-webview-debug
  {:events [:multiaccounts.ui/switch-webview-debug]}
  [{:keys [db] :as cofx} value]
  (rf/merge cofx
            {::webview-debug-changed value}
            (multiaccounts.update/multiaccount-update
             :webview-debug
             (boolean value)
             {})))

(rf/defn switch-preview-privacy-mode
  {:events [:multiaccounts.ui/preview-privacy-mode-switched]}
  [{:keys [db] :as cofx} private?]
  (rf/merge cofx
            {::blank-preview-flag-changed private?}
            (multiaccounts.update/multiaccount-update
             :preview-privacy?
             (boolean private?)
             {})))

(rf/defn switch-webview-permission-requests?
  {:events [:multiaccounts.ui/webview-permission-requests-switched]}
  [cofx enabled?]
  (multiaccounts.update/multiaccount-update
   cofx
   :webview-allow-permission-requests?
   (boolean enabled?)
   {}))

(rf/defn switch-default-sync-period
  {:events [:multiaccounts.ui/default-sync-period-switched]}
  [cofx value]
  (multiaccounts.update/multiaccount-update
   cofx
   :default-sync-period
   value
   {}))

(re-frame/reg-fx
 :multiaccounts.ui/switch-theme-fx
 (fn [[theme-type view-id reload-ui?]]
   (let [[theme status-bar-theme nav-bar-color]
         ;; Status bar theme represents status bar icons colors, so opposite to app theme
         (if (or (= theme-type constants/theme-type-dark)
                 (and (= theme-type constants/theme-type-system)
                      (theme/device-theme-dark?)))
           [:dark :light colors/neutral-100]
           [:light :dark colors/white])]
     (theme/set-theme theme)
     (re-frame/dispatch [:change-shell-status-bar-style
                         (if (shell.utils/home-stack-open?) status-bar-theme :light)])
     (when reload-ui?
       (rf/dispatch [:dissmiss-all-overlays])
       (hot-reload/reload)
       (when-not (= view-id :shell-stack)
         (re-frame/dispatch [:change-shell-nav-bar-color nav-bar-color]))))))

(rf/defn switch-appearance
  {:events [:multiaccounts.ui/appearance-switched]}
  [cofx theme]
  (rf/merge cofx
            {:multiaccounts.ui/switch-theme-fx [theme :appearance true]}
            (multiaccounts.update/multiaccount-update :appearance theme {})))

(rf/defn switch-theme
  {:events [:multiaccounts.ui/switch-theme]}
  [cofx theme view-id]
  (let [theme (or theme
                  (get-in cofx [:db :profile/profile :appearance])
                  constants/theme-type-dark)]
    {:multiaccounts.ui/switch-theme-fx [theme view-id false]}))

(rf/defn switch-profile-picture-show-to
  {:events [:multiaccounts.ui/profile-picture-show-to-switched]}
  [cofx id]
  (rf/merge cofx
            {:json-rpc/call [{:method     "wakuext_changeIdentityImageShowTo"
                              :params     [id]
                              :on-success #(log/debug "picture settings changed successfully")}]}
            (multiaccounts.update/optimistic :profile-pictures-show-to id)))

(rf/defn switch-appearance-profile
  {:events [:multiaccounts.ui/appearance-profile-switched]}
  [cofx id]
  (multiaccounts.update/multiaccount-update cofx :profile-pictures-visibility id {}))

(defn clean-path
  [path]
  (if path
    (string/replace-first path #"file://" "")
    (log/warn "[native-module] Empty path was provided")))

(rf/defn save-profile-picture
  {:events [::save-profile-picture]}
  [cofx path ax ay bx by]
  (let [key-uid (get-in cofx [:db :profile/profile :key-uid])]
    (rf/merge cofx
              {:json-rpc/call [{:method     "multiaccounts_storeIdentityImage"
                                :params     [key-uid (clean-path path) ax ay bx by]
                                ;; NOTE: In case of an error we can show a toast error
                                :on-success #(re-frame/dispatch [::update-local-picture %])}]}
              (bottom-sheet/hide-bottom-sheet-old))))

(rf/defn save-profile-picture-from-url
  {:events [::save-profile-picture-from-url]}
  [cofx url]
  (let [key-uid (get-in cofx [:db :profile/profile :key-uid])]
    (rf/merge cofx
              {:json-rpc/call [{:method     "multiaccounts_storeIdentityImageFromURL"
                                :params     [key-uid url]
                                :on-error   #(log/error "::save-profile-picture-from-url error" %)
                                :on-success #(re-frame/dispatch [::update-local-picture %])}]}
              (bottom-sheet/hide-bottom-sheet-old))))

(comment
  (re-frame/dispatch
   [::save-profile-picture-from-url
    "https://lh3.googleusercontent.com/XuKjNm3HydsaxbPkbpGs9YyCKhn5QQk5oDC8XF2jzmPyYXeZofxFtfUDZuQ3EVmacS_BlBKzbX2ypm37YNX3n1fDJA3WndeFcPsp7Z0=w600"]))

(rf/defn delete-profile-picture
  {:events [::delete-profile-picture]}
  [cofx name]
  (let [key-uid (get-in cofx [:db :profile/profile :key-uid])]
    (rf/merge cofx
              {:json-rpc/call [{:method     "multiaccounts_deleteIdentityImage"
                                :params     [key-uid]
                                ;; NOTE: In case of an error we could fallback to previous image in
                                ;; UI with a toast error
                                :on-success #(log/info "[multiaccount] Delete profile image" %)}]}
              (multiaccounts.update/optimistic :images nil)
              (bottom-sheet/hide-bottom-sheet-old))))

(rf/defn get-profile-picture
  [cofx]
  (let [key-uid (get-in cofx [:db :profile/profile :key-uid])]
    {:json-rpc/call [{:method     "multiaccounts_getIdentityImages"
                      :params     [key-uid]
                      :on-success #(re-frame/dispatch [::update-local-picture %])}]}))

(rf/defn store-profile-picture
  {:events [::update-local-picture]}
  [cofx pics]
  (multiaccounts.update/optimistic cofx :images pics))


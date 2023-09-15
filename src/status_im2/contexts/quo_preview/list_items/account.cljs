(ns status-im2.contexts.quo-preview.list-items.account
  (:require [quo2.core :as quo]
            [reagent.core :as reagent]
            [status-im2.contexts.quo-preview.preview :as preview]))

(def descriptor
  [{:key :action :type :select :options [{:key :none} {:key :icon}]}
   {:key :blur? :type :boolean}])

(defn view
  []
  (let [state (reagent/atom {:type    :default
                             :title   "New House"
                             :address "0x21a43243243242349e"})]
    (fn [] [preview/preview-container
            {:state                 state
             :descriptor            descriptor
             :blur?                 (:blur? @state)
             :show-blur-background? true
             :blur-dark-only?       true} [quo/account-item @state]])))

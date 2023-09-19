(ns status-im2.contexts.quo-preview.wallet.network-routing
  (:require [quo2.core :as quo]
            [react-native.core :as rn]
            [reagent.core :as reagent]
            [status-im2.contexts.quo-preview.preview :as preview]))

(def descriptor
  [])

(defn preview
  []
  (let [state (reagent/atom {:state               :pending
                             :customization-color :blue})]
    (fn []
      [preview/preview-container {:state state :descriptor descriptor}
       [rn/view {:style {:flex 1}}
        [quo/network-routing @state]]])))

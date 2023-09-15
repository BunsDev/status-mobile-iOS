(ns quo2.components.list-items.account.view
  (:require [quo2.components.avatars.account-avatar.view :as account-avatar]
            [quo2.components.markdown.text :as text]
            [quo2.foundations.colors :as colors]
            [quo2.theme :as quo.theme]
            [react-native.core :as rn]
            [quo2.components.list-items.account.style :as style]
            [reagent.core :as reagent]))

(defn- internal-view
  []
  (let [state (reagent/atom :default)]
    (fn [{:keys [blur? account-props customization-color token-props balance-props on-press
                 on-options-press
                 theme]}]
      [rn/pressable
       {:style               (style/container
                              {:state @state :blur? blur? :customization-color customization-color})
        :on-press-in         #(reset! state :pressed)
        :on-press-out        #(reset! state :default)
        :on-press            on-press
        :accessibility-label :container}
       [rn/view {:style style/left-container}
        [account-avatar/view (assoc account-props :size 32)]
        [rn/view {:style {:margin-left 8}}
         [text/text {:weight :semi-bold :size :paragraph-2}
          (:name account-props)]
         [text/text {:size :paragraph-2}
          [text/text
           {:size  :paragraph-2
            :style {:color (if blur?
                             colors/white-opa-40
                             (colors/theme-colors colors/neutral-50
                                                  colors/neutral-40
                                                  theme))}}
           (:address account-props)]]]]])))

(def view (quo.theme/with-theme internal-view))


(ns quo2.components.list-items.account.view
  (:require [quo2.components.avatars.account-avatar.view :as account-avatar]
            [quo2.components.markdown.text :as text]
            [quo2.foundations.colors :as colors]
            [quo2.theme :as quo.theme]
            [react-native.core :as rn]
            [quo2.components.list-items.account.style :as style]
            [reagent.core :as reagent]
            [quo2.components.icon :as icon]
            [quo.components.button.view :as button]))

(defn- f-internal-view
  []
  (let [state               (reagent/atom :default)
        active-or-selected? (atom false)
        timer               (atom nil)]
    (fn [{:keys [type selectable? blur? account-props customization-color token-props balance-props
                 on-press
                 on-options-press
                 theme]}]
      ;; TODO: set default values
      (rn/use-effect
       #(cond (and selectable? (= type :default) (= @state :active))         (reset! state :selected)
              (and (not selectable?) (= type :default) (= @state :selected)) (reset! state :active))
       [selectable?])
      [rn/pressable
       {:style               (style/container
                              {:state @state :blur? blur? :customization-color customization-color})
        :on-press-in         (fn []
                               (when-not (= @state :selected)
                                 (reset! timer (js/setTimeout #(reset! state :pressed) 100))))
        :on-press-out        (fn []
                               (let [new-state (if @active-or-selected?
                                                 :default
                                                 (if selectable? :selected :active))]
                                 (when @timer (js/clearTimeout @timer))
                                 (reset! timer nil)
                                 (reset! active-or-selected? (or (= new-state :active)
                                                                 (= new-state :selected)))
                                 (reset! state new-state)
                                 on-press))
        :accessibility-label :container}
       [rn/view {:style style/left-container}
        [account-avatar/view (assoc account-props :size 32)]
        [rn/view {:style {:margin-left 8}}
         [text/text {:weight :semi-bold :size :paragraph-2}
          (:name account-props)]
         (when (= type :icon)
           [rn/view
            {:style               {:margin-left 4}
             :accessibility-label :keycard-icon}
            [icon/icon :i/keycard
             (style/arrow-icon type theme)]])
         [text/text {:size :paragraph-2}
          [text/text
           {:size   :paragraph-2
            :weight :monospace
            :style  {:color (if blur?
                              colors/white-opa-40
                              (colors/theme-colors colors/neutral-50
                                                   colors/neutral-40
                                                   theme))}}
           (:address account-props)]]]]
       [rn/view
        (when (or (= type :balance-neutral)
                  (= type :balance-negative)
                  (= type :balance-positive))
          [rn/view
           {:style {:flex-direction :row
                    :align-items    :center}}
           [text/text
            {:size  :paragraph-2
             :style (style/metric-text type theme)} (str (:percentage-change balance-props) "%")]
           [rn/view {:style (style/dot-divider type theme)}]
           [text/text
            {:size  :paragraph-2
             :style (style/metric-text type theme)} (:fiat-change balance-props)]
           (when (not= type :balance-neutral)
             [rn/view
              {:style               {:margin-left 4}
               :accessibility-label :arrow-icon}
              [icon/icon (if (= type :balance-positive) :i/positive :i/negative)
               (style/arrow-icon type theme)]])])
        (when (= type :tag)
          [rn/view
           {:style {:flex-direction :row
                    :align-items    :center
                    :width          70
                    :heigh          24
                    :border-width   1
                    :border-color   :red}}
           [text/text
            {:size  :paragraph-2
             :style (style/metric-text type theme)}
            (str (:value token-props) " " (:symbol token-props))]])
        (when (= type :action)
          [button/button
           {:accessibility-label :options-button
            :icon-only?          true
            ;:type                :outline
            :size                20
            ;:inner-style         (style/accessory-button blur? theme)
            :on-press            on-options-press}
           :i/options])
        (when (and (= type :default)
                   (= @state :selected))
          [rn/view
           {:style               {:margin-left 4}
            :accessibility-label :keycard-icon}
           [icon/icon :i/check
            {:color (colors/custom-color-by-theme customization-color 50 60 nil nil theme)}]])]])))

(defn- internal-view
  [props]
  [:f> f-internal-view props])

(def view (quo.theme/with-theme internal-view))

